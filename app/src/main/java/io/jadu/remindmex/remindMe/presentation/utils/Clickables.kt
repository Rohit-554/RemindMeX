package io.jadu.remindmex.remindMe.presentation.utils

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput

inline fun Modifier.noRippleClickable(
    crossinline onClick: () -> Unit
): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}


fun Modifier.bounceClickable(
    pressedScale: Number = 0.9f,
    onLongPress: (() -> Unit)? = null,
    onPressIn: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) = composed {
    var isPressed by remember { mutableStateOf(false) }
    val floatScale = pressedScale.toFloat()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) floatScale else 1f,
        label = ""
    ) {

    }
    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    isPressed = true
                    onPressIn?.invoke()
                    val success = tryAwaitRelease()
                    if (success && isPressed) {
                        onClick?.invoke()
                    }
                    isPressed = false
                },
                onLongPress = {
                    isPressed = false
                    onLongPress?.invoke()
                }
            )
        }

}