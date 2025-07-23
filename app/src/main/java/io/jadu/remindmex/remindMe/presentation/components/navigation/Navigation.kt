package io.jadu.remindmex.remindMe.presentation.components.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.jadu.remindmex.remindMe.presentation.components.ui.CustomSnackbarHost
import io.jadu.remindmex.remindMe.presentation.route.NavRoute
import io.jadu.remindmex.remindMe.presentation.screens.LoginScreen
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

val LocalNavController = compositionLocalOf<NavController> { error("No NavController found!") }

@Composable
fun AppRouting(startDestination: NavRoute, route: String) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        val rootNavController = rememberNavController()
        LaunchedEffect(key1 = route) {
            if (route != "") {
                rootNavController.navigate(route)
            }
        }
        CompositionLocalProvider(LocalNavController provides rootNavController) {
            Box(modifier = Modifier.fillMaxSize()) {
                CustomSnackbarHost(
                    modifier =
                        Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 48.dp)
                            .zIndex(99999f)
                )
                NavHost(
                    navController = rootNavController,
                    startDestination = startDestination,
                    enterTransition = {
                        fadeIn(animationSpec = tween(400, easing = FastOutSlowInEasing)) +
                                scaleIn(
                                    initialScale = 0.95f,
                                    animationSpec = tween(400, easing = FastOutSlowInEasing)
                                )
                    },
                    exitTransition = {
                        fadeOut(animationSpec = tween(300, easing = FastOutSlowInEasing)) +
                                scaleOut(
                                    targetScale = 1.05f,
                                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                                )
                    },
                    popEnterTransition = {
                        fadeIn(animationSpec = tween(400, easing = FastOutSlowInEasing)) +
                                scaleIn(
                                    initialScale = 0.95f,
                                    animationSpec = tween(400, easing = FastOutSlowInEasing)
                                )
                    },
                    popExitTransition = {
                        fadeOut(animationSpec = tween(300, easing = FastOutSlowInEasing)) +
                                slideOutOfContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                                )
                    }
                ){
                    composable<NavRoute.Login> {
                        LoginScreen(rootNavController)
                    }
                }
            }
        }
    }
}