package com.truedigital.common.share.componentv3.di

import com.truedigital.common.share.componentv3.widget.searchanimation.usecase.CheckDateSearchAnimationUseCase
import com.truedigital.common.share.componentv3.widget.searchanimation.usecase.CheckDateSearchAnimationUseCaseImpl
import com.truedigital.common.share.componentv3.widget.searchanimation.usecase.GetSearchAnimationUseCase
import com.truedigital.common.share.componentv3.widget.searchanimation.usecase.GetSearchAnimationUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
interface SearchAnimationBindsModule {

    @Binds
    fun bindsGetSearchAnimationUseCase(
        getSearchAnimationUseCaseImpl: GetSearchAnimationUseCaseImpl
    ): GetSearchAnimationUseCase

    @Binds
    fun bindsCheckDateSearchAnimationUseCase(
        checkDateSearchAnimationUseCaseImpl: CheckDateSearchAnimationUseCaseImpl
    ): CheckDateSearchAnimationUseCase
}
