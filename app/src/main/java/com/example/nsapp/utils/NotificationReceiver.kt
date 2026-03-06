package com.example.nsapp.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "NotifySync Alert"
        val message = intent.getStringExtra("message") ?: "Time for your scheduled task!"
        
        val notificationHelper = NotificationHelper(context)
        notificationHelper.showNotification(title, message)
    }
}
