package com.truedigital.navigation.di.multi.webview

import com.truedigital.common.share.data.coredata.deeplink.usecase.DecodeDeeplinkUseCase
import com.truedigital.common.share.webview.webviewshare.deeplink.WebViewDecodeDeeplinkUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

@Module
interface NavigationWebViewDeepLinksModule {

    @Binds
    @IntoSet
    fun bindsWebViewDecodeDeeplinkUseCase(
        webViewDecodeDeeplinkUseCaseImpl: WebViewDecodeDeeplinkUseCaseImpl
    ): DecodeDeeplinkUseCase
}
