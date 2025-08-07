package com.example.transcribeassistant.ui.screen.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.transcribeassistant.R
import com.example.transcribeassistant.ui.viewmodel.CategoryGroup
import com.example.transcribeassistant.ui.viewmodel.DashboardViewModel
import androidx.navigation.NavHostController
import com.example.transcribeassistant.navigation.Screen

val cardColors = listOf(
    Color(0xFF3A3958),
    Color(0xFFD9725B)
)

@Composable
fun DashboardScreen(
    navController: NavHostController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val categoryGroups by viewModel.categoryGroups.collectAsState()
    var showRenameDialog by remember { mutableStateOf(false) }
    var renamingCategoryGroup by remember { mutableStateOf<CategoryGroup?>(null) }

    LaunchedEffect(Unit) {
        viewModel.fetchTranscripts()
    }

    if (showRenameDialog && renamingCategoryGroup != null) {
        RenameCategoryDialog(
            categoryGroup = renamingCategoryGroup!!,
            onDismiss = { showRenameDialog = false },
            onConfirm = { newAlias ->
                viewModel.updateAlias(renamingCategoryGroup!!.categoryId, newAlias)
                showRenameDialog = false
            }
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
            itemsIndexed(categoryGroups) { index, group ->
                CategoryCard(
                    categoryGroup = group,
                    backgroundColor = cardColors[index % cardColors.size],
                    onClick = {
                        // Navigate to the list of transcripts
                        navigateToTranscriptsScreen(navController, group.categoryId)
                    },
                    onLongClick = {
                        // Trigger rename dialog
                        renamingCategoryGroup = group
                        showRenameDialog = true
                    }
                )
            }
        }
    }
}

fun navigateToTranscriptsScreen(navController: NavHostController, categoryId: String) {
    navController.navigate(Screen.Transcripts.createRoute(categoryId))
}

@Composable
fun RenameDialog(
    categoryGroup: CategoryGroup,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf(categoryGroup.displayName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Rename ${categoryGroup.categoryName}") },
        text = {
            TextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("New Alias") }
            )
        },
        confirmButton = {
            Button(onClick = { onConfirm(text) }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun RenameCategoryDialog(
    categoryGroup: CategoryGroup,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf(categoryGroup.displayName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Rename ${categoryGroup.categoryName}") },
        text = {
            TextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("New Alias") }
            )
        },
        confirmButton = {
            Button(onClick = { onConfirm(text) }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    val cardColors = listOf(
        Color(0xFF3A3958),
        Color(0xFFD9725B)
    )
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
        }
    }
}