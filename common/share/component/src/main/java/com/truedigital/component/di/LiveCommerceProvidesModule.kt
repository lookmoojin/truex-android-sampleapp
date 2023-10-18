package com.truedigital.component.di

import com.google.gson.Gson
import com.truedigital.common.share.datalegacy.data.api.ApiGsonBuilder
import com.truedigital.common.share.datalegacy.data.api.di.JsonFeaturePathV2OkHttp
import com.truedigital.component.BuildConfig
import com.truedigital.component.widget.livecommerce.data.api.AmityActiveLiveStreamApi
import com.truedigital.core.api.di.ErrorHandlingAdapter
import com.truedigital.core.api.di.GsonConverter
import com.truedigital.core.api.di.RxErrorHandlingAdapter
import com.truedigital.core.api.di.RxJava2Adapter
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter

@Module
object LiveCommerceProvidesModule {

    @Provides
    fun providesGson(): Gson {
        return Gson()
    }

    @Provides
    fun providesAmityActiveLiveStreamApi(
        @JsonFeaturePathV2OkHttp okHttpClient: OkHttpClient,
        @GsonConverter gsonConverterFactory: Converter.Factory,
        @RxJava2Adapter rxJavaAdapterFactory: CallAdapter.Factory,
        @ErrorHandlingAdapter errorHandlingAdapterFactory: CallAdapter.Factory,
        @RxErrorHandlingAdapter rxErrorHandlingAdapterFactory: CallAdapter.Factory
    ): AmityActiveLiveStreamApi {
        return ApiGsonBuilder(
            okHttpClient,
            gsonConverterFactory,
            rxJavaAdapterFactory,
            errorHandlingAdapterFactory,
            rxErrorHandlingAdapterFactory
        ).build(BuildConfig.COMMERCE_AMITY_BASE_URL)
    }
}
