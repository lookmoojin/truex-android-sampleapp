package com.truedigital.common.share.componentv3.domain.communitytab

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.common.share.componentv3.widget.feedmenutab.data.GetCommunityTabConfigRepository
import com.truedigital.common.share.componentv3.widget.feedmenutab.data.model.CommunityTabConfigModel
import com.truedigital.common.share.componentv3.widget.feedmenutab.domain.usecase.GetCommunityTabConfigUseCase
import com.truedigital.common.share.componentv3.widget.feedmenutab.domain.usecase.GetCommunityTabConfigUseCaseImpl
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.extensions.collectSafe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetCommunityTabConfigUseCaseTest {
    private lateinit var getCommunityTabConfigUseCase: GetCommunityTabConfigUseCase
    private var getCommunityTabConfigRepository: GetCommunityTabConfigRepository = mock()
    private var localizationRepository: LocalizationRepository = mock()

    @BeforeEach
    fun setup() {
        getCommunityTabConfigUseCase = GetCommunityTabConfigUseCaseImpl(
            getCommunityTabConfigRepository, localizationRepository
        )
    }

    @Test
    fun test_get_community_tab_th_config() = runTest {
        whenever(localizationRepository.getAppLanguageCode()).thenReturn(
            LocalizationRepository.Localization.TH.languageCode
        )
        whenever(localizationRepository.getAppCountryCode()).thenReturn(
            LocalizationRepository.Localization.TH.countryCode
        )
        whenever(localizationRepository.localize(any())).thenReturn("th")
        whenever(getCommunityTabConfigRepository.getCommunityTabConfig(any())).thenReturn(
            fakeResponseConfig()
        )

        getCommunityTabConfigUseCase.execute().collectSafe { communityTabData ->
            assertEquals("th", communityTabData?.communityTitle)
            assertEquals("th", communityTabData?.forYouTitle)
        }
    }

    @Test
    fun test_get_community_tab_en_config() = runTest {
        whenever(localizationRepository.getAppLanguageCode()).thenReturn(
            LocalizationRepository.Localization.EN.languageCode
        )
        whenever(localizationRepository.getAppCountryCode()).thenReturn(
            LocalizationRepository.Localization.EN.countryCode
        )
        whenever(localizationRepository.localize(any())).thenReturn("en")
        whenever(getCommunityTabConfigRepository.getCommunityTabConfig(any())).thenReturn(
            fakeResponseConfig()
        )

        getCommunityTabConfigUseCase.execute().collectSafe { communityTabData ->
            assertEquals("en", communityTabData?.communityTitle)
            assertEquals("en", communityTabData?.forYouTitle)
        }
    }

    @Test
    fun test_get_community_tab_my_config() = runTest {
        whenever(localizationRepository.getAppLanguageCode()).thenReturn(
            LocalizationRepository.Localization.MY.languageCode
        )
        whenever(localizationRepository.getAppCountryCode()).thenReturn(
            LocalizationRepository.Localization.MY.countryCode
        )
        whenever(localizationRepository.localize(any())).thenReturn("my")
        whenever(getCommunityTabConfigRepository.getCommunityTabConfig(any())).thenReturn(
            fakeResponseConfig()
        )

        getCommunityTabConfigUseCase.execute().collectSafe { communityTabData ->
            assertEquals("my", communityTabData?.communityTitle)
            assertEquals("my", communityTabData?.forYouTitle)
        }
    }

    @Test
    fun test_get_community_tab_ph_config() = runTest {
        whenever(localizationRepository.getAppLanguageCode()).thenReturn(
            LocalizationRepository.Localization.PH.languageCode
        )
        whenever(localizationRepository.getAppCountryCode()).thenReturn(
            LocalizationRepository.Localization.PH.countryCode
        )
        whenever(localizationRepository.localize(any())).thenReturn("ph")
        whenever(getCommunityTabConfigRepository.getCommunityTabConfig(any())).thenReturn(
            fakeResponseConfig()
        )

        getCommunityTabConfigUseCase.execute().collectSafe { communityTabData ->
            assertEquals("ph", communityTabData?.communityTitle)
            assertEquals("ph", communityTabData?.forYouTitle)
        }
    }

    @Test
    fun test_get_community_tab_id_config() = runTest {
        whenever(localizationRepository.getAppLanguageCode()).thenReturn(
            LocalizationRepository.Localization.IN.languageCode
        )
        whenever(localizationRepository.getAppCountryCode()).thenReturn(
            LocalizationRepository.Localization.IN.countryCode
        )
        whenever(localizationRepository.localize(any())).thenReturn("")
        whenever(getCommunityTabConfigRepository.getCommunityTabConfig(any())).thenReturn(
            fakeResponseConfig()
        )

        getCommunityTabConfigUseCase.execute().collectSafe { communityTabData ->
            assertEquals("", communityTabData?.communityTitle)
            assertEquals("", communityTabData?.forYouTitle)
        }
    }

    @Test
    fun test_get_community_tab_vn_config() = runTest {
        whenever(localizationRepository.getAppLanguageCode()).thenReturn(
            LocalizationRepository.Localization.VN.languageCode
        )
        whenever(localizationRepository.getAppCountryCode()).thenReturn(
            LocalizationRepository.Localization.VN.countryCode
        )
        whenever(localizationRepository.localize(any())).thenReturn("vi")
        whenever(getCommunityTabConfigRepository.getCommunityTabConfig(any())).thenReturn(
            fakeResponseConfig()
        )

        getCommunityTabConfigUseCase.execute().collectSafe { communityTabData ->
            assertEquals("vi", communityTabData?.communityTitle)
            assertEquals("vi", communityTabData?.forYouTitle)
        }
    }

    @Test
    fun test_get_community_tab_km_config() = runTest {
        whenever(localizationRepository.getAppLanguageCode()).thenReturn(
            LocalizationRepository.Localization.KH.languageCode
        )
        whenever(localizationRepository.getAppCountryCode()).thenReturn(
            LocalizationRepository.Localization.KH.countryCode
        )
        whenever(localizationRepository.localize(any())).thenReturn("kh")
        whenever(getCommunityTabConfigRepository.getCommunityTabConfig(any())).thenReturn(
            fakeResponseConfig()
        )

        getCommunityTabConfigUseCase.execute().collectSafe { communityTabData ->
            assertEquals("kh", communityTabData?.communityTitle)
            assertEquals("kh", communityTabData?.forYouTitle)
        }
    }

    @Test
    fun test_get_community_tab_config_has_null() = runTest {
        whenever(localizationRepository.getAppLanguageCode()).thenReturn(
            LocalizationRepository.Localization.TH.languageCode
        )
        whenever(localizationRepository.getAppCountryCode()).thenReturn(
            LocalizationRepository.Localization.TH.countryCode
        )
        whenever(localizationRepository.localize(any())).thenReturn("th")
        whenever(getCommunityTabConfigRepository.getCommunityTabConfig(any())).thenReturn(
            null
        )

        getCommunityTabConfigUseCase.execute().collectSafe { communityTabData ->
            assertTrue(communityTabData == null)
        }
    }

    @Test
    fun test_get_community_tab_config_title_has_null() = runTest {
        whenever(localizationRepository.getAppLanguageCode()).thenReturn(
            LocalizationRepository.Localization.TH.languageCode
        )
        whenever(localizationRepository.getAppCountryCode()).thenReturn(
            LocalizationRepository.Localization.TH.countryCode
        )
        whenever(localizationRepository.localize(any())).thenReturn("th")
        whenever(getCommunityTabConfigRepository.getCommunityTabConfig(any())).thenReturn(
            CommunityTabConfigModel().apply {
                this.communityTab = CommunityTabConfigModel.CommunityTab(
                    enable = CommunityTabConfigModel.CommunityTabEnableConfig(
                        isEnable = true
                    ),
                    title = null
                )
            }
        )

        getCommunityTabConfigUseCase.execute().collectSafe { communityTabData ->
            assertTrue(communityTabData == null)
        }
    }

    @Test
    fun test_get_community_tab_config_enable_has_false() = runTest {
        whenever(localizationRepository.getAppLanguageCode()).thenReturn(
            LocalizationRepository.Localization.TH.languageCode
        )
        whenever(localizationRepository.getAppCountryCode()).thenReturn(
            LocalizationRepository.Localization.TH.countryCode
        )
        whenever(localizationRepository.localize(any())).thenReturn("th")
        whenever(getCommunityTabConfigRepository.getCommunityTabConfig(any())).thenReturn(
            CommunityTabConfigModel().apply {
                this.communityTab = CommunityTabConfigModel.CommunityTab(
                    enable = CommunityTabConfigModel.CommunityTabEnableConfig(
                        isEnable = false
                    ),
                    title = null
                )
            }
        )

        getCommunityTabConfigUseCase.execute().collectSafe { communityTabData ->
            assertTrue(communityTabData == null)
        }
    }

    @Test
    fun test_get_community_tab_config_enable_has_null() = runTest {
        whenever(localizationRepository.getAppLanguageCode()).thenReturn(
            LocalizationRepository.Localization.TH.languageCode
        )
        whenever(localizationRepository.getAppCountryCode()).thenReturn(
            LocalizationRepository.Localization.TH.countryCode
        )
        whenever(localizationRepository.localize(any())).thenReturn("th")
        whenever(getCommunityTabConfigRepository.getCommunityTabConfig(any())).thenReturn(
            CommunityTabConfigModel().apply {
                this.communityTab = CommunityTabConfigModel.CommunityTab(
                    enable = null,
                    title = CommunityTabConfigModel.CommunityTabTitle(
                        en = "en",
                        my = "my",
                        th = "th"
                    )
                )
            }
        )

        getCommunityTabConfigUseCase.execute().collectSafe { communityTabData ->
            assertTrue(communityTabData == null)
        }
    }

    private fun fakeResponseConfig() =
        CommunityTabConfigModel().apply {
            this.communityTab = CommunityTabConfigModel.CommunityTab(
                enable = CommunityTabConfigModel.CommunityTabEnableConfig(
                    isEnable = true
                ),
                title = CommunityTabConfigModel.CommunityTabTitle(
                    ph = "ph",
                    en = "en",
                    my = "my",
                    th = "th",
                    vi = "vi",
                    kh = "kh",
                    id = ""
                )
            )
            this.forYouTab = CommunityTabConfigModel.CommunityTab(
                title = CommunityTabConfigModel.CommunityTabTitle(
                    ph = "ph",
                    en = "en",
                    my = "my",
                    th = "th",
                    vi = "vi",
                    kh = "kh",
                    id = ""
                )
            )
        }
}
