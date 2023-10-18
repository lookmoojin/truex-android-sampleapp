package com.truedigital.navigation.di.multi.commerce

import com.truedigital.common.share.data.coredata.deeplink.usecase.DecodeDeeplinkUseCase
import com.truedigital.features.commerces.share.domain.usecase.deeplinks.CommerceDecodeDeeplinkUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

@Module
interface NavigationCommerceDeepLinkModule {

    @Binds
    @IntoSet
    fun bindsCommerceDecodeDeeplinkUseCaseImpl(
        commerceDecodeDeeplinkUseCaseImplImpl: CommerceDecodeDeeplinkUseCaseImpl
    ): DecodeDeeplinkUseCase
}
