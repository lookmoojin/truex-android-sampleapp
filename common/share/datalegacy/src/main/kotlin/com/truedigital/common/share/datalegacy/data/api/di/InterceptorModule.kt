package com.truedigital.common.share.datalegacy.data.api.di

import com.truedigital.authentication.data.api.interceptor.RequestTokenInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import javax.inject.Singleton

@Module
object InterceptorModule {

    @Provides
    @Singleton
    @TokenInterceptor
    fun provideRequestTokenInterceptor(): Interceptor {
        return RequestTokenInterceptor()
    }
}
