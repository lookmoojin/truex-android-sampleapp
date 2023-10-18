package com.truedigital.navigation.di.multi

import com.truedigital.navigation.di.multi.article.NavigationArticleDeepLinksModule
import com.truedigital.navigation.di.multi.commerce.NavigationCommerceDeepLinkModule
import com.truedigital.navigation.di.multi.community.NavigationCommunityDeepLinksModule
import com.truedigital.navigation.di.multi.comunicators.NavigationCommunicatorDeepLinksModule
import com.truedigital.navigation.di.multi.comunicators.myservice.NavigationMyServiceDeepLinksModule
import com.truedigital.navigation.di.multi.gamification.NavigationGamificationDeepLinksModule
import com.truedigital.navigation.di.multi.homelanding.NavigationHomeLandingDeepLinksModule
import com.truedigital.navigation.di.multi.iservicev3.NavigationIServiceV3DeepLinksModule
import com.truedigital.navigation.di.multi.listen.NavigationListenDeepLinksModule
import com.truedigital.navigation.di.multi.mobileid.NavigationMobileIdDeeplinkModule
import com.truedigital.navigation.di.multi.netcampaign.NavigationNetCampaignDeepLinksModule
import com.truedigital.navigation.di.multi.payment.share.NavigationPaymentDeepLinksModule
import com.truedigital.navigation.di.multi.payment.webview.NavigationPaymentWebViewDeepLinksModule
import com.truedigital.navigation.di.multi.privilege.NavigationPrivilegeDeeplinkModule
import com.truedigital.navigation.di.multi.profile.NavigationProfileMultiBindingModule
import com.truedigital.navigation.di.multi.qrscanner.NavigationQrScannerDeeplinkModule
import com.truedigital.navigation.di.multi.read.NavigationReadDeeplinkModule
import com.truedigital.navigation.di.multi.setting.NavigationSettingDeepLinksModule
import com.truedigital.navigation.di.multi.specialpopup.NavigationSpecialPopupDeepLinksModule
import com.truedigital.navigation.di.multi.sport.NavigationSportDeepLinksModule
import com.truedigital.navigation.di.multi.trueclouds.v2.NavigationTrueCloudsV2DeeplinkModule
import com.truedigital.navigation.di.multi.trueclouds.v3.NavigationTrueCloudsV3DeeplinkModule
import com.truedigital.navigation.di.multi.truemoney.NavigationTrueMoneyDeepLinksModule
import com.truedigital.navigation.di.multi.watch.NavigationWatchDeepLinksModule
import com.truedigital.navigation.di.multi.webview.NavigationWebViewDeepLinksModule
import dagger.Module

@Module(
    includes = [
        NavigationArticleDeepLinksModule::class,
        NavigationCommerceDeepLinkModule::class,
        NavigationCommunicatorDeepLinksModule::class,
        NavigationCommunityDeepLinksModule::class,
        NavigationGamificationDeepLinksModule::class,
        NavigationHomeLandingDeepLinksModule::class,
        NavigationHomeDeepLinksModule::class,
        NavigationIServiceV3DeepLinksModule::class,
        NavigationListenDeepLinksModule::class,
        NavigationMobileIdDeeplinkModule::class,
        NavigationMyServiceDeepLinksModule::class,
        NavigationNetCampaignDeepLinksModule::class,
        NavigationPaymentDeepLinksModule::class,
        NavigationPaymentWebViewDeepLinksModule::class,
        NavigationPrivilegeDeeplinkModule::class,
        NavigationProfileMultiBindingModule::class,
        NavigationQrScannerDeeplinkModule::class,
        NavigationReadDeeplinkModule::class,
        NavigationSettingDeepLinksModule::class,
        NavigationSpecialPopupDeepLinksModule::class,
        NavigationSportDeepLinksModule::class,
        NavigationTrueCloudsV2DeeplinkModule::class,
        NavigationTrueCloudsV3DeeplinkModule::class,
        NavigationTrueMoneyDeepLinksModule::class,
        NavigationWatchDeepLinksModule::class,
        NavigationWebViewDeepLinksModule::class
    ]
)
object NavigationMultiBindingDeepLinksModule
