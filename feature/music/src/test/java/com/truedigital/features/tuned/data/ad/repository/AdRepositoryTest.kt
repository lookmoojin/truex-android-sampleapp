package com.truedigital.features.tuned.data.ad.repository

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.tuned.api.ad.AdServicesApiInterface
import com.truedigital.features.tuned.data.ad.model.Ad
import com.truedigital.features.tuned.data.ad.model.AdSourceType
import com.truedigital.features.tuned.data.ad.model.response.CampaignAd
import io.reactivex.Single
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class AdRepositoryTest {

    private val adServicesApiInterface: AdServicesApiInterface = mock()
    private lateinit var adRepository: AdRepository

    @BeforeEach
    fun setUp() {
        adRepository = AdRepositoryImpl(adServicesApiInterface)
    }

    @Test
    fun getTritonAds_returnAds() {
        val xmlMock = "xml"
        val adMock = Ad(
            title = "title",
            impressionUrl = "impressionUrl",
            duration = "duration",
            mediaFile = "mediaFile",
            image = "image",
            clickUrl = "clickUrl",
            vast = "vast"
        )
        val campaignAdMock = CampaignAd(ad = adMock, xml = xmlMock)

        whenever(adServicesApiInterface.getTritonAd(any())).thenReturn(Single.just(campaignAdMock))

        adRepository.getTritonAds("isId", AdSourceType.NONE, 1)
            .test()
            .assertNoErrors()
            .assertValue { ad ->
                ad.title == adMock.title &&
                    ad.impressionUrl == adMock.impressionUrl &&
                    ad.duration == adMock.duration &&
                    ad.mediaFile == adMock.mediaFile &&
                    ad.image == adMock.image &&
                    ad.clickUrl == adMock.clickUrl &&
                    ad.vast == xmlMock
            }
    }
}
