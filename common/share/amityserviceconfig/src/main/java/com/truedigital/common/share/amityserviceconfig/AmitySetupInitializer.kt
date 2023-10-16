package com.truedigital.common.share.amityserviceconfig

import android.content.Context
import androidx.startup.Initializer
import com.amity.socialcloud.uikit.AmityUIKitClient
import com.newrelic.agent.android.NewRelic
import com.truedigital.common.share.amityserviceconfig.domain.repository.AmitySetupAppLocaleRepository
import com.truedigital.common.share.amityserviceconfig.domain.repository.AmitySetupRepository
import com.truedigital.common.share.amityserviceconfig.injections.AmityServiceComponent
import com.truedigital.common.share.amityserviceconfig.injections.DaggerAmityServiceComponent
import com.truedigital.common.share.security.SecurityInitializer
import com.truedigital.common.share.security.domain.appkey.amity.GetAmityKeyUseCase
import com.truedigital.common.share.security.injections.SecurityComponent
import com.truedigital.core.BuildConfig
import com.truedigital.core.CoreInitializer
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.extensions.collectSafe
import com.truedigital.core.extensions.launchSafe
import com.truedigital.core.injections.CoreComponent
import com.truedigital.share.data.firestoreconfig.initializer.FirestoreConfigInitializer
import com.truedigital.share.data.firestoreconfig.injections.FirestoreConfigComponent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class AmitySetupInitializer : Initializer<AmityServiceComponent> {

    override fun create(context: Context): AmityServiceComponent {
        return DaggerAmityServiceComponent.factory().create(
            SecurityComponent.getInstance().getSecuritySubComponent(),
            CoreComponent.getInstance().getCoreSubComponent(),
            FirestoreConfigComponent.getInstance().getFirestoreConfigSubComponent()
        ).apply {
            AmityServiceComponent.initialize(this)
        }.also {
            setupAmityApiKey()
            setupAmitySetupAppLocale()
            setupCommunityGetRegexConfig()
            setupPopularFeedConfig()
            setupMediaConfig()
        }
    }
    private fun setupAmityApiKey() {
        CoroutineScope(Dispatchers.IO + safeCoroutineExceptionHandler()).launchSafe {
            val amitySetupRepository: AmitySetupRepository =
                AmityServiceComponent.getInstance().getAmityServiceSubComponent()
                    .getAmitySetupRepository()

            val getAmityKeyUseCase: GetAmityKeyUseCase =
                SecurityComponent.getInstance().getSecuritySubComponent()
                    .getGetAmityKeyUseCase()

            amitySetupRepository.amitySetupApiKey(
                getAmityKeyUseCase.execute().toString()
            )
        }
    }

    private fun setupAmitySetupAppLocale() {
        CoroutineScope(Dispatchers.IO + safeCoroutineExceptionHandler()).launchSafe {
            val amitySetupAppLocaleRepository: AmitySetupAppLocaleRepository =
                AmityServiceComponent.getInstance().getAmityServiceSubComponent()
                    .getAmitySetupAppLocaleRepository()

            val localizationRepository: LocalizationRepository = CoreComponent.getInstance()
                .getCoreSubComponent().getLocalizationRepository()

            amitySetupAppLocaleRepository.amitySetupAppLocale(
                localizationRepository.getAppCountryCode(),
                localizationRepository.getAppLanguageCode()
            )
        }
    }

    private fun setupCommunityGetRegexConfig() {
        CoroutineScope(Dispatchers.IO + safeCoroutineExceptionHandler()).launchSafe {
            AmityServiceComponent
                .getInstance()
                .getCommunityGetRegexUseCase()
                .execute()
                .collectSafe { configJson ->
                    AmityUIKitClient.socialUISettings.maskMobilePhoneJsonString = configJson
                }
        }
    }

    private fun setupMediaConfig() {
        CoroutineScope(Dispatchers.IO + safeCoroutineExceptionHandler()).launchSafe {
            AmityServiceComponent
                .getInstance()
                .getCommunityGetMediaConfigUseCase()
                .execute()
                .collectSafe { isMediaEnable ->
                    AmityUIKitClient.socialUISettings.setMediaFeature(isMediaEnable)
                }
        }
    }

    private fun setupPopularFeedConfig() {
        CoroutineScope(Dispatchers.IO + safeCoroutineExceptionHandler()).launchSafe {
            AmityServiceComponent
                .getInstance()
                .getPopularFeedUseCase()
                .execute()
                .collectSafe { isShow ->
                    AmityUIKitClient.socialUISettings.setPopularFeedFeature(isShow)
                }
        }
    }

    private fun safeCoroutineExceptionHandler() = CoroutineExceptionHandler { context, throwable ->
        if (BuildConfig.DEBUG) {
            Thread.currentThread()
                .uncaughtExceptionHandler
                ?.uncaughtException(Thread.currentThread(), throwable)
        } else {

            val handlingExceptionMap = mapOf(
                "Key" to "AmitySetupInitializer",
                "Value" to "Problem with Coroutine caused by ${throwable.message} in context $context"
            )
            NewRelic.recordHandledException(Exception(throwable.cause), handlingExceptionMap)
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(
        SecurityInitializer::class.java,
        CoreInitializer::class.java,
        FirestoreConfigInitializer::class.java
    )
}
