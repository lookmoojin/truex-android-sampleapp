package com.truedigital.common.share.analytics.measurement.constant

class MeasurementConstant {

    object Action {
        const val ACTION_CLICK = "Click"
        const val ACTION_CLOSE = "Close"
        const val ACTION_SCREEN = "Screen"
        const val ACTION_SHARE = "Share"
        const val ACTION_OK = "OK"
        const val ACTION_NO_THANKS = "No Thanks"
        const val ACTION_RATE_NOW = "Rate Now"
        const val ACTION_REMIND_ME_LATER = "Remind Me Later"
    }

    object Category {
        const val USER_ACTION = "User Action"
    }

    class Commerce {
        object Key {
            const val KEY_PRODUCT_DETAIL = "product_detail"
            const val KEY_PRICE = "price"
        }
    }

    object Event {
        const val EVENT_CLICK = "click"
        const val EVENT_VIEW_CONSENT = "view_consent"
        const val EVENT_CONSENT_ALLOW = "allow_consent"
        const val EVENT_ALL_CONSENT_ALLOW = "allow_all_consents"
        const val EVENT_CONSENT_DENY = "deny_consent"
        const val EVENT_CHANGE_COUNTRY = "change_country"
        const val EVENT_ERROR_VIEW = "error_show"
        const val EVENT_ERROR_NOT_SHOW_VIEW = "error_not_show"
        const val EVENT_MUSIC_PLAY = "play_song_initial"
        const val EVENT_NET_PROMOTER_SCORE_BUTTON = "button"
        const val EVENT_NET_PROMOTER_SCORE_PLAYSTORE_NAME = "appstore_rate"
        const val EVENT_NET_PROMOTER_SCORE_RATE = "nps_rate"
        const val EVENT_PICK_PREFERENCE = "pick_preferences"
        const val EVENT_SEARCH = "search"
        const val EVENT_SELECT_CONTENT = "select_content"
        const val EVENT_SET_DEFAULT_SCREEN = "set_default_screen"
        const val EVENT_VIEW_PROMOTION = "view_promotion"
        const val EVENT_SELECT_PROMOTION = "select_promotion"
        const val EVENT_SOCIAL_SHARE = "social_share"
        const val EVENT_VIEW_CONTENT = "view_content"
        const val EVENT_VIEW_ITEM = "view_item"
        const val EVENT_VIEW_ITEM_LIST = "view_item_list"
        const val EVENT_NETWORK_SIGNAL_STRENGTH = "network_signal_strength"
        const val EVENT_VIEW_DIALOG = "view_dialog"
        const val EVENT_VIEW_MODAL = "view_modal"
    }

    object Key {
        const val KEY_AI = "ai"
        const val KEY_APP_FLYER_ID = "af_id"
        const val KEY_ARTICLE_ID = "article_id"
        const val KEY_BUSINESS_MODEL = "business_model"
        const val KEY_BUTTON_ALL_CHANNEL = "button_all_channel"
        const val KEY_CARD_TIER = "card_tier"
        const val KEY_CARRIER = "carrier"
        const val KEY_CATEGORY = "category"
        const val KEY_CELLULAR_BANDWIDTH = "cellular_bandwidth"
        const val KEY_CELLULAR_CATEGORY = "category"
        const val KEY_CELLULAR_CID = "cellular_cid"
        const val KEY_CELLULAR_CQI = "cellular_cqi"
        const val KEY_CELLULAR_EARFCN = "cellular_earfcn"
        const val KEY_CELLULAR_ENODEB_ID = "cellular_enodeb_id"
        const val KEY_CELLULAR_LAC = "cellular_lac"
        const val KEY_CELLULAR_LAT_LONG = "lat_long"
        const val KEY_CELLULAR_MCI = "cellular_mci"
        const val KEY_CELLULAR_MMCC = "cellular_mmcc"
        const val KEY_CELLULAR_MMNC = "cellular_mmnc"
        const val KEY_CELLULAR_MPCI = "cellular_mpci"
        const val KEY_CELLULAR_MTAC = "cellular_mtac"
        const val KEY_CELLULAR_RSRP = "cellular_rsrp"
        const val KEY_CELLULAR_RSRQ = "cellular_rsrq"
        const val KEY_CELLULAR_TA = "cellular_ta"
        const val KEY_CHANGE_COUNTRY_CURRENT_SETTING = "current_setting"
        const val KEY_CHANGE_COUNTRY_NEW_SETTING = "new_setting"
        const val KEY_CHANNEL_CODE = "channel_code"
        const val KEY_CMS = "cms"
        const val KEY_CMS_ID = "cms_id"
        const val KEY_COMMENT_ID = "comment_id"
        const val KEY_CONTENT_LIST_ID = "content_list_id"
        const val KEY_CONTENT_RIGHT = "content_rights"
        const val KEY_CONTENT_TYPE = "content_type"
        const val KEY_COUNTRY_FLAG = "country_flag"
        const val KEY_CURRENT_SETTING = "current_setting"
        const val KEY_CUSTOMER_HAS_TRUE_MONEY = "is_truemoney"
        const val KEY_DEFAULT_SCREEN = "default_screen"
        const val KEY_DEVICE_ID = "device_id"
        const val KEY_EP_MASTER = "ep_master"
        const val KEY_ERROR_BACKEND_MSG = "error_msg_bn"
        const val KEY_ERROR_CODE = "error_code"
        const val KEY_ERROR_DESC = "error_desc"
        const val KEY_ERROR_FRONTEND_MSG = "error_msg"
        const val KEY_ERROR_MESSAGE = "error_message"
        const val KEY_ERROR_MESSAGE_BN = "error_message_bn"
        const val KEY_ERROR_MSG = "error_msg"
        const val KEY_ERROR_NAME = "error_name"
        const val KEY_ERROR_TYPE = "error_type"
        const val KEY_EVENT_ACTION = "event_action"
        const val KEY_EVENT_NAME = "event_name"
        const val KEY_GENRE = "genre"
        const val KEY_GROUP_CODE = "group_code"
        const val KEY_INDEX = "index"
        const val KEY_IS_TMH = "is_tmh"
        const val KEY_ITEMS = "items"
        const val KEY_ITEM_CATEGORY = "item_category"
        const val KEY_ITEM_CATEGORY_2 = "item_category2"
        const val KEY_ITEM_CATEGORY_3 = "item_category3"
        const val KEY_ITEM_CATEGORY_4 = "item_category4"
        const val KEY_ITEM_CATEGORY_5 = "item_category5"
        const val KEY_ITEM_ID = "item_id"
        const val KEY_ITEM_INDEX = "item_index"
        const val KEY_ITEM_LIST_ID = "item_list_id"
        const val KEY_ITEM_LIST_NAME = "item_list_name"
        const val KEY_ITEM_NAME = "item_name"
        const val KEY_LANGUAGE = "language"
        const val KEY_LINK_DESC = "link_desc"
        const val KEY_LINK_TYPE = "link_type"
        const val KEY_LOCATION_LAT_LONG = "lat_long"
        const val KEY_METHOD = "method"
        const val KEY_MOVIE_TYPE = "vod_type"
        const val KEY_NET_PROMOTER_EVENT_POINT = "event_point"
        const val KEY_NET_PROMOTER_SCORE = "nps_score"
        const val KEY_NET_PROMOTER_SCORE_DESCRIPTION = "link_desc"
        const val KEY_NET_PROMOTER_SCORE_MESSAGE = "message"
        const val KEY_NET_PROMOTER_SCORE_NAME = "nps_name"
        const val KEY_NET_PROMOTER_SCORE_TYPE = "link_type"
        const val KEY_NEW_SETTING = "new_setting"
        const val KEY_OTHER_TYPE = "vod_other_type"
        const val KEY_PACKAGE_CODE = "package_code"
        const val KEY_PARAMETERS = "parameters"
        const val KEY_PARENT_ID = "parent_id"
        const val KEY_PARENT_NAME = "parent_name"
        const val KEY_PARTNER = "partner"
        const val KEY_PARTNER_ID = "partner_id"
        const val KEY_PARTNER_NAME = "partner_name"
        const val KEY_PRODUCT_CODE = "product_code"
        const val KEY_PROMOTION_ID = "promotion_id"
        const val KEY_PROMOTION_NAME = "promotion_name"
        const val KEY_PURPOSE_SLUG = "purpose_slug"
        const val KEY_REACTION_NAME = "reaction_name"
        const val KEY_RECOMMENDATION_SCHEMA_ID = "recommendation_schema_id"
        const val KEY_REDEEM_POINT = "redeem_point"
        const val KEY_REF_SSO_ID = "ref_sso_id"
        const val KEY_REF_VALUE = "ref"
        const val KEY_RE_RANKING_SCHEMA_ID = "shelf_reranking_schema_id"
        const val KEY_SCREEN_NAME = "screen_name"
        const val KEY_SCREEN_RESOLUTION = "screen_resolution"
        const val KEY_SEARCH_TERM = "search_term"
        const val KEY_SHELF = "shelf"
        const val KEY_SHELF_CODE = "shelf_code"
        const val KEY_SHELF_INDEX = "shelf_index"
        const val KEY_SHELF_NAME = "shelf_name"
        const val KEY_SHELF_SLUG = "shelf_slug"
        const val KEY_SLUG = "slug"
        const val KEY_SOCIAL_NETWORK = "social_network"
        const val KEY_SOURCE_TYPE = "source_type"
        const val KEY_SSO_ID = "sso_id"
        const val KEY_STATUS_CODE = "status_code"
        const val KEY_SUBSCRIPTION_TIERS = "subscription_tiers"
        const val KEY_SUB_EPISODE_INCLUDED = "sub_episode_included"
        const val KEY_TITLE = "title"
        const val KEY_TRUEVISIONS = "truevisions"
        const val KEY_TRUE_POINT = "truepoint"
        const val KEY_TRUE_PRODUCT = "true_product"
        const val KEY_URL = "url"
        const val KEY_USER_ID = "user_id"
        const val KEY_USER_PROFILE_ID = "user_profile_id"
        const val KEY_VERSION = "version"
        const val KEY_WIFI = "wifi"
    }

