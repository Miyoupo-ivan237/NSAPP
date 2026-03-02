package com.example.nsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nsapp.ui.theme.NSAPPTheme
import com.example.nsapp.ui.screens.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NSAPPTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignUpScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("create_notification") { CreateNotificationScreen(navController) }
        composable("schedule_manager") { ScheduleManagerScreen(navController) }
        composable("notification_list") { NotificationListScreen(navController) }
        composable("priority_settings") { PrioritySettingsScreen(navController) }
        composable("settings") { SettingsScreen(navController) }
    }
}
