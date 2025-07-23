package io.jadu.remindmex.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext


enum class MajorColors(val color: Color) {
    Red(Color(0xFFEF5350)),
    Yellow(Color(0xFFFFCA28)),
    Blue(Color(0xFF0abde3)),
    Orange(Color(0xFFFFA726)),
    Green(Color(0xFF66BB6A)),
    White(Color(0xFFFFFFFF)),
    Black(Color(0xFF000000)),
    Secondary(Color(0xFF2F4858)),
}

enum class ElementsColors(val color: Color) {
    Gray(Color(0xffF0F4FA)),
    LightYellow(Color(0xFFFAE8B6)),
    LightRed(Color(0xFFF5C5B7)),
    LightPurple(Color(0xFFCCB0FD)),
    LightBlue(Color(0xffA7C5EB)),
    LinkColor(Color(0xff5389e0)),
    BrickRed(Color(0xff8f1402)),
    LightBrown(Color(0xffC4A484)),
    LightPink(Color(0xffF8E7F6)),
    MarbleWhite(Color(0xffF7F7FB)),
    SalmonPink(Color(0xff9985FF)),
    GrayBorderColor(Color(0xffC7CFD9)),
    DarkGray(Color(0xff53647A)),
    ErrorDarkRed(Color(0xffFF0000))
}

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun RemindMeXTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}