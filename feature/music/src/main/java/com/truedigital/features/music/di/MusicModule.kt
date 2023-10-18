package com.truedigital.features.music.di

import android.content.Context
import com.truedigital.common.share.webview.externalbrowser.utils.customtabs.CustomTabsHelper
import com.truedigital.features.music.domain.usecase.router.MusicPlayerRouterUseCase
import com.truedigital.features.music.domain.usecase.router.MusicPlayerRouterUseCaseImpl
import com.truedigital.features.music.domain.usecase.router.MusicRouterUseCase
import com.truedigital.features.music.domain.usecase.router.MusicRouterUseCaseImpl
import com.truedigital.navigation.data.repository.router.NavigationRouterRepository
import com.truedigital.navigation.usecase.GetNavigationControllerRepository
import dagger.Module
import dagger.Provides

@Module
class MusicModule {

    @Provides
    fun providesMusicRouterUseCase(
        context: Context,
        customTabsHelper: CustomTabsHelper,
        navigationRouterRepository: NavigationRouterRepository,
        getNavigationControllerRepository: GetNavigationControllerRepository
    ): MusicRouterUseCase {
        return MusicRouterUseCaseImpl(
            context,
            customTabsHelper,
            navigationRouterRepository,
            getNavigationControllerRepository
        )
    }

    @Provides
    fun providesMusicPlayerRouterUseCase(
        navigationRouterRepository: NavigationRouterRepository
    ): MusicPlayerRouterUseCase {
        return MusicPlayerRouterUseCaseImpl(
            navigationRouterRepository
        )
    }
}
