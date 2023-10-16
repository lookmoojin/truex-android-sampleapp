package com.truedigital.navigation.di.multi.specialpopup

import com.truedigital.common.share.data.coredata.deeplink.usecase.DecodeDeeplinkUseCase
import com.truedigital.features.specialpopups.share.domain.usecase.deeplinks.SpecialPopupDecodeDeeplinkUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

@Module
interface NavigationSpecialPopupDeepLinksModule {

    @Binds
    @IntoSet
    fun bindsSpecialPopupDecodeDeeplinkUseCase(
        specialPopupDecodeDeeplinkUseCaseImpl: SpecialPopupDecodeDeeplinkUseCaseImpl
    ): DecodeDeeplinkUseCase
}
