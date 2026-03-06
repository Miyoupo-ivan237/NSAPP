package com.example.nsapp.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nsapp.ui.NotificationViewModel
import com.example.nsapp.ui.AppNotification
import java.util.*
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNotificationScreen(navController: NavController, viewModel: NotificationViewModel) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    
    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf("Normal") }
    var selectedDate by remember { mutableStateOf(dateFormatter.format(calendar.time)) }
    var selectedTime by remember { mutableStateOf(timeFormatter.format(calendar.time)) }
    
    val priorities = listOf("Low", "Normal", "High", "Urgent")

    // Date Picker Dialog
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            selectedDate = dateFormatter.format(calendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // Time Picker Dialog
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)
            selectedTime = timeFormatter.format(calendar.time)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        false
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Notification", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(
                        Icons.Default.NotificationsActive,
                        contentDescription = null,
                        modifier = Modifier.padding(16.dp).size(48.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Text("Notification Details", fontSize = 18.sp, fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                placeholder = { Text("e.g., Morning Standup") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )

            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Message") },
                placeholder = { Text("Don't forget the daily meeting...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                shape = MaterialTheme.shapes.medium
            )

            Text("Priority Level", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                priorities.forEach { priority ->
                    FilterChip(
                        selected = selectedPriority == priority,
                        onClick = { selectedPriority = priority },
                        label = { Text(priority) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = { datePickerDialog.show() },
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(selectedDate)
                }
                OutlinedButton(
                    onClick = { timePickerDialog.show() },
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(Icons.Default.Schedule, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(selectedTime)
                }
            }

            Button(
                onClick = { 
                    if (title.isNotEmpty() && message.isNotEmpty() && viewModel.areNotificationsEnabled) {
                        viewModel.addNotification(
                            AppNotification(
                                title = title,
                                message = message,
                                priority = selectedPriority,
                                date = selectedDate,
                                time = selectedTime,
                                triggerTimeMs = calendar.timeInMillis
                            ),
                            context
                        )
                        navController.popBackStack()
                    }
                },
                enabled = viewModel.areNotificationsEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(top = 16.dp),
                shape = MaterialTheme.shapes.large
            ) {
                if (!viewModel.areNotificationsEnabled) {
                    Text("Notifications Disabled in Settings", fontSize = 14.sp)
                } else {
                    Text("Schedule Notification", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
