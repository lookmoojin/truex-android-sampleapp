package com.truedigital.common.share.datalegacy.helpers

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.appsflyer.AppsFlyerLib
import com.google.firebase.analytics.FirebaseAnalytics
import com.truedigital.common.share.datalegacy.data.other.model.firebase.FirebaseAnalyticsModel
import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.common.share.datalegacy.domain.other.usecase.EncryptLocationUseCase
import com.truedigital.common.share.datalegacy.domain.other.usecase.NetworkInfoUseCase
import com.truedigital.common.share.datalegacy.domain.other.usecase.WifiInfoUseCase
import com.truedigital.common.share.datalegacy.login.LoginManagerInterface
import com.truedigital.core.data.repository.DeviceRepositoryImpl
import com.truedigital.core.provider.ContextDataProviderImp
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.TimeUnit.MILLISECONDS
import javax.inject.Inject

class FirebaseAnalyticsHelper @Inject constructor(
    private val loginManager: LoginManagerInterface
) {

    companion object {
        const val FIREBASE_ANALYTICS_EVENT_ERROR_SHOW = "error_show"
        const val FIREBASE_ANALYTICS_EVENT_NAME_LOGOUT = "logout"
        const val FIREBASE_ANALYTICS_EVENT_NAME_PAUSE_TV = "pause_tv"
        const val FIREBASE_ANALYTICS_EVENT_NAME_PLAY_TV = "play_tv"
        const val FIREBASE_ANALYTICS_EVENT_NAME_PLAY_MOVIE = "play_movie"
        const val FIREBASE_ANALYTICS_EVENT_NAME_PAUSE_MOVIE = "pause_movie"
        const val FIREBASE_ANALYTICS_EVENT_NAME_READ_ARTICLE = "read_article"
        const val FIREBASE_ANALYTICS_EVENT_NAME_PLAY_CLIP = "play_clip"
        const val FIREBASE_ANALYTICS_EVENT_NAME_PAUSE_CLIP = "pause_clip"
        const val FIREBASE_ANALYTICS_EVENT_NAME_PLAY_SONG = "play_song"
        const val FIREBASE_ANALYTICS_EVENT_NAME_PAUSE_SONG = "pause_song"
        const val FIREBASE_ANALYTICS_EVENT_NAME_CAST_CONTENT = "cast_content"
        const val FIREBASE_ANALYTICS_EVENT_NAME_SEARCH = "search"

        const val FIREBASE_ANALYTICS_EVENT_NAME_NCC_REDEEM_ERROR = "call_redeem_error"
        const val FIREBASE_ANALYTICS_EVENT_NAME_NCC_CONTENT_CREATE = "contact_create"
        const val FIREBASE_ANALYTICS_EVENT_NAME_NCC_CALL_COMPLETE = "call_complete"
        const val FIREBASE_ANALYTICS_EVENT_NAME_NCC_CALL_CALLING = "call_calling"
        const val FIREBASE_ANALYTICS_EVENT_NAME_NCC_CALL_BEGIN = "call_begin"
        const val FIREBASE_ANALYTICS_EVENT_NAME_NCC_CALL_ERROR = "call_error"
        const val FIREBASE_ANALYTICS_EVENT_NAME_CLICK = "click"
        const val FIREBASE_ANALYTICS_EVENT_NAME_CLICK_LASTFEED = "click_latestfeed"
        const val FIREBASE_ANALYTICS_EVENT_NAME_NCC_ERROR = "error"

        const val FIREBASE_ANALYTICS_EVENT_NAME_SELECT_CONTENT = "select_content"

        const val FIREBASE_ANALYTICS_EVENT_NAME_TRUEWIFI_ATTEMPT = "true_wifi_connect_attempt"
        const val FIREBASE_ANALYTICS_EVENT_NAME_TRUEWIFI_DISCONNECT = "true_wifi_disconnect"
        const val FIREBASE_ANALYTICS_EVENT_NAME_TRUEWIFI_FAIL = "true_wifi_connect_fail"
        const val FIREBASE_ANALYTICS_EVENT_NAME_TRUEWIFI_SUCCESS = "true_wifi_connect_success"

        const val FIREBASE_ANALYTICS_MEASUREMENT_HORIZONTAL_MENU = "horizontal menu"
        const val FIREBASE_ANALYTICS_MEASUREMENT_HAMBURGER_MENU = "hamburger menu"
        const val FIREBASE_ANALYTICS_MEASUREMENT_DISCOVER = "Discover"

        const val FIREBASE_ANALYTICS_LINK_DESC_MY_LIBRARY = "my library"
        const val FIREBASE_ANALYTICS_LINK_DESC_CONTACTS = "contacts"
        const val FIREBASE_ANALYTICS_LINK_DESC_PACKAGE = "package"
        const val FIREBASE_ANALYTICS_LINK_DESC_ACCOUNT = "account"

        const val FIREBASE_ANALYTICS_LINK_TYPE_ME_ACCOUNT = "me>account"

        const val FIREBASE_ANALYTICS_EVENT_NAME_TRUEPOINT_ERROR = "truepoint_error"
    }

    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    private var appContext: Context? = null
    private var networkInfoUseCase: NetworkInfoUseCase? = null
    private var wifiInfoUseCase: WifiInfoUseCase? = null
    private var encryptLocationUseCase: EncryptLocationUseCase? = null
    private var appsFlyerID: String = ""
    private var userRepository: UserRepository? = null

    private val customDimenFirstParam: String
        get() {
            return if (!loginManager.isLoggedIn()) {
                ""
            } else {
                userRepository?.getSsoId() ?: ""
            }
        }

    private val deviceID: String
        get() {
            if (appContext != null) {
                val contextDataProvider = ContextDataProviderImp(appContext!!)
                val deviceRepository = DeviceRepositoryImpl(contextDataProvider)
                return deviceRepository.getAndroidId()
            }
            return ""
        }

    private val networkAndWifiInfo: JSONObject
        get() {
            val output = JSONObject()
            try {
                if (wifiInfoUseCase != null) {
                    output.put("wifiInfo", wifiInfoUseCase!!.getWifiInfo())
                }
                if (networkInfoUseCase != null) {
                    val networkInfoList = networkInfoUseCase!!.getNetworkInfo()
                    if (networkInfoList.size == 3) {
                        output.put("networkInfo1", networkInfoList[0])
                        output.put("networkInfo2", networkInfoList[1])
                        output.put("networkInfo3", networkInfoList[2])
                    }
                }
                return output
            } catch (e: JSONException) {
                e.printStackTrace()
                return output
            }
        }

    fun initializeFirebaseAnalyticsHelper(context: Context, userRepository: UserRepository) {
        this.userRepository = userRepository
        this.appContext = context
        this.mFirebaseAnalytics = FirebaseAnalytics.getInstance(context)
        this.appsFlyerID = AppsFlyerLib.getInstance().getAppsFlyerUID(context).orEmpty()
    }

    fun sendEventFirebaseAnalytic(
        screenName: String,
        firebaseAnalyticsModel: FirebaseAnalyticsModel
    ) {
        if (encryptLocationUseCase != null) {
            firebaseAnalyticsModel.location =
                encryptLocationUseCase?.getEncryptLocation().toString()
            setNetworkAndWifiInfo(firebaseAnalyticsModel)
        }

        val bundle = Bundle()

        bundle.putString("device_id", deviceID)
        bundle.putString("af_id", appsFlyerID)
        bundle.putString("lat_long", firebaseAnalyticsModel.location)

        // custom param
        bundle.putString("cms_id", firebaseAnalyticsModel.cms_id)
        bundle.putString(
            "recommendation_schema_id",
            firebaseAnalyticsModel.recommendation_schema_id
        )

        when (firebaseAnalyticsModel.event_name) {
            FIREBASE_ANALYTICS_EVENT_NAME_PLAY_TV,
            FIREBASE_ANALYTICS_EVENT_NAME_PAUSE_TV -> {
                bundle.putString("channel_code", firebaseAnalyticsModel.channel_code)
                bundle.putString("channel_name_en", firebaseAnalyticsModel.channel_name_en)
                bundle.putString("channel_name_th", firebaseAnalyticsModel.channel_name_th)
                bundle.putString("title_id", firebaseAnalyticsModel.title_id)
                bundle.putString("title", firebaseAnalyticsModel.title)
                bundle.putString("episode_id", firebaseAnalyticsModel.episode_id)
                bundle.putString("episode_name", firebaseAnalyticsModel.episode_name)
                bundle.putString("category", firebaseAnalyticsModel.category)
                bundle.putString("shelf", firebaseAnalyticsModel.shelf)
                bundle.putString("content_type", firebaseAnalyticsModel.content_type)
            }

            FIREBASE_ANALYTICS_EVENT_NAME_PLAY_MOVIE -> {
                bundle.putString("title", firebaseAnalyticsModel.title)
                bundle.putString("genre", firebaseAnalyticsModel.genre)
                bundle.putString("movie_type", firebaseAnalyticsModel.movie_type)
            }

            FIREBASE_ANALYTICS_EVENT_NAME_CAST_CONTENT -> {
                bundle.putString("title", firebaseAnalyticsModel.title)
                bundle.putString("category", firebaseAnalyticsModel.category)
                bundle.putString("content_type", firebaseAnalyticsModel.content_type)
                bundle.putString("content_class", firebaseAnalyticsModel.content_class)
                bundle.putString("channel_name_en", firebaseAnalyticsModel.channel_name_en)
                bundle.putString("channel_name_th", firebaseAnalyticsModel.channel_name_th)
            }

            FIREBASE_ANALYTICS_EVENT_NAME_READ_ARTICLE -> {
                bundle.putString("title", firebaseAnalyticsModel.title)
                bundle.putString("category", firebaseAnalyticsModel.category)
                bundle.putString("shelf", firebaseAnalyticsModel.shelf)
                bundle.putString("partner_id", firebaseAnalyticsModel.partner_id)
                bundle.putString("partner_name", firebaseAnalyticsModel.partner_name)
            }

            FIREBASE_ANALYTICS_EVENT_NAME_PLAY_CLIP -> {
                bundle.putString("title", firebaseAnalyticsModel.title)
                bundle.putString("category", firebaseAnalyticsModel.category)
                bundle.putString("shelf", firebaseAnalyticsModel.shelf)
                bundle.putString("content_exclusivity", firebaseAnalyticsModel.content_exclusivity)
                bundle.putString("content_type", firebaseAnalyticsModel.content_type)
                bundle.putString("partner_id", firebaseAnalyticsModel.partner_id)
                bundle.putString("partner_name", firebaseAnalyticsModel.partner_name)
                bundle.putString("shelf_name", firebaseAnalyticsModel.shelf_name)
                bundle.putString("shelf_code", firebaseAnalyticsModel.shelf_code)
                bundle.putString("shelf_index", firebaseAnalyticsModel.shelf_index)
                bundle.putString("item_index", firebaseAnalyticsModel.item_index)
            }

            FIREBASE_ANALYTICS_EVENT_NAME_PAUSE_CLIP -> {
                bundle.putString("title", firebaseAnalyticsModel.title)
                bundle.putString("category", firebaseAnalyticsModel.category)
                bundle.putString("shelf", firebaseAnalyticsModel.shelf)
                bundle.putString("content_exclusivity", firebaseAnalyticsModel.content_exclusivity)
                bundle.putString("content_type", firebaseAnalyticsModel.content_type)
                bundle.putString("partner_id", firebaseAnalyticsModel.partner_id)
                bundle.putString("partner_name", firebaseAnalyticsModel.partner_name)
                bundle.putString("shelf_name", firebaseAnalyticsModel.shelf_name)
                bundle.putString("shelf_code", firebaseAnalyticsModel.shelf_code)
                bundle.putString("shelf_index", firebaseAnalyticsModel.shelf_index)
                bundle.putString("item_index", firebaseAnalyticsModel.item_index)
            }

            FIREBASE_ANALYTICS_EVENT_NAME_SEARCH -> {
                bundle.putString("search_term", firebaseAnalyticsModel.search_term)
            }

            FIREBASE_ANALYTICS_EVENT_NAME_PLAY_SONG,
            FIREBASE_ANALYTICS_EVENT_NAME_PAUSE_SONG -> {
                bundle.putString("title", firebaseAnalyticsModel.title)
                bundle.putString("genre", firebaseAnalyticsModel.genre)
                bundle.putString("shelf", firebaseAnalyticsModel.shelf)
                bundle.putString("album", firebaseAnalyticsModel.album)
                bundle.putString("artist", firebaseAnalyticsModel.artist)
                bundle.putString("music_code", firebaseAnalyticsModel.music_code)
                bundle.putString("provision_code", firebaseAnalyticsModel.provision_code)
                bundle.putString("timestamp", getTimeStamp())
            }

            FIREBASE_ANALYTICS_EVENT_NAME_NCC_CALL_ERROR,
            FIREBASE_ANALYTICS_EVENT_NAME_NCC_REDEEM_ERROR -> {
                bundle.putString("error_msg_en", firebaseAnalyticsModel.error_msg_en)
            }

            FIREBASE_ANALYTICS_EVENT_NAME_CLICK -> {
                bundle.putString("link_desc", firebaseAnalyticsModel.link_desc)
                bundle.putString("link_type", firebaseAnalyticsModel.link_type)
                bundle.putString("index", firebaseAnalyticsModel.index)
            }

            FIREBASE_ANALYTICS_EVENT_NAME_NCC_CALL_COMPLETE -> {
                bundle.putString("duration_s", firebaseAnalyticsModel.duration_s)
            }

            FIREBASE_ANALYTICS_EVENT_NAME_CLICK_LASTFEED -> {
                bundle.putString("item_id", firebaseAnalyticsModel.cms_id)
                bundle.putString("item_name", firebaseAnalyticsModel.title)
                bundle.putString("item_list", firebaseAnalyticsModel.page)
                bundle.putString("item_content_type", firebaseAnalyticsModel.content_type)
                bundle.putString("item_category", firebaseAnalyticsModel.category)
                bundle.putString("item_variant", firebaseAnalyticsModel.variant)
                bundle.putString("index", firebaseAnalyticsModel.index)
            }

            FIREBASE_ANALYTICS_EVENT_NAME_LOGOUT -> {
                bundle.putString("ssoid", "")
                bundle.putString(
                    "sso_id",
                    customDimenFirstParam
                ) // Move argument from ssoid to sso_id
                bundle.putString(
                    "screen_name",
                    ""
                ) // Clear all argument and sent only sso_id, device_ID
                bundle.putString("af_id", "")
                bundle.putString("lat_long", "")
                bundle.putString("network_wifi_c1", "")
                bundle.putString("network_wifi_c2", "")
                bundle.putString("network_c1", "")
                bundle.putString("network_c2", "")
                bundle.putString("network_c3", "")
                bundle.putString("cms_id", "")
                bundle.putString("recommendation_schema_id", "")
            }

            FIREBASE_ANALYTICS_EVENT_NAME_SELECT_CONTENT -> {
                bundle.putString("title", firebaseAnalyticsModel.title)
                bundle.putString("content_type", firebaseAnalyticsModel.content_type)
                bundle.putString("content_list_id", firebaseAnalyticsModel.content_list_id)
                bundle.putString("shelf_name", firebaseAnalyticsModel.shelf_name)
                bundle.putString("shelf_code", firebaseAnalyticsModel.shelf_code)
                bundle.putString("shelf_index", firebaseAnalyticsModel.shelf_index)
                bundle.putString("shelf_slug", firebaseAnalyticsModel.shelfSlug)
                bundle.putString("item_index", firebaseAnalyticsModel.item_index)
                bundle.putString("genre", firebaseAnalyticsModel.genre)
                bundle.putString("category", firebaseAnalyticsModel.category)
                bundle.putString("business_model", firebaseAnalyticsModel.tvodFlag)
                bundle.putString("content_rights", firebaseAnalyticsModel.contentRights)
                bundle.putString("vod_type", firebaseAnalyticsModel.movie_type)
                bundle.putString("vod_other_type", firebaseAnalyticsModel.other_type)
            }

            FIREBASE_ANALYTICS_EVENT_NAME_TRUEPOINT_ERROR -> {
                bundle.putString("status_code", firebaseAnalyticsModel.status_code)
                bundle.putString("error_type", firebaseAnalyticsModel.error_type)
                bundle.putString("error_msg_en", firebaseAnalyticsModel.error_msg_en)
                bundle.putString("screen_name", firebaseAnalyticsModel.screen_name)
            }
        }

        mFirebaseAnalytics?.logEvent(firebaseAnalyticsModel.event_name, bundle)
    }

    private fun getTimeStamp(): String {
        return MILLISECONDS.toSeconds(System.currentTimeMillis()).toString()
    }

    private fun setNetworkAndWifiInfo(firebaseAnalyticsModel: FirebaseAnalyticsModel) {
        val networkAndWifiInfo = networkAndWifiInfo

        try {
            if (networkAndWifiInfo.has("networkInfo1")) {
                firebaseAnalyticsModel.networkInfo1 = networkAndWifiInfo.getString("networkInfo1")
            }
            if (networkAndWifiInfo.has("networkInfo2")) {
                firebaseAnalyticsModel.networkInfo2 = networkAndWifiInfo.getString("networkInfo2")
            }
            if (networkAndWifiInfo.has("networkInfo3")) {
                firebaseAnalyticsModel.networkInfo3 = networkAndWifiInfo.getString("networkInfo3")
            }
            if (networkAndWifiInfo.has("wifiInfo")) {
                var wifiC1 = ""
                var wifiC2 = ""
                val param = networkAndWifiInfo.getString("wifiInfo").split(",".toRegex())
                    .dropLastWhile { it.isEmpty() }.toTypedArray()
                for (i in param.indices) {
                    if (i < 2)
                        wifiC1 = wifiC1 + param[i] + ","
                    else if (i == 2) {
                        wifiC1 += param[i]
                    } else if (i > 2 && i < param.lastIndex) {
                        wifiC2 = wifiC2 + param[i] + ","
                    } else if (i == param.lastIndex) {
                        wifiC2 += param[i]
                    }
                }
                if (wifiC1.isNotEmpty()) {
                    firebaseAnalyticsModel.network_wifi_c1 = "$wifiC1}"
                    firebaseAnalyticsModel.network_wifi_c2 = "{$wifiC2"
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun setTrackingScreen(activity: Activity, screenName: String, screenClass: String) {
        val bundle = Bundle()

        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass)

        mFirebaseAnalytics?.setDefaultEventParameters(bundle)
        mFirebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    fun setEncryptLocationUseCase(useCase: EncryptLocationUseCase) {
        encryptLocationUseCase = useCase
    }

    fun setWifiInfoUseCase(useCase: WifiInfoUseCase) {
        wifiInfoUseCase = useCase
    }

    fun setNetworkInfoUseCase(useCase: NetworkInfoUseCase) {
        networkInfoUseCase = useCase
    }

    fun getAppsFlyerID(): String {
        return appsFlyerID
    }
}
