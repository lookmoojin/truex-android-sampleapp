package com.tdg.login.base

import okhttp3.Interceptor
import okhttp3.OkHttpClient

fun OkHttpClient.addInterceptor(interceptor: Interceptor): OkHttpClient {
    return this.newBuilder().addInterceptor(interceptor).build()
}
