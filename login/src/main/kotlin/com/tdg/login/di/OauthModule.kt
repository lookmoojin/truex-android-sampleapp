package com.tdg.login.di

import com.tdg.login.data.repository.OauthRepository
import com.tdg.login.data.repository.OauthRepositoryImpl
import com.tdg.login.domain.usecase.LoginDomainUseCase
import com.tdg.login.domain.usecase.LoginDomainUseCaseImpl
import com.tdg.login.domain.usecase.LoginUseCase
import com.tdg.login.domain.usecase.LoginUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import retrofit2.Converter

@Module
interface OauthModule {

    @GsonConverter
    fun getGsonConverterFactory(): Converter.Factory

    @Binds
    fun bindsOauthRepository(
        oauthRepositoryImpl: OauthRepositoryImpl
    ): OauthRepository

    @Binds
    fun bindsLoginUseCase(
        loginUseCaseImpl: LoginUseCaseImpl
    ): LoginUseCase

    @Binds
    fun bindsLoginDomainUseCase(
        loginDomainUseCaseImpl: LoginDomainUseCaseImpl
    ): LoginDomainUseCase
}