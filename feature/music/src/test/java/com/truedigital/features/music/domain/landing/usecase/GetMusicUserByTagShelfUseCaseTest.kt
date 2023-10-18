package com.truedigital.features.music.domain.landing.usecase

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.common.share.data.coredata.data.repository.CmsShelvesRepository
import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.Data
import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.Setting
import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.Shelf
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.features.music.data.landing.model.response.tagname.TagNameResponse
import com.truedigital.features.music.data.landing.repository.MusicLandingRepository
import com.truedigital.features.music.data.trending.model.response.playlist.Translation
import com.truedigital.features.music.domain.landing.model.MusicForYouItemModel
import com.truedigital.features.music.domain.landing.model.MusicHeroBannerDeeplinkType
import com.truedigital.features.tuned.data.productlist.model.ProductListType
import com.truedigital.features.utils.MockDataModel
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@InternalCoroutinesApi
internal class GetMusicUserByTagShelfUseCaseTest {

    private lateinit var getMusicUserByTagShelfUseCase: GetMusicUserByTagShelfUseCase
    private var musicLandingRepository: MusicLandingRepository = mock()
    private var mapRadioUseCase: MapRadioUseCase = mock()
    private var cmsShelvesRepository: CmsShelvesRepository = mock()
    private val localizationRepository: LocalizationRepository = mock()
    private var decodeMusicHeroBannerDeeplinkUseCase: DecodeMusicHeroBannerDeeplinkUseCase = mock()
    private val mockShelfList = listOf(
        Shelf().apply {
            thumb = "thumb"
            setting = Setting().apply {
                deepLink = "deepLink"
            }
        },
        Shelf().apply {
            thumb = "thumb2"
            setting = Setting().apply {
                deepLink = "deepLink2"
            }
        }
    )

    @BeforeEach
    fun setup() {
        getMusicUserByTagShelfUseCase = GetMusicUserByTagShelfUseCaseImpl(
            musicLandingRepository,
            mapRadioUseCase,
            cmsShelvesRepository,
            localizationRepository,
            decodeMusicHeroBannerDeeplinkUseCase
        )
    }

    @Test
    fun testTag_tagIsEmpty_returnError() = runTest {
        // When
        val flow = getMusicUserByTagShelfUseCase.execute("")

        // Then
        flow.catch { exception ->
            assertEquals(
                GetMusicUserByTagShelfUseCaseImpl.ERROR_CONDITION,
                exception.message
            )
        }.collect()
    }

    @Test
    fun testGetTagName_displayNameIsNull_returnEmptyList() = runTest {
        // Given
        whenever(musicLandingRepository.getTagByName(any())).thenReturn(
            flowOf(TagNameResponse())
        )

        // When
        val flow = getMusicUserByTagShelfUseCase.execute("tag")

        // Then
        flow.collect { (itemList, productType, seeMoreDeeplink) ->
            assertTrue(itemList.isEmpty())
            assertEquals(productType, ProductListType.TAGGED_USER)
            assertTrue(seeMoreDeeplink.isEmpty())
        }
    }

    @Test
    fun testGetTagName_displayNameIsEmpty_returnEmptyList() = runTest {
        // Given
        whenever(musicLandingRepository.getTagByName(any())).thenReturn(
            flowOf(
                TagNameResponse().apply {
                    displayName = listOf()
                }
            )
        )

        // When
        val flow = getMusicUserByTagShelfUseCase.execute("tag")

        // Then
        flow.collect { (itemList, productType, seeMoreDeeplink) ->
            assertTrue(itemList.isEmpty())
            assertEquals(productType, ProductListType.TAGGED_USER)
            assertTrue(seeMoreDeeplink.isEmpty())
        }
    }

