package com.truedigital.navigation.di.multi.mobileid

import com.truedigital.common.share.data.coredata.deeplink.usecase.DecodeDeeplinkUseCase
import com.truedigital.common.share.mobileid.deeplink.MobileIdDecodeDeeplinkUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

@Module
interface NavigationMobileIdDeeplinkModule {

    @Binds
    @IntoSet
    fun bindsMobileIdDecodeDeeplinkUseCase(
        mobileIdDecodeDeeplinkUseCaseImpl: MobileIdDecodeDeeplinkUseCaseImpl
    ): DecodeDeeplinkUseCase
}
