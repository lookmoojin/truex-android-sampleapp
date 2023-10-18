package com.truedigital.common.share.datalegacy.data.api

import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit

open class ApiGsonBuilder(
    val okHttpClient: OkHttpClient,
    val gsonConverterFactory: Converter.Factory,
    val rxJavaAdapterFactory: CallAdapter.Factory,
    val errorHandlingAdapterFactory: CallAdapter.Factory,
    val rxErrorHandlingAdapterFactory: CallAdapter.Factory
) {
    inline fun <reified T> build(baseUrl: String): T {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(baseUrl)
            .addConverterFactory(gsonConverterFactory)
            .addCallAdapterFactory(rxJavaAdapterFactory)
            .addCallAdapterFactory(errorHandlingAdapterFactory)
            .addCallAdapterFactory(rxErrorHandlingAdapterFactory)
            .build()
            .create(T::class.java)
    }
}

open class ApiScalarsAndGsonBuilder(
    val okHttpClient: OkHttpClient,
    val scalarsConverterFactory: Converter.Factory,
    val gsonConverterFactory: Converter.Factory,
    val rxJavaAdapterFactory: CallAdapter.Factory,
    val errorHandlingAdapterFactory: CallAdapter.Factory,
    val rxErrorHandlingAdapterFactory: CallAdapter.Factory
) {
    inline fun <reified T> build(baseUrl: String): T {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(baseUrl)
            .addConverterFactory(scalarsConverterFactory)
            .addConverterFactory(gsonConverterFactory)
            .addCallAdapterFactory(rxJavaAdapterFactory)
            .addCallAdapterFactory(errorHandlingAdapterFactory)
            .addCallAdapterFactory(rxErrorHandlingAdapterFactory)
            .build()
            .create(T::class.java)
    }
}