    @Test
    fun testGetTagName_displayNameLanguageIsNotEn_returnEmptyList() = runTest {
        // Given
        whenever(musicLandingRepository.getTagByName(any())).thenReturn(
            flowOf(
                TagNameResponse().apply {
                    displayName = listOf(
                        Translation(
                            language = "th",
                            value = "value"
                        )
                    )
                }
            )
        )

        // When
        val flow = getMusicUserByTagShelfUseCase.execute("tag")

        // Then
        flow.collect { (itemList, productType, seeMoreDeeplink) ->
            assertTrue(itemList.isEmpty())
            assertEquals(productType, ProductListType.TAGGED_USER)
            assertTrue(seeMoreDeeplink.isEmpty())
        }
    }

    @Test
    fun testGetTagName_displayNameValueIsEmpty_returnEmptyList() = runTest {
        // Given
        whenever(musicLandingRepository.getTagByName(any())).thenReturn(
            flowOf(
                TagNameResponse().apply {
                    displayName = listOf(
                        Translation(
                            language = "EN",
                            value = ""
                        )
                    )
                }
            )
        )

        // When
        val flow = getMusicUserByTagShelfUseCase.execute("tag")

        // Then
        flow.collect { (itemList, productType, seeMoreDeeplink) ->
            assertTrue(itemList.isEmpty())
            assertEquals(productType, ProductListType.TAGGED_USER)
            assertTrue(seeMoreDeeplink.isEmpty())
        }
    }

    @Test
    fun testGetCmsPublicContentShelfListData_settingIsNull_returnEmptyList() = runTest {
        // Given
        whenever(musicLandingRepository.getTagByName(any())).thenReturn(
            flowOf(
                TagNameResponse().apply {
                    displayName = listOf(
                        Translation(
                            language = "en",
                            value = "value"
                        )
                    )
                }
            )
        )
        whenever(localizationRepository.getAppCountryCode()).thenReturn("th")
        whenever(localizationRepository.getAppLanguageCode()).thenReturn("th")
        whenever(
            cmsShelvesRepository.getCmsPublicContentShelfListData(
                any(), any(), any(), anyOrNull(), anyOrNull(), anyOrNull()
            )
        ).thenReturn(
            flowOf(
                Data().apply {
                    setting = null
                    shelfList = mockShelfList
                }
            )
        )

        // When
        val flow = getMusicUserByTagShelfUseCase.execute("tag")

        // Then
        flow.collect { (itemList, productType, seeMoreDeeplink) ->
            assertTrue(itemList.isEmpty())
            assertEquals(productType, ProductListType.TAGGED_USER)
            assertTrue(seeMoreDeeplink.isEmpty())
        }
    }

    @Test
    fun testGetCmsPublicContentShelfListData_typeIsNotMatch_returnEmptyList() = runTest {
        // Given
        whenever(musicLandingRepository.getTagByName(any())).thenReturn(
            flowOf(
                TagNameResponse().apply {
                    displayName = listOf(Translation(language = "en", value = "value"))
                }
            )
        )
        whenever(localizationRepository.getAppCountryCode()).thenReturn("th")
        whenever(localizationRepository.getAppLanguageCode()).thenReturn("th")
        whenever(
            cmsShelvesRepository.getCmsPublicContentShelfListData(
                any(), any(), any(), anyOrNull(), anyOrNull(), anyOrNull()
            )
        ).thenReturn(
            flowOf(
                Data().apply {
                    setting = Setting().apply {
                        contentType = GetMusicUserByTagShelfUseCaseImpl.CONTENT_TYPE_HILIGHT
                        type = "type"
                        viewType = GetMusicUserByTagShelfUseCaseImpl.VIEW_TYPE_BANNER_SHELF
                    }
                    shelfList = mockShelfList
                }
            )
        )

        // When
        val flow = getMusicUserByTagShelfUseCase.execute("tag")

        // Then
        flow.collect { (itemList, productType, seeMoreDeeplink) ->
            assertTrue(itemList.isEmpty())
            assertEquals(productType, ProductListType.TAGGED_USER)
            assertTrue(seeMoreDeeplink.isEmpty())
        }
    }

