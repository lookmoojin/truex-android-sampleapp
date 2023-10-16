package com.truedigital.common.share.nativeshare.utils

import android.content.Context
import com.appsflyer.AppsFlyerLib
import com.appsflyer.share.LinkGenerator.ResponseListener
import com.appsflyer.share.ShareInviteHelper
import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.common.share.nativeshare.domain.model.onelink.CreateOneLinkModel
import java.net.URL
import javax.inject.Inject

interface OneLinkGenerator {
    /**
     *
     * @param createOneLinkModel Data model from com.truedigital.common.share.nativeshare.domain.model.onelink.CreateOneLinkModel
     * @param callback Response after onelink gen finished. at OneLinkCallback interface
     */
    fun generateOneLink(
        createOneLinkModel: CreateOneLinkModel,
        callback: OneLinkCallback
    )
}

interface OneLinkCallback {
    /**
     * A onelink url when function generate success. Url will be https://trueid.onelink.me/cGLv/{linkID}
     */
    fun onSuccess(oneLinkUrl: String)

    /**
     * A message will be return when function error.
     */
    fun onFailure(errorMessage: String) = Unit
}

class OneLinkGeneratorImpl @Inject constructor(
    private val context: Context,
    private val userRepository: UserRepository
) : OneLinkGenerator {

    companion object {
        private const val KEY_PID = "pid"
        private const val KEY_URI_SCHEME = "af_dp"
        private const val KEY_DEEPLINK = "af_deep_link"
        private const val KEY_DESKTOP_URL = "af_web_dp"
        private const val KEY_RETARGETING = "is_retargeting"
        private const val KEY_SSOID = "af_sub1"
        private const val KEY_CONTENT_ID = "af_sub2"
        private const val KEY_TITLE_OF_CONTENT = "af_sub3"
        private const val KEY_CONTENT_TYPE = "af_sub4"
        private const val KEY_MASTER_OF_SERIES = "af_sub5"

        private const val VALUE_DESKTOP_URL = "https://www.trueid.net"
        private const val VALUE_CUSTOM_BRAND_URL = "ttid.co"
        private const val VALUE_ONELINK_ID = "UAnK"
        private const val VALUE_PID = "android_shared"
        private const val VALUE_RETARGETING = "true"
        private const val VALUE_URI_SCHEME = "trueid://"
    }

    init {
        AppsFlyerLib.getInstance().setAppInviteOneLink(VALUE_ONELINK_ID)
    }

    override fun generateOneLink(
        createOneLinkModel: CreateOneLinkModel,
        callback: OneLinkCallback
    ) {
        try {
            val url = URL(createOneLinkModel.deepLinkUrl)
            ShareInviteHelper.generateInviteUrl(context).apply {
                createOneLinkModel.campaign?.let { _campaign ->
                    campaign = _campaign.take(100)
                }

                createOneLinkModel.channel?.let { _channel ->
                    channel = _channel
                }

                createOneLinkModel.title?.let { title ->
                    addParameter(KEY_TITLE_OF_CONTENT, title.take(100))
                }

                createOneLinkModel.contentType?.let { contentType ->
                    addParameter(KEY_CONTENT_TYPE, contentType.take(100))
                }

                createOneLinkModel.contentId?.let { contentId ->
                    addParameter(KEY_CONTENT_ID, contentId.take(100))
                }

                createOneLinkModel.masterId?.let { masterId ->
                    addParameter(KEY_MASTER_OF_SERIES, masterId.take(100))
                }

                addParameter(
                    KEY_URI_SCHEME,
                    "$VALUE_URI_SCHEME${url.host}${url.file}"
                )

                addParameter(KEY_PID, VALUE_PID)
                addParameter(KEY_SSOID, userRepository.getSsoId())
                addParameter(KEY_DEEPLINK, createOneLinkModel.deepLinkUrl)
                addParameter(KEY_DESKTOP_URL, createOneLinkModel.desktopUrl ?: VALUE_DESKTOP_URL)
                addParameter(KEY_RETARGETING, VALUE_RETARGETING)
                setDeeplinkPath("${url.host}${url.file}")
                brandDomain = VALUE_CUSTOM_BRAND_URL

                generateLink(
                    context,
                    object : ResponseListener {
                        override fun onResponse(oneLinkUrl: String?) {
                            oneLinkUrl?.let { _oneLinkUrl ->
                                callback.onSuccess(oneLinkUrl = _oneLinkUrl)
                            } ?: run {
                                callback.onFailure(errorMessage = "Link is empty.")
                            }
                        }

                        override fun onResponseError(errorMessage: String?) {
                            callback.onFailure(errorMessage = errorMessage ?: "Unknown Error.")
                        }
                    }
                )
            }
        } catch (e: Exception) {
            callback.onFailure(errorMessage = e.message ?: "Unknown Error.")
        }
    }
}
