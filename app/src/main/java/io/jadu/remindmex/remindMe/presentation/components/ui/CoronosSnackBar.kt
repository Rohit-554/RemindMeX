package io.jadu.remindmex.remindMe.presentation.components.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.delay
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.offset
import androidx.compose.ui.zIndex
import io.jadu.remindmex.ui.theme.BodyNormal
import io.jadu.remindmex.ui.theme.MajorColors
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
object AppSnackbarHost {
    val snackbarHostState = SnackbarHostState()
}

@OptIn(DelicateCoroutinesApi::class)
fun showSnackBar(
    message: String,
    duration: SnackbarDuration = SnackbarDuration.Short,
    positiveMessage: Boolean = false,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    var actionLabelLocal = actionLabel
    if (actionLabel == null && positiveMessage)
        actionLabelLocal = "POSITIVE"
    else if (actionLabel == null && !positiveMessage)
        actionLabelLocal = "NEGATIVE"

    GlobalScope.launch {
        AppSnackbarHost.snackbarHostState.showSnackbar(
            message,
            duration = duration,
            actionLabel = actionLabelLocal
        )
    }
}


@Composable
fun CustomSnackbarHost(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = AppSnackbarHost.snackbarHostState,
) {
    var showAnimation by remember { mutableStateOf(false) }
    var currentMessage by remember { mutableStateOf("") }
    var isSuccess by remember { mutableStateOf(true) }
    var animationDuration by remember { mutableStateOf(800L) }
    LaunchedEffect(snackbarHostState.currentSnackbarData) {
        snackbarHostState.currentSnackbarData?.let { data ->
            val actionLabel = data.visuals.actionLabel

            animationDuration = when (data.visuals.duration) {
                SnackbarDuration.Short -> 800L
                SnackbarDuration.Long -> 2000L
                SnackbarDuration.Indefinite -> 4000L
            }
            when (actionLabel) {
                "POSITIVE" -> {
                    isSuccess = true
                    currentMessage = data.visuals.message
                    showAnimation = true
                }

                "NEGATIVE" -> {
                    isSuccess = false
                    currentMessage = data.visuals.message
                    showAnimation = true
                }
            }
        }
    }

    CoronosSnackBar(
        message = currentMessage,
        isSuccess = isSuccess,
        isVisible = showAnimation,
        onAnimationComplete = {
            showAnimation = false
            snackbarHostState.currentSnackbarData?.dismiss()
        },
        onDismiss = {
            showAnimation = false
            snackbarHostState.currentSnackbarData?.dismiss()
        },
        modifier = modifier,
        displayDurationMillis = animationDuration
    )
}



@Composable
fun CoronosSnackBar(
    message: String,
    isSuccess: Boolean,
    isVisible: Boolean,
    onAnimationComplete: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    displayDurationMillis: Long = 2000L
) {
    val transition = updateTransition(targetState = isVisible, label = "SnackBarVisibility")

    val offsetY by transition.animateDp(
        label = "OffsetY",
        transitionSpec = {
            tween(durationMillis = 400, easing = FastOutSlowInEasing)
        }
    ) { visible ->
        if (visible) 0.dp else (-100).dp
    }

    val alpha by transition.animateFloat(
        label = "Alpha",
        transitionSpec = { tween(durationMillis = 300) }
    ) { visible -> if (visible) 1f else 0f }

    if (isVisible || alpha > 0f) {
        LaunchedEffect(Unit) {
            delay(displayDurationMillis)
            onAnimationComplete()
        }

        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .offset(y = offsetY)
                .alpha(alpha),
            contentAlignment = Alignment.TopCenter
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isSuccess) Color(0xFF4CAF50) else MaterialTheme.colorScheme.errorContainer,
                tonalElevation = 6.dp,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = message,
                        style = BodyNormal().copy(color = MajorColors.White.color)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}
