package com.truedigital.common.share.componentv3.di

import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.data.repository.WeMallShelfRepository
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.data.repository.WeMallShelfRepositoryImpl
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.domain.usecase.ConvertWeMallResponseUseCase
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.domain.usecase.ConvertWeMallResponseUseCaseImpl
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.domain.usecase.GetWeMallShelfContentUseCase
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.domain.usecase.GetWeMallShelfContentUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
interface WeMallShelfComponentBindsModule {

    @Binds
    fun bindsWeMallShelfRepository(
        weMallShelfRepositoryImpl: WeMallShelfRepositoryImpl
    ): WeMallShelfRepository

    @Binds
    fun bindsConvertWeMallResponseUseCase(
        convertWeMallResponseUseCaseImpl: ConvertWeMallResponseUseCaseImpl
    ): ConvertWeMallResponseUseCase

    @Binds
    fun bindsGetWeMallShelfContentUseCase(
        getWeMallShelfContentUseCaseImpl: GetWeMallShelfContentUseCaseImpl
    ): GetWeMallShelfContentUseCase
}
