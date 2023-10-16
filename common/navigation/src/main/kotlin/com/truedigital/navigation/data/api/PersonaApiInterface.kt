package com.truedigital.navigation.data.api

import com.truedigital.navigation.data.PersonaResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PersonaApiInterface {

    @GET("cms-customer-content/v1/segmentation")
    suspend fun getPersonaData(
        @Query("country") country: String,
        @Query("lang") lang: String,
        @Query("page_name") pageName: String,
        @Query("placement_id") placementId: String
    ): Response<PersonaResponse>
}
