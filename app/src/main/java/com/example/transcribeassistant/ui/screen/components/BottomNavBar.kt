package com.example.transcribeassistant.ui.screen.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.transcribeassistant.navigation.Screen

// Colors matching the new Scoop light theme
private val LightBackground = Color(0xFFF5F7FA)
private val PrimaryText = Color(0xFF1F2937)
private val SecondaryText = Color(0xFF9CA3AF)
private val PurpleAccent = Color(0xFF7C3AED)

data class BottomNavItem(val label: String, val route: String, val icon: ImageVector, val size: Int = 24)

@Composable
fun BottomNavBar(
    currentRoute: String,
    onTabSelected: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem("Dashboard", Screen.Dashboard.route, Icons.Default.Home),
        BottomNavItem("Notifications", Screen.Notifications.route, Icons.Default.Notifications),
        BottomNavItem("Add", "add", Icons.Default.Add, size = 48),
        BottomNavItem("Feed", Screen.Feed.route, Icons.Outlined.Article),
    )

    NavigationBar(
        containerColor = Color.Transparent,
        tonalElevation = 0.dp,
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label, modifier = Modifier.size(item.size.dp)) },
                selected = currentRoute == item.route,
                onClick = { onTabSelected(item.route) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PurpleAccent,
                    unselectedIconColor = SecondaryText,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

