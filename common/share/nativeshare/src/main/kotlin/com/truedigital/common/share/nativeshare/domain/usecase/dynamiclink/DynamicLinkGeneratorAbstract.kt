package com.truedigital.common.share.nativeshare.domain.usecase.dynamiclink

import android.net.Uri
import com.google.android.gms.common.api.ApiException
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.truedigital.common.share.nativeshare.R
import com.truedigital.common.share.nativeshare.domain.model.dynamiclink.CreateDynamicLinkModel
import com.truedigital.core.BuildConfig
import com.truedigital.core.constant.AppConfig
import com.truedigital.core.extensions.addOnCompleteListenerWithNewExecutor
import com.truedigital.core.provider.ContextDataProvider
import com.truedigital.core.utils.networkconnection.ConnectivityStateHolder
import org.json.JSONObject

abstract class DynamicLinkGeneratorAbstract(private val contextDataProvider: ContextDataProvider) {

    internal fun createDeepLinkUrlForDetailPage(
        type: String,
        slug: String,
        info: JSONObject?,
        shareUrl: String,
        contentId: String = ""
    ): String {
        return if (info != null) {
            val cmsId = info.optString("cms_id", "")
            val infoString = info.toString()
            val infoStringReplaced = infoString.replace("\\\\", "")
            when {
                shareUrl.contains("movie") && type.contains("movie") -> {
                    "$shareUrl/movie/$cmsId"
                }
                shareUrl.contains("movie") && type.contains("series") -> {
                    "$shareUrl/series/$cmsId"
                }
                shareUrl.contains("music") && (
                    type.contains("playlist") || type.contains("album") || type.contains(
                        "song"
                    )
                    ) -> {
                    val id = info.optString("content_id", "")
                    "$shareUrl/$type/$id"
                }
                else -> {
                    if (shareUrl.contains("?")) {
                        "$shareUrl&page=detail&type=$type&slug=$slug&info=$infoStringReplaced"
                    } else {
                        "$shareUrl?page=detail&type=$type&slug=$slug&info=$infoStringReplaced"
                    }
                }
            }
        } else {
            if (contentId.isNotEmpty()) {
                "$shareUrl?page=seemore&slug=$slug&content_id=$contentId"
            } else {
                "$shareUrl?page=seemore&slug=$slug"
            }
        }
    }

    protected fun genShortenDeepLink(
        title: String,
        description: String,
        imageUrl: String,
        longUrl: String,
        shareUrl: String,
        callback: DynamicLinkGeneratorCallback
    ) {
        if (!ConnectivityStateHolder.isConnected) {
            callback.onFailure(contextDataProvider.getString(R.string.nativeshare_no_internet_connection))
        } else {
            createDynamicLinks(
                CreateDynamicLinkModel(
                    longUrl = longUrl,
                    url = shareUrl,
                    title = title,
                    description = description,
                    imageUrl = imageUrl,
                    errorMegDisplay = R.string.nativeshare_error_something_wrong
                ),
                callback
            )
        }
    }

    protected fun genShortenDeepLinkHandleGotoPlayStore(
        title: String,
        description: String,
        imageUrl: String,
        longUrl: String,
        callback: DynamicLinkGeneratorCallback
    ) {
        if (!ConnectivityStateHolder.isConnected) {
            callback.onFailure(contextDataProvider.getString(R.string.nativeshare_no_internet_connection))
        } else {
            createDynamicLinks(
                CreateDynamicLinkModel(
                    longUrl = longUrl,
                    url = AppConfig.PLAY_STORE_TRUE_ID,
                    title = title,
                    description = description,
                    imageUrl = imageUrl,
                    errorMegDisplay = R.string.nativeshare_share_fail
                ),
                callback
            )
        }
    }

    private fun createDynamicLinks(
        model: CreateDynamicLinkModel,
        callback: DynamicLinkGeneratorCallback
    ) {
        FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse(model.longUrl))
            .setDomainUriPrefix(BuildConfig.FIREBASE_DYNAMIC_LINK_DOMAIN)
            // Open links with this app on Android
            .setAndroidParameters(
                DynamicLink.AndroidParameters.Builder()
                    .setFallbackUrl(Uri.parse(model.url)).build()
            )
            // Open links with com.example.ios on iOS
            .setIosParameters(
                DynamicLink.IosParameters.Builder(IOS_APP_BUNDLE_ID)
                    .setAppStoreId(IOS_APP_STORE_ID)
                    .setFallbackUrl(Uri.parse(model.url))
                    .build()
            )
            .setSocialMetaTagParameters(
                DynamicLink.SocialMetaTagParameters.Builder()
                    .setTitle(model.title)
                    .setDescription(model.description)
                    .setImageUrl(Uri.parse(model.imageUrl))
                    .build()
            )
            .buildShortDynamicLink()
            .addOnCompleteListenerWithNewExecutor { task ->
                if (task.isSuccessful) {
                    // Short link created
                    val shortLink = task.result?.shortLink
                    if (shortLink == null || shortLink.toString().isEmpty()) {
                        callback.onFailure(contextDataProvider.getString(model.errorMegDisplay))
                    } else {
                        callback.onSuccess(shortLink.toString())
                    }
                } else {
                    if (task.exception is ApiException) {
                        callback.onFailure(contextDataProvider.getString(R.string.nativeshare_share_fail_api_exception))
                    } else {
                        callback.onFailure(contextDataProvider.getString(model.errorMegDisplay))
                    }
                }
            }
    }

    companion object {
        const val MAX_CONTENT_CHARACTER_COUNT = 250
        const val CREATE_DYNAMIC_LINK_FAILED = "can't generate dynamic link for share."
        const val IOS_APP_BUNDLE_ID = "com.tdcm.trueidapp"
        const val IOS_APP_STORE_ID = "1013814221"
    }
}
