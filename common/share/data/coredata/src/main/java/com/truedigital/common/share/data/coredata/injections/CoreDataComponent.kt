package com.truedigital.common.share.data.coredata.injections

import com.truedigital.common.share.data.coredata.data.api.CmsContentApiInterface
import com.truedigital.common.share.data.coredata.data.api.CmsShelvesApiInterface
import com.truedigital.common.share.data.coredata.data.repository.CmsContentRepository
import com.truedigital.common.share.data.coredata.data.repository.CmsShelvesRepository
import com.truedigital.common.share.data.coredata.data.repository.PreLoadAdsCacheRepository
import com.truedigital.common.share.data.coredata.data.repository.RecommendPersonalizeRepository
import com.truedigital.common.share.data.coredata.data.repository.ShortPersonalizeRepository
import com.truedigital.common.share.data.coredata.deeplink.usecase.DecodeShortLinkUseCase
import com.truedigital.common.share.data.coredata.deeplink.usecase.GenerateDeeplinkFormatUseCase
import com.truedigital.common.share.data.coredata.deeplink.usecase.IsDomainDeeplinkUrlUseCase
import com.truedigital.common.share.data.coredata.deeplink.usecase.IsDynamicUrlUseCase
import com.truedigital.common.share.data.coredata.deeplink.usecase.IsInternalDeeplinkUseCase
import com.truedigital.common.share.data.coredata.deeplink.usecase.ValidateDeeplinkUrlUseCase
import com.truedigital.common.share.data.coredata.di.DataBindsModule
import com.truedigital.common.share.data.coredata.di.DataContentBindsModule
import com.truedigital.common.share.data.coredata.di.DataContentModule
import com.truedigital.common.share.data.coredata.di.DataModule
import com.truedigital.common.share.data.coredata.di.DataMultiBindsModule
import com.truedigital.common.share.data.coredata.domain.GetBaseShelfUseCase
import com.truedigital.common.share.data.coredata.domain.GetEnableCommunicatorUseCase
import com.truedigital.common.share.data.coredata.domain.GetEnablePrivilegeUseCase
import com.truedigital.common.share.data.coredata.domain.SendAnalyticScreenViewModel
import com.truedigital.common.share.data.coredata.domain.usecase.CountViewUseCase
import com.truedigital.common.share.data.coredata.domain.usecase.GetPreLoadAdsViewUseCase
import com.truedigital.common.share.data.coredata.domain.usecase.PreLoadAdsViewUseCase
import com.truedigital.common.share.datalegacy.injections.DataLegacySubComponent
import com.truedigital.core.injections.CoreSubComponent
import com.truedigital.share.data.firestoreconfig.injections.FirestoreConfigSubComponent
import com.truedigital.share.data.prasarn.injections.PrasarnSubComponent
import dagger.Component
import dagger.Subcomponent
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        DataContentModule::class,
        DataContentBindsModule::class,
        DataModule::class,
        DataBindsModule::class,
        DataMultiBindsModule::class
    ],
    dependencies = [
        CoreSubComponent::class,
        DataLegacySubComponent::class,
        FirestoreConfigSubComponent::class,
        PrasarnSubComponent::class
    ]
)
interface CoreDataComponent {

    companion object {
        private lateinit var coreDataComponent: CoreDataComponent

        fun initialize(coreDataComponent: CoreDataComponent) {
            this.coreDataComponent = coreDataComponent
        }

        fun getInstance(): CoreDataComponent {
            if (!(::coreDataComponent.isInitialized)) {
                error("CoreDataComponent not initialize")
            }
            return coreDataComponent
        }
    }

    @Component.Factory
    interface Factory {
        fun create(
            coreSubComponent: CoreSubComponent,
            dataLegacySubComponent: DataLegacySubComponent,
            firestoreConfigSubComponent: FirestoreConfigSubComponent,
            prasarnSubComponent: PrasarnSubComponent
        ): CoreDataComponent
    }

    fun getCoreDataSubComponent(): CoreDataSubComponent
}

@Subcomponent
interface CoreDataSubComponent {
    fun getCmsShelvesApiInterface(): CmsShelvesApiInterface
    fun getCmsContentApiInterface(): CmsContentApiInterface

    // Repositories
    fun getCmsContentRepository(): CmsContentRepository
    fun getCmsShelvesRepository(): CmsShelvesRepository
    fun getRecommendPersonalizeRepository(): RecommendPersonalizeRepository
    fun getShortPersonalizeRepository(): ShortPersonalizeRepository
    fun getPreLoadAdsCacheRepository(): PreLoadAdsCacheRepository

    // Use cases
    fun getGenerateDeeplinkFormatUseCase(): GenerateDeeplinkFormatUseCase
    fun getIsDomainDeeplinkUrlUseCase(): IsDomainDeeplinkUrlUseCase
    fun getIsDynamicUrlUseCase(): IsDynamicUrlUseCase
    fun getIsInternalDeeplinkUseCase(): IsInternalDeeplinkUseCase
    fun getDecodeShortLinkUseCase(): DecodeShortLinkUseCase
    fun getGetEnableCommunicatorUseCase(): GetEnableCommunicatorUseCase
    fun getGetBaseShelfUseCase(): GetBaseShelfUseCase
    fun getGetEnablePrivilegeUseCase(): GetEnablePrivilegeUseCase
    fun getValidateDeeplinkUrlUseCase(): ValidateDeeplinkUrlUseCase
    fun getPreLoadAdsViewUseCase(): PreLoadAdsViewUseCase
    fun getGetPreLoadAdsViewUseCase(): GetPreLoadAdsViewUseCase
    fun getCountViewUseCase(): CountViewUseCase

    // ViewModel
    fun getSendAnalyticScreenViewModel(): SendAnalyticScreenViewModel
}
