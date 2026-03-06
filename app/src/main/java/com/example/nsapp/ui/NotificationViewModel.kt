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

class NotificationViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val dao = db.notificationDao()

    private val _notifications = mutableStateListOf<AppNotification>()
    val notifications: List<AppNotification> get() = _notifications

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
        // Load notifications from database on startup
        viewModelScope.launch {
            dao.getAllNotifications().collect { entities ->
                _notifications.clear()
                _notifications.addAll(entities.map { it.toAppNotification() })
            }
        }
    }

    fun addNotification(notification: AppNotification, context: Context) {
        if (areNotificationsEnabled) {
            viewModelScope.launch {
                dao.insertNotification(notification.toEntity())
                scheduleSystemAlert(notification, context)
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

    // Mapper functions
    private fun NotificationEntity.toAppNotification() = AppNotification(id, title, message, time, date, priority, triggerTimeMs, isSent)
    private fun AppNotification.toEntity() = NotificationEntity(id, title, message, time, date, priority, triggerTimeMs, isSent)
}