    object ScreenName {
        const val MEASUREMENT_MORE = ">"
        const val MEASUREMENT_MY_REWARDS = "my rewards"
        const val MEASUREMENT_DISCOVER = "Discover"
        const val MEASUREMENT_MOVIE_PLAYER = "Movie Player"
        const val MEASUREMENT_LIVE_TV = "Live TV"
        const val MEASUREMENT_SETTINGS_PAGE = "settings page"
        const val MEASUREMENT_SCREEN_DEFAULT = "set default screen"
        const val MEASUREMENT_ONBOARDING_DEFAULT = "onboarding > default screen"
    }

    object LinkType {
        const val LINK_TYPE_SEE_MORE = "see_more"
        const val LINK_TYPE_SHOW_LESS = "show_less"
        const val LINK_TYPE_MORE_NAV = "more_nav"
        const val LINK_TYPE_TOP_NAV = "top_nav"
        const val LINK_TYPE_TAB = "subcate_tab"
    }

    class Gamification {
        object ScreenName {
            const val MISSION_DASHBOARD = "mission_dashboard"
            const val MISSION_DETAIL = "mission_details"
            const val MISSION_LIST = "Mission List"
            const val MISSION_OVERVIEW_CURRENT = "mission_overview>current"
            const val MISSION_OVERVIEW_PAST = "mission_overview>past"
            const val MISSION_SEVEN_DAYS = "7D Mission Screen"
        }

        object Key {
            const val KEY_MISSION_ID = "mission_id"
            const val KEY_CMS_ID = "cms_id"
            const val KEY_CATEGORY = "category"
            const val KEY_TITLE = "title"
            const val KEY_IS_RECURRING = "is_recurring"
            const val KEY_IS_REALTIME_EVENT = "is_realtime_event"
            const val KEY_AUTO_JOIN = "auto_join"
            const val KEY_REWARD_TYPE = "reward_type"
            const val KEY_STATUS_CODE = "status_code"
            const val KEY_ERROR_CODE = "error_code"
            const val KEY_PREVISION_STATUS = "provision_status"
            const val KEY_PREVISION_DESC = "provision_desc"
            const val KEY_RETURN_CODE = "return_code"
            const val KEY_LINK_TYPE = "link_type"
            const val KEY_LINK_DESC = "link_desc"
        }

        object Event {
            const val EVENT_GAMIFICATION_MISSION_JOIN = "mission_join"
            const val EVENT_GAMIFICATION_MISSION_REDEEM = "mission_redeem"
            const val EVENT_GAMIFICATION_MISSION_REDEEM_EXCEED = "mission_redeem_fail"
            const val EVENT_GAMIFICATION_MISSION_REDEEM_ERROR = "error_show"
            const val EVENT_GAMIFICATION_SELECT_CONTENT = "select_content"
            const val EVENT_GAMIFICATION_CLICK = "click"
        }
    }

    class FreeData2GB {
        object Event {
            const val FREE_DATA_2GB_PROMOTION_ID = "promotion_id"
            const val FREE_DATA_2GB_PROMOTION_NAME = "promotion_name"
            const val GET_FREE_DATA_ATTEMPT = "get_free_data_attempt"
            const val GET_FREE_DATA_SUCCESS = "get_free_data_success"
            const val GET_FREE_DATA_FAIL = "get_free_data_fail"
        }
    }

    class BottomTab {
        object LinkType {
            const val LINK_TYPE_BOTTOM_NAV = "bottom_nav"
        }

        object LinkDesc {
            const val LINK_DESC_TODAY = "today"
            const val LINK_DESC_WATCH = "watch"
            const val LINK_DESC_PRIVILEGE = "privilege"
            const val LINK_DESC_COMMUNITY = "community"
            const val LINK_DESC_COMMUNICATOR = "communicator"
            const val LINK_DESC_GAME_CENTER = "game"
        }
    }

    class Discover {
        object Method {
            const val FORCE_LOGIN = "force_login"
            const val FORCE_LOGIN_SUCCESS = "force_login_success"
            const val LOGIN = "login"
            const val LOGIN_BEGIN = "login_begin"
            const val LOGIN_CANCEL = "login_cancel"
            const val LOGIN_FAIL = "login_fail"
        }

        object ContentType {
            const val CONTENT_TYPE_LIVE_TY = "livetv"
            const val CONTENT_TYPE_WE_MALL = "wemall"
        }

        object Event {
            const val APP_SHORTCUT_TYPE = "app-shortcuts"
            const val BOTTOM_NAV = "bottom-nav"
            const val BUTTON = "button"
            const val BOTTOM_NAV_WALLET = "bottom-nav-wallet"
            const val BOTTOM_TYPE = "button"
            const val HAMBURGER_NAV = "hamburger_nav"
            const val TUTORIAL_PAGE = "tutorial_page"
            const val TUTORIAL_GET_STARTED = "get started"
            const val TUTORIAL_DISMISS = "dismiss-tutorial"
            const val WHAT_NEW_PROMOTION_ID = "promotion_id"
            const val WHAT_NEW_PROMOTION_NAME = "promotion_name"
        }

        object ScreenName {
            const val MEASUREMENT_APP_RATING = "App Rating"
            const val MEASUREMENT_MORE_PAGE = "more_page"
            const val MEASUREMENT_DISCOVER_LATEST = "latest"
            const val MEASUREMENT_SETTING_LANGUAGE = "setting_lang"
            const val MEASUREMENT_WELCOME_PAGE = "welcome page"

            const val MEASUREMENT_CUSTOM_SURVEY = "Customer Survey"
            const val MEASUREMENT_MEASUREMOENT_HORIZONTAL_MENU = "horizontal menu"
            const val MEASUREMENT_ONBOARDING_PREFERENCE = "onboarding preference"
            const val MEASUREMENT_TRUEID_WEBVIEW_PAGE =
                "webview-dynamic-token > https://account.wemall.com"

            const val MEASUREMENT_L1_HOME = "home"
            const val MEASUREMENT_GLOBAL_SEARCH = "search"
        }

        object LinkDesc {
            const val LINK_DESC_BACK_TO_TOP = "back-to-top"
            const val LINK_DESC_DISMISS = "dismiss"
            const val LINK_DESC_REFERRAL_CODE = "referral_code"
            const val LINK_DESC_SKIP = "skip"
            const val LINK_DESC_ALLOW_PERMISSION = "allow permission"
            const val LINK_DESC_CANCEL_PERMISSION = "cancel permission"
            const val LINK_DESC_HEADER_MORE = "more"
        }

        object LinkType {
            const val LINK_TYPE_BUTTON = "button"
            const val LINK_TYPE_HEADER_MORE = "header"
            const val LINK_TYPE_REDEMPTION_THANK_YOU = "redemption_thankyou"
        }
    }

    class Notification {
        object Key {
            const val KEY_CATEGORY = "category"
            const val KEY_CAMPAIGN = "campaign"
            const val KEY_GCM_MESSAGE_ID = "gcm_msg_id"
            const val KEY_MESSAGE_ID = "msg_id"
            const val KEY_MESSAGE_INBOX_ID = "msg_inbox_id"
            const val KEY_MESSAGE_TIME = "msg_time"
            const val KEY_MESSAGE_CATEGORY = "msg_inbox_category"
            const val KEY_TITLE = "title"
        }

