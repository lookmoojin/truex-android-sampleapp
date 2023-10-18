package com.truedigital.navigation.di.multi.payment.share

import com.truedigital.common.share.data.coredata.deeplink.usecase.DecodeDeeplinkUseCase
import com.truedigital.common.share.payment.share.domain.usecase.deeplink.share.OttToWatchDecodeDeeplinkUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

@Module
interface NavigationPaymentDeepLinksModule {

    @Binds
    @IntoSet
    fun provideOttToWatchDecodeDeeplinkUseCase(
        ottToWatchDecodeDeeplinkUseCaseImpl: OttToWatchDecodeDeeplinkUseCaseImpl
    ): DecodeDeeplinkUseCase
}
