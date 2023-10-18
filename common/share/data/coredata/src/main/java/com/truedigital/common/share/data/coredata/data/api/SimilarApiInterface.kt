package com.truedigital.common.share.data.coredata.data.api

import com.truedigital.common.share.data.coredata.data.model.PersonalizeSimilarResponse
import com.truedigital.common.share.data.coredata.data.model.RequestQuerySimilar
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SimilarApiInterface {
    @POST("cms-customer-content/v1/graphql")
    suspend fun getSimilarData(
        @Body query: RequestQuerySimilar
    ): Response<PersonalizeSimilarResponse>
}
