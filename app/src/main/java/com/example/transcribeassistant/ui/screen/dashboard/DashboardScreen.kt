package com.example.transcribeassistant.ui.screen.dashboard

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.transcribeassistant.R
import com.example.transcribeassistant.navigation.Screen
import com.example.transcribeassistant.ui.viewmodel.CategoryGroup
import com.example.transcribeassistant.ui.viewmodel.DashboardViewModel

// ============================================================================
// CHANGED: New light theme colors matching Scoop login design
// ============================================================================
private val LightBackground = Color(0xFFF5F7FA)
private val LightBackgroundEnd = Color(0xFFE8ECF1)
private val CardBackground = Color.White
private val PrimaryText = Color(0xFF1F2937)
private val SecondaryText = Color(0xFF6B7280)
private val PurpleGradientStart = Color(0xFF7C3AED)
private val BlueGradientMiddle = Color(0xFF2563EB)
private val CyanGradientEnd = Color(0xFF06B6D4)
private val TealCard = Color(0xFF14B8A6)
private val OrangeCard = Color(0xFFF97316)
private val PinkCard = Color(0xFFEC4899)
private val IndigoCard = Color(0xFF6366F1)

// Card colors for categories (matching reference image aesthetic)
val scoopCardColors = listOf(
    PurpleGradientStart,
    TealCard,
    BlueGradientMiddle,
    OrangeCard,
    PinkCard,
    IndigoCard,
    CyanGradientEnd,
    Color(0xFF8B5CF6)
)

@Composable
fun DashboardScreen(
    navController: NavHostController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val categoryGroups by viewModel.categoryGroups.collectAsState()
    val usageInfo by viewModel.usageInfo.collectAsState()
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

    // ============================================================================
    // Animated blob transitions
    // ============================================================================
    val infiniteTransition = rememberInfiniteTransition(label = "dashboardBlobs")

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

    // ============================================================================
    // Light gradient background with animated blob overlay
    // ============================================================================
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

        // Main content layer
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // ============================================================================
            // CHANGED: Header with Scoop logo instead of "Categories" text
            // ============================================================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Scoop Logo (static, not floating)
                Image(
                    painter = painterResource(id = R.drawable.scoop_logo),
                    contentDescription = "Scoop Logo",
                    modifier = Modifier.height(36.dp)
                )

                // Profile dropdown menu
                var menuOpen by remember { mutableStateOf(false) }
                Box {
                    // Profile icon with gradient border
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(ScoopPurple, ScoopBlue, ScoopCyan)
                                )
                            )
                            .padding(2.dp)
                            .clickable { menuOpen = true }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_profile),
                            contentDescription = "Profile",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }

                    DropdownMenu(
                        expanded = menuOpen,
                        onDismissRequest = { menuOpen = false },
                        modifier = Modifier.background(CardBackground)
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Subscription",
                                    color = PrimaryText,
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            onClick = {
                                menuOpen = false
                                navController.navigate(Screen.Subscription.route)
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.Star,
                                    contentDescription = null,
                                    tint = ScoopPurple,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Logout",
                                    color = Color(0xFFEF4444),
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            onClick = {
                                menuOpen = false
                                viewModel.logout()
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                    launchSingleTop = true
                                }
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.ExitToApp,
                                    contentDescription = null,
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ============================================================================
            // CHANGED: Updated usage tracking card design
            // ============================================================================
            usageInfo
                ?.takeIf { !it.isPremium } // Show free tier usage only for non-premium users
                ?.let { usage ->
                    UsageTrackingCard(
                        usageInfo = usage,
                        onUpgradeClick = { navController.navigate(Screen.Subscription.route) }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

            // ============================================================================
            // Category grid - no header needed, content is self-explanatory
            // ============================================================================
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                itemsIndexed(categoryGroups) { index, group ->
                    CategoryCard(
                        categoryGroup = group,
                        backgroundColor = scoopCardColors[index % scoopCardColors.size],
                        onClick = {
                            navigateToTranscriptsScreen(navController, group.categoryId)
                        },
                        onLongClick = {
                            renamingCategoryGroup = group
                            showRenameDialog = true
                        }
                    )
                }
            }
        }
    }
}

