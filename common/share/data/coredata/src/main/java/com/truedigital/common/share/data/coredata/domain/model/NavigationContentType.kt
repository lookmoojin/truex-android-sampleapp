package com.truedigital.common.share.data.coredata.domain.model

import java.util.Locale

enum class NavigationContentType(val contentType: String) {
    DARA("dara"),
    EMPTY(""),
    HIGHLIGHT("highlight"),
    KNOWLEDGE("knowledge"),
    LIFESTYLE("lifestyle"),
    NEWS("news"),
    SPORT_ARTICLE("sportarticle"),
    WEB_VIEW("webview");

    companion object {
        fun tranValueOf(value: String): NavigationContentType {
            return when (value.toLowerCase(Locale.ENGLISH)) {
                "dara" -> DARA
                "highlight" -> HIGHLIGHT
                "knowledge" -> KNOWLEDGE
                "lifestyle" -> LIFESTYLE
                "news" -> NEWS
                "sportarticle" -> SPORT_ARTICLE
                "webview" -> WEB_VIEW
                else -> EMPTY
            }
        }
    }
}
