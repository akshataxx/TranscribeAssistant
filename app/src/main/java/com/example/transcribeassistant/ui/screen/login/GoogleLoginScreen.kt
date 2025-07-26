package com.example.transcribeassistant.ui.screen.login

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.transcribeassistant.ui.viewmodel.LoginState
import com.example.transcribeassistant.ui.viewmodel.LoginViewModel
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException

@Composable
fun GoogleLoginScreen(
    viewModel: LoginViewModel,
    webClientId: String
) {
    val context = LocalContext.current
    val loginState by viewModel.loginState.collectAsState()

    val googleSignInClient = remember {
        GoogleSignIn.getClient(
            context,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(webClientId)
                .requestEmail()
                .build()
        )
    }

    val launcher = rememberLauncherForActivityResult(StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (idToken != null) viewModel.authenticate(idToken)
        } catch (e: Exception) {
            viewModel.authenticate("")
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = { launcher.launch(googleSignInClient.signInIntent) }) {
            Text("Sign in with Google")
        }

        Spacer(modifier = Modifier.height(20.dp))

        when (val state = loginState) {
            is LoginState.Loading -> CircularProgressIndicator()
            is LoginState.Success -> Text("✅ SUCCESS: ${state.token}")
            is LoginState.Error -> Text("❌ ERROR: ${state.message}")
            else -> {}
        }
    }
}
