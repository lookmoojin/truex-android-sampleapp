package com.truedigital.common.share.resource.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.truedigital.common.share.resource.ui.theme.shelf.CalendarShelfColors
import com.truedigital.common.share.resource.ui.theme.shelf.ScheduleShelfColors

@Immutable
data class TrueIdColor(
    val shelf: ShelfColors,
    val isLight: Boolean = true
)

@Immutable
data class ShelfColors(
    val header: Color,
    val title: Color,
    val subtitle: Color,
    val seeMore: Color,
    val background: Color,
    val backgroundGradient: List<Color>,
    val fanClub: FanClubShelfColors,
    val schedule: ScheduleShelfColors,
    val calendar: CalendarShelfColors
)

@Immutable
data class FanClubShelfColors(
    val label: Color,
    val itemBackground: Color
)

val lightExtendedColors = TrueIdColor(
    shelf = ShelfColors(
        header = Color.Black,
        title = Color.Black,
        subtitle = Color.DarkGray,
        seeMore = Color.Black,
        background = Color.White,
        backgroundGradient = listOf(Color.White),
        fanClub = FanClubShelfColors(
            label = Color.Black,
            itemBackground = Color(0xFFF2F2F2)
        ),
        schedule = ScheduleShelfColors.Light,
        calendar = CalendarShelfColors.Light
    )
)

val darkExtendedColors = lightExtendedColors.copy(
    shelf = ShelfColors(
        header = Color.White,
        title = Color.White,
        subtitle = Color.LightGray,
        seeMore = Color(0xFFC7C7C7),
        background = Color.Black,
        backgroundGradient = listOf(Color.Black),
        fanClub = FanClubShelfColors(
            label = Color.White,
            itemBackground = Color(0xFF333333)
        ),
        schedule = ScheduleShelfColors.Dark,
        calendar = CalendarShelfColors.Dark
    ),
    isLight = false
)

val LocalExtendedColors = staticCompositionLocalOf { lightExtendedColors }

data class SelectableColor(
    val selected: Color,
    val unselected: Color
) {
    infix fun by(isSelected: Boolean): Color {
        return if (isSelected) this.selected else this.unselected
    }
}

@Composable
@ReadOnlyComposable
fun getShelfPalette() = TrueIdTheme.colors.shelf
