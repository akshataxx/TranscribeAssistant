package com.example.transcribeassistant.ui.screen.subscription

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.transcribeassistant.ui.screen.components.AnimatedBlobsBackground
import com.example.transcribeassistant.ui.screen.components.PrimaryText
import com.example.transcribeassistant.ui.screen.components.SecondaryText
import com.example.transcribeassistant.ui.screen.components.ScoopBlue
import com.example.transcribeassistant.ui.screen.components.ScoopCyan
import com.example.transcribeassistant.ui.screen.components.ScoopPurple
import com.example.transcribeassistant.ui.viewmodel.SubscriptionViewModel
import com.example.transcribeassistant.ui.viewmodel.SubscriptionUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(
    onNavigateBack: () -> Unit,
    viewModel: SubscriptionViewModel = hiltViewModel()
) {
    val uiState: SubscriptionUiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity

    LaunchedEffect(Unit) {
        viewModel.loadUsageInfo()
    }

    AnimatedBlobsBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Subscription",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                brush = Brush.linearGradient(
                                    colors = listOf(ScoopPurple, ScoopBlue, ScoopCyan)
                                )
                            ),
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = PrimaryText
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (val state = uiState) {
                    is SubscriptionUiState.Loading -> {
                        CircularProgressIndicator(color = ScoopPurple)
                    }

                    is SubscriptionUiState.Success -> {
                        SubscriptionContent(
                            usageInfo = state.usageInfo,
                            onUpgradeClick = {
                                activity?.let { viewModel.startPremiumUpgrade(it) }
                            },
                            onCancelClick = {
                                viewModel.cancelSubscription()
                            },
                            onManageClick = {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/store/account/subscriptions")
                                )
                                context.startActivity(intent)
                            }
                        )
                    }

                    is SubscriptionUiState.Error -> {
                        ErrorContent(
                            message = state.message,
                            onRetry = { viewModel.loadUsageInfo() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SubscriptionContent(
    usageInfo: com.example.transcribeassistant.domain.model.UsageInfo,
    onUpgradeClick: () -> Unit,
    onCancelClick: () -> Unit,
    onManageClick: () -> Unit
) {
    // Header Section with gradient text
    Text(
        text = "Premium",
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.headlineLarge.copy(
            brush = Brush.linearGradient(
                colors = listOf(ScoopPurple, ScoopBlue, ScoopCyan)
            )
        )
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "Unlock unlimited transcriptions",
        style = MaterialTheme.typography.bodyLarge,
        color = SecondaryText
    )

    Spacer(modifier = Modifier.height(24.dp))

    if (!usageInfo.isPremium) {
        // Benefits Card for free users
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Premium Benefits",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
                Spacer(modifier = Modifier.height(16.dp))

                BenefitRow(
                    icon = "\u221E", // infinity symbol
                    text = "Unlimited transcriptions"
                )
                Spacer(modifier = Modifier.height(12.dp))
                BenefitRow(
                    icon = "\u26A1", // bolt
                    text = "Priority processing"
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Premium Plan Card
        PremiumPlanCard(
            title = "Premium Plan",
            price = "$3.99/month",
            onSubscribeClick = onUpgradeClick
        )
    } else {
        // Premium Status Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.5.dp, Color(0xFF10B981))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Premium Active",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF065F46)
                )

                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = Color(0xFF10B981).copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Your benefits:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF065F46),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                BenefitRow(
                    icon = "\u221E",
                    text = "Unlimited transcriptions",
                    textColor = Color(0xFF065F46)
                )
                Spacer(modifier = Modifier.height(8.dp))
                BenefitRow(
                    icon = "\u26A1",
                    text = "Priority processing",
                    textColor = Color(0xFF065F46)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Manage Subscription button
        Button(
            onClick = onManageClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            border = BorderStroke(
                1.dp,
                Brush.linearGradient(colors = listOf(ScoopPurple, ScoopBlue, ScoopCyan))
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "Manage Subscription",
                fontWeight = FontWeight.Bold,
                style = LocalTextStyle.current.copy(
                    brush = Brush.linearGradient(
                        colors = listOf(ScoopPurple, ScoopBlue, ScoopCyan)
                    )
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Cancel option
        OutlinedButton(
            onClick = onCancelClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFFEF4444)
            ),
            border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Cancel Subscription")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "You will retain premium access until the end of your billing period.",
            style = MaterialTheme.typography.bodySmall,
            color = SecondaryText,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun BenefitRow(
    icon: String,
    text: String,
    textColor: Color = PrimaryText
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = icon,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = textColor
        )
    }
}

@Composable
private fun PremiumPlanCard(
    title: String,
    price: String,
    onSubscribeClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            2.dp,
            Brush.linearGradient(colors = listOf(ScoopPurple, ScoopBlue, ScoopCyan))
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "RECOMMENDED",
                style = MaterialTheme.typography.labelSmall.copy(
                    brush = Brush.linearGradient(
                        colors = listOf(ScoopPurple, ScoopBlue, ScoopCyan)
                    )
                ),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = PrimaryText
            )

            Text(
                text = price,
                style = MaterialTheme.typography.headlineSmall.copy(
                    brush = Brush.linearGradient(
                        colors = listOf(ScoopPurple, ScoopBlue, ScoopCyan)
                    )
                ),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            listOf("Unlimited transcriptions", "Priority processing").forEach { feature ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = ScoopPurple,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = feature,
                        style = MaterialTheme.typography.bodyMedium,
                        color = PrimaryText
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Filled gradient subscribe button
            Button(
                onClick = onSubscribeClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(ScoopPurple, ScoopBlue, ScoopCyan)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Subscribe Now",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Error loading subscription info",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFFEF4444)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = SecondaryText
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            border = BorderStroke(
                1.dp,
                Brush.linearGradient(colors = listOf(ScoopPurple, ScoopBlue, ScoopCyan))
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "Retry",
                style = LocalTextStyle.current.copy(
                    brush = Brush.linearGradient(
                        colors = listOf(ScoopPurple, ScoopBlue, ScoopCyan)
                    )
                )
            )
        }
    }
}
