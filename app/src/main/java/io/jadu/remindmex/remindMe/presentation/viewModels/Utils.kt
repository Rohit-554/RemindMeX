package io.jadu.remindmex.remindMe.presentation.viewModels

data class AuthUiState(
    val isLoading: Boolean = false,
    val email: String = "",
    val password: String = ""
)

// Events Sealed Class
sealed class AuthEvent {
    data class Success(val message: String) : AuthEvent()
    data class Error(val message: String) : AuthEvent()
}


/*
* Generalised
* Written by Jadu @01:14 AM - 8 July 2025 +5:30GMT - INDIA
* */

data class UiState(
    val isLoading: Boolean = false,
    val isLoaded:Boolean = false,
    val error:String? = null
)

sealed class UiEvent {
    data class Success(val message: String) : UiEvent()
    data class Error(val message: String) : UiEvent()
    object Idle : UiEvent()
    object Loading : UiEvent()
}