    @Test
    fun testGetCmsPublicContentShelfListData_typeIsByShelfId_viewTypeIsNotMatch_returnEmptyList() =
        runTest {
            // Given
            whenever(musicLandingRepository.getTagByName(any())).thenReturn(
                flowOf(
                    TagNameResponse().apply {
                        displayName = listOf(Translation(language = "en", value = "value"))
                    }
                )
            )
            whenever(localizationRepository.getAppCountryCode()).thenReturn("th")
            whenever(localizationRepository.getAppLanguageCode()).thenReturn("th")
            whenever(
                cmsShelvesRepository.getCmsPublicContentShelfListData(
                    any(), any(), any(), anyOrNull(), anyOrNull(), anyOrNull()
                )
            ).thenReturn(
                flowOf(
                    Data().apply {
                        setting = Setting().apply {
                            contentType = GetMusicUserByTagShelfUseCaseImpl.CONTENT_TYPE_HILIGHT
                            type = GetMusicUserByTagShelfUseCaseImpl.TYPE_BY_SHELF_ID
                            viewType = "viewType"
                        }
                        shelfList = mockShelfList
                    }
                )
            )

            // When
            val flow = getMusicUserByTagShelfUseCase.execute("tag")

            // Then
            flow.collect { (itemList, productType, seeMoreDeeplink) ->
                assertTrue(itemList.isEmpty())
                assertEquals(productType, ProductListType.TAGGED_USER)
                assertTrue(seeMoreDeeplink.isEmpty())
            }
        }

    @Test
    fun testGetCmsPublicContentShelfListData_typeIsByShelfId_viewTypeIsBanner_shelfListIsNull_returnEmptyList() =
        runTest {
            // Given
            whenever(musicLandingRepository.getTagByName(any())).thenReturn(
                flowOf(
                    TagNameResponse().apply {
                        displayName = listOf(
                            Translation(language = "en", value = "value")
                        )
                    }
                )
            )
            whenever(localizationRepository.getAppCountryCode()).thenReturn("th")
            whenever(localizationRepository.getAppLanguageCode()).thenReturn("th")
            whenever(
                cmsShelvesRepository.getCmsPublicContentShelfListData(
                    any(), any(), any(), anyOrNull(), anyOrNull(), anyOrNull()
                )
            ).thenReturn(
                flowOf(
                    Data().apply {
                        setting = Setting().apply {
                            contentType = GetMusicUserByTagShelfUseCaseImpl.CONTENT_TYPE_HILIGHT
                            type = GetMusicUserByTagShelfUseCaseImpl.TYPE_BY_SHELF_ID
                            viewType = GetMusicUserByTagShelfUseCaseImpl.VIEW_TYPE_BANNER_SHELF
                        }
                        shelfList = null
                    }
                )
            )

            // When
            val flow = getMusicUserByTagShelfUseCase.execute("tag")

            // Then
            flow.collect { (itemList, productType, seeMoreDeeplink) ->
                assertTrue(itemList.isEmpty())
                assertEquals(productType, ProductListType.TAGGED_USER)
                assertTrue(seeMoreDeeplink.isEmpty())
            }
        }

    @Test
    fun testGetCmsPublicContentShelfListData_typeIsByShelfId_viewTypeIsBanner_shelfListIsEmpty_returnEmptyList() =
        runTest {
            // Given
            whenever(musicLandingRepository.getTagByName(any())).thenReturn(
                flowOf(
                    TagNameResponse().apply {
                        displayName = listOf(
                            Translation(language = "en", value = "value")
                        )
                    }
                )
            )
            whenever(localizationRepository.getAppCountryCode()).thenReturn("th")
            whenever(localizationRepository.getAppLanguageCode()).thenReturn("th")
            whenever(
                cmsShelvesRepository.getCmsPublicContentShelfListData(
                    any(), any(), any(), anyOrNull(), anyOrNull(), anyOrNull()
                )
            ).thenReturn(
                flowOf(
                    Data().apply {
                        setting = Setting().apply {
                            contentType = GetMusicUserByTagShelfUseCaseImpl.CONTENT_TYPE_HILIGHT
                            type = GetMusicUserByTagShelfUseCaseImpl.TYPE_BY_SHELF_ID
                            viewType = GetMusicUserByTagShelfUseCaseImpl.VIEW_TYPE_BANNER_SHELF
                        }
                        shelfList = emptyList()
                    }
                )
            )

            // When
            val flow = getMusicUserByTagShelfUseCase.execute("tag")

            // Then
            flow.collect { (itemList, productType, seeMoreDeeplink) ->
                assertTrue(itemList.isEmpty())
                assertEquals(productType, ProductListType.TAGGED_USER)
                assertTrue(seeMoreDeeplink.isEmpty())
            }
        }

