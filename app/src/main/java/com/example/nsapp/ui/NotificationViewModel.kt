package com.example.nsapp.ui

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nsapp.data.AppDatabase
import com.example.nsapp.data.NotificationEntity
import com.example.nsapp.utils.NotificationReceiver
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

data class AppNotification(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val message: String,
    val time: String,
    val date: String,
    val priority: String,
    val triggerTimeMs: Long,
    val isSent: Boolean = false
)

data class PriorityLevelState(
    val name: String,
    val description: String,
    val colorHex: Long,
    var isEnabled: Boolean
)

sealed class UiMessage {
    data class Success(val message: String) : UiMessage()
    data class Error(val message: String) : UiMessage()
}

class NotificationViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val dao = db.notificationDao()

    private val _notifications = mutableStateListOf<AppNotification>()
    val notifications: List<AppNotification> get() = _notifications

    var uiMessage by mutableStateOf<UiMessage?>(null)
        private set

    // Auth and Session State
    var isLoggedIn by mutableStateOf(false)
    var userName by mutableStateOf("User")
    var userEmail by mutableStateOf("")
    var userPassword by mutableStateOf("")
    var registrationTimeInMillis by mutableStateOf(0L)

    // Settings State
    var isDarkMode by mutableStateOf(false)
    var areNotificationsEnabled by mutableStateOf(true)

    // Priority State
    val priorities = mutableStateListOf(
        PriorityLevelState("Urgent", "Immediate sound and vibration", 0xFFD32F2F, true),
        PriorityLevelState("High", "Important notification with sound", 0xFFF57C00, true),
        PriorityLevelState("Normal", "Standard behavior", 0xFF1976D2, true),
        PriorityLevelState("Low", "Silent in background", 0xFF388E3C, false)
    )

    init {
        viewModelScope.launch {
            dao.getAllNotifications().collect { entities ->
                _notifications.clear()
                val currentList = entities.map { it.toAppNotification() }
                
                // Automatically move expired notifications to history
                val now = System.currentTimeMillis()
                currentList.forEach { notification ->
                    if (!notification.isSent && notification.triggerTimeMs < now) {
                        updateNotificationStatus(notification.id, true)
                    }
                }
                _notifications.addAll(currentList)
            }
        }
    }

    fun clearUiMessage() {
        uiMessage = null
    }

    private fun showMessage(message: UiMessage) {
        viewModelScope.launch {
            uiMessage = message
            delay(3000)
            if (uiMessage == message) uiMessage = null
        }
    }

    fun checkOverlap(newTimeMs: Long): Boolean {
        // Consider a conflict if within 1 minute of another notification
        val buffer = 60000L 
        return _notifications.any { !it.isSent && Math.abs(it.triggerTimeMs - newTimeMs) < buffer }
    }

    fun addNotification(notification: AppNotification, context: Context) {
        if (!areNotificationsEnabled) {
            showMessage(UiMessage.Error("Notifications are disabled in settings"))
            return
        }

        if (checkOverlap(notification.triggerTimeMs)) {
            showMessage(UiMessage.Error("This notification conflicts with another scheduled task"))
            return
        }

        viewModelScope.launch {
            dao.insertNotification(notification.toEntity())
            scheduleSystemAlert(notification, context)
            showMessage(UiMessage.Success("Notification created successfully"))
        }
    }

    fun updateNotificationStatus(notificationId: String, isSent: Boolean) {
        viewModelScope.launch {
            val notification = _notifications.find { it.id == notificationId }
            notification?.let {
                val updated = it.copy(isSent = isSent)
                dao.insertNotification(updated.toEntity())
            }
        }
    }

    private fun scheduleSystemAlert(notification: AppNotification, context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("title", notification.title)
            putExtra("message", notification.message)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notification.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            notification.triggerTimeMs,
            pendingIntent
        )
    }

    fun deleteNotification(notificationId: String, context: Context) {
        val notification = _notifications.find { it.id == notificationId }
        notification?.let {
            viewModelScope.launch {
                cancelAlarm(it, context)
                dao.deleteById(it.id)
                showMessage(UiMessage.Success("Notification deleted successfully"))
            }
        }
    }

    private fun cancelAlarm(notification: AppNotification, context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notification.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun updatePriority(name: String, isEnabled: Boolean) {
        val index = priorities.indexOfFirst { it.name == name }
        if (index != -1) {
            priorities[index] = priorities[index].copy(isEnabled = isEnabled)
        }
    }

    fun getPendingNotifications() = _notifications.filter { !it.isSent }
    fun getSentNotifications() = _notifications.filter { it.isSent }

    fun scheduleDailyReminder(context: Context) {
        if (registrationTimeInMillis == 0L) return
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("title", "Daily Reminder")
            putExtra("message", "Time to check your NotifySync schedule!")
        }
        val pendingIntent = PendingIntent.getBroadcast(context, 999, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, registrationTimeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
    }

    fun logout() { isLoggedIn = false }

    private fun NotificationEntity.toAppNotification() = AppNotification(id, title, message, time, date, priority, triggerTimeMs, isSent)
    private fun AppNotification.toEntity() = NotificationEntity(id, title, message, time, date, priority, triggerTimeMs, isSent)
}
