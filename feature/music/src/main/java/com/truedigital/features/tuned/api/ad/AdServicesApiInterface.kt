package com.truedigital.features.tuned.api.ad

import com.truedigital.features.tuned.data.ad.model.request.TritonAdRequest
import com.truedigital.features.tuned.data.ad.model.response.CampaignAd
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface AdServicesApiInterface {
    @POST("users/gettritonad")
    fun getTritonAd(@Body tritonAdRequest: TritonAdRequest): Single<CampaignAd>
}
