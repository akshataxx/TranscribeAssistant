package com.example.transcribeassistant.ui.screen.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.transcribeassistant.navigation.Screen
import com.example.transcribeassistant.ui.theme.pastelCyan

data class BottomNavItem(val label: String, val route: String, val icon: ImageVector)

@Composable
fun BottomNavBar(
    currentRoute: String,
    onTabSelected: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem("Dashboard", Screen.Dashboard.route, Icons.Default.Home),
        BottomNavItem("Feed", Screen.Feed.route, Icons.AutoMirrored.Filled.List)
    )

    Box(modifier = Modifier.height(65.dp)) {
        NavigationBar(
            containerColor = pastelCyan,
            tonalElevation = 0.dp,
            modifier = Modifier.fillMaxSize(),
        ) {
            items.forEach { item ->
                NavigationBarItem(
                    icon = { Icon(item.icon, contentDescription = item.label) },
                    selected = currentRoute == item.route,
                    onClick = { onTabSelected(item.route) }
                )
            }
        }
    }
}

