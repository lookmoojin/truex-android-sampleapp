package com.truedigital.common.share.componentv3.di

import com.truedigital.common.share.componentv3.widget.badge.data.repository.ConfigInboxEnableRepository
import com.truedigital.common.share.componentv3.widget.badge.data.repository.ConfigInboxEnableRepositoryImpl
import com.truedigital.common.share.componentv3.widget.badge.data.repository.CountInboxRepository
import com.truedigital.common.share.componentv3.widget.badge.data.repository.CountInboxRepositoryImpl
import com.truedigital.common.share.componentv3.widget.badge.domain.usecase.CountInboxUseCase
import com.truedigital.common.share.componentv3.widget.badge.domain.usecase.CountInboxUseCaseImpl
import com.truedigital.common.share.componentv3.widget.badge.domain.usecase.GetCountInboxUseCase
import com.truedigital.common.share.componentv3.widget.badge.domain.usecase.GetCountInboxUseCaseImpl
import com.truedigital.common.share.componentv3.widget.badge.domain.usecase.GetInboxEnableUseCase
import com.truedigital.common.share.componentv3.widget.badge.domain.usecase.GetInboxEnableUseCaseImpl
import com.truedigital.common.share.componentv3.widget.badge.domain.usecase.GetNewCountInboxUseCase
import com.truedigital.common.share.componentv3.widget.badge.domain.usecase.GetNewCountInboxUseCaseImpl
import com.truedigital.common.share.componentv3.widget.badge.domain.usecase.GetServiceCountInboxUseCase
import com.truedigital.common.share.componentv3.widget.badge.domain.usecase.GetServiceCountInboxUseCaseImpl
import com.truedigital.common.share.componentv3.widget.badge.domain.usecase.GetTotalUnseenUseCase
import com.truedigital.common.share.componentv3.widget.badge.domain.usecase.GetTotalUnseenUseCaseImpl
import com.truedigital.common.share.componentv3.widget.badge.domain.usecase.SaveCountInboxUseCase
import com.truedigital.common.share.componentv3.widget.badge.domain.usecase.SaveCountInboxUseCaseImpl
import com.truedigital.common.share.componentv3.widget.badge.domain.usecase.SaveNewCountInboxUseCase
import com.truedigital.common.share.componentv3.widget.badge.domain.usecase.SaveNewCountInboxUseCaseImpl
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
    fun bindsGetCountInboxUseCase(
        getCountInboxUseCaseImpl: GetCountInboxUseCaseImpl
    ): GetCountInboxUseCase

    @Binds
    fun bindsGetInboxEnableUseCase(
        getInboxEnableUseCaseImpl: GetInboxEnableUseCaseImpl
    ): GetInboxEnableUseCase

    @Binds
    fun bindsGetNewCountInboxUseCase(
        getNewCountInboxUseCaseImpl: GetNewCountInboxUseCaseImpl
    ): GetNewCountInboxUseCase

    @Binds
    fun bindsGetServiceCountInboxUseCase(
        getServiceCountInboxUseCaseImpl: GetServiceCountInboxUseCaseImpl
    ): GetServiceCountInboxUseCase

    @Binds
    fun bindsSaveCountInboxUseCase(
        saveCountInboxUseCaseImpl: SaveCountInboxUseCaseImpl
    ): SaveCountInboxUseCase

    @Binds
    fun bindsSaveNewCountInboxUseCase(
        saveNewCountInboxUseCaseImpl: SaveNewCountInboxUseCaseImpl
    ): SaveNewCountInboxUseCase

    @Binds
    @Singleton
    fun bindsGetTotalUnseenUseCase(
        getTotalUnseenUseCaseImpl: GetTotalUnseenUseCaseImpl
    ): GetTotalUnseenUseCase

    @Binds
    @Singleton
    fun bindsCountInboxUseCase(
        countInboxUseCaseImpl: CountInboxUseCaseImpl
    ): CountInboxUseCase
}
