package com.example.nsapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ChevronRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nsapp.ui.NotificationViewModel

data class HomeFeature(
    val title: String,
    val icon: ImageVector,
    val route: String,
    val color1: Color,
    val color2: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: NotificationViewModel) {
    val features = listOf(
        HomeFeature("Create New", Icons.Default.AddAlert, "create_notification", Color(0xFF6750A4), Color(0xFF9575CD)),
        HomeFeature("Schedule", Icons.Default.Schedule, "schedule_manager", Color(0xFF00796B), Color(0xFF4DB6AC)),
        HomeFeature("History", Icons.Default.List, "notification_list", Color(0xFFC2185B), Color(0xFFF06292)),
        HomeFeature("Priorities", Icons.Default.PriorityHigh, "priority_settings", Color(0xFFE64A19), Color(0xFFFF8A65)),
        HomeFeature("Settings", Icons.Default.Settings, "settings", Color(0xFF455A64), Color(0xFF90A4AE))
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("NotifySync", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
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
                text = "Welcome Back, ${viewModel.userName}!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Manage your notifications easily",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(features) { feature ->
                    EnhancedFeatureCard(feature) {
                        navController.navigate(feature.route)
                    }
                }
            }
        }
    }
}

@Composable
fun EnhancedFeatureCard(feature: HomeFeature, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(feature.color1, feature.color2)))
                .padding(16.dp)
        ) {
            Icon(
                imageVector = feature.icon,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.TopStart),
                tint = Color.White
            )
            
            Text(
                text = feature.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.BottomStart)
            )
            
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ChevronRight,
                contentDescription = null,
                modifier = Modifier.align(Alignment.BottomEnd),
                tint = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}
