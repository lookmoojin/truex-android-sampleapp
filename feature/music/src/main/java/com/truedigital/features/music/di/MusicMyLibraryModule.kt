package com.truedigital.features.music.di

import com.truedigital.features.music.domain.myplaylist.usecase.CreateNewPlaylistUseCase
import com.truedigital.features.music.domain.myplaylist.usecase.CreateNewPlaylistUseCaseImpl
import com.truedigital.features.music.domain.myplaylist.usecase.GetMyPlaylistShelfUseCase
import com.truedigital.features.music.domain.myplaylist.usecase.GetMyPlaylistShelfUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
interface MusicMyLibraryModule {

    @Binds
    fun bindsGetMyPlaylistShelfUseCase(
        getMyPlaylistShelfUseCaseImpl: GetMyPlaylistShelfUseCaseImpl
    ): GetMyPlaylistShelfUseCase

    @Binds
    fun bindsCreateNewPlaylistUseCase(
        createNewPlaylistUseCaseImpl: CreateNewPlaylistUseCaseImpl
    ): CreateNewPlaylistUseCase
}
