package com.example.transcribeassistant.activity

import android.app.ComponentCaller
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.transcribeassistant.R
import com.example.transcribeassistant.ui.screen.TranscribeAssistantApp
import com.example.transcribeassistant.ui.theme.TranscribeAssistantTheme
import com.example.transcribeassistant.ui.viewmodel.AuthViewModel
import com.example.transcribeassistant.ui.viewmodel.AuthState
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Configure Google Sign-in
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        Log.d("OAuthDebug","default_web_client_id = ${getString(R.string.default_web_client_id)}")

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            TranscribeAssistantTheme {
                val authState by authViewModel.authState.collectAsState()

                Surface(modifier = Modifier.fillMaxSize()) {
                    when (val state = authState) {
                        is AuthState.SignedIn -> {
                            TranscribeAssistantApp() // App Root
                        }
                        is AuthState.SignedOut, is AuthState.Error -> {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ){
                                Button(onClick = {signIn()}){
                                    Text("Sign in with Google")
                                }
                                if (state is AuthState.Error) {
                                    Text(text = state.message)
                                }
                            }
                        }
                        is AuthState.Loading -> {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ){
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun signIn(){
        try {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        } catch (e: Exception) {
            Log.e("SignIn", "Error: ${e.message}")
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            //handleSignInResult(task)
            try {
                val account = task.getResult(ApiException::class.java)
                // Handle signed in account
            } catch (e: ApiException) {
                Log.e("SignIn", "Error code: ${e.statusCode}")
                Log.e("SignIn", "Error message: ${e.message}")
            }
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>){
        try{
            val account = completedTask.getResult(ApiException::class.java)
            account?.idToken?.let { token ->
                authViewModel.signInWithGoogle(token)
            }
        }catch(e:ApiException){
            Log.e("GoogleSignOn","Error during sign on: ${e.message} ")
        }
    }

    companion object{
        private const val RC_SIGN_IN = 9001
    }
}