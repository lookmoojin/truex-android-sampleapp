package com.tdg.login.di

import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
object NetworkModule {

    @Provides
    @Singleton
    @GsonConverter
    fun provideGsonConverterFactory(): Converter.Factory {
        return GsonConverterFactory.create()
    }

    @Provides
    @Singleton
    @DefaultOkHttp
    fun provideDefaultOkHttp(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }
}
