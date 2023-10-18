package com.truedigital.common.share.data.usecase

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.common.share.data.coredata.data.repository.CmsShelvesRepository
import com.truedigital.common.share.data.coredata.domain.GetBaseShelfUseCaseImpl
import com.truedigital.common.share.data.coredata.domain.constant.ShelfType
import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.Data
import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.Setting
import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.Shelf
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.extensions.collectSafe
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GetBaseShelfUseCaseTest {
    private val cmsShelvesRepository: CmsShelvesRepository = mock()
    private val localizationRepository: LocalizationRepository = mock()

    private val shelfId = "XPLrGrK8DjX5"
    private val country = "th"
    private val fields = "setting,thumb_list"

    private val getBaseShelfUseCase by lazy {
        GetBaseShelfUseCaseImpl(cmsShelvesRepository, localizationRepository)
    }

    @Test
    fun `When get baseshelf with navItems success Then should return NavigationBaseShelfModel`() =
        runTest {
            val languageCode = LocalizationRepository.Localization.TH.languageCode
            val mockData = Data().apply {
                shelfList = listOf(
                    Shelf().apply {
                        id = "XPLrGrK8DjX5"
                        title = "(TrueID App) Watch Top Navigation : For you"
                        setting = Setting().apply {
                            this.titleEn = "For You"
                            this.titleTh = "สำหรับคุณ"
                            this.viewType = ShelfType.VIEW_TYPE_TRUE_ID_TOP_NAV
                            this.type = ShelfType.TYPE_BY_SHELF_ID
                            this.shelfSlug = "highlight"
                        }
                    },
                    Shelf().apply {
                        id = "Z6EkJk23Va9W"
                        title = "(TrueID App) Watch Top Navigation : Live TV"
                        setting = Setting().apply {
                            this.titleEn = "wrong viewType shelf"
                            this.titleTh = "viewType ไม่ถูกต้อง"
                            this.viewType = ShelfType.VIEW_TYPE_VERTICAL_SHELF
                            this.type = ShelfType.TYPE_BY_SHELF_ID
                            this.shelfSlug = "highlight"
                        }
                    }
                )
                setting = Setting().apply {
                    titleEn = "Watch"
                    titleTh = "ดู"
                }
            }

            whenever(cmsShelvesRepository.getCmsShelfListData(shelfId, country, fields)).thenReturn(
                flowOf(mockData)
            )
            whenever(localizationRepository.getAppLanguageCode()).thenReturn(languageCode)
            whenever(localizationRepository.getAppCountryCode()).thenReturn(country)

            getBaseShelfUseCase.execute(shelfId)
                .collectSafe { navigationBaseShelfModel ->
                    val navItems = navigationBaseShelfModel.navItems

                    assert(navigationBaseShelfModel.title.isNotEmpty())
                    assertEquals(navigationBaseShelfModel.title, mockData.setting?.titleTh)
                    assert(navItems.size == 1)
                    assertEquals(
                        "สำหรับคุณ", navItems[0].title
                    )
                }
        }

    @Test
    fun `When get baseshelf success and navItems is empty Then should throw error`() =
        runTest {
            val languageCode = LocalizationRepository.Localization.TH.languageCode
            val mockData = Data().apply {
                shelfList = listOf()
                setting = Setting().apply {
                    titleEn = "Watch"
                    titleTh = "ดู"
                }
            }

            whenever(cmsShelvesRepository.getCmsShelfListData(shelfId, country, fields)).thenReturn(
                flowOf(mockData)
            )
            whenever(localizationRepository.getAppLanguageCode()).thenReturn(languageCode)
            whenever(localizationRepository.getAppCountryCode()).thenReturn(country)

            getBaseShelfUseCase.execute(shelfId)
                .catch { exception ->
                    assertEquals(exception.message, "Empty nav list")
                }
                .collectSafe {
                }
        }
}
