package com.truedigital.features.tuned.data.ad.repository

import com.truedigital.features.tuned.api.ad.AdServicesApiInterface
import com.truedigital.features.tuned.data.ad.model.Ad
import com.truedigital.features.tuned.data.ad.model.AdSourceType
import com.truedigital.features.tuned.data.ad.model.request.TritonAdRequest
import io.reactivex.Single

class AdRepositoryImpl(
    private val adServicesApiInterface: AdServicesApiInterface
) : AdRepository {

    override fun getTritonAds(lsid: String, adSourceType: AdSourceType, sourceId: Int): Single<Ad> {
        val tritonAdRequest = TritonAdRequest(lsid, adSourceType, sourceId)
        return adServicesApiInterface.getTritonAd(tritonAdRequest).map {
            val ad = it.ad
            ad.vast = it.xml
            ad
        }
    }
}
