package com.truedigital.common.share.componentv3.injections

import com.truedigital.common.share.analytics.injections.AnalyticsSubComponent
import com.truedigital.common.share.componentv3.di.BadgeWidgetBindsModule
import com.truedigital.common.share.componentv3.di.BadgeWidgetModule
import com.truedigital.common.share.componentv3.di.CommunityShareBindsModule
import com.truedigital.common.share.componentv3.di.CommunityTabModule
import com.truedigital.common.share.componentv3.di.ComponentV3ViewModelModules
import com.truedigital.common.share.componentv3.di.SearchAnimationBindsModule
import com.truedigital.common.share.componentv3.di.TruePointWidgetBindsModule
import com.truedigital.common.share.componentv3.di.WeMallShelfComponentBindsModule
import com.truedigital.common.share.componentv3.di.WeMallShelfComponentModule
import com.truedigital.common.share.componentv3.widget.CommonAppBar
import com.truedigital.common.share.componentv3.widget.CommonSearchBar
import com.truedigital.common.share.componentv3.widget.ads.domain.usecase.GetAdsPreLoadConfigUseCase
import com.truedigital.common.share.componentv3.widget.badge.domain.usecase.CountInboxUseCase
import com.truedigital.common.share.componentv3.widget.badge.domain.usecase.GetInboxEnableUseCase
import com.truedigital.common.share.componentv3.widget.badge.domain.usecase.GetServiceCountInboxUseCase
import com.truedigital.common.share.componentv3.widget.feedmenutab.CommunityTab
import com.truedigital.common.share.componentv3.widget.feedmenutab.data.GetCommunityTabConfigRepository
import com.truedigital.common.share.componentv3.widget.feedmenutab.domain.usecase.GetAmityConfigUseCase
import com.truedigital.common.share.componentv3.widget.feedmenutab.domain.usecase.GetCommunityTabConfigUseCase
import com.truedigital.common.share.componentv3.widget.feedmenutab.presentation.CommunityTabViewModel
import com.truedigital.common.share.componentv3.widget.header.CallIconWidget
import com.truedigital.common.share.componentv3.widget.searchanimation.presentation.SearchAnimationViewModel
import com.truedigital.common.share.componentv3.widget.truepoint.data.TruePointCardStyleCacheRepository
import com.truedigital.common.share.componentv3.widget.truepoint.domain.GetTruePointTitleUseCase
import com.truedigital.common.share.componentv3.widget.truepoint.presentation.TruePointWidgetViewModel
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.presentation.WeMallShelfWidget
import com.truedigital.common.share.currentdate.injections.CurrentDateSubComponent
import com.truedigital.common.share.data.coredata.injections.CoreDataSubComponent
import com.truedigital.common.share.datalegacy.injections.DataLegacySubComponent
import com.truedigital.core.injections.CoreSubComponent
import com.truedigital.share.data.firestoreconfig.injections.FirestoreConfigSubComponent
import dagger.Component
import dagger.Subcomponent
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        BadgeWidgetModule::class,
        BadgeWidgetBindsModule::class,
        CommunityTabModule::class,
        ComponentV3ViewModelModules::class,
        SearchAnimationBindsModule::class,
        TruePointWidgetBindsModule::class,
        WeMallShelfComponentModule::class,
        WeMallShelfComponentBindsModule::class,
        CommunityShareBindsModule::class
    ],
    dependencies = [
        AnalyticsSubComponent::class,
        CoreDataSubComponent::class,
        CoreSubComponent::class,
        DataLegacySubComponent::class,
        FirestoreConfigSubComponent::class,
        CurrentDateSubComponent::class
    ]
)
interface ComponentV3Component {

    companion object {

        private lateinit var componentV3Component: ComponentV3Component

        fun initialize(componentV3Component: ComponentV3Component) {
            this.componentV3Component = componentV3Component
        }

        fun getInstance(): ComponentV3Component {
            if (!(::componentV3Component.isInitialized)) {
                error("ComponentV3Component not initialize")
            }
            return componentV3Component
        }
    }

    @Component.Factory
    interface Factory {
        fun create(
            analyticsSubComponent: AnalyticsSubComponent,
            coreDataSubComponent: CoreDataSubComponent,
            coreSubComponent: CoreSubComponent,
            dataLegacySubComponent: DataLegacySubComponent,
            firestoreConfigSubComponent: FirestoreConfigSubComponent,
            currentDateSubComponent: CurrentDateSubComponent
        ): ComponentV3Component
    }

    fun inject(widget: WeMallShelfWidget)
    fun inject(widget: CallIconWidget)
    fun inject(appBar: CommonAppBar)
    fun inject(searchBar: CommonSearchBar)
    fun inject(view: CommunityTab)

    fun getComponentV3SubComponent(): ComponentV3SubComponent
}

@Subcomponent
interface ComponentV3SubComponent {
    fun getTruePointCardStyleCacheRepository(): TruePointCardStyleCacheRepository
    fun getGetCommunityTabConfigRepository(): GetCommunityTabConfigRepository

    // Use cases
    fun getGetServiceCountInboxUseCase(): GetServiceCountInboxUseCase
    fun getGetInboxEnableUseCase(): GetInboxEnableUseCase
    fun getGetCommunityTabConfigUseCase(): GetCommunityTabConfigUseCase
    fun getGetAmityConfigUseCase(): GetAmityConfigUseCase
    fun getGetTruePointTitleUseCase(): GetTruePointTitleUseCase
    fun getCountInboxUseCase(): CountInboxUseCase
    fun getGetAdsPreLoadConfigUseCase(): GetAdsPreLoadConfigUseCase

    // ViewModel
    fun getCommunityTabViewModel(): CommunityTabViewModel
    fun getSearchAnimationViewModel(): SearchAnimationViewModel
    fun getTruePointWidgetViewModel(): TruePointWidgetViewModel
}
