package com.example.transcribeassistant.ui.screen.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.transcribeassistant.ui.viewmodel.LoginUiState
import com.example.transcribeassistant.ui.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen (
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel(),
    invokeGoogleSignIn: suspend() -> String
){
    val uiState by viewModel.uiState.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is LoginUiState.Idle -> {
                    val scope = rememberCoroutineScope()
                    Button(onClick = {
                        scope.launch {
                            try {
                                val credential = invokeGoogleSignIn()
                                viewModel.loginWithGoogle(credential)
                            } catch (e: Exception) {
                                // optionally propagate error
                            }
                        }
                    }) {
                        Text("Login with Google")
                    }
                }
                is LoginUiState.Loading -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Logging in...", style = MaterialTheme.typography.bodyMedium)
                    }
                }
                is LoginUiState.Success -> {
                    Text("Logged in! Token ends with: ${(uiState as LoginUiState.Success).accessTokenPreview}")
                    // Navigate after a short delay or immediately
                    LaunchedEffect(Unit) {
                        navController.navigate("dashboard") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
                is LoginUiState.Error -> {
                    AlertDialog(
                        onDismissRequest = {},
                        title = { Text("Login failed") },
                        text = { Text((uiState as LoginUiState.Error).message) },
                        confirmButton = {
                            Button(onClick = { /* reset to idle */ }) {
                                Text("Retry")
                            }
                        }
                    )
                }
            }
        }
    }


}