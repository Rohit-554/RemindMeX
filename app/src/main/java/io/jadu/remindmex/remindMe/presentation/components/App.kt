package io.jadu.remindmex.remindMe.presentation.components

import androidx.compose.runtime.Composable
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import io.jadu.remindmex.remindMe.presentation.components.navigation.AppRouting
import io.jadu.remindmex.remindMe.presentation.route.NavRoute
import io.jadu.remindmex.ui.theme.RemindMeXTheme

@Composable
fun App() {
    var startDestination:NavRoute = NavRoute.Login
    if (isUserLoggedIn()) {
        startDestination = NavRoute.Home
    }
    RemindMeXTheme {
        AppRouting(startDestination,"")
    }
}

private fun isUserLoggedIn(): Boolean {
    val firebase = Firebase.auth
    return firebase.currentUser != null
}