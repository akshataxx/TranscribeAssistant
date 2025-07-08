package com.example.transcribeassistant.ui.screen.share

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.transcribeassistant.ui.viewmodel.ShareViewModel

@Composable
fun ShareScreen(
    sharedLink: String,
    viewModel: ShareViewModel = hiltViewModel(),
    onDone: () -> Unit
) {
    var isSaving by remember { mutableStateOf(false) }

    LaunchedEffect(sharedLink) {
        isSaving = true
        // Fetch metadata, transcribe, categorize, save
        processSharedLink(sharedLink, viewModel)
        isSaving = false
        onDone() // Notify that saving is done
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isSaving) {
            CircularProgressIndicator()
        } else {
            Text("Saved!")
        }
    }
}

suspend fun processSharedLink(link: String, viewModel: ShareViewModel) {
    println(link)
    viewModel.submitNewVideo(link)
}