        object Event {
            const val EVENT_NOTIFICATION_CLICK = "notification_click"
            const val EVENT_NOTIFICATION_RECEIVE = "notification_receive_custom"
            const val EVENT_NOTIFICATION_FOREGROUND = "notification_foreground_custom"
            const val EVENT_NOTIFICATION_DISMISS = "notification_dismiss_custom"
            const val EVENT_INBOX_MESSAGE_CLICK = "inbox_message_click"
            const val EVENT_INBOX_MESSAGE_CLICK_DEL = "inbox_message_delete"
            const val EVENT_INBOX_MESSAGE_CLICK_LINK = "inbox_message_link_click"
        }

        object LinkDesc {
            const val LINK_DESC_HEADER_NOTIFICATION = "user_inbox"
        }

        object LinkType {
            const val LINK_TYPE_HEADER_NOTIFICATION = "header"
        }
    }

    class OnBoarding {
        object Event {
            const val EVENT_SELECT_CONTENT = "select_content"
        }
    }

    class PrivilegeAndMerchant {
        object ScreenName {
            const val SCREEN_NAME_PRIVILEGE_BUDGET_SAVE = "budget save"
            const val SCREEN_NAME_PRIVILEGE_TAB_DISCOVER = "rewards > discover"
            const val SCREEN_NAME_PRIVILEGE_TAB_FREE_GIFT = "rewards > free"
            const val SCREEN_NAME_MERCHANT_DETAIL = "Merchant Detail"
            const val SCREEN_NAME_PRIVILEGE_DETAIL = "Privilege Detail"
            const val SCREEN_NAME_PRIVILEGE_FOR_YOU = "for you"
            const val SCREEN_NAME_PRIVILEGE_MERCHANT_BY_CATEGORY =
                "privilege > merchant by category"
            const val SCREEN_NAME_PRIVILEGE_NEAR_MY_PLACE = "near my place"
            const val SCREEN_NAME_PRIVILEGE_FREE = "privilege > free"
            const val SCREEN_NAME_PRIVILEGE_FREE_GIFT_DETAIL = "free gift detail"
            const val SCREEN_NAME_PRIVILEGE_FREE_GIFT_ANNOUNCEMENT_LIST =
                "free gift announcement list"
            const val SCREEN_NAME_PRIVILEGE_FREE_GIFT_ANNOUNCEMENT_DETAIL =
                "free gift announcement detail"
            const val SCREEN_NAME_REWARD_SEE_MORE = "rewards -"
            const val SCREEN_NAME_SEVEN_ELEVEN_COUPONS_DETAIL = "7-Eleven Coupon Detail"
            const val SCREEN_NAME_SEVEN_ELEVEN_FREE = "free at 7-eleven"
            const val SCREEN_NAME_SEVEN_ELEVEN_MY_COUPONS = "my 7-eleven coupons"
            const val SCREEN_NAME_PRIVILEGE_ALL_MERCHANT = "Privilege - All Merchants"
            const val SCREEN_NAME_THEMATIC = "thematic > "
            const val SCREEN_NAME_TRUE_CARD = "TrueIDTopBar - True card"
            const val SCREEN_NAME_TRUE_POINT = "truepoint"
            const val SCREEN_NAME_TRUE_POINT_SEE_MORE = "truepoint - "
            const val SCREEN_NAME_L1_PRIVILEGE = "privilege"
            const val SCREEN_NAME_TRUE_BONUS_DETAIL = "truebonus detail"
        }

        object ShelfName {
            const val SHELF_NAME_TODAY_RECOMMEND = "Today's recommendation!"
        }

        object Event {
            const val EVENT_BROWSE_PRIVILEGES = "Browse privileges"
            const val EVENT_REDEEM = "redeem"
            const val EVENT_REDEEM_PRIVILEGE_ATTEMPT = "redeem_privilege_attempt"
            const val EVENT_REDEEM_TRUEPOINT_ATTEMPT = "redeem_truepoint_attempt"
            const val EVENT_REDEEM_PRIVILEGE_SUCCESS = "redeem_privilege_success"
            const val EVENT_REDEEM_TRUEPOINT_SUCCESS = "redeem_truepoint_success"
            const val EVENT_REDEEM_PRIVILEGE_FAIL = "redeem_privilege_fail"
            const val EVENT_REDEEM_TRUEPOINT_FAIL = "redeem_truepoint_fail"
            const val EVENT_REDEEM_FREE_GIFT_ATTEMPT = "redeem_freegift_attempt"
            const val EVENT_REDEEM_FREE_GIFT_SUCCESS = "redeem_freegift_success"
            const val EVENT_REDEEM_FREE_GIFT_FAIL = "redeem_freegift_fail"
            const val EVENT_REDEEM_7ELEVEN_ATTEMPT = "redeem_7eleven_attempt"
            const val EVENT_REDEEM_7ELEVEN_SUCCESS = "redeem_7eleven_success"
            const val EVENT_REDEEM_7ELEVEN_FAIL = "redeem_7eleven_fail"
            const val EVENT_REDEEM_TRUEBONUS_ATTEMPT = "redeem_truebonus_attempt"
            const val EVENT_REDEEM_TRUEBONUS_SUCCESS = "redeem_truebonus_success"
            const val EVENT_REDEEM_TRUEBONUS_FAIL = "redeem_truebonus_fail"
        }

        object Category {
            const val CATEGORY_TRUE_POINT = "TruePoint"
            const val CATEGORY_PRIVILEGE = "Privilege"
            const val CATEGORY_SEVEN_ELEVEN_COUPON = "7-Eleven Coupon"
        }

        object LinkType {
            const val LINK_TYPE_BUDGET_SAVE = "budget_save"
            const val LINK_TYPE_BUTTON = "button"
            const val LINK_TYPE_CARD_POINT = "card&point"
            const val LINK_TYPE_FILTER_BOTTOM_SHEET = "bottom_sheet"
            const val LINK_TYPE_FILTER_AND_SORT = "filter-and-sort"
            const val LINK_TYPE_TOP_NAV_LV1 = "top-nav-lv1"
            const val LINK_TYPE_TURE_POINT = "truepoint"
            const val LINK_TYPE_SEE_MORE = "see_more"
            const val LINK_TYPE_SHOW_CARD = "show_card"
            const val LINK_TYPE_THEMATIC_FILTER = "thematic_filter"
        }

        object LinkDesc {
            const val LINK_DESC_SEE_MORE = "see_more"
            const val LINK_DESC_SHOW_LESS = "show_less"
            const val LINK_TYPE_BUDGET_SAVE_CATEGORY = "budget-save-category"
            const val LINK_TYPE_BUDGET_SAVE_ME_PAGE = "budget save in me page"
            const val LINK_TYPE_BUDGET_SAVE_MY_PRIVILEGE = "budget save in my privileges page"
            const val LINK_DESC_SHOW_QR = "show_qr"
            const val LINK_DESC_SCAN = "scan"
            const val LINK_DESC_USE_CODE = "use_code"
            const val LINK_DESC_CARD_INFO = "card_info"
        }

        object Key {
            const val KEY_BUSINESS_MODEL = "business_model"
            const val KEY_CATEGORY = "category"
            const val KEY_CMS_ID = "cms_id"
            const val KEY_CONTENT_TYPE = "content_type"
            const val KEY_ERROR_CODE = "error_code"
            const val KEY_ERROR_DESC = "error_desc"
            const val KEY_ITEM_INDEX = "item_index"
            const val KEY_REDEEM_POINT = "redeem_point"
            const val KEY_SHELF_CODE = "shelf_code"
            const val KEY_SHELF_NAME = "shelf_name"
            const val KEY_SHELF_INDEX = "shelf_index"
            const val KEY_TITLE = "title"
        }
    }

    class Payment {
        object ScreenName {
            const val ALACARTE_TV_PACKAGE_DETAIL = "TV Alacarte - Package detail"
            const val MEASUREMENT_WAY_TO_WATCH_TV_PACKAGE_LIST = "Ways to watch"
            const val MEASUREMENT_ALACARTE_PAYMENT_CHANNEL = "Movie Alacarte - Payment channel"
            const val MEASUREMENT_ALACARTE_TV_PAYMENT_CHANNEL = "TV Alacarte - Payment channel"
            const val MEASUREMENT_TRUEVISIONS_NOW_PACKAGES = "truevisions now packages"
            const val SCREEN_NAME_PACKAGE_RENTAL = "package rental"
        }

        object Action {
            const val MEASUREMENT_ALACARTE_BUY_ITEM = "Buy Item"
        }

