package com.truedigital.core.data

import androidx.appcompat.app.AppCompatDelegate

open class CommonShelfModel {
    var id: String = ""
    var viewType: String = ""
    var header: CommonHeaderFooterModel? = null
    var footer: CommonHeaderFooterModel? = null
    var theme: String = ""
    var items: MutableList<CommonItemModel>? = null
    var shelfCode: String = ""
    var shelfId: String = ""
    var shelfName: String = ""
    var vType: String = ""
    var typeCallApi: String = ""
    var navigate: String = ""
    var placementId: String = ""
    var data: CommonHeaderFooterDataModel? = null
    var gradientStart: String = ""
    var gradientEnd: String = ""
    var visibleShelf: Boolean = true
    var index: Int = 0

    open val moduleName: String
        get() {
            return vType.split("/").firstOrNull().orEmpty()
        }

    val isDarkTheme: Boolean
        get() {
            return when (theme) {
                "white", "light" -> false
                "black", "dark" -> true
                else -> AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
            }
        }
}

data class CommonItemModel(
    var viewType: String = "",
    var header: CommonHeaderFooterModel? = null,
    var data: ShelfItemModel? = null,
    var footer: CommonHeaderFooterModel? = null,
    var navigate: String = ""
)
