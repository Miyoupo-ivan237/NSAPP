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
import androidx.compose.ui.graphics.Color
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
    val now = Calendar.getInstance()
    
    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    var title by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf<String?>(null) }
    
    var message by remember { mutableStateOf("") }
    val maxDescriptionLength = 250
    
    var selectedPriority by remember { mutableStateOf<String?>(null) }
    var priorityError by remember { mutableStateOf<String?>(null) }
    
    var selectedDate by remember { mutableStateOf(dateFormatter.format(calendar.time)) }
    var dateError by remember { mutableStateOf<String?>(null) }
    
    var selectedTime by remember { mutableStateOf(timeFormatter.format(calendar.time)) }
    
    val priorities = listOf("Low", "Normal", "High", "Urgent")

    // Date Picker Dialog with Past Date Check
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(year, month, dayOfMonth)
            
            // Basic Past Date Validation (check day level)
            if (selectedCalendar.get(Calendar.YEAR) < now.get(Calendar.YEAR) ||
                (selectedCalendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) && 
                 selectedCalendar.get(Calendar.DAY_OF_YEAR) < now.get(Calendar.DAY_OF_YEAR))) {
                dateError = "Cannot select a past date"
            } else {
                dateError = null
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                selectedDate = dateFormatter.format(calendar.time)
            }
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
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

            // Title Field with Error Handling
            OutlinedTextField(
                value = title,
                onValueChange = { 
                    title = it
                    titleError = if (it.isEmpty()) "Title cannot be empty" else null
                },
                label = { Text("Title") },
                isError = titleError != null,
                supportingText = { titleError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )

            // Description Field with Max Length Handling
            Column {
                OutlinedTextField(
                    value = message,
                    onValueChange = { 
                        if (it.length <= maxDescriptionLength) {
                            message = it
                        }
                    },
                    label = { Text("Message (Max $maxDescriptionLength chars)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = MaterialTheme.shapes.medium,
                    colors = if (message.length >= maxDescriptionLength) 
                        OutlinedTextFieldDefaults.colors(focusedBorderColor = Color.Red, unfocusedBorderColor = Color.Red) 
                        else OutlinedTextFieldDefaults.colors()
                )
                if (message.length >= maxDescriptionLength) {
                    Text(
                        "Warning: Maximum character limit reached",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                    )
                }
            }

            // Priority Selection with Error Handling
            Text("Priority Level", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                priorities.forEach { priority ->
                    FilterChip(
                        selected = selectedPriority == priority,
                        onClick = { 
                            selectedPriority = priority 
                            priorityError = null
                        },
                        label = { Text(priority) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            priorityError?.let {
                Text(it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(start = 8.dp))
            }

            HorizontalDivider()

            // Date and Time Buttons with Date Error Handling
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    OutlinedButton(
                        onClick = { datePickerDialog.show() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        border = if (dateError != null) ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Color.Red)) else ButtonDefaults.outlinedButtonBorder
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(selectedDate)
                    }
                    dateError?.let {
                        Text(it, color = Color.Red, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
                    }
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

            // Final Schedule Button with Multi-Validation Check
            Button(
                onClick = { 
                    val hasTitleError = title.isEmpty()
                    val hasPriorityError = selectedPriority == null
                    val hasDateError = dateError != null
                    
                    if (hasTitleError) titleError = "Title is required"
                    if (hasPriorityError) priorityError = "Select a valid priority"
                    
                    if (!hasTitleError && !hasPriorityError && !hasDateError && viewModel.areNotificationsEnabled) {
                        viewModel.addNotification(
                            AppNotification(
                                title = title,
                                message = message,
                                priority = selectedPriority!!,
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
                    .padding(top = 8.dp),
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
