package com.example.transcribeassistant.ui.screen.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.transcribeassistant.ui.viewmodel.DashboardViewModel

data class CategoryTile(
    val label: String,
    val backgroundColor: Color
)

@Composable
fun DashboardScreen(viewModel: DashboardViewModel = hiltViewModel()) {
    val categories = listOf(
        CategoryTile("Recipes", Color(0xFFCFF4F6)),
        CategoryTile("Lifestyle", Color(0xFFFFD6D6)),
        CategoryTile("Gossip", Color(0xFFCFF4F6)),
        CategoryTile("Makeup", Color(0xFFFFD6D6)),
        CategoryTile("DIY", Color(0xFFFFD6D6)),
        CategoryTile("Tech", Color(0xFFCFF4F6)),
        CategoryTile("Career", Color(0xFFCFF4F6)),
        CategoryTile("Psychology", Color(0xFFFFD6D6))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFFFDEE9), Color(0xFFB5FFFC))))
            .padding(16.dp)
    ) {
        Text("Dashboard", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(categories) { category ->
                CategoryCard(category)
            }
        }
    }
}

