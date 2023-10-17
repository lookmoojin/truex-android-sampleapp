package com.truedigital.features.tuned.api

import retrofit2.Retrofit

object MusicApiBuilder {
    inline fun <reified T> build(retrofit: Retrofit): T {
        return retrofit.create(T::class.java)
    }
}
