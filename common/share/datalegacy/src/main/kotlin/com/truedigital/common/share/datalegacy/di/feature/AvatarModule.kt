package com.truedigital.common.share.datalegacy.di.feature

import com.truedigital.common.share.datalegacy.data.repository.avatar.AvatarRepository
import com.truedigital.common.share.datalegacy.data.repository.avatar.AvatarRepositoryImpl
import com.truedigital.common.share.datalegacy.domain.avatar.usecase.GetAvatarUrlUseCase
import com.truedigital.common.share.datalegacy.domain.avatar.usecase.GetAvatarUrlUseCaseImpl
import com.truedigital.common.share.datalegacy.domain.avatar.usecase.GetAvatarUrlUserLastedUseCase
import com.truedigital.common.share.datalegacy.domain.avatar.usecase.GetAvatarUrlUserLastedUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
interface AvatarModule {

    @Binds
    fun bindsAvatarRepository(repositoryImpl: AvatarRepositoryImpl): AvatarRepository

    @Binds
    fun bindsGetAvatarUrlUseCase(getAvatarUrlUseCaseImpl: GetAvatarUrlUseCaseImpl): GetAvatarUrlUseCase

    @Binds
    fun bindsGetAvatarUrlUserLastedUseCase(getAvatarUrlUserLastedUseCaseImpl: GetAvatarUrlUserLastedUseCaseImpl): GetAvatarUrlUserLastedUseCase
}
