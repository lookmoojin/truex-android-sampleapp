package com.truedigital.common.share.resource.ui.theme.shelf

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.truedigital.common.share.resource.ui.theme.Colors
import com.truedigital.common.share.resource.ui.theme.SelectableColor

@Immutable
data class ScheduleShelfColors(
    val chipBackground: SelectableColor,
    val chipLabel: SelectableColor,
    val chipBorder: Color,
    val itemSectionLabel: Color,
    val itemBackground: Color,
    val itemBorder: Color,
    val itemEmptyBackground: Color,
    val itemEmptyIcon: Color,
    val itemEmptyLabel: Color,
    val itemMatchChannelBackground: Color,
    val itemMatchUpcomingDateLabel: Color,
    val itemMatchUpcomingTimeLabel: Color,
    val itemMatchSummaryStatusLabel: Color,
    val itemMatchSummaryBackground: Color,
    val badgeLiveBackground: Color,
    val badgeLiveLabel: Color,
    val badgeLiveDot: Color,
    val badgeLeagueBackground: Color,
    val itemTimerLabel: Color,
    val itemTeamNameLabel: Color,
    val itemLeagueNameLabel: Color,
    val itemScoreLabel: Color,
    val itemHalfTimeLabel: Color,
    val itemHalfTimeScoreLabel: Color,
    val btnWatchBackground: Color,
    val btnWatchContent: Color
) {
    companion object {
        val Light = ScheduleShelfColors(
            chipBackground = SelectableColor(
                selected = Colors.System.Black,
                unselected = Colors.System.White
            ),
            chipLabel = SelectableColor(
                selected = Colors.System.White,
                unselected = Colors.System.Black
            ),
            chipBorder = Colors.System.Light04,
            itemSectionLabel = Colors.System.Black,
            itemBackground = Colors.System.White,
            itemBorder = Colors.System.Light05,
            itemEmptyBackground = Colors.System.White,
            itemEmptyIcon = Colors.System.Black,
            itemEmptyLabel = Colors.System.Black,
            itemMatchChannelBackground = Colors.System.Light06,
            itemMatchUpcomingDateLabel = Colors.Primary.LightLowEmphasis,
            itemMatchUpcomingTimeLabel = Colors.System.Black,
            itemMatchSummaryStatusLabel = Colors.Primary.LightHighEmphasis,
            itemMatchSummaryBackground = Colors.Gray89,
            badgeLiveBackground = Colors.System.Light06,
            badgeLeagueBackground = Colors.System.Dark04,
            badgeLiveLabel = Colors.System.Black,
            badgeLiveDot = Colors.Brand.TrueIDMainRed,
            itemTimerLabel = Colors.System.Black,
            itemTeamNameLabel = Colors.System.Black,
            itemLeagueNameLabel = Colors.System.Light03,
            itemScoreLabel = Colors.System.Black,
            itemHalfTimeLabel = Colors.Primary.LightHighEmphasis,
            itemHalfTimeScoreLabel = Colors.Primary.LightLowEmphasis,
            btnWatchBackground = Colors.System.Black,
            btnWatchContent = Colors.System.White
        )

        val Dark = Light.copy(
            chipBackground = SelectableColor(
                selected = Colors.System.White,
                unselected = Colors.System.Dark06
            ),
            chipLabel = SelectableColor(
                selected = Colors.System.Black,
                unselected = Colors.System.White
            ),
            chipBorder = Colors.System.Dark06,
            itemSectionLabel = Colors.System.White,
            itemBackground = Colors.System.Dark06,
            itemBorder = Colors.System.Dark05,
            itemEmptyBackground = Colors.System.Dark05,
            itemEmptyIcon = Colors.System.White,
            itemEmptyLabel = Colors.System.White,
            itemMatchChannelBackground = Colors.System.Dark05,
            itemMatchUpcomingDateLabel = Colors.System.Light05,
            itemMatchUpcomingTimeLabel = Colors.System.White,
            itemMatchSummaryStatusLabel = Colors.System.Light04,
            itemMatchSummaryBackground = Colors.System.Dark05,
            badgeLiveBackground = Colors.Status.Error,
            badgeLiveLabel = Colors.System.White,
            badgeLiveDot = Colors.System.White,
            itemTimerLabel = Colors.System.White,
            itemTeamNameLabel = Colors.System.White,
            itemScoreLabel = Colors.System.White,
            itemHalfTimeLabel = Colors.System.Light05,
            itemHalfTimeScoreLabel = Colors.System.Light06,
            btnWatchBackground = Colors.System.White,
            btnWatchContent = Colors.System.Black
        )
    }
}
