package com.truedigital.navigation.di.multi.watch

import com.truedigital.common.share.data.coredata.deeplink.usecase.DecodeDeeplinkUseCase
import com.truedigital.features.watch.share.deeplinks.domain.usecase.EmbedPlayerDecodeDeeplinkUseCaseImpl
import com.truedigital.features.watch.share.deeplinks.domain.usecase.watchmain.WatchDecodeDeeplinkUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

@Module
interface NavigationWatchDeepLinksModule {

    @Binds
    @IntoSet
    fun bindsWatchDecodeDeeplinkUseCase(
        watchDecodeDeeplinkUseCaseImpl: WatchDecodeDeeplinkUseCaseImpl
    ): DecodeDeeplinkUseCase

    @Binds
    @IntoSet
    fun bindsEmbedPlayerDecodeDeeplinkUseCase(
        embedPlayerDecodeDeeplinkUseCaseImpl: EmbedPlayerDecodeDeeplinkUseCaseImpl
    ): DecodeDeeplinkUseCase
}
