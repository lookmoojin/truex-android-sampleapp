package com.tdg.onboarding.injections

import com.tdg.onboarding.di.WhatNewBindsModule
import com.tdg.onboarding.domain.usecase.GetWhatNewConfigUseCase
import com.tdg.onboarding.presentation.WebViewDialogFragment
import com.tdg.onboarding.presentation.WhatNewDialogFragment
import com.truedigital.core.injections.CoreSubComponent
import com.truedigital.share.data.firestoreconfig.injections.FirestoreConfigSubComponent
import dagger.Component
import dagger.Subcomponent
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        WhatNewBindsModule::class
    ],
    dependencies = [
        CoreSubComponent::class,
        FirestoreConfigSubComponent::class
    ]
)
interface WhatNewComponent {
    companion object {

        private lateinit var whatNewComponent: WhatNewComponent

        fun initialize(whatNewComponent: WhatNewComponent) {
            this.whatNewComponent = whatNewComponent
        }

        fun getInstance(): WhatNewComponent {
            if (!(::whatNewComponent.isInitialized)) {
                error("WhatNewComponent not initialize")
            }
            return whatNewComponent
        }
    }

    @Component.Factory
    interface Factory {
        fun create(
            coreSubComponent: CoreSubComponent,
            firestoreConfigSubComponent: FirestoreConfigSubComponent,
        ): WhatNewComponent
    }

    fun getWhatNewConfigComponent(): WhatNewConfigComponent

    fun inject(fragment: WhatNewDialogFragment)
    fun inject(fragment: WebViewDialogFragment)
}

@Subcomponent
interface WhatNewConfigComponent {
    fun getWhatNewConfigUseCase(): GetWhatNewConfigUseCase
}