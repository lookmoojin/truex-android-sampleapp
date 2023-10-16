package com.truedigital.navigation.injections

import com.truedigital.common.share.amityserviceconfig.injections.AmityServiceSubComponent
import com.truedigital.common.share.analytics.injections.AnalyticsSubComponent
import com.truedigital.common.share.articledetails.share.injections.ArticleDetailsShareSubComponent
import com.truedigital.common.share.componentv3.injections.ComponentV3SubComponent
import com.truedigital.common.share.data.coredata.injections.CoreDataSubComponent
import com.truedigital.common.share.datalegacy.injections.DataLegacySubComponent
import com.truedigital.common.share.mobileid.injections.MobileIdSubComponent
import com.truedigital.common.share.payment.share.injections.PaymentShareSubComponent
import com.truedigital.common.share.payment.truemoney.injections.TrueMoneySubComponent
import com.truedigital.common.share.webview.webviewshare.injections.WebViewShareSubComponent
import com.truedigital.core.injections.CoreSubComponent
import com.truedigital.feature.privileges.deeplink.injections.PrivilegeDeepLinksSubComponent
import com.truedigital.features.commerces.share.injections.CommerceShareSubComponent
import com.truedigital.features.communicator.common.core.injections.CommunicatorCoreSubComponent
import com.truedigital.features.community.share.injections.CommunityShareSubComponent
import com.truedigital.features.gamifications.share.injections.GamificationShareSubComponent
import com.truedigital.features.homelandings.share.injections.HomeLandingShareSubComponent
import com.truedigital.features.homes.share.injections.HomeShareSubComponent
import com.truedigital.features.iservicev3.share.injection.IServiceV3ShareSubComponent
import com.truedigital.features.listens.share.injection.ListenShareSubComponent
import com.truedigital.features.netcampaigns.share.injections.NetCampaignShareSubComponent
import com.truedigital.features.profiles.share.injections.ProfileShareSubComponent
import com.truedigital.features.qrscanners.share.injections.QrScannerShareSubComponent
import com.truedigital.features.reads.share.injections.ReadShareSubComponent
import com.truedigital.features.setting.share.injections.SettingShareSubComponent
import com.truedigital.features.specialpopups.share.injections.SpecialPopupShareSubComponent
import com.truedigital.features.sport.deeplinks.injections.SportDeepLinksSubComponent
import com.truedigital.features.truecloudsv2.share.injections.TrueCloudsV2ShareSubComponent
import com.truedigital.features.watch.share.deeplinks.injections.WatchDeepLinksSubComponent
import com.truedigital.navigation.data.repository.router.NavigationRouterRepository
import com.truedigital.navigation.deeplink.TrackFirebaseAnalyticsDeeplinkUseCase
import com.truedigital.navigation.di.NavigationMainBindsModule
import com.truedigital.navigation.di.NavigationMultiBindingModule
import com.truedigital.navigation.di.NavigationProvidesModule
import com.truedigital.navigation.di.NavigationRouterBindingModule
import com.truedigital.navigation.di.NavigationRoutingBindsModule
import com.truedigital.navigation.di.NavigationRoutingModule
import com.truedigital.navigation.di.multi.NavigationMultiBindingDeepLinksModule
import com.truedigital.navigation.domain.usecase.SetCrossRouterNavControllerUseCase
import com.truedigital.navigation.domain.usecase.SetRouterSecondaryToNavControllerUseCase
import com.truedigital.navigation.domain.usecase.SetRouterToNavControllerUseCase
import com.truedigital.navigation.router.CrossRouter
import com.truedigital.navigation.usecase.GetCountryUseCase
import com.truedigital.navigation.usecase.GetInterContentUseCase
import com.truedigital.navigation.usecase.GetNavigationControllerRepository
import com.truedigital.navigations.share.injections.NavigationShareSubComponent
import dagger.Component
import dagger.Subcomponent
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        NavigationMainBindsModule::class,
        NavigationProvidesModule::class,
        NavigationRouterBindingModule::class,
        NavigationRoutingModule::class,
        NavigationRoutingBindsModule::class,
        NavigationMultiBindingModule::class,
        NavigationMultiBindingDeepLinksModule::class
    ],
    dependencies = [
        AnalyticsSubComponent::class,
        AmityServiceSubComponent::class,
        ArticleDetailsShareSubComponent::class,
        CommerceShareSubComponent::class,
        CommunicatorCoreSubComponent::class,
        CommunicatorCoreSubComponent::class,
        CommunityShareSubComponent::class,
        ComponentV3SubComponent::class,
        CoreDataSubComponent::class,
        CoreSubComponent::class,
        DataLegacySubComponent::class,
        GamificationShareSubComponent::class,
        HomeLandingShareSubComponent::class,
        HomeShareSubComponent::class,
        IServiceV3ShareSubComponent::class,
        ListenShareSubComponent::class,
        MobileIdSubComponent::class,
        NavigationShareSubComponent::class,
        NetCampaignShareSubComponent::class,
        PaymentShareSubComponent::class,
        PrivilegeDeepLinksSubComponent::class,
        ProfileShareSubComponent::class,
        QrScannerShareSubComponent::class,
        ReadShareSubComponent::class,
        SettingShareSubComponent::class,
        SpecialPopupShareSubComponent::class,
        SportDeepLinksSubComponent::class,
        TrueCloudsV2ShareSubComponent::class,
        TrueMoneySubComponent::class,
        WatchDeepLinksSubComponent::class,
        WebViewShareSubComponent::class,
    ],
)
interface NavigationComponent {

