package com.example.transcribeassistant.ui.screen.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun ProfileScreen(
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = onSettingsClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF5856D6),
                contentColor = Color.White
            )
        ) {
            Text("Settings", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = onLogoutClick
        ) {
            Text("Logout", style = MaterialTheme.typography.titleMedium)
        }
    }
}