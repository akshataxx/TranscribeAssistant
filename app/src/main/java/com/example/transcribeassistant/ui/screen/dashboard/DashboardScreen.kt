package com.example.transcribeassistant.ui.screen.dashboard

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import android.app.Application
import com.example.transcribeassistant.R
import com.example.transcribeassistant.di.JwtManagerEntryPoint
import com.example.transcribeassistant.navigation.Screen
import com.example.transcribeassistant.ui.viewmodel.CategoryGroup
import com.example.transcribeassistant.ui.viewmodel.DashboardViewModel
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.runBlocking

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavHostController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val categoryGroups by viewModel.categoryGroups.collectAsState()
    val usageInfo by viewModel.usageInfo.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showRenameDialog by remember { mutableStateOf(false) }
    var renamingCategoryGroup by remember { mutableStateOf<CategoryGroup?>(null) }
    var showProfileSheet by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Profile data
    val context = LocalContext.current
    val jwtManager = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext as Application,
            JwtManagerEntryPoint::class.java
        ).jwtManager()
    }
    val profileName = jwtManager.getProfileName()
    val profileEmail = jwtManager.getProfileEmail()
    val profileInitials = remember(profileName) {
        val parts = (profileName ?: "").trim().split("\\s+".toRegex())
        when {
            parts.size >= 2 -> "${parts[0].first().uppercaseChar()}${parts[1].first().uppercaseChar()}"
            parts.isNotEmpty() && parts[0].isNotEmpty() -> "${parts[0].first().uppercaseChar()}"
            else -> "?"
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchTranscripts()
        viewModel.fetchUsageInfo()
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

    // Logout confirmation dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout", color = PrimaryText) },
            text = { Text("Are you sure you want to logout?", color = SecondaryText) },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    runBlocking { jwtManager.clearTokens() }
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
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

    // Profile Bottom Sheet (matching iOS .sheet with .presentationDetents([.medium]))
    if (showProfileSheet) {
        ModalBottomSheet(
            onDismissRequest = { showProfileSheet = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
                        text = profileInitials,
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (!profileName.isNullOrBlank()) {
                    Text(
                        text = profileName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText
                    )
                }

                if (!profileEmail.isNullOrBlank() && !profileEmail.contains("privaterelay")) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = profileEmail,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SecondaryText
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Rows
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column {
                        // Subscription
                        ProfileSheetRow(
                            icon = Icons.Default.Star,
                            iconTint = ScoopPurple,
                            title = "Subscription",
                            subtitle = if (usageInfo?.isPremium == true) "Premium" else "Free Plan",
                            onClick = {
                                showProfileSheet = false
                                navController.navigate(Screen.Subscription.route)
                            }
                        )
                        HorizontalDivider(
                            color = Color(0xFFF3F4F6),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        // Settings
                        ProfileSheetRow(
                            icon = Icons.Default.Settings,
                            iconTint = SecondaryText,
                            title = "Settings",
                            onClick = {
                                showProfileSheet = false
                                showSettingsSheet = true
                            }
                        )
                        HorizontalDivider(
                            color = Color(0xFFF3F4F6),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        // Logout
                        ProfileSheetRow(
                            icon = Icons.AutoMirrored.Filled.ExitToApp,
                            iconTint = Color(0xFFEF4444),
                            title = "Logout",
                            titleColor = Color(0xFFEF4444),
                            onClick = {
                                showProfileSheet = false
                                showLogoutDialog = true
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Settings Bottom Sheet
    if (showSettingsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSettingsSheet = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
                Spacer(modifier = Modifier.height(24.dp))
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = SecondaryText,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Settings coming soon",
                    style = MaterialTheme.typography.bodyLarge,
                    color = SecondaryText
                )
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
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

                // Profile icon — opens bottom sheet (matching iOS)
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
                        .clickable { showProfileSheet = true }
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
            // Category grid or empty state
            // ============================================================================
            if (categoryGroups.isEmpty() && !isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    GetStartedCard(
                        onClick = { navController.navigate(Screen.AddLink.route) }
                    )
                }
            } else {
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
// Redesigned usage tracking card matching iOS design
// ============================================================================
@Composable
private fun UsageTrackingCard(
    usageInfo: com.example.transcribeassistant.domain.model.UsageInfo,
    onUpgradeClick: () -> Unit
) {
    val atLimit = usageInfo.hasReachedFreeLimit
    val normalGradient = listOf(ScoopPurple, ScoopCyan)
    val limitGradient = listOf(Color(0xFFF59E0B), Color(0xFFEF4444))
    val accentColors = if (atLimit) limitGradient else normalGradient

    val animatedProgress by animateFloatAsState(
        targetValue = usageInfo.usageProgress.toFloat().coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "progressAnim"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onUpgradeClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(
            1.5.dp,
            Brush.linearGradient(colors = accentColors)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Row 1: Status & Count
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (atLimit) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = ScoopPurple,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (atLimit) "Free limit reached" else "Free plan",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryText
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${usageInfo.usedTranscriptions}/${usageInfo.totalFreeTranscriptions} used",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = SecondaryText
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = null,
                        tint = SecondaryText,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Row 2: Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFFE5E7EB))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            Brush.horizontalGradient(colors = accentColors)
                        )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Row 3: Message & CTA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (atLimit) "Upgrade to Premium for unlimited" else "${usageInfo.remainingFreeTranscriptions} transcription${if (usageInfo.remainingFreeTranscriptions != 1) "s" else ""} remaining",
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = if (atLimit) "Upgrade" else "View plans",
                    style = MaterialTheme.typography.labelMedium.copy(
                        brush = Brush.linearGradient(colors = accentColors)
                    ),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ProfileSheetRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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

@Composable
private fun GetStartedCard(onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Gradient play icon
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = null,
            modifier = Modifier
                .size(56.dp)
                .graphicsLayer(alpha = 0.99f)
                .drawWithCache {
                    onDrawWithContent {
                        drawContent()
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = listOf(ScoopPurple, ScoopCyan)
                            ),
                            blendMode = BlendMode.SrcAtop
                        )
                    }
                },
            tint = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Share a video or paste a link to get started",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            color = SecondaryText,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            HintPill(icon = Icons.Default.Share, label = "Share")
            HintPill(icon = Icons.Default.Link, label = "Paste link")
        }
    }
}

@Composable
private fun HintPill(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Row(
        modifier = Modifier
            .background(ScoopPurple.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = ScoopPurple,
            modifier = Modifier.size(14.dp)
        )
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = ScoopPurple)
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




