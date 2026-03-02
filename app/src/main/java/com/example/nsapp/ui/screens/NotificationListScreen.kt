package com.example.nsapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class AppNotification(
    val id: Int,
    val title: String,
    val message: String,
    val time: String,
    val status: NotificationStatus
)

enum class NotificationStatus { SENT, PENDING, FAILED }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationListScreen(navController: NavController) {
    val notifications = listOf(
        AppNotification(1, "System Update", "Your system is up to date with the latest security patch.", "2m ago", NotificationStatus.SENT),
        AppNotification(2, "Meeting Reminder", "Strategy meeting starts in 15 minutes.", "15m ago", NotificationStatus.SENT),
        AppNotification(3, "Alert", "High CPU usage detected on Server A.", "3h ago", NotificationStatus.FAILED),
        AppNotification(4, "Daily Sync", "Scheduled daily report generation.", "Tomorrow", NotificationStatus.PENDING)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notification History", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Search */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            item {
                Text(
                    "Recent Activity",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            items(notifications) { notification ->
                EnhancedNotificationItem(notification)
                Divider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            }
        }
    }
}

@Composable
fun EnhancedNotificationItem(notification: AppNotification) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(getStatusBackgroundColor(notification.status)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = getStatusIcon(notification.status),
                contentDescription = null,
                tint = getStatusColor(notification.status),
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = notification.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = notification.time,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Text(
                text = notification.message,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun getStatusColor(status: NotificationStatus): Color {
    return when (status) {
        NotificationStatus.SENT -> Color(0xFF388E3C)
        NotificationStatus.PENDING -> Color(0xFF1976D2)
        NotificationStatus.FAILED -> Color(0xFFD32F2F)
    }
}

@Composable
fun getStatusBackgroundColor(status: NotificationStatus): Color {
    return getStatusColor(status).copy(alpha = 0.12f)
}

fun getStatusIcon(status: NotificationStatus): ImageVector = when (status) {
    NotificationStatus.SENT -> Icons.Default.DoneAll
    NotificationStatus.PENDING -> Icons.Default.Schedule
    NotificationStatus.FAILED -> Icons.Default.Notifications
}
