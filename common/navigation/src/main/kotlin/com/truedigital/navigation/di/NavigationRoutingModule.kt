package com.truedigital.navigation.di

import com.truedigital.common.share.data.coredata.deeplink.usecase.DecodeDeeplinkUseCase
import com.truedigital.navigation.deeplink.NavigateHostDeeplinkUseCase
import com.truedigital.navigation.deeplink.NavigateHostDeeplinkUseCaseImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class NavigationRoutingModule {

    @Provides
    @Singleton
    fun provideNavigateHostDeeplinkUseCase(
        decodeDeeplinkUseCaseList: Set<@JvmSuppressWildcards DecodeDeeplinkUseCase>,
    ): NavigateHostDeeplinkUseCase {
        return NavigateHostDeeplinkUseCaseImpl(
            decodeDeeplinkUseCaseList
        )
    }
}
