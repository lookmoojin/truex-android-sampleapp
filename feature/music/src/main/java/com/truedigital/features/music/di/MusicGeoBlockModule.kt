package com.truedigital.features.music.di

import com.truedigital.features.music.data.geoblock.repository.CacheMusicGeoBlockRepository
import com.truedigital.features.music.data.geoblock.repository.CacheMusicGeoBlockRepositoryImpl
import com.truedigital.features.music.domain.geoblock.usecase.GetMusicGeoBlockUseCase
import com.truedigital.features.music.domain.geoblock.usecase.GetMusicGeoBlockUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
interface MusicGeoBlockModule {

    @Binds
    fun bindsCacheMusicGeoBlockRepository(
        cacheMusicGeoBlockRepositoryImpl: CacheMusicGeoBlockRepositoryImpl
    ): CacheMusicGeoBlockRepository

    @Binds
    fun bindsGetMusicGeoBlockUseCase(
        getMusicGeoBlockUseCaseImpl: GetMusicGeoBlockUseCaseImpl
    ): GetMusicGeoBlockUseCase
}
