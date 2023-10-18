package com.truedigital.features.music.domain.search.usecase

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.features.music.domain.search.model.ThemeType
import com.truedigital.features.tuned.R
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantTaskExecutorExtension::class)
class GetSearchTopMenuUseCaseTest {

    private lateinit var getSearchTopMenuUseCase: GetSearchTopMenuUseCase
    private var localizationRepository: LocalizationRepository = mock()

    @BeforeEach
    fun setup() {
        getSearchTopMenuUseCase = GetSearchTopMenuUseCaseImpl(localizationRepository)
    }

    @Test
    fun `test getSearchTopMenuUseCase EN should return fix top menu data EN`() = runTest {
        whenever(localizationRepository.getAppLanguageCode()).thenReturn(
            LocalizationRepository.Localization.EN.languageCode
        )

        val topMenuList =
            getSearchTopMenuUseCase.execute(activePosition = 0, theme = ThemeType.DARK)

        assertThat(topMenuList.size, `is`(5))
        assertThat(topMenuList[0].name, `is`("All"))
        assertThat(topMenuList[0].isActive, `is`(true))
    }

    @Test
    fun `test getSearchTopMenuUseCase TH should return fix top menu data TH`() = runTest {
        whenever(localizationRepository.getAppLanguageCode()).thenReturn(
            LocalizationRepository.Localization.TH.languageCode
        )

        val topMenuList =
            getSearchTopMenuUseCase.execute(activePosition = 0, theme = ThemeType.DARK)

        assertThat(topMenuList.size, `is`(5))
        assertThat(topMenuList[0].name, `is`("ทั้งหมด"))
    }

    @Test
    fun `test getSearchTopMenuUseCase MY should return fix top menu data EN`() = runTest {
        whenever(localizationRepository.getAppLanguageCode()).thenReturn(
            LocalizationRepository.Localization.MY.languageCode
        )

        val topMenuList =
            getSearchTopMenuUseCase.execute(activePosition = 0, theme = ThemeType.DARK)

        assertThat(5, `is`(topMenuList.size))
        assertThat(topMenuList[0].name, `is`("All"))
    }

    @Test
    fun `test getSearchTopMenuUseCase darkTheme should return Dark`() = runTest {
        whenever(localizationRepository.getAppLanguageCode()).thenReturn(
            LocalizationRepository.Localization.EN.languageCode
        )

        val topMenuList =
            getSearchTopMenuUseCase.execute(activePosition = 0, theme = ThemeType.DARK)

        assertThat(topMenuList.size, `is`(5))
        assertThat(topMenuList[0].textActiveColor, `is`(R.color.white))
        assertThat(topMenuList[0].textInactiveColor, `is`(android.R.color.white))
        assertThat(topMenuList[0].buttonActiveDrawable, `is`(R.drawable.bg_button_round_red))
        assertThat(
            topMenuList[0].buttonInactiveDrawable,
            `is`(R.drawable.bg_button_round_inactive_dark)
        )
    }

    @Test
    fun `test getSearchTopMenuUseCase lightTheme should return Light`() = runTest {
        whenever(localizationRepository.getAppLanguageCode()).thenReturn(
            LocalizationRepository.Localization.EN.languageCode
        )

        val topMenuList =
            getSearchTopMenuUseCase.execute(activePosition = 0, theme = ThemeType.LIGHT)

        assertThat(topMenuList.size, `is`(5))
        assertThat(topMenuList[0].textActiveColor, `is`(R.color.white))
        assertThat(topMenuList[0].textInactiveColor, `is`(R.color.bg_text_tag_item_search))
        assertThat(topMenuList[0].buttonActiveDrawable, `is`(R.drawable.bg_button_round_red))
        assertThat(
            topMenuList[0].buttonInactiveDrawable,
            `is`(R.drawable.bg_button_round_inactive_light)
        )
    }

    @Test
    fun `test getSearchTopMenuUseCase activePosition should return active that position`() =
        runTest {
            whenever(localizationRepository.getAppLanguageCode()).thenReturn(
                LocalizationRepository.Localization.EN.languageCode
            )

            val topMenuList =
                getSearchTopMenuUseCase.execute(activePosition = 2, theme = ThemeType.DARK)

            assertThat(topMenuList.size, `is`(5))
            assertThat(topMenuList[0].isActive, `is`(false))
            assertThat(topMenuList[1].isActive, `is`(false))
            assertThat(topMenuList[2].isActive, `is`(true))
            assertThat(topMenuList[3].isActive, `is`(false))
            assertThat(topMenuList[4].isActive, `is`(false))
        }

    @Test
    fun `test getSearchTopMenuUseCase activePositionNotHave should not return active`() = runTest {
        whenever(localizationRepository.getAppLanguageCode()).thenReturn(
            LocalizationRepository.Localization.EN.languageCode
        )

        val topMenuList =
            getSearchTopMenuUseCase.execute(activePosition = 5, theme = ThemeType.DARK)

        assertThat(topMenuList.size, `is`(5))
        assertThat(topMenuList[0].isActive, `is`(false))
        assertThat(topMenuList[1].isActive, `is`(false))
        assertThat(topMenuList[2].isActive, `is`(false))
        assertThat(topMenuList[3].isActive, `is`(false))
        assertThat(topMenuList[4].isActive, `is`(false))
    }

    @Test
    fun `test getSearchTopMenuUseCase activePositionDefault should return active that position`() =
        runTest {
            whenever(localizationRepository.getAppLanguageCode()).thenReturn(
                LocalizationRepository.Localization.EN.languageCode
            )

            val topMenuList = getSearchTopMenuUseCase.execute(theme = ThemeType.DARK)

            assertThat(topMenuList.size, `is`(5))
            assertThat(topMenuList[0].isActive, `is`(true))
            assertThat(topMenuList[1].isActive, `is`(false))
            assertThat(topMenuList[2].isActive, `is`(false))
            assertThat(topMenuList[3].isActive, `is`(false))
            assertThat(topMenuList[4].isActive, `is`(false))
        }
}
