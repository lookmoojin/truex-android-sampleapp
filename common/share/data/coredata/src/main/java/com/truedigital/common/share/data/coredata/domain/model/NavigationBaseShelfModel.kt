package com.truedigital.common.share.data.coredata.domain.model

import android.os.Parcelable
import com.truedigital.common.share.datalegacy.domain.discover.model.shelf.BaseShelfModel
import kotlinx.parcelize.Parcelize

@Parcelize
open class NavigationBaseShelfModel : BaseShelfModel() {
    var navItems: MutableList<NavigationModel> = mutableListOf()
    var titleEn: String = ""
}

fun NavigationBaseShelfModel.findShelfSlugByShelfId(shelfId: String): String {
    return this.navItems
        .find { navigationModel ->
            navigationModel.shelfId == shelfId
        }?.shelfSlug.orEmpty()
}

fun NavigationBaseShelfModel.findShelfIdByShelfSlug(shelfSlug: String): String {
    return this.navItems
        .find { navigationModel ->
            navigationModel.shelfSlug == shelfSlug
        }?.shelfId.orEmpty()
}

fun NavigationBaseShelfModel.findPositionByShelfSlug(shelfSlug: String): Int {
    val navItem = this.navItems.find {
        it.shelfSlug == shelfSlug
    }
    return this.navItems.indexOf(navItem)
}

fun NavigationBaseShelfModel.findPositionByShelfId(shelfId: String): Int {
    val navItem = this.navItems.find {
        it.shelfId == shelfId
    }
    return this.navItems.indexOf(navItem)
}

@Parcelize
open class NavigationModel(
    var deeplinkTopNav: String = "",
    var productGroupCode: String = "",
    var title: String = "",
    var shelfId: String = "",
    var shelfCode: String = "",
    var shelfSlug: String = "",
    var navigate: String = "",
    var titleEn: String = "",
    var type: String = "",
    var viewType: String = "",
    var contentRights: String = "",
    var movieType: String = "",
    var stickyBannerId: String = "",
    var stickyBannerSlug: String = ""
) : Parcelable
