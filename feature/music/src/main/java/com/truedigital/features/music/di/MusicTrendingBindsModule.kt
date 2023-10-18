package com.truedigital.features.music.di

import com.truedigital.features.music.data.trending.repository.MusicTrendingAlbumCacheRepository
import com.truedigital.features.music.data.trending.repository.MusicTrendingAlbumCacheRepositoryImpl
import com.truedigital.features.music.data.trending.repository.MusicTrendingArtistCacheRepository
import com.truedigital.features.music.data.trending.repository.MusicTrendingArtistCacheRepositoryImpl
import com.truedigital.features.music.data.trending.repository.MusicTrendingPlaylistCacheRepository
import com.truedigital.features.music.data.trending.repository.MusicTrendingPlaylistCacheRepositoryImpl
import com.truedigital.features.music.data.trending.repository.MusicTrendingRepository
import com.truedigital.features.music.data.trending.repository.MusicTrendingRepositoryImpl
import com.truedigital.features.music.domain.trending.usecase.GetMusicTrendingAlbumUseCase
import com.truedigital.features.music.domain.trending.usecase.GetMusicTrendingAlbumUseCaseImpl
import com.truedigital.features.music.domain.trending.usecase.GetMusicTrendingArtistsUseCase
import com.truedigital.features.music.domain.trending.usecase.GetMusicTrendingArtistsUseCaseImpl
import com.truedigital.features.music.domain.trending.usecase.GetMusicTrendingPlaylistUseCase
import com.truedigital.features.music.domain.trending.usecase.GetMusicTrendingPlaylistUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
interface MusicTrendingBindsModule {

    @Binds
    fun bindsMusicTrendingRepository(
        musicTrendingRepositoryImpl: MusicTrendingRepositoryImpl
    ): MusicTrendingRepository

    @Binds
    fun bindsMusicTrendingAlbumCacheRepository(
        musicTrendingAlbumCacheRepositoryImpl: MusicTrendingAlbumCacheRepositoryImpl
    ): MusicTrendingAlbumCacheRepository

    @Binds
    fun bindsMusicTrendingArtistCacheRepository(
        musicTrendingArtistCacheRepositoryImpl: MusicTrendingArtistCacheRepositoryImpl
    ): MusicTrendingArtistCacheRepository

    @Binds
    fun bindsMusicTrendingPlaylistCacheRepository(
        musicTrendingPlaylistCacheRepositoryImpl: MusicTrendingPlaylistCacheRepositoryImpl
    ): MusicTrendingPlaylistCacheRepository

    @Binds
    fun bindsGetMusicTrendingAlbumUseCase(
        getMusicTrendingAlbumUseCaseImpl: GetMusicTrendingAlbumUseCaseImpl
    ): GetMusicTrendingAlbumUseCase

    @Binds
    fun bindsGetMusicTrendingArtistsUseCase(
        getMusicTrendingArtistsUseCaseImpl: GetMusicTrendingArtistsUseCaseImpl
    ): GetMusicTrendingArtistsUseCase

    @Binds
    fun bindsGetMusicTrendingPlaylistUseCase(
        getMusicTrendingPlaylistUseCaseImpl: GetMusicTrendingPlaylistUseCaseImpl
    ): GetMusicTrendingPlaylistUseCase
}
