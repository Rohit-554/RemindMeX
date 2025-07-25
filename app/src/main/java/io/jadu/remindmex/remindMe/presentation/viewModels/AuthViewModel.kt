package io.jadu.remindmex.remindMe.presentation.viewModels

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import io.jadu.remindmex.remindMe.presentation.components.ui.showSnackBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.koin.core.option.viewModelScopeFactory
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class LoginViewModel : ViewModel() {

    companion object {
        private const val TAG = "SettingsViewModel"
        private const val GOOGLE_CLIENT_ID = "444671546244-9frqlqi68fqlik5ufu8vhu9a6tobqnjd.apps.googleusercontent.com"
    }

    private val _signInState = MutableStateFlow<SignInState>(SignInState.Idle)
    val signInState: StateFlow<SignInState> = _signInState.asStateFlow()

    private val _isGoogleSignInEnabled = MutableStateFlow(false)
    val isGoogleSignInEnabled: StateFlow<Boolean> = _isGoogleSignInEnabled.asStateFlow()

    fun setGoogleSignInEnabled(enabled: Boolean) {
        _isGoogleSignInEnabled.value = enabled
    }

    fun signInWithGoogle(context: Context) {
        if (_signInState.value is SignInState.Loading) return

        _signInState.value = SignInState.Loading

        viewModelScope.launch {
            try {
                val credentialManager = CredentialManager.create(context)
                val request = createCredentialRequest()

                val result = credentialManager.getCredential(
                    request = request,
                    context = context
                )

                handleSignInResult(result, context)

            } catch (e: GetCredentialException) {
                Log.e(TAG, "Error getting credential", e)
                _signInState.value = SignInState.Error("Failed to get credentials: ${e.message}")
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error during sign in", e)
                _signInState.value = SignInState.Error("Unexpected error: ${e.message}")
            }
        }
    }

    fun logout() {
        signOut()
    }
    private fun createCredentialRequest(): GetCredentialRequest {
        val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(GOOGLE_CLIENT_ID)
            .build()

        return GetCredentialRequest.Builder()
            .addCredentialOption(signInWithGoogleOption)
            .build()
    }

    private suspend fun handleSignInResult(result: GetCredentialResponse, context: Context) {
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        authenticateWithFirebase(googleIdTokenCredential, context)
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Received an invalid google id token response", e)
                        _signInState.value = SignInState.Error("Invalid Google ID token")
                    }
                } else {
                    Log.e(TAG, "Unexpected type of credential: ${credential.type}")
                    _signInState.value = SignInState.Error("Unexpected credential type")
                }
            }
            else -> {
                Log.e(TAG, "Unexpected type of credential: ${credential::class.java.simpleName}")
                _signInState.value = SignInState.Error("Unexpected credential type")
            }
        }
    }

    private suspend fun authenticateWithFirebase(
        googleIdTokenCredential: GoogleIdTokenCredential,
        context: Context
    ) {
        try {
            val googleTokenId = googleIdTokenCredential.idToken
            val authCredential = GoogleAuthProvider.getCredential(googleTokenId, null)

            val authResult = Firebase.auth.signInWithCredential(authCredential).await()
            val user = authResult.user

            user?.let {
                if (!it.isAnonymous) {
                    withContext(Dispatchers.Main) {
                        showSnackBar(
                            message = "Welcome ${it.displayName ?: "User"}",
                        )
                    }
                    _signInState.value = SignInState.Success(it.displayName ?: "User")
                    Log.d(TAG, "Sign in successful for user: ${it.email}")
                } else {
                    _signInState.value = SignInState.Error("Anonymous user sign in")
                }
            } ?: run {
                _signInState.value = SignInState.Error("User is null after sign in")
            }

            Log.d(TAG, "Received google id token: ${googleIdTokenCredential.id}")

        } catch (e: Exception) {
            Log.e(TAG, "Firebase authentication failed", e)
            _signInState.value = SignInState.Error("Authentication failed: ${e.message}")
        }
    }

    // Reset sign-in state
    fun resetSignInState() {
        _signInState.value = SignInState.Idle
    }

    // Sign out
    private fun signOut() {
        viewModelScope.launch {
            try {
                Firebase.auth.signOut()
                _signInState.value = SignInState.Idle
                Log.d(TAG, "User signed out successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error during sign out", e)
            }
        }
    }
}

sealed class SignInState {
    object Idle : SignInState()
    object Loading : SignInState()
    data class Success(val userName: String) : SignInState()
    data class Error(val message: String) : SignInState()
}