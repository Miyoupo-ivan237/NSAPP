package com.example.nsapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nsapp.ui.NotificationViewModel
import com.example.nsapp.ui.PriorityLevelState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrioritySettingsScreen(navController: NavController, viewModel: NotificationViewModel) {
    val priorities = viewModel.priorities

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Priority Settings", fontWeight = FontWeight.Bold) },
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
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.onTertiaryContainer)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Configure how different priority levels behave on your device.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(priorities) { priority ->
                    PriorityItem(priority) { isChecked ->
                        viewModel.updatePriority(priority.name, isChecked)
                    }
                }
            }
        }
    }
}

@Composable
fun PriorityItem(priority: PriorityLevelState, onCheckedChange: (Boolean) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Surface(
                    modifier = Modifier.size(12.dp),
                    shape = CircleShape,
                    color = Color(priority.colorHex)
                ) {}
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = priority.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = priority.description,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Switch(
                checked = priority.isEnabled,
                onCheckedChange = onCheckedChange
            )
        }
    }
}
