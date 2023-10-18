package com.truedigital.common.share.currentdate.di

import com.truedigital.common.share.currentdate.DateTimeInterface
import com.truedigital.common.share.currentdate.DateTimeUtil
import com.truedigital.common.share.currentdate.DateTimeUtilController
import com.truedigital.common.share.currentdate.repository.DateTimeRepository
import com.truedigital.common.share.currentdate.repository.DateTimeRepositoryImpl
import com.truedigital.common.share.currentdate.repository.dmp.DmpDateTimeInterface
import com.truedigital.common.share.currentdate.repository.nondmp.NonDmpCurrentDateTimeInterface
import com.truedigital.common.share.datalegacy.data.api.ApiGsonBuilder
import com.truedigital.common.share.datalegacy.data.api.ApiScalarsAndGsonBuilder
import com.truedigital.common.share.datalegacy.data.api.di.JsonFeaturePathV1OkHttp
import com.truedigital.core.BuildConfig
import com.truedigital.core.api.di.ErrorHandlingAdapter
import com.truedigital.core.api.di.GsonConverter
import com.truedigital.core.api.di.RxErrorHandlingAdapter
import com.truedigital.core.api.di.RxJava2Adapter
import com.truedigital.core.api.di.ScalarsConverter
import com.truedigital.core.data.device.repository.LocalizationRepository
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import org.apache.commons.net.ntp.NTPUDPClient
import retrofit2.CallAdapter
import retrofit2.Converter
import javax.inject.Singleton

@Module
object DateTimeModule {

    @Provides
    @Singleton
    fun providesDateTimeUtilController(localizationRepository: LocalizationRepository): DateTimeUtilController {
        return DateTimeUtilController(localizationRepository)
    }

    @Provides
    @Singleton
    fun provideDateTimeInterface(
        dateTimeUtilController: DateTimeUtilController
    ): DateTimeInterface {
        return DateTimeUtil(
            dateTimeUtilController
        )
    }

    @Provides
    @Singleton
    fun providesDateTimeRepository(
        dmpAPIInterface: DmpDateTimeInterface,
        nonDmpAPIInterface: NonDmpCurrentDateTimeInterface
    ): DateTimeRepository {
        return DateTimeRepositoryImpl(
            NTPUDPClient(),
            dmpAPIInterface,
            nonDmpAPIInterface
        )
    }

    @Provides
    @Singleton
    fun provideDmpDateTimeInterface(
        @JsonFeaturePathV1OkHttp okHttpClient: OkHttpClient,
        @ScalarsConverter scalarsConverterFactory: Converter.Factory,
        @GsonConverter gsonConverterFactory: Converter.Factory,
        @RxJava2Adapter rxJavaAdapterFactory: CallAdapter.Factory,
        @ErrorHandlingAdapter errorHandlingAdapterFactory: CallAdapter.Factory,
        @RxErrorHandlingAdapter rxErrorHandlingAdapterFactory: CallAdapter.Factory
    ): DmpDateTimeInterface {
        return ApiScalarsAndGsonBuilder(
            okHttpClient = okHttpClient,
            scalarsConverterFactory = scalarsConverterFactory,
            gsonConverterFactory = gsonConverterFactory,
            rxJavaAdapterFactory = rxJavaAdapterFactory,
            errorHandlingAdapterFactory = errorHandlingAdapterFactory,
            rxErrorHandlingAdapterFactory = rxErrorHandlingAdapterFactory
        ).build(BuildConfig.BASE_URL_DMP2)
    }

    @Provides
    @Singleton
    fun provideNonDmpCurrentDateTimeInterface(
        @JsonFeaturePathV1OkHttp okHttpClient: OkHttpClient,
        @GsonConverter gsonConverterFactory: Converter.Factory,
        @RxJava2Adapter rxJavaAdapterFactory: CallAdapter.Factory,
        @ErrorHandlingAdapter errorHandlingAdapterFactory: CallAdapter.Factory,
        @RxErrorHandlingAdapter rxErrorHandlingAdapterFactory: CallAdapter.Factory
    ): NonDmpCurrentDateTimeInterface {
        return ApiGsonBuilder(
            okHttpClient = okHttpClient,
            gsonConverterFactory = gsonConverterFactory,
            rxJavaAdapterFactory = rxJavaAdapterFactory,
            errorHandlingAdapterFactory = errorHandlingAdapterFactory,
            rxErrorHandlingAdapterFactory = rxErrorHandlingAdapterFactory
        ).build(BuildConfig.FIREBASE_FUNCTIONS_URL)
    }
}
