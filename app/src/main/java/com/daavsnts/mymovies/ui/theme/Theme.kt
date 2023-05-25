package com.daavsnts.mymovies.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView

private val DarkColorScheme = darkColorScheme(
    background = MyNight,
    onBackground = MyWhite,
    primary = MyBlack,
    onPrimary = MyWhite,
    surface = MyBlack,
    onSurface = MyWhite,
    secondaryContainer = MyNight,
    onSecondaryContainer = MyWhite,
    error = MyRed
)

private val LightColorScheme = lightColorScheme(
    background = MyNight,
    onBackground = MyWhite,
    primary = MyBlack,
    onPrimary = MyWhite,
    surface = MyBlack,
    onSurface = MyWhite,
    secondaryContainer = MyNight,
    onSecondaryContainer = MyWhite,
    error = MyRed
)

@Composable
fun MyMoviesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}