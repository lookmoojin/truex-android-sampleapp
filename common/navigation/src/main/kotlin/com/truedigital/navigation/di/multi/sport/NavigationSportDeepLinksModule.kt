package com.truedigital.navigation.di.multi.sport

import com.truedigital.common.share.data.coredata.deeplink.usecase.DecodeDeeplinkUseCase
import com.truedigital.features.sport.deeplinks.domain.usecase.deeplinks.SportDecodeDeeplinkUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

@Module
interface NavigationSportDeepLinksModule {

    @Binds
    @IntoSet
    fun bindsSportDecodeDeeplinkUseCaseImpl(
        sportDecodeDeeplinkUseCaseImpl: SportDecodeDeeplinkUseCaseImpl
    ): DecodeDeeplinkUseCase
}
