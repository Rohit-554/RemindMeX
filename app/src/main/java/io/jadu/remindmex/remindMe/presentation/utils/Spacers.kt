package io.jadu.remindmex.remindMe.presentation.utils

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun HSpacer(width: Dp = MaterialTheme.spacing.s4) {
    Spacer(modifier = Modifier.width(width))
}

@Composable
fun HSpacer(factor: Float) {
    Spacer(modifier = Modifier.width(MaterialTheme.spacing.s4 * factor))
}

/**
 * Vertical Spacer with default height of 16.dp
 */
@Composable
fun VSpacer(height: Dp = MaterialTheme.spacing.s4) {
    Spacer(modifier = Modifier.height(height))
}
@Composable
fun VSpacer(factor: Int) {
    Spacer(modifier = Modifier.height(MaterialTheme.spacing.s4 * factor))
}


@Composable
fun HDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier,
        thickness = 0.5.dp,
        color = Color(0xffA9B0B8).copy(alpha = 0.7f)
    )
}

@Composable
fun VDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = 0.5.dp,
    color: Color = Color(0xffA9B0B8).copy(alpha = 0.7f)
) {
    androidx.compose.material3.VerticalDivider(
        modifier = modifier,
        thickness = thickness,
        color = color
    )
}

data class Spacing(
    val default: Dp = 0.dp,
    val sHalf: Dp = 2.dp,
    val s1: Dp = 4.dp,
    val s2: Dp = 8.dp,
    val s3: Dp = 12.dp,
    val s4: Dp = 16.dp,
    val s5: Dp = 20.dp,
    val s6: Dp = 24.dp,
    val s8: Dp = 32.dp,
    val s10: Dp = 40.dp,
    val s16: Dp = 64.dp,
)

val LocalSpacingProvider = compositionLocalOf { Spacing() }

val MaterialTheme.spacing: Spacing
    @Composable
    @ReadOnlyComposable
    get() = LocalSpacingProvider.current