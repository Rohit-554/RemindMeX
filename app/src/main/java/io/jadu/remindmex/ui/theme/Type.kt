package io.jadu.remindmex.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import io.jadu.remindmex.R

val manropeFamily = FontFamily(
    Font(R.font.manrope_regular, FontWeight.Normal),
    Font(R.font.manrope_medium, FontWeight.Medium),
    Font(R.font.manrope_semibold, FontWeight.SemiBold),
    Font(R.font.manrope_bold, FontWeight.Bold),
    Font(R.font.manrope_extra_bold, FontWeight.Black)
)

/**
 * Font size 32.sp
 */
@Composable
fun H1TextStyle() = TextStyle(
    fontFamily = manropeFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 32.sp,
)

/**
 * Font size 28.sp
 */

@Composable
fun H2TextStyle() = TextStyle(
    fontFamily = manropeFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 28.sp,
)

/**
 * Font size 24.sp
 */
@Composable
fun H3TextStyle() = TextStyle(
    fontFamily = manropeFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 24.sp,
)

/**
 * Font size 20.sp
 */

@Composable
fun BodyXXLarge() = TextStyle(
    fontFamily = manropeFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 20.sp,
)

/**
 * Font size 18.sp
 */
@Composable
fun BodyXLarge() = TextStyle(
    fontFamily = manropeFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 18.sp,
)

/**
 * Font size 16.sp
 */
@Composable
fun BodyLarge() = TextStyle(
    fontFamily = manropeFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 16.sp,
)

/**
 * Font size 14.sp
 */
@Composable
fun BodyNormal() = TextStyle(
    fontFamily = manropeFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
)

/**
 * Font size 12.sp
 */
@Composable
fun BodySmall() = TextStyle(
    fontFamily = manropeFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
)

/**
 * Font size 10.sp
 */
@Composable
fun BodyXSmall() = TextStyle(
    fontFamily = manropeFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 10.sp,
)

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = manropeFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)