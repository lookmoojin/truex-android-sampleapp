package com.truedigital.common.share.data.coredata.deeplink.constants

import androidx.collection.arrayMapOf

object ConfigsDeeplinkFormat : Map<DeeplinkConstants.DeeplinkContentType, String> by arrayMapOf(
    DeeplinkConstants.DeeplinkContentType.ARTICLE to DeeplinkConstants.DeeplinkFormat.ARTICLE,
    DeeplinkConstants.DeeplinkContentType.ARTICLE_DARA to DeeplinkConstants.DeeplinkFormat.ARTICLE_DARA,
    DeeplinkConstants.DeeplinkContentType.ARTICLE_ENTERTAINMENT to DeeplinkConstants.DeeplinkFormat.ARTICLE_ENTERTAINMENT,
    DeeplinkConstants.DeeplinkContentType.ARTICLE_FOOD to DeeplinkConstants.DeeplinkFormat.ARTICLE_FOOD,
    DeeplinkConstants.DeeplinkContentType.ARTICLE_HOROSCORPE to DeeplinkConstants.DeeplinkFormat.ARTICLE_HOROSCORPE,
    DeeplinkConstants.DeeplinkContentType.ARTICLE_MUSIC to DeeplinkConstants.DeeplinkFormat.ARTICLE_MUSIC,
    DeeplinkConstants.DeeplinkContentType.ARTICLE_NEWS to DeeplinkConstants.DeeplinkFormat.ARTICLE_NEWS,
    DeeplinkConstants.DeeplinkContentType.ARTICLE_SPORT to DeeplinkConstants.DeeplinkFormat.ARTICLE_SPORT,
    DeeplinkConstants.DeeplinkContentType.ARTICLE_TRAVEL to DeeplinkConstants.DeeplinkFormat.ARTICLE_TRAVEL,
    DeeplinkConstants.DeeplinkContentType.ARTICLE_WOMEN to DeeplinkConstants.DeeplinkFormat.ARTICLE_WOMEN,
    DeeplinkConstants.DeeplinkContentType.EXTERNAL_BROWSER to DeeplinkConstants.DeeplinkFormat.EXTERNAL_BROWSER,
    DeeplinkConstants.DeeplinkContentType.INAPP_BROWSER to DeeplinkConstants.DeeplinkFormat.INAPP_BROWSER,
    DeeplinkConstants.DeeplinkContentType.MUSIC_ALBUM to DeeplinkConstants.DeeplinkFormat.MUSIC_ALBUM,
    DeeplinkConstants.DeeplinkContentType.MUSIC_PLAYLIST to DeeplinkConstants.DeeplinkFormat.MUSIC_PLAYLIST,
    DeeplinkConstants.DeeplinkContentType.MUSIC_SONG to DeeplinkConstants.DeeplinkFormat.MUSIC_SONG,
    DeeplinkConstants.DeeplinkContentType.PRIVILEGE_ARTICLE to DeeplinkConstants.DeeplinkFormat.PRIVILEGE_ARTICLE,
    DeeplinkConstants.DeeplinkContentType.PRIVILEGE_COUPONS to DeeplinkConstants.DeeplinkFormat.PRIVILEGE_COUPONS,
    DeeplinkConstants.DeeplinkContentType.PRIVILEGE_FREE_GIFTS to DeeplinkConstants.DeeplinkFormat.PRIVILEGE_FREE_GIFTS,
    DeeplinkConstants.DeeplinkContentType.PRIVILEGE_MERCHANTS to DeeplinkConstants.DeeplinkFormat.PRIVILEGE_MERCHANTS,
    DeeplinkConstants.DeeplinkContentType.PRIVILEGE_PRIVILEGES to DeeplinkConstants.DeeplinkFormat.PRIVILEGE_PRIVILEGES,
    DeeplinkConstants.DeeplinkContentType.PRIVILEGE_THEMATIC to DeeplinkConstants.DeeplinkFormat.PRIVILEGE_THEMATIC,
    DeeplinkConstants.DeeplinkContentType.WATCH_CLIP to DeeplinkConstants.DeeplinkFormat.WATCH_CLIP,
    DeeplinkConstants.DeeplinkContentType.WATCH_DETAIL to DeeplinkConstants.DeeplinkFormat.WATCH_DETAIL,
    DeeplinkConstants.DeeplinkContentType.WATCH_PARTY to DeeplinkConstants.DeeplinkFormat.WATCH_PARTY,
    DeeplinkConstants.DeeplinkContentType.WATCH_SERIES to DeeplinkConstants.DeeplinkFormat.WATCH_SERIES,
    DeeplinkConstants.DeeplinkContentType.WATCH_SPORT_CLIPS to DeeplinkConstants.DeeplinkFormat.WATCH_SPORT_CLIPS,
    DeeplinkConstants.DeeplinkContentType.WATCH_TV to DeeplinkConstants.DeeplinkFormat.WATCH_TV,
    DeeplinkConstants.DeeplinkContentType.SPORT to DeeplinkConstants.DeeplinkFormat.SPORT,
    DeeplinkConstants.DeeplinkContentType.SPORT_MATCH to DeeplinkConstants.DeeplinkFormat.SPORT_MATCH,
    DeeplinkConstants.DeeplinkContentType.UGC_BROWSER to DeeplinkConstants.DeeplinkFormat.UGC_BROWSER
)
