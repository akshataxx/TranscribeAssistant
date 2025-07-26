package com.example.transcribeassistant.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.transcribeassistant.ui.screen.TranscribeAssistantApp
import com.example.transcribeassistant.ui.theme.TranscribeAssistantTheme
import dagger.hilt.android.AndroidEntryPoint
import com.example.transcribeassistant.ui.screen.login.GoogleLoginScreen
import com.example.transcribeassistant.ui.viewmodel.LoginViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.transcribeassistant.R

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TranscribeAssistantTheme {
//                Surface(modifier = Modifier.fillMaxSize()) {
//                    TranscribeAssistantApp() // 🔄 Your app root
//                }
                val viewModel: LoginViewModel = hiltViewModel()
                GoogleLoginScreen(
                    viewModel = viewModel,
                    webClientId = getString(R.string.default_web_client_id)
                )
            }
        }
    }
}