        object Event {
            const val EVENT_PURCHASE = "purchase"
        }

        object Key {
            const val KEY_METHOD = "method"
            const val KEY_TRANSACTION_ID = "transaction_id"
            const val KEY_VALUE = "value"
            const val KEY_CURRENCY = "currency"
        }

        object Category {
            const val CATEGORY_TV = "tv"
            const val CATEGORY_THB = "THB"
            const val PAYMENT_INTERACTION = "Payment Interaction"
        }
    }

    class MeProfile {
        object ScreenClass {
            const val SCREEN_CLASS_PROFILE_CONTACT_FRAGMENT = "ProfileContactFragment"
            const val MEASUREMENT_ME_ACCOUNT = "me > account"
            const val MEASUREMENT_ME_CONTACT = "me > contacts"
            const val MEASUREMENT_ME_LIBRARY = "me > library"
            const val MEASUREMENT_ME_PACKAGE = "me > package"
            const val MEASUREMENT_ME_TAB = "me-tab"
        }

        object LinkDesc {
            const val MEASUREMENT_ME_LINK_DESC_MY_LIBRARY = "library"
            const val MEASUREMENT_ME_LINK_DESC_CONTACTS = "contacts"
            const val MEASUREMENT_ME_LINK_DESC_PACKAGE = "package"
            const val MEASUREMENT_ME_LINK_DESC_ACCOUNT = "account"
            const val LINK_DESC_HEADER_AVATAR = "avatar"
        }

        object LinkType {
            const val MEASUREMENT_ME_LINK_TYPE_ACCOUNT_SETTING = "account-settings"
            const val LINK_TYPE_HEADER_AVATAR = "header"
        }

        object ShelfName {
            const val SHELF_NAME_UGC_TAB = "ugc_tab"
        }
    }

    class Chat {
        object Key {
            const val ITEM_ID = "item_id"
            const val ITEM_NAME = "item_name"
            const val REACTION = "reaction"
            const val BADGE_TYPE = "badge_type"
            const val EMOJI_TYPE = "emoji"
            const val LINK_TYPE = "link_type"
            const val LINK_DESC = "link_desc"
        }

        object Event {
            const val SELECT_CONTENT = "select_content"
            const val VIEW_ITEM = "view_item"
            const val EDIT_PROFILE_NAME = "edit_profile_name"
            const val EVENT_CLICK = "click"
            const val EVENT_EMOJI = "use_emoji"
            const val ERROR_SENT_MESSAGE = "livechat_sendMessage_errors"
            const val EVENT_SHOW_REACTION = "show_reaction"
            const val EVENT_ADD_REACTION = "add_reaction"
            const val EVENT_VIEW_BADGE = "viewbadge_livechat"
            const val EVENT_START_REPLY = "start_reply"
            const val EVENT_REPLY_SUCCESS = "reply_success"
            const val EVENT_ANONYMOUS_USER = "anonymous_viewlivechat"
            const val EVENT_ANONYMOUS_START_LIVE_CHAT = "anonymous_startlivechat"
            const val EVENT_CHAT_SIGNUP_COMPLETE = "chat_signup_complete"
            const val EVENT_PHONE_LINK_CALL = "phone_link_call"
        }

        object ReactionType {
            const val reactionHeart = "heart"
            const val reactionSmile = "smile"
            const val reactionLaugh = "laugh"
            const val reactionLove = "love"
            const val reactionFire = "fire"
        }

        object ItemId {
            const val VIEW = "View"
        }

        object ItemName {
            const val CHAT_VIEW = "Chat View"
            const val CONTACT_SELECTION = "Contact selection"
            const val GROUP_INFO = "Group Info"
            const val TERMS_AND_CONDITION = "Terms and condition"
        }

        object LinkDesc {
            const val LINK_DESC_INVITE_CHAT_1 = "invite to chat - 1"
            const val LINK_DESC_INVITE_CHAT_2 = "invite to chat - 2"
            const val LINK_DESC_CHANGE_PROFILE_NAME = "change profile name from live chat"
            const val LINK_DESC_SAVE_PROFILE_NAME = "save profile name on me page"
            const val LINK_DESC_REPORT_LIVE_CHAT = "report live chat message"
            const val LINK_DESC_CONFIRM_REPORT = "confirm report live chat message"
            const val LINK_DESC_DISABLE_AUTO_DOWNLOAD = "disable"
            const val LINK_DESC_ENABLE_AUTO_DOWNLOAD = "enable"
            const val LINK_DESC_DETAIL = "view_profile_detail"
            const val LINK_DESC_DETAIL_AVATAR = "view_profile_detail_avatar"
            const val LINK_DESC_DETAIL_NAME = "view_profile_detail_name"
            const val LINK_DESC_RECENT_CHAT = "chat"
        }

        object LinkType {
            const val LINK_TYPE_BUTTON = "button"
            const val LINK_TYPE_SHARE = "share"
            const val LINK_TYPE_PROFILE = "profile"
            const val LINK_TYPE_TOGGLE_PRELOAD_IMAGE = "toggle preload image function"
            const val LINK_TYPE_RECENT_CHAT = "live_chat"
        }
    }

    class Comment {
        object Event {
            const val ADD_COMMENT = "add_comment"
            const val ANONYMOUS_STARTCOMMENT = "anonymous_startcomment"
            const val ANONYMOUS_VIEWCOMMENT = "anonymous_viewcomment"
            const val COMMENT_ICON_VIEWALL = "comment_icon_viewall"
            const val COMMENT_VIEWALL = "comment_viewall"
            const val SEND_COMMENT = "send_comment"
            const val COMMENT_SEND = "comment_send"
            const val COMMENT_REPORT = "comment_report"
        }

        object Key {
            const val KEY_REFERENCE1 = "reference1"
            const val KEY_REFERENCE2 = "reference2"
        }

        object LinkType {
            const val LINK_TYPE_ICON = "icon"
        }
    }

    class Community {
        object ScreenName {
            const val MEASUREMENT_COMMUNITY_INTRO_PAGE = "community > intro_page"
            const val MEASUREMENT_COMMUNITY_NEW_SFEED = "community > newsfeed"
            const val MEASUREMENT_COMMUNITY_EXPLOR = "community > explore"
            const val MEASUREMENT_COMMUNITY_CATEGORY_LIST = "community category list"
            const val MEASUREMENT_COMMUNITY_CATEGORY_NAME = "community %s  category"
            const val MEASUREMENT_COMMUNITY_PAGE_TIMELINE = "community page > timeline"
            const val MEASUREMENT_COMMUNITY_USER_PROFILE_TIMELINE =
                "community user profile > timeline"
        }

        object Event {
            const val EVENT_TOPBAR_SEARCH = "community_topbar_search"
            const val EVENT_TOPBAR_PROFILE = "community_topbar_profile"
            const val EVENT_EXPLORE_BUTTON = "community_explore_button"
            const val EVENT_CREATE_POST = "community_createpost_button"
            const val EVENT_MY_COMMUNITY_SELECTED = "community_mycommunity_section"
            const val EVENT_COMMUNITY_JOIN = "community_join_button"
            const val EVENT_COMMUNITY_DETAIL_SELECTED = "community_communitydetail_section"
            const val EVENT_COMMUNITY_POST_SUCCESS = "community_post_success"
            const val EVENT_CATEGORY_BUTTON = "community_category_button"
            const val EVENT_ALL_CATEGORY_BUTTON = "community_allcategory_button"
            const val EVENT_RECOMMEND_SECTION = "community_recommend_section"
            const val EVENT_EDIT_PROFILE_BUTTON = "community_editprofile_button"
            const val EVENT_SAVE_PROFILE_BUTTON = "community_saveeditprofile_button"
            const val EVENT_INTRO_LOGIN = "community_intro_login"
            const val EVENT_SOCIAL_SHARE = "social_share"
            const val EVENT_SELECT_CONTENT = "select_content"
            const val EVENT_JOIN = "join"
            const val EVENT_COMMUNITY = "community"
            const val EVENT_COMMUNITY_SHORTCUT = "community_shortcut"
        }

        object Key {
            const val KEY_TITLE = "title"
            const val KEY_IS_OFFICIAL = "is_official"
            const val KEY_IS_PUBLIC = "is_public"
            const val KEY_CATEGORY = "category"
            const val KEY_INDEX = "index"
        }

