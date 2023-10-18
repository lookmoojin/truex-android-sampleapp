package com.truedigital.navigation

import android.content.Context
import androidx.startup.Initializer
import com.truedigital.common.share.amityserviceconfig.AmitySetupInitializer
import com.truedigital.common.share.amityserviceconfig.injections.AmityServiceComponent
import com.truedigital.common.share.analytics.AnalyticsInitializer
import com.truedigital.common.share.analytics.injections.AnalyticsComponent
import com.truedigital.common.share.articledetails.share.ArticleDetailsShareInitializer
import com.truedigital.common.share.articledetails.share.injections.ArticleDetailsShareComponent
import com.truedigital.common.share.componentv3.ComponentV3Initializer
import com.truedigital.common.share.componentv3.injections.ComponentV3Component
import com.truedigital.common.share.data.coredata.CoreDataInitializer
import com.truedigital.common.share.data.coredata.injections.CoreDataComponent
import com.truedigital.common.share.datalegacy.DataLegacyInitializer
import com.truedigital.common.share.datalegacy.injections.DataLegacyComponent
import com.truedigital.common.share.mobileid.MobileIdInitializer
import com.truedigital.common.share.mobileid.injections.MobileIdComponent
import com.truedigital.common.share.payment.share.PaymentShareInitializer
import com.truedigital.common.share.payment.share.injections.PaymentShareComponent
import com.truedigital.common.share.payment.truemoney.TrueMoneyInitializer
import com.truedigital.common.share.payment.truemoney.injections.TrueMoneyComponent
import com.truedigital.common.share.webview.webviewshare.WebViewShareInitializer
import com.truedigital.common.share.webview.webviewshare.injections.WebViewShareComponent
import com.truedigital.core.CoreInitializer
import com.truedigital.core.injections.CoreComponent
import com.truedigital.feature.privileges.deeplink.PrivilegeDeepLinksInitializer
import com.truedigital.feature.privileges.deeplink.injections.PrivilegeDeepLinksComponent
import com.truedigital.features.commerces.share.CommerceShareInitializer
import com.truedigital.features.commerces.share.injections.CommerceShareComponent
import com.truedigital.features.communicator.common.core.CommunicatorCoreInitializer
import com.truedigital.features.communicator.common.core.injections.CommunicatorCoreComponent
import com.truedigital.features.community.share.CommunityShareInitializer
import com.truedigital.features.community.share.injections.CommunityShareComponent
import com.truedigital.features.gamifications.share.GamificationShareInitializer
import com.truedigital.features.gamifications.share.injections.GamificationShareComponent
import com.truedigital.features.homelandings.share.HomeLandingShareInitializer
import com.truedigital.features.homelandings.share.injections.HomeLandingShareComponent
import com.truedigital.features.homes.share.HomeShareInitializer
import com.truedigital.features.homes.share.injections.HomeShareComponent
import com.truedigital.features.iservicev3.share.IServiceV3ShareInitializer
import com.truedigital.features.iservicev3.share.injection.IServiceV3ShareComponent
import com.truedigital.features.listens.share.ListenShareInitializer
import com.truedigital.features.listens.share.injection.ListenShareComponent
import com.truedigital.features.netcampaigns.share.NetCampaignShareInitializer
import com.truedigital.features.netcampaigns.share.injections.NetCampaignShareComponent
import com.truedigital.features.profiles.share.ProfileShareInitializer
import com.truedigital.features.profiles.share.injections.ProfileShareComponent
import com.truedigital.features.qrscanners.share.QrScannerShareInitializer
import com.truedigital.features.qrscanners.share.injections.QrScannerShareComponent
import com.truedigital.features.reads.share.ReadShareInitializer
import com.truedigital.features.reads.share.injections.ReadShareComponent
import com.truedigital.features.setting.share.SettingShareInitializer
import com.truedigital.features.setting.share.injections.SettingShareComponent
import com.truedigital.features.specialpopups.share.SpecialPopupShareInitializer
import com.truedigital.features.specialpopups.share.injections.SpecialPopupShareComponent
import com.truedigital.features.sport.deeplinks.SportDeepLinksInitializer
import com.truedigital.features.sport.deeplinks.injections.SportDeepLinksComponent
import com.truedigital.features.truecloudsv2.share.TrueCloudsV2ShareInitializer
import com.truedigital.features.truecloudsv2.share.injections.TrueCloudsV2ShareComponent
import com.truedigital.features.watch.share.deeplinks.WatchDeepLinksInitializer
import com.truedigital.features.watch.share.deeplinks.injections.WatchDeepLinksComponent
import com.truedigital.navigation.injections.DaggerNavigationComponent
import com.truedigital.navigation.injections.NavigationComponent
import com.truedigital.navigations.share.NavigationShareInitializer
import com.truedigital.navigations.share.injections.NavigationShareComponent

