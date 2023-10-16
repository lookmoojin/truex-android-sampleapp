package com.truedigital.navigation.di

import com.truedigital.common.share.data.coredata.deeplink.usecase.DecodeDeeplinkUseCase
import dagger.Module
import dagger.multibindings.Multibinds

@Module
interface NavigationMultiBindingModule {

    @Multibinds
    fun multiBindsSetOfDecodeDeeplinkUseCase(): Set<DecodeDeeplinkUseCase>
}
