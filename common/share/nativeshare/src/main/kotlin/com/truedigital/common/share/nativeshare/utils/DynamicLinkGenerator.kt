package com.truedigital.common.share.nativeshare.utils

import android.net.Uri
import com.google.android.gms.common.api.ApiException
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.truedigital.common.share.nativeshare.R
import com.truedigital.core.BuildConfig
import com.truedigital.core.constant.AppConfig
import com.truedigital.core.extensions.addOnCompleteListenerWithNewExecutor
import com.truedigital.core.provider.ContextDataProvider
import com.truedigital.core.utils.networkconnection.ConnectivityStateHolder
import io.reactivex.Single
import org.json.JSONObject
import javax.inject.Inject

interface DynamicLinkGenerator {
    fun generateDynamicLink(
        title: String,
        description: String,
        imageUrl: String,
        type: String,
        slug: String,
        info: JSONObject?,
        shareUrl: String,
        goToPlayStore: Boolean,
        contentId: String,
        callback: ShortenUrlCallback
    )

    fun generateDynamicLink(
        title: String,
        description: String,
        imageUrl: String,
        type: String,
        slug: String,
        info: JSONObject?,
        shareUrl: String,
        goToPlayStore: Boolean,
        callback: ShortenUrlCallback
    )

    fun generateDynamicLinkToSingle(
        title: String,
        description: String,
        imageUrl: String,
        type: String,
        slug: String,
        info: JSONObject?,
        shareUrl: String,
        goToPlayStore: Boolean
    ): Single<String>
}

interface ShortenUrlCallback {
    fun onLoading()
    fun onSuccess(shortUrl: String)
    fun onFailure(errorMessage: String)
}

