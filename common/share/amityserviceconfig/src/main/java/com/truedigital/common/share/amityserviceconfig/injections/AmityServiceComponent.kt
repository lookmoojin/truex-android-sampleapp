package com.truedigital.common.share.amityserviceconfig.injections

import com.truedigital.common.share.amityserviceconfig.data.repository.AmityConfigRepository
import com.truedigital.common.share.amityserviceconfig.di.AmityServiceBindsModule
import com.truedigital.common.share.amityserviceconfig.di.AmityServiceProvidesModule
import com.truedigital.common.share.amityserviceconfig.domain.repository.AmityEnableSecureModeRepository
import com.truedigital.common.share.amityserviceconfig.domain.repository.AmitySetupAppLocaleRepository
import com.truedigital.common.share.amityserviceconfig.domain.repository.AmitySetupRepository
import com.truedigital.common.share.amityserviceconfig.domain.usecase.CommunityGetMediaConfigUseCase
import com.truedigital.common.share.amityserviceconfig.domain.usecase.CommunityGetRegexUseCase
import com.truedigital.common.share.amityserviceconfig.domain.usecase.PopularFeedConfigUseCase
import com.truedigital.common.share.security.injections.SecuritySubComponent
import com.truedigital.core.injections.CoreSubComponent
import com.truedigital.share.data.firestoreconfig.injections.FirestoreConfigSubComponent
import dagger.Component
import dagger.Subcomponent
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AmityServiceBindsModule::class,
        AmityServiceProvidesModule::class
    ],
    dependencies = [
        SecuritySubComponent::class,
        CoreSubComponent::class,
        FirestoreConfigSubComponent::class
    ]
)
interface AmityServiceComponent {

    companion object {
        private lateinit var amityServiceComponent: AmityServiceComponent

        fun initialize(amityServiceComponent: AmityServiceComponent) {
            this.amityServiceComponent = amityServiceComponent
        }

        fun getInstance(): AmityServiceComponent {
            if (!(::amityServiceComponent.isInitialized)) {
                error("AmityServiceComponent not initialize")
            }
            return amityServiceComponent
        }
    }

    @Component.Factory
    interface Factory {
        fun create(
            securitySubComponent: SecuritySubComponent,
            coreSubComponent: CoreSubComponent,
            firestoreConfigSubComponent: FirestoreConfigSubComponent
        ): AmityServiceComponent
    }

    fun getCommunityGetRegexUseCase(): CommunityGetRegexUseCase

    fun getPopularFeedUseCase(): PopularFeedConfigUseCase

    fun getAmityServiceSubComponent(): AmityServiceSubComponent
    fun getCommunityGetMediaConfigUseCase(): CommunityGetMediaConfigUseCase
}

@Subcomponent
interface AmityServiceSubComponent {
    fun getAmityConfigRepository(): AmityConfigRepository
    fun getEnableSecureModeRepository(): AmityEnableSecureModeRepository
    fun getAmitySetupRepository(): AmitySetupRepository
    fun getAmitySetupAppLocaleRepository(): AmitySetupAppLocaleRepository
}
