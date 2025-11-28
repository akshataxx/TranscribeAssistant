package com.example.transcribeassistant.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.transcribeassistant.navigation.TranscribeNavGraph
import com.example.transcribeassistant.ui.viewmodel.AuthViewModel

@Composable
fun TranscribeAssistantApp() {
    val authViewModel: AuthViewModel = hiltViewModel()
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val navController = rememberNavController()
        TranscribeNavGraph(navController = navController, authViewModel = authViewModel)
    }
}
