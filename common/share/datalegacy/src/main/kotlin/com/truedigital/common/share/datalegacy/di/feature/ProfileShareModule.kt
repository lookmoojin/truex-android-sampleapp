package com.truedigital.common.share.datalegacy.di.feature

import com.truedigital.common.share.datalegacy.domain.GetCurrentSubProfileIdUseCase
import com.truedigital.common.share.datalegacy.domain.GetCurrentSubProfileIdUseCaseImpl
import com.truedigital.common.share.datalegacy.domain.profile.usecase.userdetails.ClearProfileCacheUseCase
import com.truedigital.common.share.datalegacy.domain.profile.usecase.userdetails.ClearProfileCacheUseCaseImpl
import com.truedigital.common.share.datalegacy.domain.profile.usecase.userdetails.GetCurrentSubProfileUseCase
import com.truedigital.common.share.datalegacy.domain.profile.usecase.userdetails.GetCurrentSubProfileUseCaseImpl
import com.truedigital.common.share.datalegacy.domain.profile.usecase.userdetails.GetNonCachedProfileUseCase
import com.truedigital.common.share.datalegacy.domain.profile.usecase.userdetails.GetNonCachedProfileUseCaseImpl
import com.truedigital.common.share.datalegacy.domain.profile.usecase.userdetails.GetOtherProfileUseCase
import com.truedigital.common.share.datalegacy.domain.profile.usecase.userdetails.GetOtherProfileUseCaseImpl
import com.truedigital.common.share.datalegacy.domain.profile.usecase.userdetails.GetProfileUseCase
import com.truedigital.common.share.datalegacy.domain.profile.usecase.userdetails.GetProfileUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
interface ProfileShareModule {

    @Binds
    fun bindsClearProfileCacheUseCase(
        useCase: ClearProfileCacheUseCaseImpl
    ): ClearProfileCacheUseCase

    @Binds
    fun bindsGetProfileUseCase(
        useCase: GetProfileUseCaseImpl
    ): GetProfileUseCase

    @Binds
    fun bindsGetNonCachedProfileUseCase(
        useCase: GetNonCachedProfileUseCaseImpl
    ): GetNonCachedProfileUseCase

    @Binds
    fun bindsGetOtherProfileUseCase(
        useCase: GetOtherProfileUseCaseImpl
    ): GetOtherProfileUseCase

    @Binds
    fun bindsGetCurrentSubProfileUseCase(
        getCurrentSubProfileUseCaseImpl: GetCurrentSubProfileUseCaseImpl
    ): GetCurrentSubProfileUseCase

    @Binds
    fun bindsGetCurrentSubProfileIdUseCase(
        getCurrentSubProfileIdUseCaseImpl: GetCurrentSubProfileIdUseCaseImpl
    ): GetCurrentSubProfileIdUseCase
}
