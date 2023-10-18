package com.truedigital.features.music.di

import com.truedigital.features.music.domain.addsong.usecase.AddSongUseCase
import com.truedigital.features.music.domain.addsong.usecase.AddSongUseCaseImpl
import com.truedigital.features.music.domain.addsong.usecase.GetSearchSongPagingUseCase
import com.truedigital.features.music.domain.addsong.usecase.GetSearchSongPagingUseCaseImpl
import com.truedigital.features.music.domain.addsong.usecase.SearchSongStreamUseCase
import com.truedigital.features.music.domain.addsong.usecase.SearchSongStreamUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
interface MusicAddSongModule {

    @Binds
    fun bindsSearchSongStreamUseCase(
        searchSongStreamUseCaseImpl: SearchSongStreamUseCaseImpl
    ): SearchSongStreamUseCase

    @Binds
    fun bindsGetSearchSongPagingUseCase(
        getSearchSongPagingUseCaseImpl: GetSearchSongPagingUseCaseImpl
    ): GetSearchSongPagingUseCase

    @Binds
    fun bindsAddSongUseCase(
        addSongUseCaseImpl: AddSongUseCaseImpl
    ): AddSongUseCase
}
