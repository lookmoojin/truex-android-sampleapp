package com.truedigital.common.share.data.coredata.di

import com.truedigital.common.share.data.coredata.deeplink.usecase.DecodeDeeplinkUseCase
import dagger.Module
import dagger.multibindings.Multibinds

@Module
interface DataMultiBindsModule {

    @Multibinds
    fun multiBindsSetOfDecodeDeeplinkUseCase(): Set<DecodeDeeplinkUseCase>
}
