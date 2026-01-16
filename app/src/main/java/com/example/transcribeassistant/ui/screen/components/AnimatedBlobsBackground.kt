package com.example.transcribeassistant.ui.screen.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.transcribeassistant.R

// Scoop theme colors
val ScoopPurple = Color(0xFF7165E0)
val ScoopBlue = Color(0xFF85ACEC)
val ScoopCyan = Color(0xFF7FD9EA)
val LightBackground = Color(0xFFF5F7FA)
val LightBackgroundEnd = Color(0xFFE8ECF1)
val PrimaryText = Color(0xFF1F2937)
val SecondaryText = Color(0xFF6B7280)

@Composable
fun AnimatedBlobsBackground(
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "blobs")

    // First blob animations (top-right area)
    val blob1OffsetX by infiniteTransition.animateFloat(
        initialValue = 80f,
        targetValue = 120f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blob1OffsetX"
    )
    val blob1OffsetY by infiniteTransition.animateFloat(
        initialValue = -60f,
        targetValue = -20f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blob1OffsetY"
    )
    val blob1Scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blob1Scale"
    )
    val blob1Rotation by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blob1Rotation"
    )

    // Second blob animations (bottom-left area)
    val blob2OffsetX by infiniteTransition.animateFloat(
        initialValue = -100f,
        targetValue = -60f,
        animationSpec = infiniteRepeatable(
            animation = tween(4500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blob2OffsetX"
    )
    val blob2OffsetY by infiniteTransition.animateFloat(
        initialValue = 200f,
        targetValue = 260f,
        animationSpec = infiniteRepeatable(
            animation = tween(3800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blob2OffsetY"
    )
    val blob2Scale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blob2Scale"
    )
    val blob2Rotation by infiniteTransition.animateFloat(
        initialValue = 5f,
        targetValue = -15f,
        animationSpec = infiniteRepeatable(
            animation = tween(4800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blob2Rotation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        LightBackground,
                        LightBackgroundEnd
                    )
                )
            )
    ) {
        // Animated blob 1 (top-right)
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = blob1OffsetX.dp, y = blob1OffsetY.dp)
                .graphicsLayer(
                    scaleX = blob1Scale,
                    scaleY = blob1Scale,
                    rotationZ = blob1Rotation,
                    alpha = 0.4f
                )
                .align(Alignment.TopEnd)
        ) {
            Image(
                painter = painterResource(id = R.drawable.scoop_png),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // Animated blob 2 (bottom-left)
        Box(
            modifier = Modifier
                .size(280.dp)
                .offset(x = blob2OffsetX.dp, y = blob2OffsetY.dp)
                .graphicsLayer(
                    scaleX = blob2Scale,
                    scaleY = blob2Scale,
                    rotationZ = blob2Rotation,
                    alpha = 0.35f
                )
                .align(Alignment.BottomStart)
        ) {
            Image(
                painter = painterResource(id = R.drawable.scoop_png),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // Content layer
        content()
    }
}
