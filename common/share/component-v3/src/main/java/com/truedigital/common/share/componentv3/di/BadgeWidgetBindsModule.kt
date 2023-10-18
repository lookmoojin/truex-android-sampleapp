package com.truedigital.common.share.componentv3.di

import com.truedigital.common.share.componentv3.widget.badge.data.repository.ConfigInboxEnableRepository
import com.truedigital.common.share.componentv3.widget.badge.data.repository.ConfigInboxEnableRepositoryImpl
import com.truedigital.common.share.componentv3.widget.badge.data.repository.CountInboxRepository
import com.truedigital.common.share.componentv3.widget.badge.data.repository.CountInboxRepositoryImpl
import com.truedigital.common.share.componentv3.widget.badge.domain.usecase.CountInboxUseCase
import com.truedigital.common.share.componentv3.widget.badge.domain.usecase.CountInboxUseCaseImpl
import com.truedigital.common.share.componentv3.widget.badge.domain.usecase.GetInboxEnableUseCase
import com.truedigital.common.share.componentv3.widget.badge.domain.usecase.GetInboxEnableUseCaseImpl
import com.truedigital.common.share.componentv3.widget.badge.domain.usecase.GetServiceCountInboxUseCase
import com.truedigital.common.share.componentv3.widget.badge.domain.usecase.GetServiceCountInboxUseCaseImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface BadgeWidgetBindsModule {

    @Binds
    fun bindsConfigInboxEnableRepository(
        configInboxEnableRepositoryImpl: ConfigInboxEnableRepositoryImpl
    ): ConfigInboxEnableRepository

    @Binds
    fun bindsCountInboxRepository(
        countInboxRepositoryImpl: CountInboxRepositoryImpl
    ): CountInboxRepository


    @Binds
    fun bindsGetInboxEnableUseCase(
        getInboxEnableUseCaseImpl: GetInboxEnableUseCaseImpl
    ): GetInboxEnableUseCase


    @Binds
    fun bindsGetServiceCountInboxUseCase(
        getServiceCountInboxUseCaseImpl: GetServiceCountInboxUseCaseImpl
    ): GetServiceCountInboxUseCase


    @Binds
    @Singleton
    fun bindsCountInboxUseCase(
        countInboxUseCaseImpl: CountInboxUseCaseImpl
    ): CountInboxUseCase
}
