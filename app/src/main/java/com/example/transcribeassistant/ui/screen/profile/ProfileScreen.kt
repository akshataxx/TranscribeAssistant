package com.example.transcribeassistant.ui.screen.profile

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.transcribeassistant.di.JwtManagerEntryPoint
import com.example.transcribeassistant.ui.screen.components.AnimatedBlobsBackground
import com.example.transcribeassistant.ui.screen.components.PrimaryText
import com.example.transcribeassistant.ui.screen.components.SecondaryText
import com.example.transcribeassistant.ui.screen.components.ScoopBlue
import com.example.transcribeassistant.ui.screen.components.ScoopCyan
import com.example.transcribeassistant.ui.screen.components.ScoopPurple
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.runBlocking

@Composable
fun ProfileScreen(
    onSettingsClick: () -> Unit,
    onSubscriptionClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val context = LocalContext.current
    val jwtManager = EntryPointAccessors.fromApplication(
        context.applicationContext as Application,
        JwtManagerEntryPoint::class.java
    ).jwtManager()

    val name = jwtManager.getProfileName()
    val email = jwtManager.getProfileEmail()
    val initials = remember(name) {
        val parts = (name ?: "").trim().split("\\s+".toRegex())
        when {
            parts.size >= 2 -> "${parts[0].first().uppercaseChar()}${parts[1].first().uppercaseChar()}"
            parts.isNotEmpty() && parts[0].isNotEmpty() -> "${parts[0].first().uppercaseChar()}"
            else -> "?"
        }
    }

    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout", color = PrimaryText) },
            text = { Text("Are you sure you want to logout?", color = SecondaryText) },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    runBlocking { jwtManager.clearTokens() }
                    onLogoutClick()
                }) {
                    Text("Logout", color = Color(0xFFEF4444))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = SecondaryText)
                }
            },
            containerColor = Color.White
        )
    }

    AnimatedBlobsBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Avatar Circle
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(ScoopPurple, ScoopCyan)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // User Name
            if (!name.isNullOrBlank()) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
            }

            // User Email (hide if private relay)
            if (!email.isNullOrBlank() && !email.contains("privaterelay")) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryText
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action Rows Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    // Subscription Row
                    ProfileActionRow(
                        icon = Icons.Default.Star,
                        iconTint = ScoopPurple,
                        title = "Subscription",
                        subtitle = "Free Plan",
                        onClick = onSubscriptionClick
                    )

                    Divider(
                        color = Color(0xFFF3F4F6),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Settings Row
                    ProfileActionRow(
                        icon = Icons.Default.Settings,
                        iconTint = SecondaryText,
                        title = "Settings",
                        onClick = onSettingsClick
                    )

                    Divider(
                        color = Color(0xFFF3F4F6),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Logout Row
                    ProfileActionRow(
                        icon = Icons.AutoMirrored.Filled.ExitToApp,
                        iconTint = Color(0xFFEF4444),
                        title = "Logout",
                        titleColor = Color(0xFFEF4444),
                        onClick = { showLogoutDialog = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ProfileActionRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String? = null,
    titleColor: Color = PrimaryText,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = titleColor
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText
                )
            }
        }

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = SecondaryText.copy(alpha = 0.5f),
            modifier = Modifier.size(20.dp)
        )
    }
}
