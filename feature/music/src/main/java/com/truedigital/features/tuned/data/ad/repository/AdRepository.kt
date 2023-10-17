package com.truedigital.features.tuned.data.ad.repository

import com.truedigital.features.tuned.data.ad.model.Ad
import com.truedigital.features.tuned.data.ad.model.AdSourceType
import io.reactivex.Single

interface AdRepository {
    fun getTritonAds(lsid: String, adSourceType: AdSourceType, sourceId: Int): Single<Ad>
}