class NavigationInitializer : Initializer<NavigationComponent> {

    override fun create(context: Context): NavigationComponent {
        return DaggerNavigationComponent.factory().create(
            AnalyticsComponent.getInstance().getAnalyticsSubComponent(),
            AmityServiceComponent.getInstance().getAmityServiceSubComponent(),
            ArticleDetailsShareComponent.getInstance().getArticleDetailsShareSubComponent(),
            CommerceShareComponent.getInstance().getCommerceShareSubComponent(),
            CommunicatorCoreComponent.getInstance().getCommunicatorCoreSubComponent(),
            CommunityShareComponent.getInstance().getCommunityShareSubComponent(),
            ComponentV3Component.getInstance().getComponentV3SubComponent(),
            CoreDataComponent.getInstance().getCoreDataSubComponent(),
            CoreComponent.getInstance().getCoreSubComponent(),
            DataLegacyComponent.getInstance().getDataLegacySubComponent(),
            GamificationShareComponent.getInstance().getGamificationShareSubComponent(),
            HomeLandingShareComponent.getInstance().getHomeLandingShareSubComponent(),
            HomeShareComponent.getInstance().getHomeShareSubComponent(),
            IServiceV3ShareComponent.getInstance().getIServiceV3ShareSubComponent(),
            ListenShareComponent.getInstance().getListenShareSubComponent(),
            MobileIdComponent.getInstance().getMobileIdSubComponent(),
            NavigationShareComponent.getInstance().getNavigationShareSubComponent(),
            NetCampaignShareComponent.getInstance().getNetCampaignShareSubComponent(),
            PaymentShareComponent.getInstance().getPaymentShareSubComponent(),
            PrivilegeDeepLinksComponent.getInstance().getPrivilegeDeepLinksSubComponent(),
            ProfileShareComponent.getInstance().getProfileShareSubComponent(),
            QrScannerShareComponent.getInstance().getQrScannerShareSubComponent(),
            ReadShareComponent.getInstance().getReadShareSubComponent(),
            SettingShareComponent.getInstance().getSettingShareSubComponent(),
            SpecialPopupShareComponent.getInstance().getSpecialPopupShareSubComponent(),
            SportDeepLinksComponent.getInstance().getSportDeepLinksSubComponent(),
            TrueCloudsV2ShareComponent.getInstance().getTrueCloudsV2ShareSubComponent(),
            TrueMoneyComponent.getInstance().getTrueMoneySubComponent(),
            WatchDeepLinksComponent.getInstance().getWatchDeepLinksSubComponent(),
            WebViewShareComponent.getInstance().getWebViewShareSubComponent()
        ).apply {
            NavigationComponent.initialize(this)
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(
        AnalyticsInitializer::class.java,
        AmitySetupInitializer::class.java,
        ArticleDetailsShareInitializer::class.java,
        CommerceShareInitializer::class.java,
        CommunicatorCoreInitializer::class.java,
        CommunityShareInitializer::class.java,
        ComponentV3Initializer::class.java,
        CoreDataInitializer::class.java,
        CoreInitializer::class.java,
        DataLegacyInitializer::class.java,
        GamificationShareInitializer::class.java,
        HomeLandingShareInitializer::class.java,
        HomeShareInitializer::class.java,
        IServiceV3ShareInitializer::class.java,
        ListenShareInitializer::class.java,
        MobileIdInitializer::class.java,
        NavigationShareInitializer::class.java,
        NetCampaignShareInitializer::class.java,
        PaymentShareInitializer::class.java,
        PrivilegeDeepLinksInitializer::class.java,
        ProfileShareInitializer::class.java,
        QrScannerShareInitializer::class.java,
        ReadShareInitializer::class.java,
        SettingShareInitializer::class.java,
        SpecialPopupShareInitializer::class.java,
        SportDeepLinksInitializer::class.java,
        TrueCloudsV2ShareInitializer::class.java,
        TrueMoneyInitializer::class.java,
        WatchDeepLinksInitializer::class.java,
        WebViewShareInitializer::class.java
    )
}
