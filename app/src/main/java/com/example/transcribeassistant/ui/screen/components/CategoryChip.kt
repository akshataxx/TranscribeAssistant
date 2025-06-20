package com.example.transcribeassistant.ui.screen.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.transcribeassistant.ui.theme.*

/**
 * TODO: Add emojis next to each categoryName
 */
@Composable
fun CategoryChip(label: String) {
    Surface(
        color = pastelCyan,
        shape = RoundedCornerShape(10.dp)
    ) {
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

