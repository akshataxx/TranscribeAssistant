package com.example.transcribeassistant.ui.screen.login

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.transcribeassistant.ui.viewmodel.LoginUiState
import com.example.transcribeassistant.ui.viewmodel.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.launch

@Composable
fun LoginScreen (
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel(),
    webClientId: String
){
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Build GoogleSignInClient configured to request ID token
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    // Launcher for the sign-in intent
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken
            if (!idToken.isNullOrBlank()) {
                Log.d("Login", "Got Google ID token (last8): ${idToken.takeLast(8)}")
                viewModel.loginWithGoogle(idToken)
            } else {
                Log.e("Login", "ID token was null or empty")
            }
        } catch (e: ApiException) {
            Log.e("Login", "Google sign-in failed", e)
            viewModel.setError("Google sign-in failed: ${e.localizedMessage}")
        }
    }
    Surface(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is LoginUiState.Idle -> {
                    Button(onClick = {
                        // Kick off Google sign-in
                        launcher.launch(googleSignInClient.signInIntent)
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
                    LaunchedEffect(Unit) {
                        navController.navigate("dashboard") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                    Text("Logged in! Token ends with: ${(uiState as LoginUiState.Success).accessTokenPreview}")
                }
                is LoginUiState.Error -> {
                    Text("Error: ${(uiState as LoginUiState.Error).message}")
                }
            }
        }
    }
}