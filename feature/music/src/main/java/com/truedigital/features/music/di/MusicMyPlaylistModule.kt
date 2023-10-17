package com.truedigital.features.music.di

import com.truedigital.features.music.domain.image.usecase.GenerateGridImageUseCase
import com.truedigital.features.music.domain.image.usecase.GenerateGridImageUseCaseImpl
import com.truedigital.features.music.domain.image.usecase.UploadCoverImageUseCase
import com.truedigital.features.music.domain.image.usecase.UploadCoverImageUseCaseImpl
import com.truedigital.features.music.domain.myplaylist.usecase.DeleteMyPlaylistUseCase
import com.truedigital.features.music.domain.myplaylist.usecase.DeleteMyPlaylistUseCaseImpl
import com.truedigital.features.music.domain.myplaylist.usecase.GetMyPlaylistTrackUseCase
import com.truedigital.features.music.domain.myplaylist.usecase.GetMyPlaylistTrackUseCaseImpl
import com.truedigital.features.music.domain.myplaylist.usecase.GetMyPlaylistUseCase
import com.truedigital.features.music.domain.myplaylist.usecase.GetMyPlaylistUseCaseImpl
import com.truedigital.features.music.domain.track.usecase.RemoveTrackUseCase
import com.truedigital.features.music.domain.track.usecase.RemoveTrackUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
interface MusicMyPlaylistModule {

    @Binds
    fun bindsDeleteMyPlaylistUseCase(
        deleteMyPlaylistUseCaseImpl: DeleteMyPlaylistUseCaseImpl
    ): DeleteMyPlaylistUseCase

    @Binds
    fun bindsGenerateGridImageUseCase(
        generateGridImageUseCaseImpl: GenerateGridImageUseCaseImpl
    ): GenerateGridImageUseCase

    @Binds
    fun bindsGetMyPlaylistUseCase(
        getMyPlaylistUseCaseImpl: GetMyPlaylistUseCaseImpl
    ): GetMyPlaylistUseCase

    @Binds
    fun bindsGetMyPlaylistTrackUseCase(
        getMyPlaylistTrackUseCaseImpl: GetMyPlaylistTrackUseCaseImpl
    ): GetMyPlaylistTrackUseCase

    @Binds
    fun bindsRemoveTrackUseCase(
        removeTrackUseCaseImpl: RemoveTrackUseCaseImpl
    ): RemoveTrackUseCase

    @Binds
    fun bindsUploadCoverImageUseCase(
        uploadCoverImageUseCaseImpl: UploadCoverImageUseCaseImpl
    ): UploadCoverImageUseCase
}
