package com.truedigital.common.share.resource.compose

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val themeColor = lightColors(
    primary = Red500,
    primaryVariant = Red500,
    secondary = Color.White,
    secondaryVariant = Color.White,
    background = Color.White,
    surface = Color.White,
    error = Red500,
    onPrimary = Color.White,
    onSecondary = LightOnSurfaceHighEmphasis,
    onBackground = LightOnSurfaceHighEmphasis,
    onSurface = LightOnSurfaceHighEmphasis,
    onError = Color.White
)

@Deprecated(
    message = "Use TrueIdTheme { ... } instead",
    replaceWith = ReplaceWith(
        expression = "TrueIdTheme()",
        imports = ["com.truedigital.common.share.resource.ui.theme.TrueIdTheme"]
    )
)
@Composable
fun JetTrueIDM2Theme(content: @Composable () -> Unit) {
    val lang = LocalContext.current.resources.configuration.locales[0].language
    TrueIdFonts.setFontsByLang(lang)
    val typography = TypeFacesM2.getTypography(TrueIdFonts.current)

    MaterialTheme(colors = themeColor, typography = typography, content = content)
}
