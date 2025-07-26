package com.example.transcribeassistant.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.example.transcribeassistant.ui.screen.TranscribeAssistantApp
import com.example.transcribeassistant.ui.theme.TranscribeAssistantTheme
import dagger.hilt.android.AndroidEntryPoint
import com.example.transcribeassistant.ui.screen.login.GoogleLoginScreen
import com.example.transcribeassistant.ui.viewmodel.LoginViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.transcribeassistant.R
import com.example.transcribeassistant.data.session.JwtManager
import com.example.transcribeassistant.ui.viewmodel.LoginState

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TranscribeAssistantTheme {
                val viewModel: LoginViewModel = hiltViewModel()
                val loginState = viewModel.loginState.collectAsState().value

                when (loginState) {
                    is LoginState.Idle, is LoginState.Loading, is LoginState.Error -> {
                        GoogleLoginScreen(
                            viewModel = viewModel,
                            webClientId = getString(R.string.default_web_client_id)
                        )
                    }
                    is LoginState.Success -> {
                        TranscribeAssistantApp()
                    }
                }
            }
        }
    }
}

