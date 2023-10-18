package com.truedigital.common.share.resource.ui.theme.shelf

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.truedigital.common.share.resource.ui.theme.Colors
import com.truedigital.common.share.resource.ui.theme.SelectableColor

@Immutable
data class CalendarShelfColors(
    val itemBackground: SelectableColor,
    val labelDayOfWeek: SelectableColor,
    val labelDayOfMonth: SelectableColor,
    val labelMonth: SelectableColor,
    val itemDotIndicator: Color
) {
    companion object {
        val Light = CalendarShelfColors(
            itemBackground = SelectableColor(
                selected = Colors.System.Dark06,
                unselected = Colors.System.Light06
            ),
            labelDayOfWeek = SelectableColor(
                selected = Colors.System.White,
                unselected = Colors.System.Black
            ),
            labelDayOfMonth = SelectableColor(
                selected = Colors.System.White,
                unselected = Colors.System.Black
            ),
            labelMonth = SelectableColor(
                selected = Colors.Primary.DarkHighEmphasis,
                unselected = Colors.Primary.LightLowEmphasis
            ),
            itemDotIndicator = Colors.Status.Error
        )
        val Dark = Light.copy(
            itemBackground = SelectableColor(
                selected = Colors.System.Light06,
                unselected = Colors.System.Dark06
            ),
            labelDayOfWeek = SelectableColor(
                selected = Colors.System.Black,
                unselected = Colors.System.Light05
            ),
            labelDayOfMonth = SelectableColor(
                selected = Colors.System.Black,
                unselected = Colors.System.Light05
            ),
            labelMonth = SelectableColor(
                selected = Colors.System.Dark03,
                unselected = Colors.System.Dark01
            )
        )
    }
}
