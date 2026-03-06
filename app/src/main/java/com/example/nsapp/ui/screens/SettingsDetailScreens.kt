package com.example.nsapp.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenericSettingsDetailScreen(title: String, navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
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
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Detailed information about $title will appear here.",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "This section guides the user on how to manage their $title preferences and settings within the NotifySync application.",
                fontSize = 14.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSupportScreen(navController: NavController) {
    val context = LocalContext.current
    val supportNumber = "698415093"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help & Support") },
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
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Frequently Asked Questions",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            HelpItem("How to create a notification?", "Go to the 'Create New' card on the dashboard and fill in the details.")
            HelpItem("How to edit a schedule?", "Navigate to 'Schedule Manager' to view and manage your upcoming notifications.")
            HelpItem("Is my data secure?", "Yes, we prioritize your privacy. Check 'Security & Privacy' for more details.")
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Still need help?",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Our support team is available to assist you. Click the button below to contact us.",
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Button(
                onClick = { 
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$supportNumber"))
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Default.Call, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Contact Support ($supportNumber)", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun HelpItem(question: String, answer: String) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(text = question, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Text(text = answer, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
