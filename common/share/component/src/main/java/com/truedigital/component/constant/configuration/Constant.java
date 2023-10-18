package com.truedigital.component.constant.configuration;

import com.truedigital.component.R;
import com.truedigital.core.BuildConfig;

/**
 * Created by Aof on 9/8/15 AD.
 */
public class Constant {

    /**
     * =============================================================================================
     * TAG for Logging
     * =============================================================================================
     */
    public static final String REFRESH_TOKEN = "refresh_token";

    public static final String TRUEID_LOGIN_ACCOUNT = "trueid.login.account";

    public static final String VISITED_BRANCH = "visited_Constantbranch";
    public static final String DAILY_VISITED = "daily.visited";

    /**
     * =============================================================================================
     * BASE URL for API
     * =============================================================================================
     */
    public static final String REDIRECT_URL = "true-trueid://";
    public static final String AUTHEN_URL = "https://auth." + BuildConfig.HOST;

    // url for premium thumbnail
    public static final String JSESSION_HEADER = ";jsessionid={jsessionid}";

    /**
     * =============================================================================================
     * Cloud Enable checkedhero_cash_wallet_not_match
     * =============================================================================================
     */
    public static final String PHOTOS_SYNC_CHECKED = "photos_sync_check";
    public static final String VIDEOS_SYNC_CHECKED = "videos_sync_check";
    public static final String MUSICS_SYNC_CHECKED = "musics_sync_check";
    public static final String CONTACTS_SYNC_CHECKED = "contacts_sync_check";
    public static final String FILES_SYNC_CHECKED = "files_sync_check";
    public static final String METHOD_CHECKED = "method_check";
    public static final String FIRST_SYNC_CLICKED = "first_sync_click";

    /**
     * =============================================================================================
     * Check connection type
     * =============================================================================================
     */
    public static final String CHECK_ACCESS = "apple_captive_portal";
    public static final String CONNECT_TO_PORTAL = "true_captive_portal";

    /**
     * =============================================================================================
     * Media type
     * =============================================================================================
     */
    public static final String[] MEDIA_TYPE_TEXT = {"Photo", "Video", "Music", "Contacts", "Files"};
    public static final int[] MEDIA_TYPE_PIC = {R.drawable.access_ic_photos,
            R.drawable.access_ic_videos,
            R.drawable.access_ic_music,
            R.drawable.access_ic_contact_calendar,
            R.drawable.access_ic_files};
    public static final int[] MEDIA_TYPE_CHECK_ICON = {R.drawable.icon_check_yellow_2x,
            R.drawable.icon_check_orange_2x,
            R.drawable.icon_check_green_2x,
            R.drawable.icon_check_red_2x,
            R.drawable.icon_check_gray_2x};
    /**
     * Hawk
     */
    public static final String MOVIE_PLAYER_RECENTLY_PLAYED_LIST = "movie.player.recently.played";
    public static final String MOVIE_PLAYER_LAST_PLAYED = "movie.player.last.played";
    public static final String MOVIE_PLAYER_LAST_PLAYED_ELAPSE = "movie.player.last.played.elapse";
    public static final String MOVIE_PLAYER_LAST_PLAYED_AUDIO = "movie.player.last.played.audio";
    public static final String MOVIE_PLAYER_LAST_PLAYED_SUBTITLE = "movie.player.last.played.subtitle";
    public static final String SERIES_PLAYER_RECENTLY_PLAYED_LIST = "series.player.recently.played";
    public static final String SERIES_PLAYER_LAST_PLAYED = "series.player.last.played";
    public static final String SERIES_PLAYER_LAST_PLAYED_ELAPSE = "series.player.last.played.elapse";
    public static final String SERIES_PLAYER_LAST_PLAYED_AUDIO = "series.player.last.played.audio";
    public static final String SERIES_PLAYER_LAST_PLAYED_SUBTITLE = "series.player.last.played.subtitle";
    public static final String TV_PLAYER_RECENTLY_PLAYED_LIST = "tv.player.recently.played";
    public static final String CATCHUP_PLAYER_RECENTLY_PLAYED_LIST = "catchup.player.recently.played";
    public static final String MOVIE_CMS_PLAYER_RECENTLY_PLAYED_LIST = "movie.cms.player.recently.played";
    public static final String MOVIE_CMS_PLAYER_LAST_PLAYED = "movie.cms.player.last.played";
    public static final String MOVIE_CMS_PLAYER_LAST_PLAYED_ELAPSE = "movie.cms.player.last.played.elapse";
    public static final String MOVIE_CMS_PLAYER_LAST_PLAYED_AUDIO = "movie.cms.player.last.played.audio";
    public static final String MOVIE_CMS_PLAYER_LAST_PLAYED_SUBTITLE = "movie.cms.player.last.played.subtitle";
    public static final String SERIES_CMS_PLAYER_RECENTLY_PLAYED_LIST = "series.cms.player.recently.played";
    public static final String SERIES_CMS_PLAYER_LAST_PLAYED = "series.cms.player.last.played";
    public static final String SERIES_CMS_PLAYER_LAST_PLAYED_ELAPSE = "series.cms.player.last.played.elapse";
    public static final String SERIES_CMS_PLAYER_LAST_PLAYED_AUDIO = "series.cms.player.last.played.audio";
    public static final String SERIES_CMS_PLAYER_LAST_PLAYED_SUBTITLE = "series.cms.player.last.played.subtitle";
    public static final String TV_CMS_PLAYER_RECENTLY_PLAYED_LIST = "tv.cms.player.recently.played";
    public static final String FEATURE_BANNER_CONNECT_TRUEVISIONS_KEY = "SHOW_TRUEVISIONS_BANNER";
    public static final String FEATURE_COUNT_INBOX_MESSAGE = "KEY_COUNT_INBOX_MESSAGE";
    public static final String FEATURE_NEW_COUNT_INBOX_MESSAGE = "KEY_NEW_COUNT_INBOX_MESSAGE";