    @Test
    fun testGetCmsPublicContentShelfListData_typeIsByShelfId_viewTypeIsBanner_shelfListNotEmpty_returnData() =
        runTest {
            // Given
            whenever(musicLandingRepository.getTagByName(any())).thenReturn(
                flowOf(
                    TagNameResponse().apply {
                        displayName = listOf(Translation(language = "en", value = "value"))
                    }
                )
            )
            whenever(localizationRepository.getAppCountryCode()).thenReturn("th")
            whenever(localizationRepository.getAppLanguageCode()).thenReturn("th")
            whenever(
                cmsShelvesRepository.getCmsPublicContentShelfListData(
                    any(), any(), any(), anyOrNull(), anyOrNull(), anyOrNull()
                )
            ).thenReturn(
                flowOf(
                    Data().apply {
                        setting = Setting().apply {
                            contentType = GetMusicUserByTagShelfUseCaseImpl.CONTENT_TYPE_HILIGHT
                            type = GetMusicUserByTagShelfUseCaseImpl.TYPE_BY_SHELF_ID
                            viewType = GetMusicUserByTagShelfUseCaseImpl.VIEW_TYPE_BANNER_SHELF
                        }
                        shelfList = mockShelfList
                    }
                )
            )
            whenever(decodeMusicHeroBannerDeeplinkUseCase.execute(any())).thenReturn(
                Pair(MusicHeroBannerDeeplinkType.PLAYLIST, "playlist")
            )

            // When
            val flow = getMusicUserByTagShelfUseCase.execute("tag")

            // Then
            flow.collect { (itemList, productType, seeMoreDeeplink) ->
                assertTrue(itemList.isNotEmpty())
                assertEquals(mockShelfList.size, itemList.size)
                assertEquals(
                    mockShelfList.first().thumb,
                    (itemList.first() as MusicForYouItemModel.MusicHeroBannerShelfItem).coverImage
                )
                assertEquals(productType, ProductListType.TAGGED_USER)
                assertTrue(seeMoreDeeplink.isEmpty())
            }
        }

