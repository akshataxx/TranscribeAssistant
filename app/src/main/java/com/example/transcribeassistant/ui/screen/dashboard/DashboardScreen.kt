package com.example.transcribeassistant.ui.screen.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
        CategoryTile("recipes", Color(0xFFCFF4F6)),
        CategoryTile("lifestyle", Color(0xFFFFD6D6)),
        CategoryTile("meal prep", Color(0xFFCFF4F6)),
        CategoryTile("makeup", Color(0xFFFFD6D6)),
        CategoryTile("DIY", Color(0xFFFFD6D6)),
        CategoryTile("home", Color(0xFFCFF4F6)),
        CategoryTile("DIY", Color(0xFFCFF4F6)),
        CategoryTile("home", Color(0xFFFFD6D6))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFFFDEE9), Color(0xFFB5FFFC))))
            .padding(horizontal = 16.dp)
    ) {
        TopBar()
        Spacer(modifier = Modifier.height(16.dp))

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

@Composable
fun TopBar() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Categories",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White)
        ) {
            // Placeholder for profile image
        }
    }
}

@Composable
fun CategoryCard(category: CategoryTile) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(category.backgroundColor)
            .clickable { /* onClick */ }
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = category.label, color = Color.Black, fontWeight = FontWeight.Medium)
    }
}
