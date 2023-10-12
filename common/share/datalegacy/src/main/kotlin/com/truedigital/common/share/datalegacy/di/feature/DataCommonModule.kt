package com.truedigital.common.share.datalegacy.di.feature

import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.repository.CmsShelfRepository
import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.repository.CmsShelfRepositoryImpl
import com.truedigital.common.share.datalegacy.data.repository.profile.ProfileRepository
import com.truedigital.common.share.datalegacy.data.repository.profile.ProfileRepositoryImpl
import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepositoryImpl
import com.truedigital.common.share.datalegacy.domain.config.usecase.GetAppConfigUseCase
import com.truedigital.common.share.datalegacy.domain.config.usecase.GetAppConfigUseCaseImpl
import com.truedigital.common.share.datalegacy.domain.endpoint.usecase.GetApiConfigurationUseCase
import com.truedigital.common.share.datalegacy.domain.endpoint.usecase.GetApiConfigurationUseCaseImpl
import com.truedigital.common.share.datalegacy.domain.webview.usecase.GetSystemWebViewMinimumVersionUseCase
import com.truedigital.common.share.datalegacy.domain.webview.usecase.GetSystemWebViewMinimumVersionUseCaseImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface DataCommonModule {

    @Binds
    fun bindsCmsShelfRepository(
        cmsShelfRepositoryImpl: CmsShelfRepositoryImpl
    ): CmsShelfRepository

    @Binds
    @Singleton
    fun bindsUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    fun bindsProfileRepository(
        profileRepositoryImpl: ProfileRepositoryImpl
    ): ProfileRepository

    @Binds
    fun bindsGetAppConfigUseCase(
        getAppConfigUseCaseImpl: GetAppConfigUseCaseImpl
    ): GetAppConfigUseCase

    @Binds
    @Singleton
    fun bindsGetApiConfigurationUseCase(
        getApiConfigurationUseCaseImpl: GetApiConfigurationUseCaseImpl
    ): GetApiConfigurationUseCase

    @Binds
    fun bindsGetSystemWebViewMinimumVersionUseCase(
        getSystemWebViewMinimumVersionUseCaseImpl: GetSystemWebViewMinimumVersionUseCaseImpl
    ): GetSystemWebViewMinimumVersionUseCase
}
