package com.tdg.login.api

import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit

class ApiBuilder {
    open class ApiBasicBuilder(
        val okHttpClient: OkHttpClient,
        val gsonConverterFactory: Converter.Factory
    ) {
        inline fun <reified T> build(baseUrl: String): T {
            return Retrofit.Builder()
//                .client(OkHttpClient.Builder().build())
                .client(okHttpClient)
                .baseUrl(baseUrl)
                .addConverterFactory(gsonConverterFactory)
                .build()
                .create(T::class.java)
        }
    }
}