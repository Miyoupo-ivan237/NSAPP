package com.example.nsapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nsapp.ui.NotificationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, viewModel: NotificationViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                SettingsCategoryHeader("App Experience")
                SettingsToggleItem(
                    title = "Dark Mode",
                    icon = Icons.Default.DarkMode,
                    checked = viewModel.isDarkMode,
                    onCheckedChange = { viewModel.isDarkMode = it }
                )
                SettingsToggleItem(
                    title = "Notifications Enabled",
                    icon = Icons.Default.Notifications,
                    checked = viewModel.areNotificationsEnabled,
                    onCheckedChange = { viewModel.areNotificationsEnabled = it }
                )
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                SettingsCategoryHeader("Information & Guidance")
                SettingsClickItem("About Notifications", Icons.Default.Info) { 
                    navController.navigate("about_notifications") 
                }
                SettingsClickItem("Help & Support", Icons.Default.Help) { 
                    navController.navigate("help_support") 
                }
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                SettingsCategoryHeader("Security & Account")
                SettingsClickItem("Profile Settings", Icons.Default.Person) { 
                    navController.navigate("profile_settings") 
                }
                SettingsClickItem("Security & Privacy", Icons.Default.Security) { 
                    navController.navigate("security_privacy") 
                }
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                SettingsCategoryHeader("Session")
                SettingsClickItem("Logout", Icons.Default.Logout) { 
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "NotifySync v1.0.0",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun SettingsCategoryHeader(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(16.dp),
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun SettingsToggleItem(title: String, icon: ImageVector, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, modifier = Modifier.weight(1f), fontSize = 16.sp)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun SettingsClickItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, modifier = Modifier.weight(1f), fontSize = 16.sp)
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
    }
}