    /**
     * Seven Redeem
     */
    public static final String DEVICE_PLATFORM = "android";
    /**
     * Open app from notification intent
     */
    public static final String INTENT_NOTIFICATION_BIG_IMAGE_URL = "big_image_url";
    public static final String INTENT_NOTIFICATION_BODY = "body";
    public static final String INTENT_NOTIFICATION_DEEP_LINK = "deeplink";
    public static final String INTENT_NOTIFICATION_JOB_LABEL = "jobLabel";
    public static final String INTENT_NOTIFICATION_MESSAGE_ID = "messageID";
    public static final String INTENT_NOTIFICATION_PLATFORM_MESSAGE_ID = "platformMessageId";
    public static final String INTENT_NOTIFICATION_SENT_TIME = "sendTime";
    public static final String INTENT_NOTIFICATION_SENT_TYPE = "sendType";
    public static final String INTENT_NOTIFICATION_TITLE = "title";
    /**
     * Shortcuts intent
     **/

    public static final String INTENT_SHORT_CUTS_INDEX = "short_cuts_index";
    public static final String INTENT_SHORT_CUTS_TITLE = "short_cuts_title";
    /**
     * Deeplink intent param
     */
    public static final String INTENT_TRUEID_SCHEME_URL = "trueid";
    /**
     * Deeplink handle params
     */
    public static String DEEP_LINK_KEY_COMMUNITY_ID = "communityId";
    public static String DEEP_LINK_KEY_CHANNEL_ID = "channelId";
    public static String DEEP_LINK_KEY_POST_ID = "postId";
    public static String DEEP_LINK_KEY_INFO = "info";
    public static String DEEP_LINK_KEY_TYPE = "type";

    public static String APP_LINK_PAGE_LIVE = "live";
    public static String APP_LINK_PAGE_TV = "tv.trueid.net";
    public static String APPSFLYER_KEY_IS_FIRST_LAUNCH = "is_first_launch";

    /**
     * @deprecated, Use this class for creating analytic constant
     * {@link com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant} instead.
     */
    @Deprecated
    public static class TRUEID_GA {
        public static class ScreenNameMeasurement {
            public static String MEASUREMENT_SETTING = "Settings";
            public static String MESUREMENT_SETTING_TRUECLOUD_PICTURE = "Settings - TrueCloud Picture";
            public static String MESUREMENT_SETTING_TRUECLOUD_VIDEO = "Settings - TrueCloud Video";
            public static String MESUREMENT_SETTING_TRUECLOUD_MUSIC = "Settings - TrueCloud Music";
            public static String MESUREMENT_SETTING_TRUECLOUD_CONTACT = "Settings - TrueCloud Contact";
            public static String MESUREMENT_SETTING_TRUECLOUD_FILE = "Settings - TrueCloud File";
        }

        public static class TypeMeasurement {
            public static String LIVE_TV = "live_tv";
        }

    }

    /**
     * Constant for stream
     */
    public static final class STREAM {

        public static final int MARK_WATCHED_DURATION = 30; //in seconds.

        public static final String TYPE_SVOD = "svod";

        //Key content_type
        public static final String CONTENT_TYPE_SPORT_CLIP = "sportclip";

        //Key movie_type
        public static final String MOVIE_TYPE_MOVIE = "movie";
        public static final String MOVIE_TYPE_SERIES = "series";

        public static final String VIDEO_STREAM_TYPE_CATCHUP = "catchup";

    }

    /**
     * Sticky Banner
     */
    public static final String KEY_STICKY_BANNER_TIME_STAMP = "sticky_banner";

    /**
     * Debug mode FC noti
     */
    public static final String DEBUG_MODE_CONFIG_STATE = "config_state";

}