package com.truedigital.features.music.di

import com.truedigital.features.music.data.authentication.repository.MusicAuthenticationRepository
import com.truedigital.features.music.data.authentication.repository.MusicAuthenticationRepositoryImpl
import com.truedigital.features.music.domain.authentication.usecase.RefreshMusicTokenUseCase
import com.truedigital.features.music.domain.authentication.usecase.RefreshMusicTokenUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
interface MusicAuthenticationBindsModule {

    @Binds
    fun bindsMusicAuthenticationRepository(
        musicAuthenticationRepositoryImpl: MusicAuthenticationRepositoryImpl
    ): MusicAuthenticationRepository

    @Binds
    fun bindsRefreshMusicTokenUseCase(
        refreshMusicTokenUseCaseImpl: RefreshMusicTokenUseCaseImpl
    ): RefreshMusicTokenUseCase
}
