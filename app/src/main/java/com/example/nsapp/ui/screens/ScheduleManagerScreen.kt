package com.example.nsapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nsapp.ui.NotificationViewModel
import com.example.nsapp.ui.AppNotification

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleManagerScreen(navController: NavController, viewModel: NotificationViewModel) {
    val pendingTasks = viewModel.getPendingNotifications()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Schedule Manager", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("create_notification") },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Schedule")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Text(
                text = "Upcoming Notifications",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (pendingTasks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No scheduled notifications", color = MaterialTheme.colorScheme.outline)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(pendingTasks) { task ->
                        EnhancedTaskItem(task) {
                            viewModel.deleteNotification(task.id, context)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EnhancedTaskItem(task: AppNotification, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.NotificationsActive,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = task.date, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = task.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = "Scheduled at ${task.time}", fontSize = 14.sp, color = MaterialTheme.colorScheme.outline)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    color = getPriorityColor(task.priority),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = task.priority,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

fun getPriorityColor(priority: String): Color {
    return when (priority) {
        "Urgent" -> Color(0xFFD32F2F)
        "High" -> Color(0xFFF57C00)
        "Normal" -> Color(0xFF1976D2)
        else -> Color(0xFF388E3C)
    }
}
