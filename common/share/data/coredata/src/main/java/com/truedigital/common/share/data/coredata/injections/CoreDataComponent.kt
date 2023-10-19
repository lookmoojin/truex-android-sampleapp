package com.truedigital.common.share.data.coredata.injections

import com.truedigital.common.share.data.coredata.data.api.CmsContentApiInterface
import com.truedigital.common.share.data.coredata.data.api.CmsShelvesApiInterface
import com.truedigital.common.share.data.coredata.data.repository.CmsContentRepository
import com.truedigital.common.share.data.coredata.data.repository.CmsShelvesRepository
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
import com.truedigital.common.share.datalegacy.injections.DataLegacySubComponent
import com.truedigital.core.injections.CoreSubComponent
import com.truedigital.share.data.firestoreconfig.injections.FirestoreConfigSubComponent
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
        FirestoreConfigSubComponent::class
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
            firestoreConfigSubComponent: FirestoreConfigSubComponent
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

    // Use cases
    fun getGenerateDeeplinkFormatUseCase(): GenerateDeeplinkFormatUseCase
    fun getIsDomainDeeplinkUrlUseCase(): IsDomainDeeplinkUrlUseCase
    fun getIsDynamicUrlUseCase(): IsDynamicUrlUseCase
    fun getIsInternalDeeplinkUseCase(): IsInternalDeeplinkUseCase
    fun getDecodeShortLinkUseCase(): DecodeShortLinkUseCase
    fun getGetBaseShelfUseCase(): GetBaseShelfUseCase
    fun getValidateDeeplinkUrlUseCase(): ValidateDeeplinkUrlUseCase
}
