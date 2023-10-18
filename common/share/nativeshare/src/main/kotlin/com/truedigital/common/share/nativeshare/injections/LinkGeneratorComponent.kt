package com.truedigital.common.share.nativeshare.injections

import com.truedigital.common.share.datalegacy.injections.DataLegacySubComponent
import com.truedigital.common.share.nativeshare.di.LinkGeneratorModule
import com.truedigital.common.share.nativeshare.domain.usecase.dynamiclink.DynamicLinkGeneratorUseCaseImpl
import com.truedigital.common.share.nativeshare.utils.DynamicLinkGenerator
import com.truedigital.common.share.nativeshare.utils.OneLinkGenerator
import com.truedigital.core.injections.CoreSubComponent
import dagger.Component
import dagger.Subcomponent
import javax.inject.Singleton

@Singleton
@Component(
    modules = [LinkGeneratorModule::class],
    dependencies = [
        CoreSubComponent::class,
        DataLegacySubComponent::class
    ]
)
interface LinkGeneratorComponent {

    companion object {
        private lateinit var linkGeneratorComponent: LinkGeneratorComponent

        fun initialize(linkGeneratorComponent: LinkGeneratorComponent) {
            this.linkGeneratorComponent = linkGeneratorComponent
        }

        fun getInstance(): LinkGeneratorComponent {
            if (!(::linkGeneratorComponent.isInitialized)) {
                error("LinkGeneratorComponent not initialize")
            }
            return linkGeneratorComponent
        }
    }

    @Component.Factory
    interface Factory {
        fun create(
            coreSubComponent: CoreSubComponent,
            dataLegacySubComponent: DataLegacySubComponent
        ): LinkGeneratorComponent
    }

    fun getLinkGeneratorSubComponent(): LinkGeneratorSubComponent
}

@Subcomponent
interface LinkGeneratorSubComponent {
    fun getDynamicLinkGenerator(): DynamicLinkGenerator
    fun getOneLinkGenerator(): OneLinkGenerator
    fun getDynamicLinkGeneratorUseCase(): DynamicLinkGeneratorUseCaseImpl
}
