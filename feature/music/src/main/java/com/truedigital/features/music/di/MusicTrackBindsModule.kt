package com.truedigital.features.music.di

import com.truedigital.features.music.data.queue.repository.CacheTrackQueueRepository
import com.truedigital.features.music.data.queue.repository.CacheTrackQueueRepositoryImpl
import com.truedigital.features.music.data.track.repository.MusicTrackRepository
import com.truedigital.features.music.data.track.repository.MusicTrackRepositoryImpl
import com.truedigital.features.music.domain.queue.usecase.ClearCacheTrackQueueUseCase
import com.truedigital.features.music.domain.queue.usecase.ClearCacheTrackQueueUseCaseImpl
import com.truedigital.features.music.domain.queue.usecase.GetAllTrackQueueUseCase
import com.truedigital.features.music.domain.queue.usecase.GetAllTrackQueueUseCaseImpl
import com.truedigital.features.music.domain.queue.usecase.GetCacheTrackQueueUseCase
import com.truedigital.features.music.domain.queue.usecase.GetCacheTrackQueueUseCaseImpl
import com.truedigital.features.music.domain.track.usecase.GetTrackListUseCase
import com.truedigital.features.music.domain.track.usecase.GetTrackListUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
interface MusicTrackBindsModule {

    @Binds
    fun bindsCacheTrackQueueRepository(
        cacheTrackQueueRepositoryImpl: CacheTrackQueueRepositoryImpl
    ): CacheTrackQueueRepository

    @Binds
    fun bindsMusicTrackRepository(
        musicTrackRepositoryImpl: MusicTrackRepositoryImpl
    ): MusicTrackRepository

    @Binds
    fun bindsClearCacheTrackQueueUseCase(
        clearCacheTrackQueueUseCaseImpl: ClearCacheTrackQueueUseCaseImpl
    ): ClearCacheTrackQueueUseCase

    @Binds
    fun bindsGetAllTrackQueueUseCase(
        getAllTrackQueueUseCaseImpl: GetAllTrackQueueUseCaseImpl
    ): GetAllTrackQueueUseCase

    @Binds
    fun bindsGetCacheTrackQueueUseCase(
        getCacheTrackQueueUseCaseImpl: GetCacheTrackQueueUseCaseImpl
    ): GetCacheTrackQueueUseCase

    @Binds
    fun bindsGetTrackListUseCase(
        getTrackListUseCaseImpl: GetTrackListUseCaseImpl
    ): GetTrackListUseCase
}
