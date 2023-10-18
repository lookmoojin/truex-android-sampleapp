package com.truedigital.features.music.di

import com.truedigital.features.music.data.addsong.repository.AddSongRepository
import com.truedigital.features.music.data.addsong.repository.AddSongRepositoryImpl
import com.truedigital.features.music.data.playlist.repository.MusicPlaylistRepository
import com.truedigital.features.music.data.playlist.repository.MusicPlaylistRepositoryImpl
import com.truedigital.features.music.domain.player.usecase.SetMusicPlayerVisibleUseCase
import com.truedigital.features.music.domain.player.usecase.SetMusicPlayerVisibleUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
interface MusicPlaylistBindsModule {

    @Binds
    fun bindsMusicPlaylistRepository(
        musicPlaylistRepositoryImpl: MusicPlaylistRepositoryImpl
    ): MusicPlaylistRepository

    @Binds
    fun bindsAddSongRepository(
        addSongRepositoryImpl: AddSongRepositoryImpl
    ): AddSongRepository

    @Binds
    fun bindsSetMusicPlayerVisibleUseCase(
        setMusicPlayerVisibleUseCaseImpl: SetMusicPlayerVisibleUseCaseImpl
    ): SetMusicPlayerVisibleUseCase
}
