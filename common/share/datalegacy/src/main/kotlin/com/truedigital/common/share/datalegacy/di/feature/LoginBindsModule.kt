package com.truedigital.common.share.datalegacy.di.feature

import com.truedigital.common.share.datalegacy.data.repository.login.LoginCdnRepository
import com.truedigital.common.share.datalegacy.data.repository.login.LoginCdnRepositoryImpl
import com.truedigital.common.share.datalegacy.data.repository.login.StateUserLoginRepository
import com.truedigital.common.share.datalegacy.data.repository.login.StateUserLoginRepositoryImpl
import com.truedigital.common.share.datalegacy.domain.login.usecase.GetLoginUrlUseCase
import com.truedigital.common.share.datalegacy.domain.login.usecase.GetLoginUrlUseCaseImpl
import com.truedigital.common.share.datalegacy.login.LoginManagerImpl
import com.truedigital.common.share.datalegacy.login.LoginManagerInterface
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface LoginBindsModule {

    @Binds
    fun bindsLoginCdnRepository(
        loginCdnRepositoryImpl: LoginCdnRepositoryImpl
    ): LoginCdnRepository

    @Binds
    @Singleton
    fun bindsStateUserLoginRepository(
        stateUserLoginRepositoryImpl: StateUserLoginRepositoryImpl
    ): StateUserLoginRepository

    @Binds
    fun bindsGetLoginUrlUseCase(
        getLoginUrlUseCaseImpl: GetLoginUrlUseCaseImpl
    ): GetLoginUrlUseCase

    @Binds
    @Singleton
    fun bindsLoginManagerInterface(
        loginManagerImpl: LoginManagerImpl
    ): LoginManagerInterface
}
