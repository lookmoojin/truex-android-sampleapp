package com.truedigital.common.share.componentv3.widget.searchanimation.usecase

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.share.data.firestoreconfig.initialappconfig.repository.InitialAppConfigRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetSearchAnimationUseCaseTest {

    private val initialAppConfigRepository: InitialAppConfigRepository = mock()
    private lateinit var getSearchAnimationUseCase: GetSearchAnimationUseCase

    @BeforeEach
    fun setup() {
        getSearchAnimationUseCase = GetSearchAnimationUseCaseImpl(initialAppConfigRepository)
    }

    @Test
    fun getSearchAnimationUseCaseWithCorrectPageKey() {
        val listenChildMap = mapOf(
            "ads_url" to "https://fn.dmpcdn.com/TrueIDApp/ads/trueid_ads.json",
            "campaign_name" to "campaign-music-2021",
            "deeplink" to "https://music.trueid.net",
            "end_date" to "2021-09-01 08:00:00",
            "start_date" to "2021-07-01 08:00:00"
        )
        val childMap = mapOf(
            "listen" to listenChildMap
        )
        val response = mapOf(
            "ads" to childMap
        )
        doReturn(response).whenever(initialAppConfigRepository).getConfigByKey("search")

        val result = getSearchAnimationUseCase.execute("listen").test()

        verify(initialAppConfigRepository, times(1)).getConfigByKey("search")
        result.assertValue { data ->
            data.adsUrl == "https://fn.dmpcdn.com/TrueIDApp/ads/trueid_ads.json" &&
                data.deeplink == "https://music.trueid.net" &&
                data.searchAnimationTime.startDate == "2021-07-01 08:00:00" &&
                data.searchAnimationTime.endDate == "2021-09-01 08:00:00"
        }
    }

    @Test
    fun getSearchAnimationUseCaseWithIncorrectPageKey() {
        val listenChildMap = mapOf(
            "ads_url" to "https://fn.dmpcdn.com/TrueIDApp/ads/trueid_ads.json",
            "campaign_name" to "campaign-music-2021",
            "deeplink" to "https://music.trueid.net",
            "end_date" to "2021-09-01 08:00:00",
            "start_date" to "2021-07-01 08:00:00"
        )
        val childMap = mapOf(
            "listen" to listenChildMap
        )
        val response = mapOf(
            "ads" to childMap
        )
        doReturn(response).whenever(initialAppConfigRepository).getConfigByKey("search")

        val result = getSearchAnimationUseCase.execute("lis").test()

        verify(initialAppConfigRepository, times(1)).getConfigByKey("search")
        result.assertValue { data ->
            data.adsUrl == "" &&
                data.deeplink == "" &&
                data.searchAnimationTime.startDate == "" &&
                data.searchAnimationTime.endDate == ""
        }
    }

    @Test
    fun getSearchAnimationUseCaseWithBlankPageKey() {
        val listenChildMap = mapOf(
            "ads_url" to "https://fn.dmpcdn.com/TrueIDApp/ads/trueid_ads.json",
            "campaign_name" to "campaign-music-2021",
            "deeplink" to "https://music.trueid.net",
            "end_date" to "2021-09-01 08:00:00",
            "start_date" to "2021-07-01 08:00:00"
        )
        val childMap = mapOf(
            "listen" to listenChildMap
        )
        val response = mapOf(
            "ads" to childMap
        )
        doReturn(response).whenever(initialAppConfigRepository).getConfigByKey("search")

        val result = getSearchAnimationUseCase.execute("").test()

        verify(initialAppConfigRepository, times(1)).getConfigByKey("search")
        result.assertValue { data ->
            data.adsUrl == "" &&
                data.deeplink == "" &&
                data.searchAnimationTime.startDate == "" &&
                data.searchAnimationTime.endDate == ""
        }
    }

    @Test
    fun getSearchAnimationUseCaseWithIncorrectInitialKey() {
        val listenChildMap = mapOf(
            "ads_url" to "https://fn.dmpcdn.com/TrueIDApp/ads/trueid_ads.json",
            "campaign_name" to "campaign-music-2021",
            "deeplink" to "https://music.trueid.net",
            "end_date" to "2021-09-01 08:00:00",
            "start_date" to "2021-07-01 08:00:00"
        )
        val childMap = mapOf(
            "listen" to listenChildMap
        )
        val response = mapOf(
            "ads" to childMap
        )
        doReturn(response).whenever(initialAppConfigRepository).getConfigByKey("s")

        val result = getSearchAnimationUseCase.execute("listen").test()

        result.assertValue { data ->
            data.adsUrl == "" &&
                data.deeplink == "" &&
                data.searchAnimationTime.startDate == "" &&
                data.searchAnimationTime.endDate == ""
        }
    }

    @Test
    fun getSearchAnimationUseCaseWithIncorrectAdsKey() {
        val listenChildMap = mapOf(
            "ads_url" to "https://fn.dmpcdn.com/TrueIDApp/ads/trueid_ads.json",
            "campaign_name" to "campaign-music-2021",
            "deeplink" to "https://music.trueid.net",
            "end_date" to "2021-09-01 08:00:00",
            "start_date" to "2021-07-01 08:00:00"
        )
        val childMap = mapOf(
            "listen" to listenChildMap
        )
        val response = mapOf(
            "advertise" to childMap
        )
        doReturn(response).whenever(initialAppConfigRepository).getConfigByKey("search")

        val result = getSearchAnimationUseCase.execute("listen").test()

        result.assertValue { data ->
            data.adsUrl == "" &&
                data.deeplink == "" &&
                data.searchAnimationTime.startDate == "" &&
                data.searchAnimationTime.endDate == ""
        }
    }
}
