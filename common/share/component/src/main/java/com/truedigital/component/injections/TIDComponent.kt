package com.truedigital.component.injections

import com.truedigital.common.share.analytics.injections.AnalyticsSubComponent
import com.truedigital.common.share.currentdate.injections.CurrentDateSubComponent
import com.truedigital.common.share.datalegacy.injections.DataLegacySubComponent
import com.truedigital.common.share.nativeshare.injections.LinkGeneratorSubComponent
import com.truedigital.component.base.core.CoreActivity
import com.truedigital.component.view.AppButton
import com.truedigital.component.view.AppEditText
import com.truedigital.component.view.AppTextView
import com.truedigital.core.injections.CoreSubComponent
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [],
    dependencies = [
        AnalyticsSubComponent::class,
        CoreSubComponent::class,
        CurrentDateSubComponent::class,
        DataLegacySubComponent::class,
        LinkGeneratorSubComponent::class,

    ]
)
interface TIDComponent {

    companion object {

        private lateinit var tIDComponent: TIDComponent

        fun initialize(tIDComponent: TIDComponent) {
            this.tIDComponent = tIDComponent
        }

        fun getInstance(): TIDComponent {
            if (!(::tIDComponent.isInitialized)) {
                error("TIDComponent not initialize")
            }
            return tIDComponent
        }
    }

    @Component.Factory
    interface Factory {
        fun create(
            analyticsSubComponent: AnalyticsSubComponent,
            coreSubComponent: CoreSubComponent,
            currentDateSubComponent: CurrentDateSubComponent,
            dataLegacySubComponent: DataLegacySubComponent,
            linkGeneratorSubComponent: LinkGeneratorSubComponent,
        ): TIDComponent
    }

    // Injects
    fun inject(appButton: AppButton)
    fun inject(appEditText: AppEditText)
    fun inject(appTextView: AppTextView)
    fun inject(coreActivity: CoreActivity)
}
