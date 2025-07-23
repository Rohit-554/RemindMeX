package io.jadu.remindmex.remindMe.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.jadu.remindmex.ui.theme.BodyLarge
import io.jadu.remindmex.ui.theme.ElementsColors

enum class ButtonType {
    Filled,
    Outlined,
    Tonal,
    Text,
    IconOnly
}
@Composable
fun ButtonUI(
    text: String = "",
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonType: ButtonType = ButtonType.Filled,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: Painter? = null,
    shape: Shape = RoundedCornerShape(40f),
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    textStyle: TextStyle = BodyLarge().copy(fontWeight = FontWeight.Bold),
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    iconSize: Dp = 20.dp
) {
    val backgroundColor = when (buttonType) {
        ButtonType.Filled -> if (enabled) containerColor else ElementsColors.GrayBorderColor.color
        ButtonType.Outlined -> Color.Transparent
        ButtonType.Tonal -> if (enabled) containerColor.copy(alpha = 0.2f) else Color.Gray
        ButtonType.Text -> Color.Transparent
        ButtonType.IconOnly -> if (enabled) containerColor else Color.Gray
    }

    val borderStroke = when (buttonType) {
        ButtonType.Outlined -> BorderStroke(1.dp, containerColor)
        else -> null
    }


    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .bounceClickable {
                onClick()
            }
            .then(if (borderStroke != null) Modifier.border(borderStroke, shape) else Modifier)
            .padding(contentPadding)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = contentColor,
                strokeWidth = 2.dp,
                modifier = Modifier
                    .size(18.dp)
                    .align(Alignment.Center)
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.align(Alignment.Center)
            ) {
                if (leadingIcon != null) {
                    Icon(
                        painter = leadingIcon,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(iconSize)
                    )
                    if (text.isNotEmpty()) Spacer(modifier = Modifier.width(8.dp))
                }
                if (text.isNotEmpty()) {
                    Text(
                        text = text,
                        style = textStyle,
                        color = contentColor
                    )
                }
            }
        }
    }
}

@Composable
fun CronosButton(text:String, onClick: () -> Unit, modifier: Modifier = Modifier, isEnabled: Boolean = true) {
    ButtonUI(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .height(48.dp),
        onClick = {
            onClick()
        },
        text = text,
        enabled = isEnabled,
    )
}