fun navigateToTranscriptsScreen(navController: NavHostController, categoryId: String) {
    navController.navigate(Screen.Transcripts.createRoute(categoryId))
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
        title = {
            Text(
                text = "Rename ${categoryGroup.categoryName}",
                color = PrimaryText
            )
        },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("New Alias") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PurpleGradientStart,
                    focusedLabelColor = PurpleGradientStart
                ),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(text) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = PurpleGradientStart
                )
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = SecondaryText)
            }
        },
        containerColor = CardBackground
    )
}

// ============================================================================
// Scoop gradient colors for usage card
// ============================================================================
private val ScoopPurple = Color(0xFF7165E0)
private val ScoopBlue = Color(0xFF85ACEC)
private val ScoopCyan = Color(0xFF7FD9EA)

// ============================================================================
// CHANGED: Redesigned usage tracking card with transparent bg and gradient blobs
// ============================================================================
@Composable
private fun UsageTrackingCard(
    usageInfo: com.example.transcribeassistant.domain.model.UsageInfo,
    onUpgradeClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "usageCardBlobs")

    // Small blob 1 animation
    val blob1X by infiniteTransition.animateFloat(
        initialValue = -20f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blob1X"
    )
    val blob1Scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blob1Scale"
    )

    // Small blob 2 animation
    val blob2X by infiniteTransition.animateFloat(
        initialValue = 40f,
        targetValue = 80f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blob2X"
    )
    val blob2Scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blob2Scale"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Brush.linearGradient(
                colors = listOf(ScoopPurple.copy(alpha = 0.3f), ScoopCyan.copy(alpha = 0.3f))
            )
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            // Small floating blob 1
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .offset(x = blob1X.dp, y = (-10).dp)
                    .graphicsLayer(
                        scaleX = blob1Scale,
                        scaleY = blob1Scale,
                        alpha = 0.3f
                    )
                    .align(Alignment.CenterStart)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.scoop_png),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Small floating blob 2
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .offset(x = blob2X.dp, y = 5.dp)
                    .graphicsLayer(
                        scaleX = blob2Scale,
                        scaleY = blob2Scale,
                        alpha = 0.25f
                    )
                    .align(Alignment.CenterEnd)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.scoop_png),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Content
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (usageInfo.isPremium) "Premium Active" else "Free Plan",
                        style = MaterialTheme.typography.titleMedium.copy(
                            brush = Brush.linearGradient(
                                colors = listOf(ScoopPurple, ScoopBlue, ScoopCyan)
                            )
                        ),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = usageInfo.usageMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = ScoopBlue
                    )
                }

                if (!usageInfo.isPremium && usageInfo.hasReachedFreeLimit) {
                    Button(
                        onClick = onUpgradeClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Brush.linearGradient(
                                colors = listOf(ScoopPurple, ScoopCyan)
                            )
                        )
                    ) {
                        Text(
                            "Upgrade",
                            fontWeight = FontWeight.Bold,
                            style = LocalTextStyle.current.copy(
                                brush = Brush.linearGradient(
                                    colors = listOf(ScoopPurple, ScoopBlue, ScoopCyan)
                                )
                            )
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DashboardScreenPreview() {
    val mockCategories = listOf(
        CategoryGroup("Trending", "1", "Trending", emptyList()),
        CategoryGroup("Music & Podcasts", "2", "Music & Podcasts", emptyList()),
        CategoryGroup("Fitness & Wellness", "3", "Fitness & Wellness", emptyList()),
        CategoryGroup("Tech & Gadgets", "4", "Tech & Gadgets", emptyList())
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Placeholder for Scoop logo
                Text(
                    text = "Scoop",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    style = LocalTextStyle.current.copy(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                PurpleGradientStart,
                                BlueGradientMiddle,
                                CyanGradientEnd
                            )
                        )
                    )
                )

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SecondaryText),
                    contentAlignment = Alignment.Center
                ) {
                    Text("P", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Usage card preview
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF59E0B)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "Free Plan",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "5 of 10 transcripts used",
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Categories",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryText,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Category grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                itemsIndexed(mockCategories) { index, group ->
                    CategoryCard(
                        categoryGroup = group,
                        backgroundColor = scoopCardColors[index % scoopCardColors.size],
                        onClick = { },
                        onLongClick = { }
                    )
                }
            }
        }
    }
}




