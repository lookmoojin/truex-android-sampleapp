package com.truedigital.features.music.di

import com.truedigital.features.music.data.forceloginbanner.repository.MusicConfigRepository
import com.truedigital.features.music.data.forceloginbanner.repository.MusicConfigRepositoryImpl
import dagger.Binds
import dagger.Module

@Module
interface MusicConfigModule {

    @Binds
    fun bindsMusicConfigRepository(
        musicConfigRepositoryImpl: MusicConfigRepositoryImpl
    ): MusicConfigRepository
}
