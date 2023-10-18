package com.truedigital.common.share.componentv3.di

import com.truedigital.common.share.componentv3.widget.badge.data.api.NotificationBadgeApiInterface
import com.truedigital.common.share.datalegacy.data.api.ApiScalarsAndGsonBuilder
import com.truedigital.common.share.datalegacy.data.api.di.JsonFeaturePathV2OkHttp
import com.truedigital.core.BuildConfig
import com.truedigital.core.api.di.ErrorHandlingAdapter
import com.truedigital.core.api.di.GsonConverter
import com.truedigital.core.api.di.RxErrorHandlingAdapter
import com.truedigital.core.api.di.RxJava2Adapter
import com.truedigital.core.api.di.ScalarsConverter
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import javax.inject.Singleton

@Module
object BadgeWidgetModule {

    @Singleton
    @Provides
    fun provideNotificationBadgeApiInterface(
        @JsonFeaturePathV2OkHttp okHttpClient: OkHttpClient,
        @GsonConverter gsonConverterFactory: Converter.Factory,
        @ScalarsConverter scalarsConverterFactory: Converter.Factory,
        @RxJava2Adapter rxJavaAdapterFactory: CallAdapter.Factory,
        @ErrorHandlingAdapter errorHandlingAdapterFactory: CallAdapter.Factory,
        @RxErrorHandlingAdapter rxErrorHandlingAdapterFactory: CallAdapter.Factory
    ): NotificationBadgeApiInterface {
        return ApiScalarsAndGsonBuilder(
            okHttpClient = okHttpClient,
            gsonConverterFactory = gsonConverterFactory,
            scalarsConverterFactory = scalarsConverterFactory,
            rxJavaAdapterFactory = rxJavaAdapterFactory,
            errorHandlingAdapterFactory = errorHandlingAdapterFactory,
            rxErrorHandlingAdapterFactory = rxErrorHandlingAdapterFactory
        ).build(BuildConfig.BASE_URL_DMP2)
    }
}
