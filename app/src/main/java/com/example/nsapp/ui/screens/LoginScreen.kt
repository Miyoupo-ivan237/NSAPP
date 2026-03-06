package com.example.nsapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nsapp.ui.NotificationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, viewModel: NotificationViewModel) {
    // Sync local state with current registered info from ViewModel
    var email by remember { mutableStateOf(viewModel.userEmail) }
    var password by remember { mutableStateOf(viewModel.userPassword) }
    
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    fun validateEmail(email: String): Boolean {
        // More flexible check: allows exp.@gmail.com
        return email.contains("@") && email.contains(".")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome Back",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Log in to your account",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { 
                    email = it
                    emailError = null
                },
                label = { Text("Email Address") },
                isError = emailError != null,
                supportingText = { emailError?.let { Text(it) } },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { 
                    password = it
                    passwordError = null
                },
                label = { Text("Password") },
                isError = passwordError != null,
                supportingText = { passwordError?.let { Text(it) } },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { 
                    val isEmailValid = validateEmail(email)
                    val isPasswordEmpty = password.isEmpty()

                    if (!isEmailValid) {
                        emailError = "Please enter a valid email (e.g., exp.@gmail.com)"
                    } else if (isPasswordEmpty) {
                        passwordError = "Password cannot be empty"
                    } else {
                        // Check against the stored user data in ViewModel
                        if (email.trim().lowercase() == viewModel.userEmail.trim().lowercase() && 
                            password == viewModel.userPassword) {
                            viewModel.isLoggedIn = true
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            emailError = "Incorrect email or password"
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Login", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            if (viewModel.userEmail.isEmpty()) {
                TextButton(onClick = { navController.navigate("signup") }) {
                    Text("Don't have an account? Sign Up")
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Current Registered: ${viewModel.userEmail}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