class DynamicLinkGeneratorImpl @Inject constructor(private val contextDataProvider: ContextDataProvider) :
    DynamicLinkGenerator {

    companion object {
        const val API_EXCEPTION = "Api Exception"
        const val NO_INTERNET = "No Internet Connection"

        private const val MAX_CONTENT_CHARACTER_COUNT = 250
        private const val CREATE_DYNAMIC_LINK_FAILED = "can't generate dynamic link for share."
        private const val IOS_APP_BUNDLE_ID = "com.tdcm.trueidapp"
        private const val IOS_APP_STORE_ID = "1013814221"
    }

    override fun generateDynamicLink(
        title: String,
        description: String,
        imageUrl: String,
        type: String,
        slug: String,
        info: JSONObject?,
        shareUrl: String,
        goToPlayStore: Boolean,
        contentId: String,
        callback: ShortenUrlCallback
    ) {

        val maxSubstringIndex = Math.min(MAX_CONTENT_CHARACTER_COUNT, description.indices.last)
        val newDescription = description.substring(0..maxSubstringIndex)
        if (goToPlayStore) {
            genShortenDeepLinkHandleGotoPlayStore(
                title = title,
                description = newDescription,
                imageUrl = imageUrl,
                callback = callback,
                longUrl = createDeepLinkUrlForDetailPage(
                    type = type,
                    slug = slug,
                    info = info,
                    shareUrl = shareUrl,
                    contentId = contentId
                )
            )
        } else {
            genShortenDeepLink(
                title = title,
                description = newDescription,
                imageUrl = imageUrl,
                shareUrl = shareUrl,
                callback = callback,
                longUrl = createDeepLinkUrlForDetailPage(
                    type = type,
                    slug = slug,
                    info = info,
                    shareUrl = shareUrl,
                    contentId = contentId
                )
            )
        }
    }

    override fun generateDynamicLink(
        title: String,
        description: String,
        imageUrl: String,
        type: String,
        slug: String,
        info: JSONObject?,
        shareUrl: String,
        goToPlayStore: Boolean,
        callback: ShortenUrlCallback
    ) {

        val maxSubstringIndex = Math.min(MAX_CONTENT_CHARACTER_COUNT, description.indices.last)
        val newDescription = description.substring(0..maxSubstringIndex)
        if (goToPlayStore) {
            genShortenDeepLinkHandleGotoPlayStore(
                title = title,
                description = newDescription,
                imageUrl = imageUrl,
                callback = callback,
                longUrl = createDeepLinkUrlForDetailPage(
                    type = type,
                    slug = slug,
                    info = info,
                    shareUrl = shareUrl
                )
            )
        } else {
            genShortenDeepLink(
                title = title,
                description = newDescription,
                imageUrl = imageUrl,
                shareUrl = shareUrl,
                callback = callback,
                longUrl = createDeepLinkUrlForDetailPage(
                    type = type,
                    slug = slug,
                    info = info,
                    shareUrl = shareUrl
                )
            )
        }
    }

    private fun createDeepLinkUrlForDetailPage(
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
            if (shareUrl.contains("movie") && type.contains("movie")) {
                "$shareUrl/movie/$cmsId"
            } else if (shareUrl.contains("movie") && type.contains("series")) {
                "$shareUrl/series/$cmsId"
            } else if (shareUrl.contains("music") &&
                (type.contains("playlist") || type.contains("album") || type.contains("song"))
            ) {
                val id = info.optString("content_id", "")
                "$shareUrl/$type/$id"
            } else {
                if (shareUrl.contains("?")) {
                    "$shareUrl&page=detail&type=$type&slug=$slug&info=$infoStringReplaced"
                } else {
                    "$shareUrl?page=detail&type=$type&slug=$slug&info=$infoStringReplaced"
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

    private fun genShortenDeepLink(
        title: String,
        description: String,
        imageUrl: String,
        longUrl: String,
        shareUrl: String,
        callback: ShortenUrlCallback
    ) {

        if (!ConnectivityStateHolder.isConnected) {
            callback.onFailure(contextDataProvider.getString(R.string.nativeshare_no_internet_connection))
        } else {
            FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(longUrl))
                .setDomainUriPrefix(BuildConfig.FIREBASE_DYNAMIC_LINK_DOMAIN)
                // Open links with this app on Android
                .setAndroidParameters(
                    DynamicLink.AndroidParameters.Builder()
                        .setFallbackUrl(Uri.parse(shareUrl)).build()
                )
                // Open links with com.example.ios on iOS
                .setIosParameters(
                    DynamicLink.IosParameters.Builder(IOS_APP_BUNDLE_ID)
                        .setAppStoreId(IOS_APP_STORE_ID)
                        .setFallbackUrl(Uri.parse(shareUrl))
                        .build()
                )
                .setSocialMetaTagParameters(
                    DynamicLink.SocialMetaTagParameters.Builder()
                        .setTitle(title)
                        .setDescription(description)
                        .setImageUrl(Uri.parse(imageUrl))
                        .build()
                )
                .buildShortDynamicLink()
                .addOnCompleteListenerWithNewExecutor { task ->
                    if (task.isSuccessful) {
                        // Short link created
                        val shortLink = task.result?.shortLink
                        if (shortLink == null || shortLink.toString().isEmpty()) {
                            callback.onFailure(contextDataProvider.getString(R.string.nativeshare_error_something_wrong))
                        } else {
                            callback.onSuccess(shortLink.toString())
                        }
                    } else {
                        if (task.exception is ApiException) {
                            callback.onFailure(contextDataProvider.getString(R.string.nativeshare_share_fail_api_exception))
                        } else {
                            callback.onFailure(contextDataProvider.getString(R.string.nativeshare_error_something_wrong))
                        }
                    }
                }
        }
    }

    private fun genShortenDeepLinkHandleGotoPlayStore(
        title: String,
        description: String,
        imageUrl: String,
        longUrl: String,
        callback: ShortenUrlCallback
    ) {
        if (!ConnectivityStateHolder.isConnected) {
            callback.onFailure(contextDataProvider.getString(R.string.nativeshare_no_internet_connection))
        } else {
            FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(longUrl))
                .setDomainUriPrefix(BuildConfig.FIREBASE_DYNAMIC_LINK_DOMAIN)
                // Open links with this app on Android
                .setAndroidParameters(
                    DynamicLink.AndroidParameters.Builder()
                        .setFallbackUrl(Uri.parse(AppConfig.PLAY_STORE_TRUE_ID)).build()
                )
                // Open links with com.example.ios on iOS
                .setIosParameters(
                    DynamicLink.IosParameters.Builder(IOS_APP_BUNDLE_ID)
                        .setAppStoreId(IOS_APP_STORE_ID)
                        .setFallbackUrl(Uri.parse(AppConfig.APP_STORE_TRUE_ID))
                        .build()
                )
                .setSocialMetaTagParameters(
                    DynamicLink.SocialMetaTagParameters.Builder()
                        .setTitle(title)
                        .setDescription(description)
                        .setImageUrl(Uri.parse(imageUrl))
                        .build()
                )
                .buildShortDynamicLink()
                .addOnCompleteListenerWithNewExecutor { task ->
                    if (task.isSuccessful) {
                        // Short link created
                        val shortLink = task.result?.shortLink
                        if (shortLink == null || shortLink.toString().isEmpty()) {
                            callback.onFailure(contextDataProvider.getString(R.string.nativeshare_share_fail))
                        } else {
                            callback.onSuccess(shortLink.toString())
                        }
                    } else {
                        if (task.exception is ApiException) {
                            callback.onFailure(contextDataProvider.getString(R.string.nativeshare_share_fail_api_exception))
                        } else {
                            callback.onFailure(contextDataProvider.getString(R.string.nativeshare_share_fail))
                        }
                    }
                }
        }
    }

    override fun generateDynamicLinkToSingle(
        title: String,
        description: String,
        imageUrl: String,
        type: String,
        slug: String,
        info: JSONObject?,
        shareUrl: String,
        goToPlayStore: Boolean
    ): Single<String> {

        val maxSubstringIndex = Math.min(MAX_CONTENT_CHARACTER_COUNT, description.indices.last)
        val newDescription = description.substring(0..maxSubstringIndex)
        val deepLinkUrl = createDeepLinkUrlForDetailPage(type, slug, info, shareUrl)
        val fallBackUrl = if (goToPlayStore) AppConfig.PLAY_STORE_TRUE_ID else shareUrl

        return Single.create<String> { emitter ->
            if (!ConnectivityStateHolder.isConnected) {
                emitter.tryOnError(Throwable(NO_INTERNET))
            } else {
                FirebaseDynamicLinks.getInstance().createDynamicLink()
                    .setLink(Uri.parse(deepLinkUrl))
                    .setDomainUriPrefix(BuildConfig.FIREBASE_DYNAMIC_LINK_DOMAIN)
                    // Open links with this app on Android
                    .setAndroidParameters(
                        DynamicLink.AndroidParameters.Builder()
                            .setFallbackUrl(Uri.parse(fallBackUrl)).build()
                    )
                    // Open links with com.example.ios on iOS
                    .setIosParameters(
                        DynamicLink.IosParameters.Builder(IOS_APP_BUNDLE_ID)
                            .setAppStoreId(IOS_APP_STORE_ID)
                            .setFallbackUrl(Uri.parse(fallBackUrl))
                            .build()
                    )
                    .setSocialMetaTagParameters(
                        DynamicLink.SocialMetaTagParameters.Builder()
                            .setTitle(title)
                            .setDescription(newDescription)
                            .setImageUrl(Uri.parse(imageUrl))
                            .build()
                    )
                    .buildShortDynamicLink()
                    .addOnCompleteListenerWithNewExecutor { task ->
                        if (task.isSuccessful) {
                            // Short link created
                            val shortLink = task.result?.shortLink
                            if (shortLink == null || shortLink.toString().isEmpty()) {
                                if (!emitter.isDisposed) {
                                    emitter.tryOnError(Throwable(CREATE_DYNAMIC_LINK_FAILED))
                                }
                            } else {
                                emitter.onSuccess(shortLink.toString())
                            }
                        } else {
                            if (!emitter.isDisposed) {
                                if (task.exception is ApiException) {
                                    emitter.tryOnError(Throwable(API_EXCEPTION))
                                } else {
                                    emitter.tryOnError(Throwable(CREATE_DYNAMIC_LINK_FAILED))
                                }
                            }
                        }
                    }
            }
        }
    }
}