        object LinkDesc {
            const val LINK_DESC_GO_TO_LOGIN = "go to login page"
            const val LINK_DESC_NEWSFEED = "newsfeed"
            const val LINK_DESC_EXPLORE = "explore"
            const val LINK_DESC_EXPLORE_SEEMORE = "categories"
            const val LINK_DESC_ICON_HEADER_COMMUNITY = "community"
            const val LINK_DESC_RECENTCHAT = "recentchat"
            const val LINK_DESC_TEXT = "text"
            const val LINK_DESC_IMAGE = "image"
            const val LINK_DESC_CAMERA = "camera"
            const val LINK_DESC_VIDEO = "video"
            const val LINK_DESC_POLL = "poll"
            const val LINK_DESC_AVATAR = "avatar"
        }

        object LinkType {
            const val LINK_TYPE_INTRODUCE_PAGE = "introduce feature page"
            const val LINK_TYPE_EXPLORE = "community-tab"
            const val LINK_TYPE_EXPLORE_SEEMORE = "see-more"
            const val LINK_TYPE_ICON_HEADER_COMMUNITY = "header"
            const val LINK_TYPE_COMMUNITY_SHORTCUT = "community_shortcut"
            const val LINK_TYPE_SFV = "sfv"
        }
    }

    class Player {
        object ScreenName {
            const val SCREEN_NAME_TV_PLAYER = "tv player"
            const val SCREEN_NAME_MOVIE_PLAYER = "movie player"
            const val SCREEN_NAME_SERIES_PLAYER = "series player"
            const val SCREEN_NAME_SPORT_PLAYER = "sports clip player"
            const val SCREEN_NAME_TV_LIVE = "Tv - Live"
            const val SCREEN_NAME_TV_LIVE_ALL_CHANNEL = "Tv - Live All Channel"
            const val SCREEN_NAME_TV_TRUE_VISION = "Tv - TrueVisions"
            const val SCREEN_NAME_L1_WATCH = "watch"
            const val SCREEN_NAME_CLIP_PLAYER = "clip player"
            const val SCREEN_NAME_MOVIE_DETAIL = "movie detail"
            const val SCREEN_NAME_SERIES_DETAIL = "series detail"
        }

        object Event {
            const val EVENT_CATCH_UP = "catch_up"
            const val EVENT_EARN_POINT = "earn_point"
            const val EVENT_EARN_POINT_ERROR = "error_not_show"
            const val EVENT_PAUSE_MOVIE = "pause_movie"
            const val EVENT_PAUSE_SERIES = "pause_series"
            const val EVENT_PAUSE_TV = "pause_tv"
            const val EVENT_PLAY_TV_INITIAL = "play_tv_initial"
            const val EVENT_PLAY_TV = "play_tv"
            const val EVENT_PLAY_SERIES = "play_series"
            const val EVENT_PLAY_MOVIE = "play_movie"
            const val EVENT_PLAY_MOVIE_INITIAL = "play_movie_initial"
            const val EVENT_PLAY_SERIES_INITIAL = "play_series_initial"
            const val EVENT_PLAY_AD_PREROLL = "play_ad_preroll"
            const val EVENT_PLAY_AD_MIDROLL = "play_ad_midroll"
            const val EVENT_PLAYER_PAUSED = "Player Paused"
            const val EVENT_SEEK_FORWARD = "seek_forward"
            const val EVENT_SEEK_BACKWARD = "seek_backward"
            const val EVENT_SWITCH_AUDIO = "Switch Audio"
            const val EVENT_UNLOAD_MOVIE = "unload_movie"
            const val EVENT_UNLOAD_SERIES = "unload_series"
            const val EVENT_UNLOAD_TV = "unload_tv"
            const val EVENT_ADD_FAVORITE = "add_favorite"
            const val EVENT_REMOVE_FAVORITE = "remove_favorite"
            const val EVENT_CLICK = "click"
            const val EVENT_MORE_OPTIONS = "more-options"
            const val EVENT_CAST_CONTENT = "cast_content"
            const val EVENT_NAME_PIP_START = "pip_start_app_closed"
            const val EVENT_NAME_PIP_CLOSE = "pip_close"
            const val EVENT_SKIP_INTRO = "skip_intro"
            const val EVENT_PLAY_NEXT = "play_next"
            const val EVENT_TRAILER = "trailer"
            const val EVENT_PREVIEW = "preview"
            const val EVENT_PLAY_VIDEO_INITIAL = "play_video_initial"
            const val EVENT_PLAY_VIDEO = "play_video"
            const val EVENT_PAUSE_VIDEO = "pause_video"
            const val EVENT_UNLOAD_VIDEO = "unload_video"
            const val EVENT_PLAY_CLIP_INITIAL = "play_clip_initial"
            const val EVENT_PLAY_CLIP = "play_clip"
            const val EVENT_PAUSE_CLIP = "pause_clip"
            const val EVENT_UNLOAD_CLIP = "unload_clip"

            const val EVENT_PLAY_SFV_INITIAL = "play_sfv_initial"
            const val EVENT_PLAY_SFV = "play_sfv"
            const val EVENT_PAUSE_SFV = "pause_sfv"
            const val EVENT_UNLOAD_SFV = "unload_sfv"

            const val EVENT_REPORT_CONTENT = "report_content"
        }

        object Key {
            const val KEY_ARTICLE_CATEGORY = "article_category"
            const val KEY_CATEGORY = "category"
            const val KEY_CHANNEL_CODE = "channel_code"
            const val KEY_CHANNEL_NAME_TH = "channel_name_th"
            const val KEY_CHANNEL_NAME_EN = "channel_name_en"
            const val KEY_CONTENT_DURATION_MS = "content_duration_ms"
            const val KEY_DURATION_MS = "duration_ms"
            const val KEY_EPISODE_ID = "episode_id"
            const val KEY_EPISODE_NAME = "episode_name"
            const val KEY_GENRE = "genre"
            const val KEY_IS_TRAILER = "is_trailer"
            const val KEY_IS_PREVIEW = "is_preview"
            const val KEY_LINK_DESC = "link_desc"
            const val KEY_LINK_TYPE = "link_type"
            const val KEY_PLAY_SESSION_ID = "play_session_id"
            const val KEY_PROGRAM_NAME = "program_name"
            const val KEY_PROGRAM_ID = "program_id"
            const val KEY_SEASON_ID = "season_id"
            const val KEY_SEASON_NAME = "season_name"
            const val KEY_SUB_EPISODE_ID = "sub_episode_id"
            const val KEY_SUB_EPISODE_NAME = "sub_episode_name"
            const val KEY_TITLE_ID = "title_id"
            const val KEY_TITLE_EN = "title_en"
            const val KEY_PLAY_TYPE = "play_type"
        }

        object Category {
            const val CATEGORY_TVOD = "tvod"
            const val CATEGORY_SVOD = "svod"
            const val CATEGORY_CTV = "c-tv"
            const val CATEGORY_MOVIE = "movie"
            const val CATEGORY_PLAYER_INTERACTION = "Player Interaction"
            const val CATEGORY_SERIES = "series"
            const val CATEGORY_TV = "tv"
            const val CATEGORY_TV_CATEGORY = "tv-category"
            const val CATEGORY_TV_CHANNEL_ALL = "TV Channels - All"
            const val CATEGORY_TV_CHANNEL_TRUE_VISIONS = "TV Channels - TrueVisions"
        }

        object LinkDesc {
            const val LINK_DESC_ADD_MY_LIST = "click-my-list-vod"
            const val LINK_DESC_CONTINUE_WATCHING = "continue-watch-iqiyi-popup"
            const val LINK_DESC_CATEGORY_FILTER = "click-filter-vod-category"
            const val LINK_DESC_CATEGORY_SORTING = "click-sorting-vod-category"
            const val LINK_DESC_DISMISS_POPUP = "dismiss-iqiyi-popup"
            const val LINK_DESC_LEARN_MORE_INFO_BUTTON = "learn-more-info-button "
            const val LINK_DESC_LEARN_MORE_TRUE_ID_PLUS_BANNER_MOVIE =
                "learn-more-trueidplus-banner-movies"
            const val LINK_DESC_LEARN_MORE_TRUE_ID_PLUS_BANNER_SERIES =
                "learn-more-trueidplus-banner-series"
            const val LINK_DESC_LIKE = "click-like-vod"
            const val LINK_DESC_LIKE_SPORT_PLAYER = "click-like-sport-player"
            const val LINK_DESC_TRUE_ID_PLUS_BANNER_EXPLORE = "trueidplusbanner-explore"
            const val LINK_DESC_WATCH_NOW_BIG_POSTER = "watch-now-big-poster"
            const val LINK_DESC_LIVE_TV_MY_LIST = "click-my-list-livetv"
            const val LINK_DESC_LIVE_TV_SHARE = "click-share-livetv"
            const val LINK_DESC_LIVE_TV_MORE = "click-more-livetv"
            const val LINK_DESC_LIVE_TV_CATEGORY_SORTING = "click-sorting-livetv-category"
            const val LINK_DESC_READ_MORE_SYNOPSIS = "read-more-synopsis"
            const val LINK_DESC_SHARE = "click-share-vod"
            const val LINK_DESC_SHARE_VOD_PLAYER = "click-share-vod-player"
            const val LINK_DESC_SHARE_SPORT_PLAYER = "click-share-sport-player"
            const val LINK_DESC_REPORT = "report"
        }

