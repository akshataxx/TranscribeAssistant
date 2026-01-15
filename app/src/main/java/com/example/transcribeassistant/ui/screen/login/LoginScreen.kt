package com.example.transcribeassistant.ui.screen.login

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.app.Activity
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Login
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.transcribeassistant.R
import com.example.transcribeassistant.ui.viewmodel.LoginUiState
import com.example.transcribeassistant.ui.viewmodel.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException


// ============================================================================
// CHANGED: New light theme colors for Scoop design (was dark purple theme)
// ============================================================================
private val LightBackground = Color(0xFFF5F7FA)
private val LightBackgroundEnd = Color(0xFFE8ECF1)
private val CardBackground = Color.White
private val PrimaryText = Color(0xFF1F2937)
private val SecondaryText = Color(0xFF6B7280)
private val PurpleGradientStart = Color(0xFF7C3AED)
private val BlueGradientMiddle = Color(0xFF2563EB)
private val CyanGradientEnd = Color(0xFF06B6D4)
private val YellowAccent = Color(0xFFFBBF24)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel(),
    webClientId: String
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val view = LocalView.current

    // Ensure status bar icons are dark (for light background)
    SideEffect {
        val window = (view.context as? Activity)?.window
        window?.let {
            val insetsController = WindowCompat.getInsetsController(it, view)
            insetsController.isAppearanceLightStatusBars = true
            it.statusBarColor = android.graphics.Color.TRANSPARENT
            it.navigationBarColor = android.graphics.Color.TRANSPARENT
        }
    }

    // Build GoogleSignInClient configured to request ID token
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
    }
    Log.d("Google signin client ", "$gso")

    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    // Launcher for the sign-in intent
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken
            if (!idToken.isNullOrBlank()) {
                Log.d("Login", "Got Google ID token (last8): ${idToken.takeLast(8)}")
                viewModel.loginWithGoogle(idToken)
            } else {
                Log.e("Login", "ID token was null or empty")
            }
        } catch (e: ApiException) {
            Log.e("Login", "Google sign-in failed", e)
            viewModel.setError("Google sign-in failed: ${e.localizedMessage}")
        }
    }

    // Handle navigation on successful login
    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Success) {
            navController.navigate("dashboard") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    // ============================================================================
    // CHANGED: Wrapped everything in Box with light gradient background
    // (was Column with dark purple solid background)
    // ============================================================================
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        LightBackground,
                        LightBackgroundEnd
                    )
                )
            )
    ) {
        // ============================================================================
        // REMOVED: "Welcome Back" header with profile icon (Scoop design is centered)
        // ============================================================================

        // Main content area - different states
        when (uiState) {
            is LoginUiState.Loading -> {
                // ============================================================================
                // CHANGED: Updated colors to light theme
                // ============================================================================
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        color = PurpleGradientStart, // CHANGED: was Color.White
                        modifier = Modifier.size(64.dp),
                        strokeWidth = 4.dp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Signing you in...",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText // CHANGED: was Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Please wait while we authenticate your account",
                        fontSize = 14.sp,
                        color = SecondaryText, // CHANGED: was Color.Gray
                        textAlign = TextAlign.Center
                    )
                }
            }

            is LoginUiState.Success -> {
                // ============================================================================
                // CHANGED: Updated colors to light theme
                // ============================================================================
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = Color(0xFF10B981), // Green success color
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Welcome back!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText // CHANGED: was Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Taking you to your dashboard...",
                        fontSize = 14.sp,
                        color = SecondaryText
                    )
                }
            }

            is LoginUiState.Error -> {
                // ============================================================================
                // CHANGED: Updated colors to light theme
                // ============================================================================
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Error",
                        tint = Color(0xFFEF4444), // Red error color
                        modifier = Modifier.size(80.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Sign-in Failed",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText // CHANGED: was Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = (uiState as LoginUiState.Error).message,
                        fontSize = 14.sp,
                        color = SecondaryText, // CHANGED: was Color.Gray
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            viewModel.resetToIdle()
                            launcher.launch(googleSignInClient.signInIntent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PurpleGradientStart, // CHANGED: was cardColors[0]
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text(
                            text = "Try Again",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            is LoginUiState.Idle -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.weight(0.35f))

                    Image(
                        painter = painterResource(id = R.drawable.scoop_logo),
                        contentDescription = "Scoop Logo",
                        modifier = Modifier
                            .height(60.dp)
                            .padding(bottom = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Organize. Transcribe. Discover.",
                        fontSize = 16.sp,
                        color = SecondaryText,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    // Animated gradient blob image
                    val infiniteTransition = rememberInfiniteTransition(label = "blob")

                    val offsetY by infiniteTransition.animateFloat(
                        initialValue = -10f,
                        targetValue = 20f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(3500, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "offsetY"
                    )

                    val scale by infiniteTransition.animateFloat(
                        initialValue = 0.95f,
                        targetValue = 1.15f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(2800, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "scale"
                    )

                    val rotation by infiniteTransition.animateFloat(
                        initialValue = -5f,
                        targetValue = 5f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(4200, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "rotation"
                    )

                    Box(
                        modifier = Modifier
                            .size(360.dp)
                            .offset(y = offsetY.dp)
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                rotationZ = rotation
                            )
                    ) {
                        // Vector drawable for crisp, scalable graphics
                        Image(
                            painter = painterResource(id = R.drawable.scoop_png),
                            contentDescription = "Gradient Blob",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                        // Radial gradient overlay to fade the edges
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Transparent,
                                            LightBackground.copy(alpha = 0.3f),
                                            LightBackground.copy(alpha = 0.7f),
                                            LightBackground
                                        ),
                                        radius = 650f
                                    )
                                )
                        )
                    }

                    Spacer(modifier = Modifier.weight(0.3f))

                    Button(
                        onClick = {
                            launcher.launch(googleSignInClient.signInIntent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CardBackground, // White button
                            contentColor = PrimaryText // Dark text
                        ),
                        shape = RoundedCornerShape(28.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 8.dp
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // TODO: Replace with actual Google logo
                            // Image(
                            //     painter = painterResource(id = R.drawable.ic_google_logo),
                            //     contentDescription = "Google",
                            //     modifier = Modifier.size(20.dp)
                            // )
                            Icon(
                                imageVector = Icons.Default.Login,
                                contentDescription = "Google",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Continue with Google",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = PrimaryText
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ============================================================================
                    // CHANGED: Simple text instead of card for terms
                    // ============================================================================
                    Text(
                        text = "Terms & Privacy",
                        fontSize = 14.sp,
                        color = SecondaryText,
                        modifier = Modifier.padding(top = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}



// ============================================================================
// PREVIEW: For viewing in Android Studio without running the app
// ============================================================================
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    // Mock NavController for preview
    val navController = rememberNavController()

    // Preview showing the Idle state
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(0.1f))

            // Logo placeholder
            Text(
                text = "Scoop",
                fontSize = 56.sp,
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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Organize. Transcribe. Discover.",
                fontSize = 16.sp,
                color = SecondaryText,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Gradient blob image (vector drawable)
            Image(
                painter = painterResource(id = R.drawable.scoop_png),
                contentDescription = "Gradient Blob",
                modifier = Modifier.size(240.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.weight(0.3f))

            // Google button
            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CardBackground,
                    contentColor = PrimaryText
                ),
                shape = RoundedCornerShape(28.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Login,
                        contentDescription = "Google",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Continue with Google",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Terms & Privacy",
                fontSize = 14.sp,
                color = SecondaryText,
                modifier = Modifier.padding(top = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}