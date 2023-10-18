package com.truedigital.navigation.di.multi.read

import com.truedigital.common.share.data.coredata.deeplink.usecase.DecodeDeeplinkUseCase
import com.truedigital.features.reads.share.domain.usecase.deeplinks.ReadDecodeDeeplinkUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

@Module
interface NavigationReadDeeplinkModule {

    @Binds
    @IntoSet
    fun bindsReadDecodeDeeplinkUseCase(
        readDecodeDeeplinkUseCaseImpl: ReadDecodeDeeplinkUseCaseImpl
    ): DecodeDeeplinkUseCase
}
