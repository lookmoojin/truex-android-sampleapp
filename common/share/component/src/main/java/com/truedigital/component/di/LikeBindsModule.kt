package com.truedigital.component.di

import com.truedigital.component.widget.like.domain.LikeUseCase
import com.truedigital.component.widget.like.domain.LikeUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
interface LikeBindsModule {

    @Binds
    fun bindsLikeUseCase(likeUseCaseImpl: LikeUseCaseImpl): LikeUseCase
}
