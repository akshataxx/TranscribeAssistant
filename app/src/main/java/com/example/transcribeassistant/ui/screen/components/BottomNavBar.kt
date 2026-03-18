package com.example.transcribeassistant.ui.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.transcribeassistant.navigation.Screen

// Use SecondaryText, ScoopPurple, ScoopBlue, ScoopCyan from AnimatedBlobsBackground.kt (same package)

data class BottomNavItem(val label: String, val route: String, val icon: ImageVector, val size: Int = 24)

@Composable
fun BottomNavBar(
    currentRoute: String,
    activityBadgeCount: Int = 0,
    onTabSelected: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem("Home", Screen.Dashboard.route, Icons.Default.Home),
        BottomNavItem("Activity", Screen.Activity.route, Icons.Default.List),
        BottomNavItem("Add", "add", Icons.Default.Add, size = 28),
        BottomNavItem("Feed", Screen.Feed.route, Icons.Outlined.Article),
    )

    NavigationBar(
        containerColor = Color.Transparent,
        tonalElevation = 0.dp,
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            val isAddButton = item.route == "add"
            val showBadge = item.route == Screen.Activity.route && activityBadgeCount > 0

            NavigationBarItem(
                icon = {
                    if (isAddButton) {
                        // Special gradient circular add button
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(ScoopPurple, ScoopBlue, ScoopCyan)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                item.icon,
                                contentDescription = item.label,
                                modifier = Modifier.size(item.size.dp),
                                tint = Color.White
                            )
                        }
                    } else if (showBadge) {
                        BadgedBox(badge = {
                            Badge {
                                Text(
                                    text = if (activityBadgeCount > 9) "9+" else "$activityBadgeCount",
                                    fontSize = 9.sp
                                )
                            }
                        }) {
                            Icon(
                                item.icon,
                                contentDescription = item.label,
                                modifier = Modifier
                                    .size(item.size.dp)
                                    .graphicsLayer(alpha = 0.99f)
                                    .drawWithCache {
                                        onDrawWithContent {
                                            drawContent()
                                            drawRect(
                                                brush = Brush.linearGradient(
                                                    colors = listOf(ScoopPurple, ScoopBlue, ScoopCyan)
                                                ),
                                                blendMode = BlendMode.SrcAtop
                                            )
                                        }
                                    },
                                tint = Color.White
                            )
                        }
                    } else if (isSelected) {
                        // Gradient icon for selected state
                        Icon(
                            item.icon,
                            contentDescription = item.label,
                            modifier = Modifier
                                .size(item.size.dp)
                                .graphicsLayer(alpha = 0.99f)
                                .drawWithCache {
                                    onDrawWithContent {
                                        drawContent()
                                        drawRect(
                                            brush = Brush.linearGradient(
                                                colors = listOf(ScoopPurple, ScoopBlue, ScoopCyan)
                                            ),
                                            blendMode = BlendMode.SrcAtop
                                        )
                                    }
                                },
                            tint = Color.White
                        )
                    } else {
                        // Regular icon for unselected state
                        Icon(
                            item.icon,
                            contentDescription = item.label,
                            modifier = Modifier.size(item.size.dp),
                            tint = SecondaryText
                        )
                    }
                },
                selected = isSelected && !isAddButton,
                onClick = { onTabSelected(item.route) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Transparent,
                    unselectedIconColor = SecondaryText,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