        object LinkType {
            const val LINK_TYPE_BIG_POSTER_BANNER = "big-poster-banner"
            const val LINK_TYPE_CATE_TAB = "cate_tab"
            const val LINK_TYPE_LEARN_MORE = "learn-more"
            const val LINK_TYPE_PARTNERSHIP_POPUP = "partnership-popup"
            const val LINK_TYPE_SYNOPSIS_TITLE_PAGE = "synopsis-title-page"
            const val LINK_TYPE_TOP_NAV = "top_nav"
            const val LINK_TYPE__TRUE_ID_PLUS_BANNER_EXPLORE_BUTTON = "trueidplus-explore-button"
            const val LINK_TYPE_USER_ACTION_CATEGORY_PAGE = "users-action-vod-category-page"
            const val LINK_TYPE_USER_ACTION_LIVE_TV_PLAYER_PAGE = "users-action-livetv-player-page"
            const val LINK_TYPE_USER_ACTION_LIVE_TV_CHANNEL_SELECTION_PAGE =
                "users-action-livetv-channels-selection-page"
            const val LINK_TYPE_USER_ACTION_PLAYER_PAGE = "users-action-player-page"
            const val LINK_TYPE_USER_ACTION_SPORT_PLAYER_PAGE = "users-action-sport-player-page"
            const val LINK_TYPE_USER_ACTION_TITLE_PAGE = "users-action-title-page"
            const val LINK_TYPE_SFV_BOTTOM_SHEET = "sfv_bottom_sheet"
        }
    }

    class Calling {
        object Event {
            const val EVENT_NAME_INVITE_FRIEND = "invite_friend"
            const val EVENT_NAME_PORTSIP_ERROR = "call_error_freeswitch"
            const val EVENT_NAME_CALL_ERROR = "call_error"
            const val EVENT_NAME_ERROR_SHOW = "error_show"
            const val EVENT_NAME_CALL_BEGIN = "call_begin"
            const val EVENT_NAME_CALL_CALLING = "call_calling"
            const val EVENT_NAME_CALL_COMPLETE = "call_complete"
        }

        object LinkDesc {
            const val CONTACT_PAGE_INVITE_FRIEND = "invite friend text contact page"
            const val LINK_DESC_HEADER_CALLING = "communicator"
        }

        object LinkType {
            const val TEXT = "text"
            const val LINK_TYPE_HEADER_CALLING = "header"
            const val LINK_TYPE_CALL_FEEDBACK = "call_feedback_score"
            const val LINK_TYPE_CALL_FEEDBACK_COMMENT = "call_feedback_comment"
        }
    }

    object Contact {

        object ErrorType {
            const val ERROR_TYPE_COMMUNICATOR_CALL = "communicator-call"
        }

        object Event {
            const val EVENT_NAME_NCC_CONTACT_LIST = "tab_contact_list"
            const val EVENT_NAME_NCC_REDEEM_ERROR = "call_redeem_error"
            const val EVENT_NAME_NCC_REDEEM_COMPLETE = "call_redeem_complete"
            const val EVENT_NAME_NCC_REDEEM_BEGIN = "call_redeem_begin"
        }

        object Key {
            const val KEY_ERROR_TYPE = "error_type"
            const val KEY_ERROR_MSG_EN = "error_msg_en"
            const val KEY_CAMPAIGN = "campaign"
        }

        object LinkDesc {
            const val LINK_DESC_CHAT_TAB = "chat tab"
            const val LINK_DESC_ADD_CONTACT_BEGIN = "add contact begin"
            const val LINK_DESC_ACTION_NAME = "action_name"
            const val LINK_DESC_ATTACHMENT_BEGIN = "attachment begin"
            const val LINK_DESC_CONTACT_MOBILE_NUMBER = "contact > mobile number"
            const val LINK_DESC_CONTACT_CHAT = "contact > chat"
            const val LINK_DESC_CONTACT_INVITE_TO_CHAT = "contact > invite to chat"
            const val LINK_DESC_CONTACT_AUDIO_CALL = "contact > audio call"
            const val LINK_DESC_CONTACT_VIDEO_CALL = "contact > video call"
            const val LINK_DESC_CONTACT_INVITE_TO_AUDIO_CALL = "contact > invite to audio call"
            const val LINK_DESC_CONTACT_INVITE_TO_VIDEO_CALL = "contact > invite to video call"
            const val LINK_DESC_CONTACT_AUDIO_CALL_BEGIN = "audio call begin"
            const val LINK_DESC_CONTACT_VIDEO_CALL_BEGIN = "video call begin"
            const val LINK_DESC_ENABLE_CONTACTS = "enable contacts"
            const val LINK_DESC_DIAL_PAD_BEGIN = "dial pad begin"
            const val LINK_DESC_INTRODUCE_TO_LOGIN = "go to login page"
            const val LINK_DESC_INTRODUCE_TO_DISCOVERY = "back to discovery page"
            const val LINK_DESC_TAB_ON_CONTACT_LIST = "tab on contact list"
            const val LINK_DESC_REDEEM_BUTTON = "redeem button"
            const val LINK_DESC_REDEEM_BUTTON_POPUP = "redeem button - pop up"
            const val LINK_DESC_STEP_1_CREATE_GROUP = "step1 create group"
            const val LINK_DESC_STEP_2_CREATE_GROUP = "step2 create group"
            const val LINK_DESC_STEP_3_CREATE_GROUP = "step3 create group"
            const val LINK_DESC_STEP_4_CREATE_GROUP = "step4 create group"
            const val LINK_DESC_STEP_5_CREATE_GROUP = "step5 create group"
            const val LINK_DESC_TOP_BANNER = "top banner in friend tab"
            const val LINK_DESC_VOICE_MESSAGE = "voice message begin"
            const val LINK_DESC_VOICE_SENT = "voice message sent"
            const val LINK_DESC_FREE_CALL_CHAT = "freecallandchat"
            const val LINK_DESC_MY_SERVICE = "myservice"
        }

        object LinkType {
            const val LINK_TYPE_TAB = "tab"
            const val LINK_TYPE_BANNER = "banner"
            const val LINK_TYPE_BUTTON = "button"
            const val LINK_TYPE_BUTTON_ON_CONTACT_TAB = "button on contact tab"
            const val LINK_TYPE_BUTTON_ON_TOP_BAR = "button on top bar"
            const val LINK_TYPE_CONTACT_LIST = "contact list"
            const val LINK_TYPE_INTRODUCE = "introduce feature page"
            const val LINK_TYPE_CONTACT_MENU = "contact menu"
            const val LINK_TYPE_MENU = "menu"
            const val LINK_TYPE_RIGHT_NAV = "top right nav"
            const val LINK_TYPE_CAPSULE_SEC = "capsule_sec"
        }
    }

    class Recent {
        object Event {
            const val TAB_RECENT_CALL_LIST = "tab_recent_call_list"
            const val TAB_VIEW_MORE = "tab_view_more"
            const val TAB_VIEW_LESS = "tab_view_less"
        }

        object LinkDesc {
            const val LINK_DESC_START_CALL = "startacall"
            const val TAB_ON_VIEW_MORE_IN_RECENT_CALL_LIST = "tab on view more in recent call list"
            const val TAB_ON_VIEW_LESS_IN_RECENT_CALL_LIST = "tab on view less in recent call list"
        }

        object LinkType {
            const val LINK_TYPE_CALL_AND_CHAT = "callandchat"
        }
    }

    class WebRTCCall {
        object Event {
            const val EVENT_COMMUNICATOR_ERROR = "communicator_error"
            const val EVENT_CHAT_MESSAGE_SENT = "chat_message_sent"
        }

        object Key {
            const val KEY_TRANSACTION_ID = "transaction_id"
            const val KEY_ERROR_TYPE = "error_type"
            const val KEY_ERROR_MSG_EN = "error_msg_en"
        }
    }

    object SpecialPopup {
        const val VIEW_PROMOTION = "view_promotion"
        const val BOOSTUP_POPUP = "boostup_popup"
        const val REDEEM_BOOSTUP = "redeem_boostup"
        const val DISMISS_BOOSTUP = "dismiss_boostup"
    }

