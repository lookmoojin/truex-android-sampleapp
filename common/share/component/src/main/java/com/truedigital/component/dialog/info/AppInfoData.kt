package com.truedigital.component.dialog.info

abstract class AppInfoData {
    var icon: IconType = IconType.OTHER
    var title: String = ""
    var message: String = ""
    var code: String = ""
}

class OneButton : AppInfoData() {
    var buttonMessage: String = ""
    var buttonColor: ColorButton = ColorButton.GRAY
    var buttonContentDescription: String? = null
}

class TwoButton : AppInfoData() {
    var rightButtonMessage: String = ""
    var leftButtonMessage: String = ""
    var rightButtonColor: ColorButton = ColorButton.GRAY
    var leftButtonColor: ColorButton = ColorButton.GRAY
    var rightButtonContentDescription: String? = null
    var leftButtonContentDescription: String? = null
}

enum class IconType {
    ERROR, SUCCESS, WARNING, OTHER
}

enum class ColorButton {
    GRAY, RED, BLACK
}
