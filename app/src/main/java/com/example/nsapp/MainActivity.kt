package com.example.nsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nsapp.ui.theme.NSAPPTheme
import com.example.nsapp.ui.screens.*
import com.example.nsapp.ui.NotificationViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val notificationViewModel: NotificationViewModel = viewModel()
            NSAPPTheme(darkTheme = notificationViewModel.isDarkMode) {
                MainScreen(notificationViewModel)
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: NotificationViewModel) {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = "signup") {
        composable("signup") { SignUpScreen(navController, viewModel) }
        composable("login") { LoginScreen(navController, viewModel) }
        composable("home") { HomeScreen(navController, viewModel) }
        composable("create_notification") { CreateNotificationScreen(navController, viewModel) }
        composable("schedule_manager") { ScheduleManagerScreen(navController, viewModel) }
        composable("notification_list") { NotificationListScreen(navController, viewModel) }
        composable("priority_settings") { PrioritySettingsScreen(navController, viewModel) }
        composable("settings") { SettingsScreen(navController, viewModel) }
        composable("profile_settings") { ProfileSettingsScreen(navController, viewModel) }
        
        // Settings Sub-pages
        composable("about_notifications") { GenericSettingsDetailScreen("About Notifications", navController) }
        composable("security_privacy") { GenericSettingsDetailScreen("Security & Privacy", navController) }
        composable("help_support") { HelpSupportScreen(navController) }
    }
}