    @Test
    fun execute_displayNameIsNotEmpty_responseDataIsHeroBanner_returnMusicHeroBannerShelfItem() =
        runTest {
            // Given
            val tagNameResponse = TagNameResponse().apply {
                displayName = listOf(Translation(language = "en", value = "value"))
            }

            val dataResponse = Data().apply {
                setting = Setting().apply {
                    contentType = GetMusicUserByTagShelfUseCaseImpl.CONTENT_TYPE_HILIGHT
                    type = GetMusicUserByTagShelfUseCaseImpl.TYPE_BY_SHELF_ID
                    viewType = GetMusicUserByTagShelfUseCaseImpl.VIEW_TYPE_BANNER_SHELF
                }
                shelfList = mockShelfList
            }
            val decodeDeeplinkResponse = Pair(MusicHeroBannerDeeplinkType.PLAYLIST, "playlist")

            whenever(localizationRepository.getAppCountryCode()).thenReturn("th")
            whenever(localizationRepository.getAppLanguageCode()).thenReturn("th")
            whenever(musicLandingRepository.getTagByName(any())).thenReturn(flowOf(tagNameResponse))
            whenever(
                cmsShelvesRepository.getCmsPublicContentShelfListData(
                    any(), any(), any(), anyOrNull(), anyOrNull(), anyOrNull()
                )
            ).thenReturn(flowOf(dataResponse))
            whenever(decodeMusicHeroBannerDeeplinkUseCase.execute(any())).thenReturn(
                decodeDeeplinkResponse
            )

            // When
            val flow = getMusicUserByTagShelfUseCase.execute("tag")

            // Then
            flow.collect { (itemList, productType, seeMoreDeeplink) ->
                assertTrue(itemList.first() is MusicForYouItemModel.MusicHeroBannerShelfItem)
                assertEquals(mockShelfList.size, itemList.size)

                val heroBannerItem =
                    itemList.first() as MusicForYouItemModel.MusicHeroBannerShelfItem
                assertEquals(0, heroBannerItem.index)
                assertEquals(mockShelfList.first().thumb, heroBannerItem.coverImage)
                assertEquals(decodeDeeplinkResponse, heroBannerItem.deeplinkPair)
                assertEquals(productType, ProductListType.TAGGED_USER)
                assertTrue(seeMoreDeeplink.isEmpty())
            }
        }

