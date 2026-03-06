package com.example.nsapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val time: String,
    val date: String,
    val priority: String,
    val triggerTimeMs: Long,
    val isSent: Boolean
)
