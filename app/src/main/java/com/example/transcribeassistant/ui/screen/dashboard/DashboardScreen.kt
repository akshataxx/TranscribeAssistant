package com.example.transcribeassistant.ui.screen.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.transcribeassistant.R
import com.example.transcribeassistant.ui.viewmodel.DashboardViewModel

data class CategoryTile(
    val id: Int,
    val label: String,
    val emoji: String,
    val backgroundColor: Color
)

@Composable
fun DashboardScreen(viewModel: DashboardViewModel = hiltViewModel()) {
    var categories by remember {
        mutableStateOf(
            listOf(
                CategoryTile(1, "recipes", "", Color(0xFF3A3958)),
                CategoryTile(2, "lifestyle", "", Color(0xFFD9725B)),
                CategoryTile(3, "meal prep", "", Color(0xFF3A3958)),
                CategoryTile(4, "makeup", "", Color(0xFFD9725B)),
                CategoryTile(5, "DIY", "", Color(0xFFD9725B)),
                CategoryTile(6, "home", "", Color(0xFF3A3958)),
                CategoryTile(7, "fixing", "", Color(0xFF3A3958)),
                CategoryTile(8, "interior", "", Color(0xFFD9725B))
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C2B3E))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Categories",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Image(
                painter = painterResource(id = R.drawable.ic_profile),
                contentDescription = "Profile",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(32.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(categories) { category ->
                CategoryCard(
                    category = category,
                    onEmojiChange = { newEmoji ->
                        categories = categories.map {
                            if (it.id == category.id) {
                                it.copy(emoji = newEmoji)
                            } else {
                                it
                            }
                        }
                    }
                )
            }
        }
    }
}

