package com.truedigital.features.music.di

import com.truedigital.features.music.data.ads.repository.CacheMusicPlayerAdsRepository
import com.truedigital.features.music.data.ads.repository.CacheMusicPlayerAdsRepositoryImpl
import com.truedigital.features.music.data.player.repository.CacheServicePlayerRepository
import com.truedigital.features.music.data.player.repository.CacheServicePlayerRepositoryImpl
import com.truedigital.features.music.data.player.repository.MusicPlayerCacheRepository
import com.truedigital.features.music.data.player.repository.MusicPlayerCacheRepositoryImpl
import com.truedigital.features.music.domain.ads.usecase.ActionPreviousNextUseCase
import com.truedigital.features.music.domain.ads.usecase.ActionPreviousNextUseCaseImpl
import com.truedigital.features.music.domain.ads.usecase.ClearCacheMusicPlayerAdsUseCase
import com.truedigital.features.music.domain.ads.usecase.ClearCacheMusicPlayerAdsUseCaseImpl
import com.truedigital.features.music.domain.ads.usecase.GetMusicPlayerAdsUrlUseCase
import com.truedigital.features.music.domain.ads.usecase.GetMusicPlayerAdsUrlUseCaseImpl
import com.truedigital.features.music.domain.ads.usecase.IsShowMusicPlayerAdsUseCase
import com.truedigital.features.music.domain.ads.usecase.IsShowMusicPlayerAdsUseCaseImpl
import com.truedigital.features.music.domain.logout.usecase.LogoutMusicUseCase
import com.truedigital.features.music.domain.logout.usecase.LogoutMusicUseCaseImpl
import com.truedigital.features.music.domain.player.usecase.GetLandingOnListenScopeUseCase
import com.truedigital.features.music.domain.player.usecase.GetLandingOnListenScopeUseCaseImpl
import com.truedigital.features.music.domain.player.usecase.GetMusicPlayerVisibleUseCase
import com.truedigital.features.music.domain.player.usecase.GetMusicPlayerVisibleUseCaseImpl
import com.truedigital.features.music.domain.player.usecase.SetLandingOnListenScopeUseCase
import com.truedigital.features.music.domain.player.usecase.SetLandingOnListenScopeUseCaseImpl
import com.truedigital.features.music.manager.player.MusicPlayerActionManager
import com.truedigital.features.music.manager.player.MusicPlayerActionManagerImpl
import dagger.Binds
import dagger.Module

@Module
interface MusicPlayerModule {

    @Binds
    fun bindsMusicPlayerActionManager(
        musicPlayerActionManagerImpl: MusicPlayerActionManagerImpl
    ): MusicPlayerActionManager

    @Binds
    fun bindsCacheMusicPlayerAdsRepository(
        cacheMusicPlayerAdsRepositoryImpl: CacheMusicPlayerAdsRepositoryImpl
    ): CacheMusicPlayerAdsRepository

    @Binds
    fun bindsCacheServicePlayerRepository(
        cacheServicePlayerRepositoryImpl: CacheServicePlayerRepositoryImpl
    ): CacheServicePlayerRepository

    @Binds
    fun bindsMusicPlayerCacheRepository(
        musicPlayerCacheRepositoryImpl: MusicPlayerCacheRepositoryImpl
    ): MusicPlayerCacheRepository

    @Binds
    fun bindsActionPreviousNextUseCase(
        actionPreviousNextUseCaseImpl: ActionPreviousNextUseCaseImpl
    ): ActionPreviousNextUseCase

    @Binds
    fun bindsClearCacheMusicPlayerAdsUseCase(
        clearCacheMusicPlayerAdsUseCaseImpl: ClearCacheMusicPlayerAdsUseCaseImpl
    ): ClearCacheMusicPlayerAdsUseCase

    @Binds
    fun bindsGetMusicPlayerAdsUrlUseCase(
        getMusicPlayerAdsUrlUseCaseImpl: GetMusicPlayerAdsUrlUseCaseImpl
    ): GetMusicPlayerAdsUrlUseCase

    @Binds
    fun bindsIsShowMusicPlayerAdsUseCase(
        isShowMusicPlayerAdsUseCaseImpl: IsShowMusicPlayerAdsUseCaseImpl
    ): IsShowMusicPlayerAdsUseCase

    @Binds
    fun bindsLogoutMusicUseCase(
        logoutMusicUseCaseImpl: LogoutMusicUseCaseImpl
    ): LogoutMusicUseCase

    @Binds
    fun bindsSetLandingOnListenScopeUseCase(
        setLandingOnListenScopeUseCaseImpl: SetLandingOnListenScopeUseCaseImpl
    ): SetLandingOnListenScopeUseCase

    @Binds
    fun bindsGetLandingOnListenScopeUseCase(
        getLandingOnListenScopeUseCaseImpl: GetLandingOnListenScopeUseCaseImpl
    ): GetLandingOnListenScopeUseCase

    @Binds
    fun bindsGetMusicPlayerVisibleUseCase(
        getMusicPlayerVisibleUseCaseImpl: GetMusicPlayerVisibleUseCaseImpl
    ): GetMusicPlayerVisibleUseCase
}