    companion object {
        private lateinit var navigationComponent: NavigationComponent

        fun initialize(navigationComponent: NavigationComponent) {
            this.navigationComponent = navigationComponent
        }

        fun getInstance(): NavigationComponent {
            if (!(::navigationComponent.isInitialized)) {
                error("NavigationComponent not initialize")
            }
            return navigationComponent
        }
    }

    @Component.Factory
    interface Factory {
        fun create(
            analyticsSubComponent: AnalyticsSubComponent,
            amityServiceSubComponent: AmityServiceSubComponent,
            articleDetailsShareSubComponent: ArticleDetailsShareSubComponent,
            commerceShareSubComponent: CommerceShareSubComponent,
            communicatorCoreSubComponent: CommunicatorCoreSubComponent,
            communityShareSubComponent: CommunityShareSubComponent,
            componentV3SubComponent: ComponentV3SubComponent,
            coreDataSubComponent: CoreDataSubComponent,
            coreSubComponent: CoreSubComponent,
            dataLegacySubComponent: DataLegacySubComponent,
            gamificationShareSubComponent: GamificationShareSubComponent,
            homeLandingShareSubComponent: HomeLandingShareSubComponent,
            homeShareSubComponent: HomeShareSubComponent,
            iServiceV3ShareSubComponent: IServiceV3ShareSubComponent,
            listenShareSubComponent: ListenShareSubComponent,
            mobileIdSubComponent: MobileIdSubComponent,
            navigationShareSubComponent: NavigationShareSubComponent,
            netCampaignShareSubComponent: NetCampaignShareSubComponent,
            paymentShareSubComponent: PaymentShareSubComponent,
            privilegeDeepLinksSubComponent: PrivilegeDeepLinksSubComponent,
            profileShareSubComponent: ProfileShareSubComponent,
            qrScannerShareSubComponent: QrScannerShareSubComponent,
            readShareSubComponent: ReadShareSubComponent,
            settingShareSubComponent: SettingShareSubComponent,
            specialPopupShareSubComponent: SpecialPopupShareSubComponent,
            sportDeepLinksSubComponent: SportDeepLinksSubComponent,
            trueCloudsV2ShareSubComponent: TrueCloudsV2ShareSubComponent,
            trueMoneySubComponent: TrueMoneySubComponent,
            watchDeepLinksSubComponent: WatchDeepLinksSubComponent,
            webViewShareSubComponent: WebViewShareSubComponent,
        ): NavigationComponent
    }

    fun getNavigationSubComponent(): NavigationSubComponent
}

@Subcomponent
interface NavigationSubComponent {

    fun getNavigationRouterRepository(): NavigationRouterRepository
    fun getCrossRouter(): CrossRouter

    fun getGetCountryUseCase(): GetCountryUseCase

    // Use cases
    fun getSetRouterToNavControllerUseCase(): SetRouterToNavControllerUseCase
    fun getSetRouterSecondaryToNavControllerUseCase(): SetRouterSecondaryToNavControllerUseCase
    fun getSetCrossRouterNavControllerUseCase(): SetCrossRouterNavControllerUseCase
    fun getGetInterContentUseCase(): GetInterContentUseCase
    fun getTrackFirebaseAnalyticsDeeplinkUseCase(): TrackFirebaseAnalyticsDeeplinkUseCase

    fun getGetNavigationControllerUseCase(): GetNavigationControllerRepository
}
