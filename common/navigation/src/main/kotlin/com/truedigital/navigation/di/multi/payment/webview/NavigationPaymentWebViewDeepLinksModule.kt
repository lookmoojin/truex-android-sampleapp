package com.truedigital.navigation.di.multi.payment.webview

import com.truedigital.common.share.data.coredata.deeplink.usecase.DecodeDeeplinkUseCase
import com.truedigital.common.share.payment.share.domain.usecase.deeplink.webview.CheckoutDecodeDeepLinkUseCaseImpl
import com.truedigital.common.share.payment.share.domain.usecase.deeplink.webview.PrivilegeDecodeDeepLinkUseCaseImpl
import com.truedigital.common.share.payment.share.domain.usecase.deeplink.webview.TrueBindingDecodeDeepLinkUseCaseImpl
import com.truedigital.common.share.payment.share.domain.usecase.deeplink.webview.TrueVisionDecodeDeepLinkUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

@Module
interface NavigationPaymentWebViewDeepLinksModule {

    @Binds
    @IntoSet
    fun bindsCheckoutDecodeDeepLinkUseCase(
        decodeDeeplinkUseCaseImpl: CheckoutDecodeDeepLinkUseCaseImpl
    ): DecodeDeeplinkUseCase

    @Binds
    @IntoSet
    fun bindsPrivilegeDecodeDeepLinkUseCase(
        decodeDeeplinkUseCaseImpl: PrivilegeDecodeDeepLinkUseCaseImpl
    ): DecodeDeeplinkUseCase

    @Binds
    @IntoSet
    fun bindsTrueBindingDecodeDeepLinkUseCase(
        decodeDeeplinkUseCaseImpl: TrueBindingDecodeDeepLinkUseCaseImpl
    ): DecodeDeeplinkUseCase

    @Binds
    @IntoSet
    fun bindsTrueVisionDecodeDeepLinkUseCase(
        decodeDeeplinkUseCaseImpl: TrueVisionDecodeDeepLinkUseCaseImpl
    ): DecodeDeeplinkUseCase
}
