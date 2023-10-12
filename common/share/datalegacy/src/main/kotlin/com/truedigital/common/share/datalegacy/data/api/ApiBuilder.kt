package com.truedigital.common.share.datalegacy.data.api

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit

open class ApiBasicBuilder(
    val okHttpClient: OkHttpClient,
    val rxJavaAdapterFactory: CallAdapter.Factory,
    val errorHandlingAdapterFactory: CallAdapter.Factory,
    val rxErrorHandlingAdapterFactory: CallAdapter.Factory
) {
    inline fun <reified T> build(baseUrl: String): T {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(baseUrl)
            .addCallAdapterFactory(rxJavaAdapterFactory)
            .addCallAdapterFactory(errorHandlingAdapterFactory)
            .addCallAdapterFactory(rxErrorHandlingAdapterFactory)
            .build()
            .create(T::class.java)
    }
}

open class ApiScalarsBuilder(
    val okHttpClient: OkHttpClient,
    val scalarsConverterFactory: Converter.Factory,
    val rxJavaAdapterFactory: CallAdapter.Factory,
    val errorHandlingAdapterFactory: CallAdapter.Factory,
    val rxErrorHandlingAdapterFactory: CallAdapter.Factory
) {
    inline fun <reified T> build(baseUrl: String): T {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(baseUrl)
            .addConverterFactory(scalarsConverterFactory)
            .addCallAdapterFactory(rxJavaAdapterFactory)
            .addCallAdapterFactory(errorHandlingAdapterFactory)
            .addCallAdapterFactory(rxErrorHandlingAdapterFactory)
            .build()
            .create(T::class.java)
    }
}

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

open class ApiMultipartBuilder(
    val okHttpClient: OkHttpClient,
    val gsonConverterFactory: Converter.Factory,
    val scalarsConverterFactory: Converter.Factory,
    val rxJavaAdapterFactory: CallAdapter.Factory,
    val errorHandlingAdapterFactory: CallAdapter.Factory,
    val rxErrorHandlingAdapterFactory: CallAdapter.Factory
) {
    inline fun <reified T> build(baseUrl: String): T {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(baseUrl)
            .addConverterFactory(gsonConverterFactory)
            .addConverterFactory(scalarsConverterFactory)
            .addCallAdapterFactory(rxJavaAdapterFactory)
            .addCallAdapterFactory(errorHandlingAdapterFactory)
            .addCallAdapterFactory(rxErrorHandlingAdapterFactory)
            .build()
            .create(T::class.java)
    }
}

open class ApolloBuilder(
    val okHttpClient: OkHttpClient
) {
    fun build(baseUrl: String): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl(baseUrl)
            .okHttpClient(okHttpClient)
            .build()
    }
}
