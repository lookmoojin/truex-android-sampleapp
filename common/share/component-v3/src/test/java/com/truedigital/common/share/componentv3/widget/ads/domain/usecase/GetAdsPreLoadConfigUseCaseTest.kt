package com.truedigital.common.share.componentv3.widget.ads.domain.usecase

import com.truedigital.common.share.componentv3.widget.feedmenutab.data.GetCommunityTabConfigRepository
import com.truedigital.common.share.componentv3.widget.feedmenutab.data.model.CommunityTabConfigModel
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.extensions.collectSafe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GetAdsPreLoadConfigUseCaseTest {
    private lateinit var getAdsPreLoadConfigUseCase: GetAdsPreLoadConfigUseCase
    private var getCommunityTabConfigRepository: GetCommunityTabConfigRepository = mockk()
    private var localizationRepository: LocalizationRepository = mockk()

    @BeforeEach
    fun setUp() {
        getAdsPreLoadConfigUseCase = GetAdsPreLoadConfigUseCaseImpl(
            getCommunityTabConfigRepository = getCommunityTabConfigRepository,
            localizationRepository = localizationRepository
        )
    }

    @Test
    fun testGetAdsPreLoadConfig_Success_Should_Return_True() = runTest {
        val mockResponse = CommunityTabConfigModel().apply {
            todayPage = CommunityTabConfigModel.TodayPageConfig().apply {
                adsPreLoad = CommunityTabConfigModel.AdsPreLoad().apply {
                    enable = CommunityTabConfigModel.CommunityTabEnableConfig(
                        isEnable = true
                    )
                }
            }
        }
        every { localizationRepository.getAppCountryCode() } returns
            LocalizationRepository.Localization.TH.languageCode
        coEvery {
            getCommunityTabConfigRepository.getCommunityTabConfig(any())
        } returns mockResponse

        getAdsPreLoadConfigUseCase.execute()
            .collectSafe { isEnable ->
                assertTrue { isEnable }
            }
    }

    @Test
    fun testGetAdsPreLoadConfig_NotHaveConfig_Should_Return_False() = runTest {
        every { localizationRepository.getAppCountryCode() } returns
            LocalizationRepository.Localization.TH.languageCode
        coEvery {
            getCommunityTabConfigRepository.getCommunityTabConfig(any())
        } returns null

        getAdsPreLoadConfigUseCase.execute()
            .collectSafe { isEnable ->
                assertFalse { isEnable }
            }
    }

    @Test
    fun testGetAdsPreLoadConfig_Success_But_False_Should_Return_False() = runTest {
        val mockResponse = CommunityTabConfigModel().apply {
            todayPage = CommunityTabConfigModel.TodayPageConfig().apply {
                adsPreLoad = CommunityTabConfigModel.AdsPreLoad().apply {
                    enable = CommunityTabConfigModel.CommunityTabEnableConfig(
                        isEnable = false
                    )
                }
            }
        }
        every { localizationRepository.getAppCountryCode() } returns
            LocalizationRepository.Localization.TH.languageCode
        coEvery {
            getCommunityTabConfigRepository.getCommunityTabConfig(any())
        } returns mockResponse

        getAdsPreLoadConfigUseCase.execute()
            .collectSafe { isEnable ->
                assertFalse { isEnable }
            }
    }

    @Test
    fun testGetAdsPreLoadConfig_Failed_Not_Have_Enable_Should_Return_False() = runTest {
        val mockResponse = CommunityTabConfigModel().apply {
            todayPage = CommunityTabConfigModel.TodayPageConfig().apply {
                adsPreLoad = CommunityTabConfigModel.AdsPreLoad().apply {
                    enable = null
                }
            }
        }
        every { localizationRepository.getAppCountryCode() } returns
            LocalizationRepository.Localization.TH.languageCode
        coEvery {
            getCommunityTabConfigRepository.getCommunityTabConfig(any())
        } returns mockResponse

        getAdsPreLoadConfigUseCase.execute()
            .collectSafe { isEnable ->
                assertFalse { isEnable }
            }
    }

    @Test
    fun testGetAdsPreLoadConfig_Failed_Not_Have_AdsPreLoad_Should_Return_False() = runTest {
        val mockResponse = CommunityTabConfigModel().apply {
            todayPage = CommunityTabConfigModel.TodayPageConfig().apply {
                adsPreLoad = null
            }
        }
        every { localizationRepository.getAppCountryCode() } returns
            LocalizationRepository.Localization.TH.languageCode
        coEvery {
            getCommunityTabConfigRepository.getCommunityTabConfig(any())
        } returns mockResponse

        getAdsPreLoadConfigUseCase.execute()
            .collectSafe { isEnable ->
                assertFalse { isEnable }
            }
    }

    @Test
    fun testGetAdsPreLoadConfig_Failed_Not_Have_TodayPage_Should_Return_False() = runTest {
        val mockResponse = CommunityTabConfigModel().apply {
            todayPage = null
        }
        every { localizationRepository.getAppCountryCode() } returns
            LocalizationRepository.Localization.TH.languageCode
        coEvery {
            getCommunityTabConfigRepository.getCommunityTabConfig(any())
        } returns mockResponse

        getAdsPreLoadConfigUseCase.execute()
            .collectSafe { isEnable ->
                assertFalse { isEnable }
            }
    }
}
