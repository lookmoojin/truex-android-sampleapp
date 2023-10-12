package com.truedigital.core.constants

class CoreConstants {
    object TopNavViewType {
        const val SETTING = "topNavSetting"
        const val COMMUNITY = "topNavCommunity"
        const val CALL = "topNavCall"
        const val NOTIFICATION = "topNavNotification"
        const val PROFILE = "topNavProfile"
        const val PROFILE_FEED = "topNavProfileFeed"
        const val RECENT_CHAT = "topNavRecentChat"
        const val TOP_LEFT = "top-left"
    }

    object Theme {
        const val LIGHT = "light"
        const val DARK = "dark"
    }

    object Environment {
        const val STAGING = "dev_oneapp"
        const val PREPROD = "preprod_oneapp"
        const val PROD = "oneapp"
    }

    object Module {
        const val WATCH_MODULE = "lib:watch"
        const val SPORT_MODULE = "lib:sport"
    }
}
