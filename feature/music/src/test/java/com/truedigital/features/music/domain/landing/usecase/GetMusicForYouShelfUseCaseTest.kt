package com.truedigital.features.music.domain.landing.usecase

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.features.music.data.landing.repository.MusicLandingRepository
import com.truedigital.features.music.data.queue.repository.CacheTrackQueueRepository
import com.truedigital.features.tuned.data.productlist.model.ProductListType
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GetMusicForYouShelfUseCaseTest {

    private lateinit var getMusicForYouShelfUseCase: GetMusicForYouShelfUseCase
    private val musicLandingRepository: MusicLandingRepository = mock()
    private val mapProductListTypeUseCase: MapProductListTypeUseCase = mock()
    private val cacheTrackQueueRepository: CacheTrackQueueRepository = mock()
    private val localizationRepository: LocalizationRepository = mock()

    @BeforeEach
    fun setup() {
        getMusicForYouShelfUseCase = GetMusicForYouShelfUseCaseImpl(
            musicLandingRepository,
            mapProductListTypeUseCase,
            cacheTrackQueueRepository,
            localizationRepository
        )
    }

    @Test
    fun testGetMusicForYouShelf_apiPathIsEmpty_returnError() = runTest {
        getMusicForYouShelfUseCase.execute("")
            .catch { exception ->
                assertEquals(
                    GetMusicForYouShelfUseCaseImpl.ERROR_API_PATH_EMPTY,
                    exception.message
                )
            }.collect()
    }

    @Test
    fun testGetMusicForYouShelf_mapData_returnListOfMusicForYouShelfModel() = runTest {
        whenever(musicLandingRepository.getMusicForYouShelf(any())).thenReturn(flowOf(gsonString()))
        whenever(mapProductListTypeUseCase.execute(any())).thenReturn(ProductListType.TRACKS_PLAYLIST)
        whenever(localizationRepository.getAppLanguageCode()).thenReturn(LocalizationRepository.Localization.EN.languageCode)
        getMusicForYouShelfUseCase.execute("apiPath")
            .collect { list ->
                assertEquals(13, list.size)

                val firstItem = list.firstOrNull()
                assertEquals(1, firstItem?.index)
                assertEquals("herobanner", firstItem?.titleFA)
                assertEquals("herobanner", firstItem?.title)

                val secondItem = list[1]
                assertEquals(3, secondItem.index)
                assertEquals("Topchart", secondItem.titleFA)
                assertEquals("Topchart", secondItem.title)
                assertNotNull(secondItem.options)
                assertEquals("15", secondItem.options?.limit)
                assertEquals(true, secondItem.options?.displayTitle)
                assertEquals("verticallist", secondItem.options?.displayType)
                assertEquals(false, secondItem.options?.targetTime)

                val thirdItem = list[2]
                assertEquals(6, thirdItem.index)
                assertEquals("Albums Newreleases", thirdItem.titleFA)
                assertEquals("Albums Newreleases", thirdItem.title)
                assertNotNull(thirdItem.options)
                assertEquals("10", thirdItem.options?.limit)
                assertEquals(true, thirdItem.options?.displayTitle)
                assertEquals("shelf", thirdItem.options?.displayType)
                assertEquals(false, thirdItem.options?.targetTime)

                val lastItem = list.last()
                assertEquals(26, lastItem.index)
                assertEquals("Ads pos1", lastItem.titleFA)
                assertEquals("Ads pos1", lastItem.title)
                assertNotNull(lastItem.options)
            }
        verify(cacheTrackQueueRepository, times(1)).clearCacheTrackQueue()
    }

    private fun gsonString() =
        "{\"Title\":[{\"Language\":\"en\",\"Value\":\"\"},{\"Language\":\"th\",\"Value\":\"\"}]," +
            "\"Items\":[" +
            "{\"Type\":\"h1\"," +
            "\"Options\":{\"Route\":\"\",\"BgColour\":\"ffffff\",\"BgAlpha\":\"0\",\"DisplayTitle\":" +
            "\"true\"},\"Text\":[{\"Language\":\"en\",\"Value\":\"herobanner\"},{\"Language\":" +
            "\"th\",\"Value\":\"\"}]},{\"Type\":\"spacing\"," +
            "\"Options\":{\"Spacing\":\"10\",\"BgColour\":\"ffffff\",\"BgAlpha\":\"0\"}}," +
            "{\"Type\":\"users_bytag\"," +
            "\"Options\":{\"Display\":\"shelf\",\"DisplayType\":\"shelf\",\"DisplaySubType\":" +
            "\"standard\",\"ListLimit\":\"10\",\"BgColour\":\"ffffff\",\"BgAlpha\":\"0\",\"Tag\":" +
            "\"herobanner-dev\"}},{\"Type\":\"spacing\"," +
            "\"Options\":{\"Spacing\":\"30\",\"BgColour\":\"ffffff\",\"BgAlpha\":\"0\"}}," +
            "{\"Type\":\"h1\"," +
            "\"Options\":{\"Route\":\"\",\"BgColour\":\"ffffff\",\"BgAlpha\":\"0\",\"DisplayTitle\":" +
            "\"true\"},\"Text\":[{\"Language\":\"en\",\"Value\":\"Topchart\"},{\"Language\":\"th\"," +
            "\"Value\":\"เพลงฮิต \"}]},{\"Type\":\"playlist_tracks\"," +
            "\"Options\":{\"Display\":\"shelf\",\"DisplayType\":\"verticallist\",\"DisplaySubType\":" +
            "\"standard\",\"ListLimit\":\"15\",\"BgColour\":\"ffffff\",\"BgAlpha\":\"0\",\"Format\":" +
            "\"square\",\"PlaylistId\":\"340420\"}},{\"Type\":\"h1\"," +
            "\"Options\":{\"Route\":\"\",\"BgColour\":\"ffffff\",\"BgAlpha\":\"0\"},\"Text\":[{" +
            "\"Language\":\"en\",\"Value\":\"Albums by Tag\"},{\"Language\":\"th\",\"Value\":\"\"}]}," +
            "{\"Type\":\"albums_bytag\"," +
            "\"Options\":{\"Display\":\"shelf\",\"DisplaySubType\":\"standard\",\"BgColour\":" +
            "\"ffffff\",\"BgAlpha\":\"0\",\"TargetTime\":\"true\",\"Tag\":\"thealbums\"}}," +
            "{\"Type\":\"h1\"," +
            "\"Options\":{\"Route\":\"\",\"BgColour\":\"ffffff\",\"BgAlpha\":\"0\",\"DisplayTitle\":" +
            "\"true\"},\"Text\":[{\"Language\":\"en\",\"Value\":\"Albums Newreleases\"}," +
            "{\"Language\":\"th\",\"Value\":\"\"}]},{\"Type\":\"albums_newreleases\"," +
            "\"Options\":{\"Display\":\"shelf\",\"DisplayType\":\"shelf\",\"DisplaySubType\":" +
            "\"standard\",\"ListLimit\":\"10\",\"BgColour\":\"ffffff\",\"BgAlpha\":\"0\"}}," +
            "{\"Type\":\"h1\"," +
            "\"Options\":{\"Route\":\"\",\"BgColour\":\"ffffff\",\"BgAlpha\":\"0\",\"DisplayTitle\":" +
            "\"true\"},\"Text\":[{\"Language\":\"en\",\"Value\":\"Artists by Tag\"},{\"Language\":" +
            "\"th\",\"Value\":\"\"}]},{\"Type\":\"artists_bytag\"," +
            "\"Options\":{\"Display\":\"shelf\",\"DisplayType\":\"shelf\",\"DisplaySubType\":" +
            "\"standard\",\"ListLimit\":\"10\",\"BgColour\":\"ffffff\",\"BgAlpha\":\"0\",\"Tag\":" +
            "\"kpop\"}},{\"Type\":\"h1\"," +
            "\"Options\":{\"Route\":\"\",\"BgColour\":\"ffffff\",\"BgAlpha\":\"0\",\"DisplayTitle\":" +
            "\"true\"},\"Text\":[{\"Language\":\"en\",\"Value\":\"Artists Recommended\"}," +
            "{\"Language\":\"th\",\"Value\":\"\"}]},{\"Type\":\"artists_recommended\"," +
            "\"Options\":{\"Display\":\"shelf\",\"DisplayType\":\"shelf\",\"DisplaySubType\":" +
            "\"standard\",\"ListLimit\":\"10\",\"BgColour\":\"ffffff\",\"BgAlpha\":\"0\"}}," +
            "{\"Type\":\"ad_banner\"," +
            "\"Options\":{\"AdId\":\"123456grgrg\\\\rgggg\",\"AdIdiOS\":\"123456\",\"DisplayAlways\":" +
            "\"true\"}},{\"Type\":\"h1\"," +
            "\"Options\":{\"Route\":\"\",\"BgColour\":\"ffffff\",\"BgAlpha\":\"0\",\"DisplayTitle\":" +
            "\"true\"},\"Text\":[{\"Language\":\"en\",\"Value\":\"Playlists (By Tag) 1\"}," +
            "{\"Language\":\"th\",\"Value\":\"\"}]},{\"Type\":\"playlists_bytag\"," +
            "\"Options\":{\"Display\":\"shelf\",\"DisplayType\":\"shelf\",\"DisplaySubType\":" +
            "\"standard\",\"ListLimit\":\"10\",\"BgColour\":\"ffffff\",\"BgAlpha\":\"0\",\"Tag\":" +
            "\"just-in\"}},{\"Type\":\"h1\"," +
            "\"Options\":{\"Route\":\"\",\"BgColour\":\"ffffff\",\"BgAlpha\":\"0\",\"DisplayTitle\":" +
            "\"true\"},\"Text\":[{\"Language\":\"en\",\"Value\":\"Ads pos3\"},{\"Language\":\"th\"," +
            "\"Value\":\"\"}]},{\"Type\":\"users_bytag\"," +
            "\"Options\":{\"Display\":\"shelf\",\"DisplayType\":\"shelf\",\"DisplaySubType\":" +
            "\"standard\",\"ListLimit\":\"10\",\"BgColour\":\"ffffff\",\"BgAlpha\":\"0\",\"Tag\":" +
            "\"adshome_dev2\"}},{\"Type\":\"h1\"," +
            "\"Options\":{\"Route\":\"\",\"BgColour\":\"ffffff\",\"BgAlpha\":\"0\",\"DisplayTitle\":" +
            "\"true\"},\"Text\":[{\"Language\":\"en\",\"Value\":\"Playlists (By Tag) 2\"}," +
            "{\"Language\":\"th\",\"Value\":\"\"}]},{\"Type\":\"playlists_bytag\"," +
            "\"Options\":{\"Display\":\"shelf\",\"DisplayType\":\"shelf\",\"DisplaySubType\":" +
            "\"standard\",\"ListLimit\":\"10\",\"BgColour\":\"ffffff\",\"BgAlpha\":\"0\",\"Tag\":" +
            "\"mood\"}},{\"Type\":\"h1\"," +
            "\"Options\":{\"Route\":\"\",\"BgColour\":\"ffffff\",\"BgAlpha\":\"0\",\"DisplayTitle\":" +
            "\"true\"},\"Text\":[{\"Language\":\"en\",\"Value\":\"Playlists (By Tag) 3\"}," +
            "{\"Language\":\"th\",\"Value\":\"\"}]},{\"Type\":\"playlists_bytag\"," +
            "\"Options\":{\"Display\":\"shelf\",\"DisplayType\":\"shelf\",\"DisplaySubType\"" +
            ":\"standard\",\"ListLimit\":\"10\",\"BgColour\":\"ffffff\",\"BgAlpha\":\"0\",\"Tag\"" +
            ":\"testwebplaylist\"}},{\"Type\":\"h1\"," +
            "\"Options\":{\"Route\":\"\",\"BgColour\":\"ffffff\",\"BgAlpha\":\"0\",\"DisplayTitle\"" +
            ":\"true\"},\"Text\":[{\"Language\":\"en\",\"Value\":\"Ads pos 2\"},{\"Language\":\"th\"" +
            ",\"Value\":\"\"}]},{\"Type\":\"users_bytag\"," +
            "\"Options\":{\"Display\":\"shelf\",\"DisplayType\":\"shelf\",\"DisplaySubType\"" +
            ":\"standard\",\"ListLimit\":\"10\",\"BgColour\":\"ffffff\",\"BgAlpha\":\"0\",\"Tag\"" +
            ":\"adshome_dev1\"}},{\"Type\":\"h1\"," +
            "\"Options\":{\"Route\":\"\",\"BgColour\":\"ffffff\",\"BgAlpha\":\"0\",\"DisplayTitle\"" +
            ":\"true\"},\"Text\":[{\"Language\":\"en\",\"Value\":\"Playlists (Songs)\"}," +
            "{\"Language\":\"th\",\"Value\":\"\"}]},{\"Type\":\"playlist_tracks\"," +
            "\"Options\":{\"Display\":\"shelf\",\"DisplayType\":\"shelf\",\"DisplaySubType\":" +
            "\"standard\",\"ListLimit\":\"10\",\"BgColour\":\"ffffff\",\"BgAlpha\":\"0\",\"Format\":" +
            "\"square\",\"PlaylistId\":\"338001\"}}," +
            "{\"Type\":\"h1\"," +
            "\"Options\":{\"Route\":\"\",\"BgColour\":\"ffffff\",\"BgAlpha\":\"0\",\"DisplayTitle\":" +
            "\"true\"},\"Text\":[{\"Language\":\"en\",\"Value\":\"Artists by Tag\"},{\"Language\":" +
            "\"th\",\"Value\":\"\"}]},{\"Type\":\"spacing\"," +
            "\"Options\":{\"Spacing\":\"10\",\"BgColour\":\"ffffff\",\"BgAlpha\":\"0\"}}," +
            "{\"Type\":\"artists_bytag\"," +
            "\"Options\":{\"Display\":\"shelf\",\"DisplayType\":\"shelf\",\"DisplaySubType\":" +
            "\"standard\",\"ListLimit\":\"15\",\"BgColour\":\"ffffff\",\"BgAlpha\":\"0\",\"Tag\":" +
            "\"rock\"}},{\"Type\":\"h1\"," +
            "\"Options\":{\"Route\":\"\",\"BgColour\":\"ffffff\",\"BgAlpha\":\"0\",\"DisplayTitle\":" +
            "\"true\"},\"Text\":[{\"Language\":\"en\",\"Value\":\"Ads pos1\"},{\"Language\":\"th\"," +
            "\"Value\":\"\"}]},{\"Type\":\"spacing\"," +
            "\"Options\":{\"Spacing\":\"10\",\"BgColour\":\"ffffff\",\"BgAlpha\":\"0\"}}," +
            "{\"Type\":\"users_bytag\"," +
            "\"Options\":{\"Display\":\"shelf\",\"DisplayType\":\"shelf\",\"DisplaySubType\":" +
            "\"standard\",\"ListLimit\":\"10\",\"BgColour\":\"ffffff\",\"BgAlpha\":\"0\",\"Tag\":" +
            "\"adshome_dev\"}},{\"Type\":\"spacing\"," +
            "\"Options\":{\"Spacing\":\"30\",\"BgColour\":\"ffffff\",\"BgAlpha\":\"0\"}}]}"
}
