package com.example.transcribeassistant.ui.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private val CoachScoopPurple = Color(0xFF7C3AED)
private val CoachScoopBlue = Color(0xFF2563EB)
private val CoachScoopCyan = Color(0xFF06B6D4)
private val CoachSecondaryText = Color(0xFF6B7280)

enum class CoachmarkPosition { Top, Center, Bottom }

data class CoachmarkStep(
    val title: String,
    val message: String,
    val tooltipPosition: CoachmarkPosition
)

/**
 * Full-screen coachmark overlay shown to first-time users.
 * Steps through [steps] one at a time. Calls [onFinished] when the user
 * completes all steps or taps "Skip".
 */
@Composable
fun CoachmarkOverlay(
    steps: List<CoachmarkStep>,
    onFinished: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(0) }
    val step = steps[currentStep]
    val isLast = currentStep == steps.lastIndex

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { /* consume taps so content beneath is not interactive */ }
    ) {
        val cardAlignment = when (step.tooltipPosition) {
            CoachmarkPosition.Top -> Alignment.TopCenter
            CoachmarkPosition.Center -> Alignment.Center
            CoachmarkPosition.Bottom -> Alignment.BottomCenter
        }
        val cardPaddingTop = if (step.tooltipPosition == CoachmarkPosition.Top) 96.dp else 0.dp
        val cardPaddingBottom = if (step.tooltipPosition == CoachmarkPosition.Bottom) 96.dp else 0.dp

        Card(
            modifier = Modifier
                .align(cardAlignment)
                .padding(horizontal = 24.dp, vertical = 0.dp)
                .padding(top = cardPaddingTop, bottom = cardPaddingBottom),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = step.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        brush = Brush.linearGradient(
                            colors = listOf(CoachScoopPurple, CoachScoopBlue, CoachScoopCyan)
                        )
                    ),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = step.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = CoachSecondaryText,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onFinished) {
                        Text("Skip", color = CoachSecondaryText)
                    }

                    Button(
                        onClick = { if (isLast) onFinished() else currentStep++ },
                        colors = ButtonDefaults.buttonColors(containerColor = CoachScoopPurple),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (isLast) "Got it!" else "Next →",
                            color = Color.White
                        )
                    }
                }

                if (steps.size > 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        steps.indices.forEach { i ->
                            Box(
                                modifier = Modifier
                                    .size(if (i == currentStep) 8.dp else 6.dp)
                                    .background(
                                        color = if (i == currentStep) CoachScoopPurple else Color(0xFFD1D5DB),
                                        shape = CircleShape
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}
