package com.truedigital.common.share.nativeshare.domain.usecase.dynamiclink

import android.net.Uri
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.truedigital.common.share.nativeshare.domain.model.dynamiclink.GenerateDynamicLinkModel
import com.truedigital.common.share.nativeshare.utils.DynamicLinkGeneratorImpl
import com.truedigital.core.BuildConfig
import com.truedigital.core.constant.AppConfig
import com.truedigital.core.extensions.addOnCompleteListenerWithNewExecutor
import com.truedigital.core.provider.ContextDataProvider
import com.truedigital.core.utils.networkconnection.ConnectivityStateHolder
import io.reactivex.Single
import io.reactivex.SingleEmitter

class DynamicLinkGeneratorUseCaseImpl(contextDataProvider: ContextDataProvider) :
    DynamicLinkGeneratorUseCase, DynamicLinkGeneratorAbstract(contextDataProvider) {

    override fun generateDynamicLink(
        dynamicLinkModel: GenerateDynamicLinkModel,
        contentId: String,
        callback: DynamicLinkGeneratorCallback
    ) {
        val maxSubstringIndex =
            Math.min(MAX_CONTENT_CHARACTER_COUNT, dynamicLinkModel.description.indices.last)
        val newDescription = dynamicLinkModel.description.substring(0..maxSubstringIndex)
        if (dynamicLinkModel.goToPlayStore) {
            genShortenDeepLinkHandleGotoPlayStore(
                title = dynamicLinkModel.title,
                description = newDescription,
                imageUrl = dynamicLinkModel.imageUrl,
                longUrl = createDeepLinkUrlForDetailPage(
                    type = dynamicLinkModel.type,
                    slug = dynamicLinkModel.slug,
                    info = dynamicLinkModel.info,
                    shareUrl = dynamicLinkModel.shareUrl,
                    contentId = contentId
                ),
                callback = callback
            )
        } else {
            genShortenDeepLink(
                title = dynamicLinkModel.title,
                description = newDescription,
                imageUrl = dynamicLinkModel.imageUrl,
                shareUrl = dynamicLinkModel.shareUrl,
                longUrl = createDeepLinkUrlForDetailPage(
                    type = dynamicLinkModel.type,
                    slug = dynamicLinkModel.slug,
                    info = dynamicLinkModel.info,
                    shareUrl = dynamicLinkModel.shareUrl,
                    contentId = contentId
                ),
                callback = callback
            )
        }
    }

    override fun generateDynamicLink(
        dynamicLinkModel: GenerateDynamicLinkModel,
        callback: DynamicLinkGeneratorCallback
    ) {
        val maxSubstringIndex =
            Math.min(MAX_CONTENT_CHARACTER_COUNT, dynamicLinkModel.description.indices.last)
        val newDescription = dynamicLinkModel.description.substring(0..maxSubstringIndex)
        return if (dynamicLinkModel.goToPlayStore) {
            genShortenDeepLinkHandleGotoPlayStore(
                title = dynamicLinkModel.title,
                description = newDescription,
                imageUrl = dynamicLinkModel.imageUrl,
                longUrl = createDeepLinkUrlForDetailPage(
                    type = dynamicLinkModel.type,
                    slug = dynamicLinkModel.slug,
                    info = dynamicLinkModel.info,
                    shareUrl = dynamicLinkModel.shareUrl
                ),
                callback = callback
            )
        } else {
            genShortenDeepLink(
                title = dynamicLinkModel.title,
                description = newDescription,
                imageUrl = dynamicLinkModel.imageUrl,
                shareUrl = dynamicLinkModel.shareUrl,
                longUrl = createDeepLinkUrlForDetailPage(
                    type = dynamicLinkModel.type,
                    slug = dynamicLinkModel.slug,
                    info = dynamicLinkModel.info,
                    shareUrl = dynamicLinkModel.shareUrl
                ),
                callback = callback
            )
        }
    }

    override fun generateDynamicLinkToSingle(dynamicLinkModel: GenerateDynamicLinkModel): Single<String> {
        val maxSubstringIndex =
            Math.min(MAX_CONTENT_CHARACTER_COUNT, dynamicLinkModel.description.indices.last)
        val newDescription = dynamicLinkModel.description.substring(0..maxSubstringIndex)
        val deepLinkUrl = createDeepLinkUrlForDetailPage(
            dynamicLinkModel.type,
            dynamicLinkModel.slug,
            dynamicLinkModel.info,
            dynamicLinkModel.shareUrl
        )
        val fallBackUrl =
            if (dynamicLinkModel.goToPlayStore) AppConfig.PLAY_STORE_TRUE_ID else dynamicLinkModel.shareUrl

        return Single.create { emitter ->
            if (!ConnectivityStateHolder.isConnected) {
                emitter.tryOnError(Throwable(DynamicLinkGeneratorImpl.NO_INTERNET))
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
                            .setTitle(dynamicLinkModel.title)
                            .setDescription(newDescription)
                            .setImageUrl(Uri.parse(dynamicLinkModel.imageUrl))
                            .build()
                    )
                    .buildShortDynamicLink()
                    .addOnCompleteListenerWithNewExecutor { task ->
                        handleOnCompleteListener(task, emitter)
                    }
            }
        }
    }

    private fun handleOnCompleteListener(
        task: Task<ShortDynamicLink>,
        emitter: SingleEmitter<String>
    ) {
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
                    emitter.tryOnError(Throwable(DynamicLinkGeneratorImpl.API_EXCEPTION))
                } else {
                    emitter.tryOnError(Throwable(CREATE_DYNAMIC_LINK_FAILED))
                }
            }
        }
    }
}
