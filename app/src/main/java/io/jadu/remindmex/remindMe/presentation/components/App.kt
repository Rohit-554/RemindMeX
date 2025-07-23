package io.jadu.remindmex.remindMe.presentation.components

import androidx.compose.runtime.Composable
import io.jadu.remindmex.remindMe.presentation.components.navigation.AppRouting
import io.jadu.remindmex.remindMe.presentation.route.NavRoute
import io.jadu.remindmex.ui.theme.RemindMeXTheme

@Composable
fun App() {
    val startDestination = NavRoute.Login
    RemindMeXTheme {
        AppRouting(startDestination,"")
    }
}