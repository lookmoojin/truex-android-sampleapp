package com.truedigital.common.share.data.coredata.deeplink.constants

class DeeplinkConstants {

    object DeeplinkConstants {
        const val HOST_COMMERCE = "shopping.trueid.net"
        const val HOST_PRIVILEGE = "privilege.trueid.net"
        const val HOST_DYNAMIC_LINK = "app.goo.gl"
        const val HOST_DYNAMIC_LINK_DEV = "s.trueid-dev.net"
        const val HOST_DYNAMIC_LINK_PREPROD = "s.trueid-preprod.net"
        const val HOST_DYNAMIC_LINK_PROD = "s.trueid.net"
        const val HOST_ONELINK = "onelink.me"
        const val HOST_ONELINK_TTID = "ttid.co"
        const val HOST_TRUE_ID = "trueid://"
        const val HOST_TRUE_ID_HTTP = "trueid.net"
        const val HOST_TRUE_ID_PREPROD = "trueid-preprod.net"
        const val KEY_ONELINK = "af_dp="
    }

    enum class DeeplinkContentType {
        ARTICLE,
        ARTICLE_DARA,
        ARTICLE_ENTERTAINMENT,
        ARTICLE_FOOD,
        ARTICLE_HOROSCORPE,
        ARTICLE_MUSIC,
        ARTICLE_NEWS,
        ARTICLE_SPORT,
        ARTICLE_TRAVEL,
        ARTICLE_WOMEN,
        EXTERNAL_BROWSER,
        INAPP_BROWSER,
        MUSIC_ALBUM,
        MUSIC_PLAYLIST,
        MUSIC_SONG,
        PRIVILEGE_ARTICLE,
        PRIVILEGE_COUPONS,
        PRIVILEGE_FREE_GIFTS,
        PRIVILEGE_MERCHANTS,
        PRIVILEGE_PRIVILEGES,
        PRIVILEGE_THEMATIC,
        WATCH_CLIP,
        WATCH_DETAIL,
        WATCH_PARTY,
        WATCH_SERIES,
        WATCH_SPORT_CLIPS,
        WATCH_TV,
        SPORT,
        SPORT_MATCH,
        UGC_BROWSER
    }

    object DeeplinkFormat {
        const val ARTICLE = "https://read.trueid.net/detail/{cms_id}"
        const val ARTICLE_DARA = "https://dara.trueid.net/detail/{cms_id}"
        const val ARTICLE_ENTERTAINMENT = "https://entertainment.trueid.net/detail/{cms_id}"
        const val ARTICLE_FOOD = "https://food.trueid.net/detail/{cms_id}"
        const val ARTICLE_HOROSCORPE = "https://horoscope.trueid.net/detail/{cms_id}"
        const val ARTICLE_MUSIC = "https://music.trueid.net/detail/{cms_id}"
        const val ARTICLE_NEWS = "https://news.trueid.net/detail/{cms_id}"
        const val ARTICLE_SPORT = "https://sport.trueid.net/detail/{cms_id}"
        const val ARTICLE_TRAVEL = "https://travel.trueid.net/detail/{cms_id}"
        const val ARTICLE_WOMEN = "https://women.trueid.net/detail/{cms_id}"
        const val EXTERNAL_BROWSER = "https://home.trueid.net/external-browser?website={website}"
        const val GAME_CENTER = "https://game.trueid.net"
        const val INAPP_BROWSER = "https://home.trueid.net/inapp-browser?website={website}"
        const val MUSIC_ALBUM = "https://trueid.net/listen/album/{cms_id}"
        const val MUSIC_PLAYLIST = "https://trueid.net/listen/playlist/{cms_id}"
        const val MUSIC_SONG = "https://trueid.net/listen/song/{cms_id}"
        const val COMMERCE = "trueidapp://commerce"
        const val PRIVILEGE_ARTICLE = "https://privilege.trueid.net/articles/{cms_id}"
        const val PRIVILEGE_COUPONS = "https://privilege.trueid.net/coupons/{cms_id}"
        const val PRIVILEGE_FREE_GIFTS = "https://privilege.trueid.net/free-gifts/{cms_id}"
        const val PRIVILEGE_MERCHANTS = "https://privilege.trueid.net/merchants/{cms_id}"
        const val PRIVILEGE_PRIVILEGES = "https://privilege.trueid.net/privileges/{cms_id}"
        const val PRIVILEGE_THEMATIC = "https://privilege.trueid.net/thematic/{cms_id}"
        const val WATCH_CLIP = "https://movie.trueid.net/clip/{cms_id}"
        const val WATCH_DETAIL = "https://movie.trueid.net/movie/{cms_id}"
        const val WATCH_PARTY = "https://live.trueid.net/live/{slug_channel}/{room_id}"
        const val WATCH_SERIES = "https://movie.trueid.net/series/{cms_id}"
        const val WATCH_SPORT_CLIPS = "https://sport.trueid.net/premier-league/clips/{cms_id}"
        const val WATCH_TV = "https://tv.trueid.net/live/{slug_name}"
        const val SPORT = "https://sport.trueid.net"
        const val SPORT_MATCH = "https://trueid.net/sport/match/{match_id}"
        const val COMMUNITY_HOME = "https://community.trueid.net"
        const val QR_SCANNER = "https://home.trueid.net/scanner"
        const val SETTINGS = "https://home.trueid.net/settings"
        const val SETTINGS_MANAGE_DEVICES = "https://home.trueid.net/settings/manage_devices"
        const val TRUECLOUD = "https://home.trueid.net/truecloud"
        const val TRUE_CLOUD_V3 = "https://truecloud.trueid.net"
        const val UGC_BROWSER = "https://home.trueid.net/ugc-browser?website={website}"
        const val COMMUNICATOR_HOME = "https://communicator.trueid.net"
    }

