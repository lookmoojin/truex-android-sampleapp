package com.truedigital.common.share.resource.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val themeColor = lightColors(
    primary = Colors.Red500,
    primaryVariant = Colors.Red500,
    secondary = Color.White,
    secondaryVariant = Color.White,
    background = Color.White,
    surface = Color.White,
    error = Colors.Red500,
    onPrimary = Color.White,
    onSecondary = Colors.LightOnSurfaceHighEmphasis,
    onBackground = Colors.LightOnSurfaceHighEmphasis,
    onSurface = Colors.LightOnSurfaceHighEmphasis,
    onError = Color.White
)

@Composable
fun TrueIdTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) darkExtendedColors else lightExtendedColors
    val language = LocalContext.current.resources.configuration.locales[0].language
    val fonts = TrueIdFont(language)
    val typography = trueIdTypography(fonts.default)
    val shapes = Shapes()
    CompositionLocalProvider(
        LocalExtendedColors provides colors,
        LocalFontFamily provides fonts
    ) {
        MaterialTheme(
            colors = themeColor,
            typography = typography,
            shapes = shapes,
            content = content
        )
    }
}

object TrueIdTheme {
    val colors: TrueIdColor
        @Composable
        @ReadOnlyComposable
        get() = LocalExtendedColors.current
    val fonts: TrueIdFont
        @Composable
        @ReadOnlyComposable
        get() = LocalFontFamily.current
}
