package com.truedigital.features.utils

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MusicTestApiInterface {
    @GET("/test")
    fun test(): Call<TestApiData>

    @GET("/test")
    fun testWithQuery(
        @Query("query") query: String
    ): Call<TestApiData>
}

data class TestApiData(val name: String)