    class Sport {
        object ScreenName {
            const val SCREEN_NAME_SHARE_MATCH = "Share Match"
            const val SCREEN_NAME_SPORT_MATCH_PLAYER = "sports match player"
            const val SCREEN_NAME_SPORT_PLAYER = "Sport Player"
            const val SCREEN_NAME_SPORT_TEAM = "Sport Team"
            const val SCREEN_NAME_SPORT_TEAM_CLIPS = "Clips"
            const val SCREEN_NAME_SPORT_TEAM_FIXTURES = "Fixtures"
            const val SCREEN_NAME_SPORT_TEAM_NEWS = "Article"
            const val SCREEN_NAME_SPORT_TEAM_OVERVIEW = "Overview"
            const val SCREEN_NAME_SPORT_TEAM_PLAYER = "Squad"
            const val SCREEN_NAME_SPORT_TEAM_PLAYER_DETAIL = "Squad Player"
            const val SCREEN_NAME_SPORT_TEAM_RESULTS = "Results"
            const val SCREEN_NAME_SPORT_TEAM_STATS = "Stats"
            const val SCREEN_NAME_SPORT_TEAM_TABLE_SCORE = "Table Score"
            const val SCREEN_NAME_SPORT_SEEMORE = "sports > "
            const val SCREEN_NAME_SPORT_TEAM_DETAIL = "team detail"
        }

        object Action {
            const val ACTION_PLAY_CLIP = "Play Clip"
            const val ACTION_PLAY_TV = "Play TV"
        }

        object Event {
            const val EVENT_PAUSE_CLIP = "pause_clip"
            const val EVENT_PLAY_CLIP = "play_clip"
            const val EVENT_PLAY_CLIP_INITIAL = "play_clip_initial"
            const val EVENT_UNLOAD_CLIP = "unload_clip"
        }

        object Key {
            const val EVENT_LEAGUE_CODE = "league_code"
        }
    }

    object LimitDevice {
        object Category {
            const val DEVICE_LIMITATION = "Device Limit"
        }

        object Action {
            const val MEASUREMENT_DEVICE_LIMIT_SIGNUP = "Signup"
            const val MEASUREMENT_DEVICE_LIMIT_LOGIN = "Login"
        }
    }

    object LiveChat {
        object ScreenName {
            const val MEASUREMENT_CHAT = "Player - Live Chat"
        }

        object Category {
            const val CHAT_INTERACTION = "Chat Interaction"
        }

        object Event {
            const val OPEN_LIVE_CHAT = "open_livechat"
            const val CLOSE_LIVE_CHAT = "close_livechat"
            const val EVENT_JOIN_LIVECHAT = "join_livechat"
            const val EVENT_SEND_MESSAGE_LIVECHAT = "sendMessage_livechat"
        }

        object Action {
            const val MEASUREMENT_CLOSE_CHAT_WINDOW = "Close Chat Window"
            const val MEASUREMENT_JOIN_CHAT = "Join Chat"
            const val MEASUREMENT_LEAVE_CHAT = "Leave Chat"
            const val MEASUREMENT_OPEN_CHAT_WINDOW = "Open Chat Window"
            const val MEASUREMENT_SEND_MESSAGE_CHAT = "Send Message"
        }

        object Key {
            const val KEY_CHANNEL_CMS_ID = "channel_cms_id"
            const val KEY_CHANNEL_NAME = "channel_name"
            const val KEY_PROGRAM_ID = "program_id"
            const val KEY_PROGRAM_NAME = "program_name"
        }
    }

    object Article {
        object ScreenName {
            const val MEASUREMENT_ARTICLE = " - Article"
            const val MEASUREMENT_ARTICLE_LIFE_STYLE = "lifestyle > %s"
            const val MEASUREMENT_ARTICLE_SCREEN = "%s article"
        }

        object Category {
            const val MEASUREMENT_ARTICLE_INTERACTION = "Article Interaction"
        }

        object Action {
            const val MEASUREMENT_READ = "Read"
        }
    }

    object Read {
        object LinkType {
            const val TOP_NAV = "top_nav"
            const val CATE_TAB = "cate_tab"
            const val SEE_MORE = "see_more"
        }

        object ScreenName {
            const val SCREEN_NAME_L1_READ = "read"
        }
    }

    object Music {
        object Key {
            const val MEASUREMENT_MUSIC_TITLE = "title"
            const val MEASUREMENT_MUSIC_ALBUM = "album"
            const val MEASUREMENT_MUSIC_ARTIST = "artist"
            const val MEASUREMENT_MUSIC_PLAYLIST = "playlist"
            const val MEASUREMENT_MUSIC_OWNER = "owner"
            const val MEASUREMENT_MUSIC_LABEL = "label"
            const val MEASUREMENT_MUSIC_GENRE = "genre"
            const val MEASUREMENT_MUSIC_CONTENT_LANGUAGE = "content_language"
            const val MEASUREMENT_MUSIC_MY_PLAYLIST = "playlist"
        }

        object Event {
            const val EVENT_SEARCH_BEGIN = "search_begin"
            const val EVENT_SEARCH_CATEGORY = "search-category"
        }

        object ScreenName {
            const val SCREEN_NAME_LISTEN_ALBUM = "listen > album"
            const val SCREEN_NAME_LISTEN_ARTIST = "listen > artist"
            const val SCREEN_NAME_LISTEN_PLAYLIST_DETAILS = "listen > playlist"
            const val SCREEN_NAME_LISTEN_SEE_MORE = "listen"
            const val SCREEN_NAME_L1_LISTEN = "listen"
        }
    }

    object QRCodeScanner {
        object Event {
            const val EVENT_SCAN_QR_CODE = "scan_qr_code"
        }

        object ScreenName {
            const val SCREEN_NAME_QR_CODE_SCANNER = "qr code scanner"
        }

        object Category {
            const val CATEGORY_GO_TO_CONTENT = "go_to_content"
            const val CATEGORY_GO_TO_PRIVILEGE = "go_to_privilege"
            const val CATEGORY_INVALID = "Invalid"
            const val CATEGORY_LOGIN_BOX = "login_box"
            const val CATEGORY_LOGIN_WEB = "login_web"
            const val CATEGORY_LOGIN_TRUE_WIFI = "login_true_wifi"
            const val CATEGORY_OTHERS = "others"
            const val CATEGORY_PURCHASE_BOX = "purchase_box"
            const val CATEGORY_PURCHASE_VENDING_MACHINE = "purchase_vending_machine"
            const val CATEGORY_POINT_PAY = "pointpay_cash_coupon"
            const val CATEGORY_SIM_ACTIVATION = "sim_activation"
        }
    }

    object TopBar {
        object ScreenName {
            const val SCREEN_NAME_TOP_BAR_MY_BILL = "top bar - my bills"
            const val SCREEN_NAME_TOP_BAR_REDEEM_PRIVILEGE = "top bar - redeem privilege"
            const val SCREEN_NAME_TOP_BAR_EARN_TRUE_POINT = "top bar - earn truepoint"
            const val SCREEN_NAME_TOP_BAR_TRUE_POINT = "top bar - truepoint"
            const val SCREEN_NAME_TOP_BAR_SHELF = "top bar - "
        }
    }

    object TrueVisions {
        object ScreenName {
            var MEASUREMENT_TVS_CONNECT = "Connect TVS"
            var MEASUREMENT_TVS_DISCONNECT = "Disconnect TVS"
        }
    }

    object CustomerScore {
        object Slug {
            const val CUSTOMER_SCORE_SLUG_PROFILE = "profile_truescore"
            const val TRUE_SCORE_SLUG_TODAY = "today_truescore"
        }

        object Key {
            const val CUSTOMER_SCORE_KEY_CUSTOMER_SCORE = "customer_score"
            const val CUSTOMER_SCORE_KEY_SCORES = "scores"
            const val CUSTOMER_SCORE_KEY_CREDIT_LIMIT = "credit_limit"
            const val CUSTOMER_SCORE_KEY_TITLE = "title"
            const val CUSTOMER_SCORE_KEY_ERROR_CODE = "error_code"
            const val CUSTOMER_SCORE_KEY_ERROR_DESC = "error_desc"
        }

        object Action {
            const val CUSTOMER_SCORE_ACTION_NAME = "customer_score"
            const val CUSTOMER_SCORE_ACTION_LEARN_MORE = "learn more"
            const val CUSTOMER_SCORE_ACTION_BENEFITS = "check your benefits"
        }

        object Event {
            const val CUSTOMER_SCORE_EVENT_VIEW = "view_customer_score"
            const val CUSTOMER_SCORE_EVENT_ERROR = "error_not_show"
        }
    }

    class Communicator {

        object ScreenName {
            const val SCREEN_NAME_MY_SERVICES = "my services"
        }