    object DeeplinkKey {
        const val CMS_ID = "{cms_id}"
        const val SLUG_NAME = "{slug_name}"
        const val WEBSITE = "{website}"
        const val MATCH_ID = "{match_id}"
        const val ROOM_ID = "{room_id}"
        const val SLUG_CHANNEL = "{slug_channel}"
    }

    object DeeplinkInternal {
        const val DEEPLINK_PROFILE = "https://myaccount.trueid.net/"
        const val DEEPLINK_SEARCH = "trueid://search"
        const val DEEPLINK_SEARCH_PRIVILEGE = "trueid://search?slug=bonus"
        const val DEEPLINK_SEARCH_READ = "trueid://search?slug=read"
        const val DEEPLINK_SEARCH_WATCH = "trueid://search?slug=watch"
        const val DEEPLINK_SEARCH_WATCH_SFV = "trueid://www.trueid.net/search?slug=sfv"
        const val DEEPLINK_SEARCH_WATCH_SFV_KEYWORD = "trueid://www.trueid.net/search?slug=sfv&q="
        const val DEEPLINK_HOME = "trueid://home"
        const val DEEPLINK_COMMUNICATOR = "trueid://communicator"
        const val DEEP_LINK_COMMUNITY = "https://www.trueid.net?page=community"
        const val DEEPLINK_Q = "q"
        const val DEEPLINK_SEARCH_SLUG = "slug"
    }

    object DeeplinkSwitchTab {
        const val HOME_TAP = "home"
        const val WATCH_TAP = "watch"
        const val PRIVILEGE_TAP = "privilege"
        const val LISTEN_TAP = "listen"
        const val COMMUNITY_TAP = "community"
        const val COMMUNICATOR_TAP = "communicator"
        const val READ_TAP = "read"
        const val GAME_CENTER_TAP = "game"
    }

    object KeyBundle {
        const val CMS_ID = "cmsId"
        const val ITEM_INDEX = "itemIndex"
        const val SEARCH_TYPE = "searchType"
        const val SEARCH_KEYWORD = "searchKeyword"
        const val SHELF_CODE = "shelfCode"
        const val SHELF_INDEX = "shelfIndex"
        const val SHELF_NAME = "shelfName"
    }
}