    @Test
    fun execute_displayNameIsNotEmpty_responseDataIsAdsBanner_returnMusicAdsBannerShelfItem() =
        runTest {
            // Given
            val tagNameResponse = TagNameResponse().apply {
                displayName = listOf(Translation(language = "en", value = "value"))
            }

            val dataResponse = Data().apply {
                setting = Setting().apply {
                    contentType = GetMusicUserByTagShelfUseCaseImpl.CONTENT_TYPE_MISC
                    type = GetMusicUserByTagShelfUseCaseImpl.TYPE_BY_SHELF_ID
                    viewType = GetMusicUserByTagShelfUseCaseImpl.VIEW_TYPE_ADS_SHELF
                    adsId = "adsId"
                    mobileSize = "mobileSize"
                    tabletSize = "tabletSize"
                }
            }

            whenever(localizationRepository.getAppCountryCode()).thenReturn("th")
            whenever(localizationRepository.getAppLanguageCode()).thenReturn("th")
            whenever(musicLandingRepository.getTagByName(any())).thenReturn(flowOf(tagNameResponse))
            whenever(
                cmsShelvesRepository.getCmsPublicContentShelfListData(
                    any(),
                    any(),
                    any(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull()
                )
            ).thenReturn(flowOf(dataResponse))

            // When
            val flow = getMusicUserByTagShelfUseCase.execute("tag")

            // Then
            flow.collect { (itemList, productType, seeMoreDeeplink) ->
                assertTrue(itemList.first() is MusicForYouItemModel.AdsBannerShelfItem)

                val adsBannerItem = itemList.first() as MusicForYouItemModel.AdsBannerShelfItem
                assertEquals(dataResponse.setting?.adsId, adsBannerItem.adsId)
                assertEquals(dataResponse.setting?.mobileSize, adsBannerItem.mobileSize)
                assertEquals(dataResponse.setting?.tabletSize, adsBannerItem.tabletSize)
                assertEquals(productType, ProductListType.TAGGED_ADS)
                assertTrue(seeMoreDeeplink.isEmpty())
            }
        }

    @Test
    fun execute_displayNameIsNotEmpty_responseDataIsAdsBanner_adsIdIsEmpty_returnEmptyList() =
        runTest {
            // Given
            val tagNameResponse = TagNameResponse().apply {
                displayName = listOf(Translation(language = "en", value = "value"))
            }

            val dataResponse = Data().apply {
                setting = Setting().apply {
                    contentType = GetMusicUserByTagShelfUseCaseImpl.CONTENT_TYPE_MISC
                    type = GetMusicUserByTagShelfUseCaseImpl.TYPE_BY_SHELF_ID
                    viewType = GetMusicUserByTagShelfUseCaseImpl.VIEW_TYPE_ADS_SHELF
                    adsId = ""
                    mobileSize = "mobileSize"
                    tabletSize = "tabletSize"
                }
            }

            whenever(localizationRepository.getAppCountryCode()).thenReturn("th")
            whenever(localizationRepository.getAppLanguageCode()).thenReturn("th")
            whenever(musicLandingRepository.getTagByName(any())).thenReturn(flowOf(tagNameResponse))
            whenever(
                cmsShelvesRepository.getCmsPublicContentShelfListData(
                    any(),
                    any(),
                    any(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull()
                )
            ).thenReturn(flowOf(dataResponse))

            // When
            val flow = getMusicUserByTagShelfUseCase.execute("tag")

            // Then
            flow.collect { (itemList, productType, seeMoreDeeplink) ->
                assertTrue(itemList.isEmpty())
                assertEquals(productType, ProductListType.TAGGED_ADS)
                assertTrue(seeMoreDeeplink.isEmpty())
            }
        }

    @Test
    fun execute_displayNameIsNotEmpty_responseDataIsAdsBanner_adsIdIsNull_returnEmptyList() =
        runTest {
            // Given
            val tagNameResponse = TagNameResponse().apply {
                displayName = listOf(Translation(language = "en", value = "value"))
            }

            val dataResponse = Data().apply {
                setting = Setting().apply {
                    contentType = GetMusicUserByTagShelfUseCaseImpl.CONTENT_TYPE_MISC
                    type = GetMusicUserByTagShelfUseCaseImpl.TYPE_BY_SHELF_ID
                    viewType = GetMusicUserByTagShelfUseCaseImpl.VIEW_TYPE_ADS_SHELF
                    adsId = null
                    mobileSize = "mobileSize"
                    tabletSize = "tabletSize"
                }
            }

            whenever(localizationRepository.getAppCountryCode()).thenReturn("th")
            whenever(localizationRepository.getAppLanguageCode()).thenReturn("th")
            whenever(musicLandingRepository.getTagByName(any())).thenReturn(flowOf(tagNameResponse))
            whenever(
                cmsShelvesRepository.getCmsPublicContentShelfListData(
                    any(),
                    any(),
                    any(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull()
                )
            ).thenReturn(flowOf(dataResponse))

            // When
            val flow = getMusicUserByTagShelfUseCase.execute("tag")

            // Then
            flow.collect { (itemList, productType, seeMoreDeeplink) ->
                assertTrue(itemList.isEmpty())
                assertEquals(productType, ProductListType.TAGGED_ADS)
                assertTrue(seeMoreDeeplink.isEmpty())
            }
        }

    @Test
    fun execute_displayNameIsNotEmpty_responseDataIsAdsBanner_mobileAndTabletSizeIsEmpty_returnEmptyList() =
        runTest {
            // Given
            val tagNameResponse = TagNameResponse().apply {
                displayName = listOf(Translation(language = "en", value = "value"))
            }

            val dataResponse = Data().apply {
                setting = Setting().apply {
                    contentType = GetMusicUserByTagShelfUseCaseImpl.CONTENT_TYPE_MISC
                    type = GetMusicUserByTagShelfUseCaseImpl.TYPE_BY_SHELF_ID
                    viewType = GetMusicUserByTagShelfUseCaseImpl.VIEW_TYPE_ADS_SHELF
                    adsId = "adsId"
                    mobileSize = ""
                    tabletSize = ""
                }
            }

            whenever(localizationRepository.getAppCountryCode()).thenReturn("th")
            whenever(localizationRepository.getAppLanguageCode()).thenReturn("th")
            whenever(musicLandingRepository.getTagByName(any())).thenReturn(flowOf(tagNameResponse))
            whenever(
                cmsShelvesRepository.getCmsPublicContentShelfListData(
                    any(),
                    any(),
                    any(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull()
                )
            ).thenReturn(flowOf(dataResponse))

            // When
            val flow = getMusicUserByTagShelfUseCase.execute("tag")

            // Then
            flow.collect { (itemList, productType, seeMoreDeeplink) ->
                assertTrue(itemList.isEmpty())
                assertEquals(productType, ProductListType.TAGGED_ADS)
                assertTrue(seeMoreDeeplink.isEmpty())
            }
        }

    @Test
    fun execute_displayNameIsNotEmpty_responseDataIsAdsBanner_mobileAndTabletSizeIsNull_returnEmptyList() =
        runTest {
            // Given
            val tagNameResponse = TagNameResponse().apply {
                displayName = listOf(Translation(language = "en", value = "value"))
            }

            val dataResponse = Data().apply {
                setting = Setting().apply {
                    contentType = GetMusicUserByTagShelfUseCaseImpl.CONTENT_TYPE_MISC
                    type = GetMusicUserByTagShelfUseCaseImpl.TYPE_BY_SHELF_ID
                    viewType = GetMusicUserByTagShelfUseCaseImpl.VIEW_TYPE_ADS_SHELF
                    adsId = "adsId"
                    mobileSize = null
                    tabletSize = null
                }
            }

            whenever(localizationRepository.getAppCountryCode()).thenReturn("th")
            whenever(localizationRepository.getAppLanguageCode()).thenReturn("th")
            whenever(musicLandingRepository.getTagByName(any())).thenReturn(flowOf(tagNameResponse))
            whenever(
                cmsShelvesRepository.getCmsPublicContentShelfListData(
                    any(), any(), any(), anyOrNull(), anyOrNull(), anyOrNull()
                )
            ).thenReturn(flowOf(dataResponse))

            // When
            val flow = getMusicUserByTagShelfUseCase.execute("tag")

            // Then
            flow.collect { (itemList, productType, seeMoreDeeplink) ->
                assertTrue(itemList.isEmpty())
                assertEquals(productType, ProductListType.TAGGED_ADS)
                assertTrue(seeMoreDeeplink.isEmpty())
            }
        }

    @Test
    fun execute_responseDataIsRadio_seeMoreIsNotNull_returnRadioShelfItem() = runTest {
        // Given
        val tagNameResponse = TagNameResponse().apply {
            displayName = listOf(Translation(language = "en", value = "value"))
        }
        val mockSeeMore = "seeMore"

        val dataResponse = Data().apply {
            shelfList = listOf(
                Shelf().apply {
                    id = "lpjjLaGw8Exp"
                    setting = Setting().apply {
                        descriptionEn = "descriptionEn"
                        descriptionTh = "descriptionTh"
                        titleTh = "titleTh"
                        titleEn = "titleEn"
                        thumbnail = "thumbnail"
                    }
                    contentType = "contentType"
                }
            )
            setting = Setting().apply {
                viewType = GetMusicUserByTagShelfUseCaseImpl.VIEW_TYPE_RADIO
                type = GetMusicUserByTagShelfUseCaseImpl.TYPE_BY_SHELF_ID
                seemore = mockSeeMore
            }
        }

        whenever(localizationRepository.getAppCountryCode()).thenReturn("th")
        whenever(
            mapRadioUseCase.execute(any(), any(), any(), anyOrNull(), anyOrNull(), anyOrNull())
        ).thenReturn(MockDataModel.mockRadioShelf)
        whenever(localizationRepository.getAppLanguageCode()).thenReturn("th")
        whenever(musicLandingRepository.getTagByName(any())).thenReturn(flowOf(tagNameResponse))
        whenever(
            cmsShelvesRepository.getCmsPublicContentShelfListData(
                any(), any(), any(), anyOrNull(), anyOrNull(), anyOrNull()
            )
        ).thenReturn(flowOf(dataResponse))

        // When
        val flow = getMusicUserByTagShelfUseCase.execute("tag")

        // Then
        flow.collect { (itemList, productType, seeMoreDeeplink) ->
            assertTrue(itemList.first() is MusicForYouItemModel.RadioShelfItem)
            val radioItem = itemList.first() as MusicForYouItemModel.RadioShelfItem
            assertEquals(MockDataModel.mockRadioShelf, radioItem)
            assertEquals(productType, ProductListType.TAGGED_RADIO)
            assertEquals(mockSeeMore, seeMoreDeeplink)
        }
    }

    @Test
    fun execute_responseDataIsRadio_seeMoreIsNull_returnRadioShelfItem() = runTest {
        // Given
        val tagNameResponse = TagNameResponse().apply {
            displayName = listOf(Translation(language = "en", value = "value"))
        }

        val dataResponse = Data().apply {
            shelfList = listOf(
                Shelf().apply {
                    id = "lpjjLaGw8Exp"
                    setting = Setting().apply {
                        descriptionEn = "descriptionEn"
                        descriptionTh = "descriptionTh"
                        titleTh = "titleTh"
                        titleEn = "titleEn"
                        thumbnail = "thumbnail"
                    }
                    contentType = "contentType"
                }
            )
            setting = Setting().apply {
                viewType = GetMusicUserByTagShelfUseCaseImpl.VIEW_TYPE_RADIO
                type = GetMusicUserByTagShelfUseCaseImpl.TYPE_BY_SHELF_ID
                seemore = null
            }
        }

        whenever(localizationRepository.getAppCountryCode()).thenReturn("th")
        whenever(
            mapRadioUseCase.execute(any(), any(), any(), anyOrNull(), anyOrNull(), anyOrNull())
        ).thenReturn(MockDataModel.mockRadioShelf)
        whenever(localizationRepository.getAppLanguageCode()).thenReturn("th")
        whenever(musicLandingRepository.getTagByName(any())).thenReturn(flowOf(tagNameResponse))
        whenever(
            cmsShelvesRepository.getCmsPublicContentShelfListData(
                any(),
                any(),
                any(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(flowOf(dataResponse))

        // When
        val flow = getMusicUserByTagShelfUseCase.execute("tag")

        // Then
        flow.collect { (itemList, productType, seeMoreDeeplink) ->
            assertTrue(itemList.first() is MusicForYouItemModel.RadioShelfItem)
            val radioItem = itemList.first() as MusicForYouItemModel.RadioShelfItem
            assertEquals(MockDataModel.mockRadioShelf, radioItem)
            assertEquals(productType, ProductListType.TAGGED_RADIO)
            assertTrue(seeMoreDeeplink.isEmpty())
        }
    }

    @Test
    fun execute_responseDataIsRadio_shelfListIsNull_returnEmptyList() = runTest {
        // Given
        val tagNameResponse = TagNameResponse().apply {
            displayName = listOf(Translation(language = "en", value = "value"))
        }

        val dataResponse = Data().apply {
            shelfList = null
            setting = Setting().apply {
                viewType = GetMusicUserByTagShelfUseCaseImpl.VIEW_TYPE_RADIO
                type = GetMusicUserByTagShelfUseCaseImpl.TYPE_BY_SHELF_ID
            }
        }

        whenever(localizationRepository.getAppCountryCode()).thenReturn("th")
        whenever(localizationRepository.getAppLanguageCode()).thenReturn("th")
        whenever(musicLandingRepository.getTagByName(any())).thenReturn(flowOf(tagNameResponse))
        whenever(
            cmsShelvesRepository.getCmsPublicContentShelfListData(
                any(),
                any(),
                any(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(flowOf(dataResponse))

        // When
        val flow = getMusicUserByTagShelfUseCase.execute("tag")

        // Then
        flow.collect { (itemList, productType, seeMoreDeeplink) ->
            assertTrue(itemList.isEmpty())
            assertEquals(productType, ProductListType.TAGGED_RADIO)
            assertTrue(seeMoreDeeplink.isEmpty())
        }
    }
}
