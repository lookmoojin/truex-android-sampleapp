package com.truedigital.core.api.di

import com.truedigital.core.api.ErrorHandlingCallAdapterFactory
import com.truedigital.core.api.RxErrorHandlingCallAdapterFactory
import dagger.Module
import dagger.Provides
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import javax.inject.Singleton

@Module
object NetworkModule {

    @Provides
    @Singleton
    @RxJava2Adapter
    fun provideRxJava2CallAdapterFactory(): CallAdapter.Factory {
        return RxJava2CallAdapterFactory.create()
    }

    @Provides
    @Singleton
    @ErrorHandlingAdapter
    fun provideErrorHandlingCallAdapterFactory(): CallAdapter.Factory {
        return ErrorHandlingCallAdapterFactory()
    }

    @Provides
    @Singleton
    @RxErrorHandlingAdapter
    fun provideRxErrorHandlingCallAdapterFactory(): CallAdapter.Factory {
        return RxErrorHandlingCallAdapterFactory.create()
    }

    @Provides
    @Singleton
    @ScalarsConverter
    fun provideScalarsConverterFactory(): Converter.Factory {
        return ScalarsConverterFactory.create()
    }

    @Provides
    @Singleton
    @GsonConverter
    fun provideGsonConverterFactory(): Converter.Factory {
        return GsonConverterFactory.create()
    }

    @Provides
    @Singleton
    @XmlConverter
    fun provideSimpleXmlConverterFactory(): Converter.Factory {
        return SimpleXmlConverterFactory.create()
    }
}
