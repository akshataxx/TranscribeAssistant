package com.example.transcribeassistant.ui.screen.subscription

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.transcribeassistant.R
import com.example.transcribeassistant.ui.screen.components.AnimatedBlobsBackground
import com.example.transcribeassistant.ui.screen.components.PrimaryText
import com.example.transcribeassistant.ui.screen.components.SecondaryText
import com.example.transcribeassistant.ui.screen.components.ScoopBlue
import com.example.transcribeassistant.ui.screen.components.ScoopCyan
import com.example.transcribeassistant.ui.screen.components.ScoopPurple
import com.example.transcribeassistant.ui.viewmodel.SubscriptionUiState
import com.example.transcribeassistant.ui.viewmodel.SubscriptionViewModel

@Composable
fun SubscriptionScreen(
    onNavigateBack: () -> Unit,
    viewModel: SubscriptionViewModel = hiltViewModel()
) {
    val uiState: SubscriptionUiState by viewModel.uiState.collectAsState()
    val productDetails by viewModel.productDetails.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity

    LaunchedEffect(Unit) {
        viewModel.loadUsageInfo()
    }

    AnimatedBlobsBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Logo
                Image(
                    painter = painterResource(id = R.drawable.scoop_logo),
                    contentDescription = "Scoop",
                    modifier = Modifier.height(32.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // "Premium" gradient heading
                Text(
                    text = "Premium",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        brush = Brush.linearGradient(
                            colors = listOf(ScoopPurple, ScoopBlue, ScoopCyan)
                        )
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Unlock unlimited transcriptions",
                    fontSize = 14.sp,
                    color = SecondaryText
                )

                Spacer(modifier = Modifier.height(24.dp))

                when (val state = uiState) {
                    is SubscriptionUiState.Loading -> {
                        Spacer(modifier = Modifier.height(40.dp))
                        CircularProgressIndicator(color = ScoopPurple)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Loading...", fontSize = 14.sp, color = SecondaryText)
                        Spacer(modifier = Modifier.height(40.dp))
                    }

                    is SubscriptionUiState.Success -> {
                        if (!state.usageInfo.isPremium) {
                            // Benefits card
                            BenefitsCard()

                            Spacer(modifier = Modifier.height(20.dp))

                            // Product card with real price from billing
                            val displayName = productDetails?.name ?: "Premium Monthly"
                            val displayPrice = productDetails
                                ?.subscriptionOfferDetails?.firstOrNull()
                                ?.pricingPhases?.pricingPhaseList?.firstOrNull()
                                ?.formattedPrice ?: "$3.99"
                            ProductCard(displayName = displayName, displayPrice = displayPrice)

                            Spacer(modifier = Modifier.height(20.dp))

                            // Subscribe Now button
                            Button(
                                onClick = { activity?.let { viewModel.startPremiumUpgrade(it) } },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
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
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White,
                                        fontSize = 16.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Restore Purchases
                            TextButton(onClick = { /* restore handled by billing */ }) {
                                Text(
                                    "Restore Purchases",
                                    fontSize = 14.sp,
                                    color = ScoopPurple
                                )
                            }
                        } else {
                            // Premium status card
                            PremiumStatusCard()

                            Spacer(modifier = Modifier.height(20.dp))

                            // Manage Subscription — white card with border + arrow icon
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val intent = Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse("https://play.google.com/store/account/subscriptions")
                                        )
                                        context.startActivity(intent)
                                    },
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.5.dp, SecondaryText.copy(alpha = 0.3f)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp, vertical = 16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Manage Subscription",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 16.sp,
                                        color = PrimaryText
                                    )
                                    Icon(
                                        Icons.Default.OpenInNew,
                                        contentDescription = null,
                                        tint = PrimaryText,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    is SubscriptionUiState.Error -> {
                        ErrorContent(
                            message = state.message,
                            onRetry = { viewModel.loadUsageInfo() }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Back button overlaid at top-start
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(4.dp)
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = PrimaryText
                )
            }
        }
    }
}

@Composable
private fun BenefitsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, SecondaryText.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Premium Benefits",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryText
            )
            Spacer(modifier = Modifier.height(12.dp))
            BenefitRow(icon = "\u221E", text = "Unlimited transcriptions")
            Spacer(modifier = Modifier.height(8.dp))
            BenefitRow(icon = "\u26A1", text = "Priority processing")
        }
    }
}

@Composable
private fun ProductCard(displayName: String, displayPrice: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.5.dp, ScoopPurple.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = displayName,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryText
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = displayPrice,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineLarge.copy(
                    brush = Brush.linearGradient(
                        colors = listOf(ScoopPurple, ScoopBlue)
                    )
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "per month",
                fontSize = 12.sp,
                color = SecondaryText
            )
        }
    }
}

@Composable
private fun PremiumStatusCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8FDF0)),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFF4ADE80).copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Status badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color(0xFF22C55E),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Premium Active",
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryText,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFF22C55E).copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(12.dp))

            // Benefits
            Text(
                text = "Your benefits:",
                fontSize = 12.sp,
                color = SecondaryText
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color(0xFF22C55E),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Unlimited transcriptions",
                    fontSize = 12.sp,
                    color = PrimaryText
                )
            }
        }
    }
}

@Composable
private fun BenefitRow(icon: String, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = icon,
            fontSize = 18.sp,
            color = ScoopPurple,
            modifier = Modifier.width(24.dp),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            color = PrimaryText
        )
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = message,
            fontSize = 14.sp,
            color = Color(0xFFEF4444),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFEF4444).copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                .padding(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onRetry) {
            Text("Retry", color = ScoopPurple)
        }
    }
}
