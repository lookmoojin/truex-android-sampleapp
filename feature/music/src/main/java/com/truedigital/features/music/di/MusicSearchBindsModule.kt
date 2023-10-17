package com.truedigital.features.music.di

import com.truedigital.features.music.data.search.repository.MusicSearchRepository
import com.truedigital.features.music.data.search.repository.MusicSearchRepositoryImpl
import com.truedigital.features.music.domain.search.usecase.GetSearchAlbumUseCase
import com.truedigital.features.music.domain.search.usecase.GetSearchAlbumUseCaseImpl
import com.truedigital.features.music.domain.search.usecase.GetSearchAllUseCase
import com.truedigital.features.music.domain.search.usecase.GetSearchAllUseCaseImpl
import com.truedigital.features.music.domain.search.usecase.GetSearchArtistUseCase
import com.truedigital.features.music.domain.search.usecase.GetSearchArtistUseCaseImpl
import com.truedigital.features.music.domain.search.usecase.GetSearchPlaylistUseCase
import com.truedigital.features.music.domain.search.usecase.GetSearchPlaylistUseCaseImpl
import com.truedigital.features.music.domain.search.usecase.GetSearchSongUseCase
import com.truedigital.features.music.domain.search.usecase.GetSearchSongUseCaseImpl
import com.truedigital.features.music.domain.search.usecase.GetSearchTopMenuUseCase
import com.truedigital.features.music.domain.search.usecase.GetSearchTopMenuUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
interface MusicSearchBindsModule {

    @Binds
    fun bindsMusicSearchRepository(
        musicSearchRepositoryImpl: MusicSearchRepositoryImpl
    ): MusicSearchRepository

    @Binds
    fun bindsGetSearchTopMenuUseCase(
        getSearchTopMenuUseCaseImpl: GetSearchTopMenuUseCaseImpl
    ): GetSearchTopMenuUseCase

    @Binds
    fun bindsGetSearchAllUseCase(
        getSearchAllUseCaseImpl: GetSearchAllUseCaseImpl
    ): GetSearchAllUseCase

    @Binds
    fun bindsGetSearchArtistUseCase(
        getSearchArtistUseCaseImpl: GetSearchArtistUseCaseImpl
    ): GetSearchArtistUseCase

    @Binds
    fun bindsGetSearchSongUseCase(
        getSearchSongUseCaseImpl: GetSearchSongUseCaseImpl
    ): GetSearchSongUseCase

    @Binds
    fun bindsGetSearchAlbumUseCase(
        getSearchAlbumUseCaseImpl: GetSearchAlbumUseCaseImpl
    ): GetSearchAlbumUseCase

    @Binds
    fun bindsGetSearchPlaylistUseCase(
        getSearchPlaylistUseCaseImpl: GetSearchPlaylistUseCaseImpl
    ): GetSearchPlaylistUseCase
}