        object Key {
            const val KEY_ERROR_API = "error_api"
            const val KEY_ERROR_CODE_BODY = "error_code_body"
            const val KEY_ERROR_CODE_HEADER = "error_code_header"
        }

        object LinkType {
            const val LINK_TYPE_HEADER_COMMUNICATOR = "header"
            const val LINK_TYPE_CALL_AND_CHAT = "callandchat"
            const val LINK_TYPE_MY_SERVICES = "my_services"
            const val LINK_TYPE_DAILY_HOROSCOPE = "daily_horoscope"
            const val LINK_TYPE_CONTACTS_AMITY = "Header_1:1chat"
        }

        object LinkDesc {
            const val LINK_DESC_COMMUNICATOR = "communicator"
            const val LINK_DESC_CONTACTLIST = "contactlist"
            const val LINK_DESC_SETTING = "setting"
            const val LINK_DESC_STARTACALL = "startacall"
            const val LINK_DESC_RECENTCALL = "recentcall"
            const val LINK_DESC_DIALPAD = "dialpad"
            const val LINK_DESC_STARTCHAT = "startchat"
            const val LINK_DESC_GETSTARTED = "getstarted"
            const val LINK_DESC_SEE_DETAILS = "true_point_see_details"
            const val LINK_DESC_READ_MORE = "read_more"
            const val LINK_DESC_TOP_UP = "top_up"
            const val LINK_DESC_PAY_NOW = "pay_now"
            const val LINK_DESC_SEE_ALL = "see_all"
            const val LINK_DESC_CONTACTS_AMITY = "contacts"
            const val LINK_DESC_VIEW_ALL_SERVICES = "view_all_services"
        }

        object ErrorName {
            const val ERROR_NAME_CALL_CHAT_LOGIN = "TrueID callchat - login"
            const val ERROR_NAME_MIRRORFLY_CALLCHAT_ERROR = "mirrorfly_callchat_error"
        }
    }

    class WebView {
        object Event {
            const val WEBVIEW_EVENT_WEBVIEW_OPEN = "webview_open"
        }

        object LinkType {
            const val LINK_TYPE_WEBVIEW_INTERFACE = "webview_interface"
        }
    }

    class Deeplink {
        object Event {
            const val DEEPLINK_EVENT_DEEP_LINKING_OPEN = "deep_linking_open"
            const val DEEPLINK_EVENT_DEEP_LINKING_ERROR = "deep_linking_error"
        }

        object Title {
            const val DEEPLINK_TITLE =
                "This content is currently not available in your country or removed"
        }
    }

    object Settings {
        const val ERROR_NAME_REFERRAL = "Referral"
    }

    class InlineBanner {
        object Event {
            const val INLINEBANNER_EVENT_CLICK = "select_promotion"
        }

        object Key {
            const val PROMOTION_ID = "promotion_id"
            const val PROMOTION_NAME = "promotion_name"
            const val CATEGORY = "category"
        }
    }

    class Ugc {
        object Event {
            const val FOLLOW_USER_EVENT = "follow_user"
            const val UNFOLLOW_USER_EVENT = "unfollow_user"
            const val VIEW_ITEM = "view_item"
        }

        object Key {
            const val KEY_CREATOR_ID = "creator_id"
            const val KEY_SSOID = "ssoid"
        }
    }

    class Watch {
        object Reaction {
            const val EVENT_LIKE = "like"
            const val EVENT_UNLIKE = "unlike"
        }
    }

    class WatchParty {
        object Page {
            const val BANNER = "Banner"
            const val HOTLIVE = "Hot Live"
            const val ROOM_CREATION = "Room creation"
            const val WATCH_PARTY_ROOM = "Watch party room"
        }
        object Screen {
            const val LIVE_TV = "Live TV (WPT under player)"
            const val WATCH = "Watch"
            const val WATCH_PARTY_FEED = "Watch Party feed"
        }

        object LinkType {
            const val WATCH_PARTY = "watch_party"
            const val WATCH_PARTY_SUBSCRIBE = "watch_party_subscribe"
            const val WATCH_PARTY_ACTION = "watch_action"
        }

        object LinkDesc {
            const val CREATE_ROOM = "create_room"
            const val HOT_LIVE = "hot_live"
            const val COPY_LINK = "copy_link"
            const val BANNER = "banner"
            const val PURCHASE_PACKAGE_CREATE = "purchase_package_create"
            const val PURCHASE_PACKAGE_JOIN = "purchase_package_join"
            const val CANCEL = "cancel"
            const val FOLLOW = "mute/unmute/sound_effect/invite/follow/following"
        }

        object Key {
            const val KEY_PAGE = "page"
            const val KEY_ROOM_ID = "room_id"
            const val KEY_PROGRAM_ID = "program_id"
            const val KEY_PROGRAM_NAME = "program_name"
            const val KEY_MATCH_ID = "match_id"
            const val KEY_LEAGUE_CODE = "league_code"
            const val KEY_MATCH_NAME = "match_name"
            const val KEY_SHELF_NAME = "shelf_name"
            const val KEY_SHELF_CODE = "shelf_code"
            const val KEY_SHELF_INDEX = "shelf_index"
            const val KEY_ITEM_INDEX = "item_index"
            const val KEY_SCHEDULE_DATE = "schedule_date"
            const val KEY_SCHEDULE_TIME = "schedule_time"
        }
    }

    class SubscriptionsTrueDataSim {

        object GlobalKey {
            const val KEY_METHOD_NAME = "method"
        }

        object Event {
            const val EVENT_NETWORK_SIGNAL = "network_signal"
        }

        class Cellular {
            object Key {
                const val KEY_CARRIER_ID_SIM_1_NAME = "carrier_id1"
                const val KEY_CARRIER_SIM_1_NAME = "carrier_name1"
                const val KEY_CARRIER_ID_SIM_2_NAME = "carrier_id2"
                const val KEY_CARRIER_SIM_2_NAME = "carrier_name2"
            }
        }

        class Wifi {
            object Key {
                const val KEY_SSID_NAME = "ssid"
            }
        }
    }

    class TrueBonus {
        object ScreenName {
            const val SCREEN_NAME_TRUE_BONUS = "truebonus"
        }
    }

    class SimActivate {
        object ScreenName {
            const val SCREEN_NAME_SCAN_BARCODE = "sim_activation_begin > scan_barcode"
            const val SCREEN_NAME_FILL_IN_NUMBER = "sim_activation_begin > fill_in_number"
            const val SCREEN_NAME_SIM_ACTIVATE = "sim_activation"
            const val SCREEN_NAME_SIM_ACTIVATE_STATUS = "sim_activation_status"
            const val SCREEN_NAME_SIM_ACTIVATE_SUCCESS = "sim_activation_success"
        }
    }

    class TvsNow {
        object Event {
            const val EVENT_PACKAGE_CHECK = "package_check"
        }

        object LinkType {
            const val LINK_TYPE_DEFAULT_SCREEN_SETTING = "default_screen_setting"
        }

        object LinkDesc {
            const val LINK_DESC_DEFAULT_YES = "yes"
            const val LINK_DESC_DEFAULT_NO = "no"
        }
    }

    class StickyBanner {
        object Key {
            const val KEY_PROMOTION_ID = "promotion_id"
            const val KEY_PROMOTION_NAME = "promotion_name"
            const val KEY_CATEGORY = "category"
            const val KEY_LINK_TYPE = "link_type"
            const val KEY_LINK_DESC = "link_desc"
        }

        object Category

        object LinkType {
            const val LINK_TYPE_STICKY_BANNER = "sticky_banner"
        }

        object LinkDesc {
            const val LINK_DESC_BUTTON_CLICK = "button_click"
            const val LINK_DESC_DISMISS_CLICK = "dismiss_click"
        }
    }

    class MyServices {
        object LinkType {
            const val LINK_TYPE_REDEEM_NOT_ELIGIBLE = "redeem_not_eligible"
        }

        object LinkDesc {
            const val LINK_DESC_RE_LOGIN = "re-login"
            const val LINK_DESC_CANCEL = "cancel"
        }
    }

    class Maintenance {
        object Key {
            const val TITLE_MAINTENANCE = "maintenance"
        }

        object LinkType {
            const val LINK_TYPE_MAINTENANCE = "maintenance"
        }

        object LinkDesc {
            const val LINK_DESC_DISMISS_MAINTENANCE = "dismiss_maintenance"
        }
    }

    class TvSeeMorePage {
        object Key {
            const val PROGRAM_ID = "program_id"
            const val PROGRAM_NAME = "program_name"
        }

        object Shelf {
            const val SHELF_CODE = "M0KlvKO35o2X"
            const val SHELF_NAME = "[TrueIDApp] Live TV see more by category"
        }
    }
}
