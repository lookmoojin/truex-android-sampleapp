package com.tdg.login.di

import com.tdg.login.base.CoroutineDispatcherProvider
import com.tdg.login.base.DefaultCoroutineDispatcherProvider
import dagger.Module
import dagger.Provides

@Module
class OauthBaseModule {

    @Provides
    fun provideCoroutineDispatcherProvider(): CoroutineDispatcherProvider {
        return DefaultCoroutineDispatcherProvider()
    }
}