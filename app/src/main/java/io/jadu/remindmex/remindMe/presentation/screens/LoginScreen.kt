package io.jadu.remindmex.remindMe.presentation.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import io.jadu.remindmex.R
import io.jadu.remindmex.remindMe.presentation.components.ui.GSignSwitchComp
import io.jadu.remindmex.remindMe.presentation.components.ui.createBoldAnnotatedString
import io.jadu.remindmex.remindMe.presentation.components.ui.showSnackBar
import io.jadu.remindmex.remindMe.presentation.route.NavRoute
import io.jadu.remindmex.remindMe.presentation.utils.VSpacer
import io.jadu.remindmex.remindMe.presentation.utils.bounceClickable
import io.jadu.remindmex.remindMe.presentation.viewModels.AuthEvent
import io.jadu.remindmex.remindMe.presentation.viewModels.LoginViewModel
import io.jadu.remindmex.remindMe.presentation.viewModels.SignInState
import io.jadu.remindmex.ui.theme.BodyLarge
import io.jadu.remindmex.ui.theme.ElementsColors
import io.jadu.remindmex.ui.theme.H3TextStyle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LoginScreen(
    rootNavController: NavHostController,
    viewModel: LoginViewModel = koinViewModel() // Using your SettingsViewModel
) {
    val context = LocalContext.current
    val signInState by viewModel.signInState.collectAsState()
    val isGoogleSignInEnabled by viewModel.isGoogleSignInEnabled.collectAsState()
    val focusManager = LocalFocusManager.current

    // Handle automatic sign-in when enabled
    LaunchedEffect(isGoogleSignInEnabled) {
        if (isGoogleSignInEnabled && signInState is SignInState.Idle) {
            viewModel.signInWithGoogle(context)
        }
    }

    // Handle sign-in states
    LaunchedEffect(signInState) {
        when (signInState) {
            is SignInState.Error -> {
                showSnackBar(
                    message = (signInState as SignInState.Error).message,
                    positiveMessage = false
                )
            }
            is SignInState.Success -> {
                showSnackBar(
                    message = "Welcome back, ${(signInState as SignInState.Success).userName}!",
                    positiveMessage = true
                )
                // Navigate to main screen or wherever you want after successful login
                // rootNavController.navigate("main_screen") {
                //     popUpTo("login") { inclusive = true }
                // }
            }
            else -> { /* Handle other states if needed */ }
        }
    }

    Scaffold { contentPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 16.dp)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
        ) {
            val screenWidth = maxWidth
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.account_login_protection),
                    contentDescription = "Login Image",
                    modifier = Modifier
                        .size(240.dp)
                        .padding(top = 16.dp),
                    contentScale = ContentScale.Fit
                )
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "Sign In",
                            style = H3TextStyle().copy(fontWeight = FontWeight.Bold)
                        )
                        VSpacer(8.dp)
                        Text(
                            "Welcome back! Please enter your details.",
                            style = BodyLarge().copy(fontWeight = FontWeight.Normal)
                        )
                    }
                    VSpacer(16.dp)

                    // Google Sign-In Toggle
                    GSignSwitchComp(
                        title = "Enable Drive Sync",
                        subtitle = "Sign in with Google to sync your notes",
                        isChecked = isGoogleSignInEnabled,
                        onCheckedChange = { enabled ->
                            viewModel.setGoogleSignInEnabled(enabled)
                        }
                    )

                    VSpacer(24.dp)

                    // Sign-In Status Display
                    when (signInState) {
                        is SignInState.Loading -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    "Signing in...",
                                    style = BodyLarge().copy(
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                        }
                        is SignInState.Success -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Success",
                                    tint = Color.Green,
                                    modifier = Modifier.size(32.dp)
                                )
                                VSpacer(8.dp)
                                Text(
                                    "Signed in as ${(signInState as SignInState.Success).userName}",
                                    style = BodyLarge().copy(
                                        color = Color.Green,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                                VSpacer(16.dp)
                                Button(
                                    onClick = { viewModel.signOut() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.outline
                                    )
                                ) {
                                    Text("Sign Out")
                                }
                            }
                        }
                        is SignInState.Error -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Error",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(32.dp)
                                )
                                VSpacer(8.dp)
                                Text(
                                    "Sign-in failed",
                                    style = BodyLarge().copy(
                                        color = MaterialTheme.colorScheme.error,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                                VSpacer(16.dp)
                                Button(
                                    onClick = {
                                        viewModel.resetSignInState()
                                        if (isGoogleSignInEnabled) {
                                            viewModel.signInWithGoogle(context)
                                        }
                                    }
                                ) {
                                    Text("Retry")
                                }
                            }
                        }
                        is SignInState.Idle -> {
                            if (!isGoogleSignInEnabled) {
                                GSignSwitchComp(
                                    title = "Enable Google Sign-In",
                                    subtitle = "Sign in with Google to sync your notes",
                                    isChecked = isGoogleSignInEnabled,
                                    onCheckedChange = { enabled ->
                                        viewModel.setGoogleSignInEnabled(enabled)
                                    }
                                )
                                Button(
                                    onClick = { viewModel.signInWithGoogle(context) },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.Login, // Add Google icon
                                            contentDescription = "Google",
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Sign In with Google")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Loading Overlay (if you want to show full-screen loading)
        if (signInState is SignInState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                    .clickable(enabled = false) {}
            ) {
                CircularWavyProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}