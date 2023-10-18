package com.truedigital.features.music.presentation.landing

import com.truedigital.common.share.analytics.measurement.AnalyticManager
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant
import com.truedigital.features.music.constant.MusicShelfType
import com.truedigital.features.music.domain.landing.model.FAMusicLandingShelfModel
import com.truedigital.features.music.domain.landing.model.MusicForYouItemModel
import com.truedigital.features.music.domain.landing.model.MusicForYouShelfModel
import com.truedigital.features.music.domain.landing.model.MusicHeroBannerDeeplinkType
import com.truedigital.features.music.domain.landing.model.MusicLandingFASelectContentModel
import com.truedigital.features.music.domain.landing.usecase.GetDataForTrackFAMusicLandingPageUseCase
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class MusicLandingTrackFAViewModelTest {

    @MockK
    lateinit var getDataForTrackFAMusicLandingPageUseCase: GetDataForTrackFAMusicLandingPageUseCase
    private lateinit var musicLandingTrackFAViewModel: MusicLandingTrackFAViewModel
    private val analyticManager: AnalyticManager = mockk()

    private val mockMusicLandingFASelectContentModel = MusicLandingFASelectContentModel(
        itemId = "1",
        title = "title",
        contentType = "contentType",
        shelfName = "shelfName",
        itemIndex = 1,
        shelfIndex = 8
    )
    private val mockBaseShelfId = "baseShelfId"

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { analyticManager.trackEvent(any()) } just runs
        musicLandingTrackFAViewModel = MusicLandingTrackFAViewModel(
            getDataForTrackFAMusicLandingPageUseCase,
            analyticManager
        )
    }

    @Test
    fun onScrollShelves_faDataIsNotNull_trackFAShelf() {
        // Given
        val mockFAMusicLandingShelfModel = FAMusicLandingShelfModel(
            shelfIndex = 1,
            shelfName = "shelfName"
        )
        every {
            getDataForTrackFAMusicLandingPageUseCase.execute(
                any(), any(), any()
            )
        } returns mockFAMusicLandingShelfModel

        // When
        musicLandingTrackFAViewModel.onScrollShelves(mockBaseShelfId, MusicForYouShelfModel())

        // Then
        verify(exactly = 1) {
            getDataForTrackFAMusicLandingPageUseCase.execute(
                any(), any(), any()
            )
        }
        verify(exactly = 1) { analyticManager.trackEvent((any())) }
    }

    @Test
    fun onScrollShelves_faDataIsNull_doNotTrackFAShelf() {
        // Given
        every {
            getDataForTrackFAMusicLandingPageUseCase.execute(
                any(), any(), any()
            )
        } returns null

        // When
        musicLandingTrackFAViewModel.onScrollShelves(mockBaseShelfId, MusicForYouShelfModel())

        // Then
        verify(exactly = 1) {
            getDataForTrackFAMusicLandingPageUseCase.execute(
                any(),
                any(),
                any()
            )
        }
        verify(exactly = 0) { analyticManager.trackEvent((any())) }
    }

    @Test
    fun trackFASelectContent_album_verifyTracking() {
        musicLandingTrackFAViewModel.trackFASelectContent(
            MusicForYouItemModel.AlbumShelfItem(
                albumId = 1,
                releaseId = 2,
                coverImage = null,
                albumName = "albumName",
                artistName = null
            ),
            mockMusicLandingFASelectContentModel
        )

        verify {
            analyticManager.trackEvent(
                hashMapOf(
                    MeasurementConstant.Key.KEY_EVENT_NAME to MeasurementConstant.Event.EVENT_SELECT_CONTENT,
                    MeasurementConstant.Key.KEY_CMS_ID to "2",
                    MeasurementConstant.Key.KEY_TITLE to "albumName",
                    MeasurementConstant.Key.KEY_CONTENT_TYPE to com.truedigital.features.listens.share.constant.MusicConstant.Type.ALBUM,
                    MeasurementConstant.Key.KEY_SHELF_NAME to "shelfName",
                    MeasurementConstant.Key.KEY_ITEM_INDEX to 1,
                    MeasurementConstant.Key.KEY_SHELF_INDEX to 8
                )
            )
        }
    }

    @Test
    fun trackFASelectContent_album_albumNameNull_verifyTracking() {
        musicLandingTrackFAViewModel.trackFASelectContent(
            MusicForYouItemModel.AlbumShelfItem(
                albumId = 1,
                releaseId = 2,
                coverImage = null,
                albumName = null,
                artistName = null
            ),
            mockMusicLandingFASelectContentModel
        )

        verify {
            analyticManager.trackEvent(
                hashMapOf(
                    MeasurementConstant.Key.KEY_EVENT_NAME to MeasurementConstant.Event.EVENT_SELECT_CONTENT,
                    MeasurementConstant.Key.KEY_CMS_ID to "2",
                    MeasurementConstant.Key.KEY_TITLE to "",
                    MeasurementConstant.Key.KEY_CONTENT_TYPE to com.truedigital.features.listens.share.constant.MusicConstant.Type.ALBUM,
                    MeasurementConstant.Key.KEY_SHELF_NAME to "shelfName",
                    MeasurementConstant.Key.KEY_ITEM_INDEX to 1,
                    MeasurementConstant.Key.KEY_SHELF_INDEX to 8
                )
            )
        }
    }

    @Test
    fun trackFASelectContent_artist_verifyTracking() {
        musicLandingTrackFAViewModel.trackFASelectContent(
            MusicForYouItemModel.ArtistShelfItem(
                artistId = 2,
                coverImage = null,
                name = "artistName"
            ),
            mockMusicLandingFASelectContentModel
        )

        verify {
            analyticManager.trackEvent(
                hashMapOf(
                    MeasurementConstant.Key.KEY_EVENT_NAME to MeasurementConstant.Event.EVENT_SELECT_CONTENT,
                    MeasurementConstant.Key.KEY_CMS_ID to "2",
                    MeasurementConstant.Key.KEY_TITLE to "artistName",
                    MeasurementConstant.Key.KEY_CONTENT_TYPE to com.truedigital.features.listens.share.constant.MusicConstant.Type.ARTIST,
                    MeasurementConstant.Key.KEY_SHELF_NAME to "shelfName",
                    MeasurementConstant.Key.KEY_ITEM_INDEX to 1,
                    MeasurementConstant.Key.KEY_SHELF_INDEX to 8
                )
            )
        }
    }

    @Test
    fun trackFASelectContent_artist_artistNameNull_verifyTracking() {
        musicLandingTrackFAViewModel.trackFASelectContent(
            MusicForYouItemModel.ArtistShelfItem(
                artistId = 2,
                coverImage = null,
                name = null
            ),
            mockMusicLandingFASelectContentModel
        )

        verify {
            analyticManager.trackEvent(
                hashMapOf(
                    MeasurementConstant.Key.KEY_EVENT_NAME to MeasurementConstant.Event.EVENT_SELECT_CONTENT,
                    MeasurementConstant.Key.KEY_CMS_ID to "2",
                    MeasurementConstant.Key.KEY_TITLE to "",
                    MeasurementConstant.Key.KEY_CONTENT_TYPE to com.truedigital.features.listens.share.constant.MusicConstant.Type.ARTIST,
                    MeasurementConstant.Key.KEY_SHELF_NAME to "shelfName",
                    MeasurementConstant.Key.KEY_ITEM_INDEX to 1,
                    MeasurementConstant.Key.KEY_SHELF_INDEX to 8
                )
            )
        }
    }

    @Test
    fun trackFASelectContent_playlist_verifyTracking() {
        musicLandingTrackFAViewModel.trackFASelectContent(
            MusicForYouItemModel.PlaylistShelfItem(
                playlistId = 2,
                coverImage = null,
                name = "",
                nameEn = "playlistName"
            ),
            mockMusicLandingFASelectContentModel
        )

        verify {
            analyticManager.trackEvent(
                hashMapOf(
                    MeasurementConstant.Key.KEY_EVENT_NAME to MeasurementConstant.Event.EVENT_SELECT_CONTENT,
                    MeasurementConstant.Key.KEY_CMS_ID to "2",
                    MeasurementConstant.Key.KEY_TITLE to "playlistName",
                    MeasurementConstant.Key.KEY_CONTENT_TYPE to com.truedigital.features.listens.share.constant.MusicConstant.Type.PLAYLIST,
                    MeasurementConstant.Key.KEY_SHELF_NAME to "shelfName",
                    MeasurementConstant.Key.KEY_ITEM_INDEX to 1,
                    MeasurementConstant.Key.KEY_SHELF_INDEX to 8
                )
            )
        }
    }

    @Test
    fun trackFASelectContent_playlist_playlistNameEn_verifyTracking() {
        musicLandingTrackFAViewModel.trackFASelectContent(
            MusicForYouItemModel.PlaylistShelfItem(
                playlistId = 2,
                coverImage = null,
                name = null,
                nameEn = null
            ),
            mockMusicLandingFASelectContentModel
        )

        verify {
            analyticManager.trackEvent(
                hashMapOf(
                    MeasurementConstant.Key.KEY_EVENT_NAME to MeasurementConstant.Event.EVENT_SELECT_CONTENT,
                    MeasurementConstant.Key.KEY_CMS_ID to "2",
                    MeasurementConstant.Key.KEY_TITLE to "",
                    MeasurementConstant.Key.KEY_CONTENT_TYPE to com.truedigital.features.listens.share.constant.MusicConstant.Type.PLAYLIST,
                    MeasurementConstant.Key.KEY_SHELF_NAME to "shelfName",
                    MeasurementConstant.Key.KEY_ITEM_INDEX to 1,
                    MeasurementConstant.Key.KEY_SHELF_INDEX to 8
                )
            )
        }
    }

    @Test
    fun trackFASelectContent_song_verifyTracking() {
        musicLandingTrackFAViewModel.trackFASelectContent(
            MusicForYouItemModel.TrackPlaylistShelf(
                playlistTrackId = 1,
                trackId = 2,
                coverImage = null,
                name = "songName",
                artist = null,
                position = null
            ),
            mockMusicLandingFASelectContentModel
        )

        verify {
            analyticManager.trackEvent(
                hashMapOf(
                    MeasurementConstant.Key.KEY_EVENT_NAME to MeasurementConstant.Event.EVENT_SELECT_CONTENT,
                    MeasurementConstant.Key.KEY_CMS_ID to "2",
                    MeasurementConstant.Key.KEY_TITLE to "songName",
                    MeasurementConstant.Key.KEY_CONTENT_TYPE to com.truedigital.features.listens.share.constant.MusicConstant.Type.MUSIC,
                    MeasurementConstant.Key.KEY_SHELF_NAME to "shelfName",
                    MeasurementConstant.Key.KEY_ITEM_INDEX to 1,
                    MeasurementConstant.Key.KEY_SHELF_INDEX to 8
                )
            )
        }
    }

    @Test
    fun trackFASelectContent_song_songNameNull_verifyTracking() {
        musicLandingTrackFAViewModel.trackFASelectContent(
            MusicForYouItemModel.TrackPlaylistShelf(
                playlistTrackId = 1,
                trackId = 2,
                coverImage = null,
                name = null,
                artist = null,
                position = null
            ),
            mockMusicLandingFASelectContentModel
        )

        verify {
            analyticManager.trackEvent(
                hashMapOf(
                    MeasurementConstant.Key.KEY_EVENT_NAME to MeasurementConstant.Event.EVENT_SELECT_CONTENT,
                    MeasurementConstant.Key.KEY_CMS_ID to "2",
                    MeasurementConstant.Key.KEY_TITLE to "",
                    MeasurementConstant.Key.KEY_CONTENT_TYPE to com.truedigital.features.listens.share.constant.MusicConstant.Type.MUSIC,
                    MeasurementConstant.Key.KEY_SHELF_NAME to "shelfName",
                    MeasurementConstant.Key.KEY_ITEM_INDEX to 1,
                    MeasurementConstant.Key.KEY_SHELF_INDEX to 8
                )
            )
        }
    }

    @Test
    fun trackFASelectContent_heroBanner_verifyTracking() {
        musicLandingTrackFAViewModel.trackFASelectContent(
            MusicForYouItemModel.MusicHeroBannerShelfItem(
                index = 1,
                heroBannerId = "2",
                coverImage = null,
                title = "heroBanner",
                deeplinkPair = Pair(MusicHeroBannerDeeplinkType.ALBUM, ""),
                contentType = "type"
            ),
            mockMusicLandingFASelectContentModel
        )

        verify {
            analyticManager.trackEvent(
                hashMapOf(
                    MeasurementConstant.Key.KEY_EVENT_NAME to MeasurementConstant.Event.EVENT_SELECT_CONTENT,
                    MeasurementConstant.Key.KEY_CMS_ID to "2",
                    MeasurementConstant.Key.KEY_TITLE to "heroBanner",
                    MeasurementConstant.Key.KEY_CONTENT_TYPE to "type",
                    MeasurementConstant.Key.KEY_SHELF_NAME to "shelfName",
                    MeasurementConstant.Key.KEY_ITEM_INDEX to 1,
                    MeasurementConstant.Key.KEY_SHELF_INDEX to 8
                )
            )
        }
    }

    @Test
    fun trackFASelectContent_radioShelf_verifyTracking() {
        musicLandingTrackFAViewModel.trackFASelectContent(
            MusicForYouItemModel.RadioShelfItem(
                mediaAssetId = 2,
                index = 1,
                radioId = "001",
                contentType = "radio",
                description = "description",
                thumbnail = "thumbnail",
                titleEn = "radioStation",
                titleTh = "titleTh",
                title = "title",
                viewType = "viewType",
                shelfType = MusicShelfType.VERTICAL,
                streamUrl = "streamUrl",
            ),
            mockMusicLandingFASelectContentModel
        )

        verify {
            analyticManager.trackEvent(
                hashMapOf(
                    MeasurementConstant.Key.KEY_EVENT_NAME to MeasurementConstant.Event.EVENT_SELECT_CONTENT,
                    MeasurementConstant.Key.KEY_CMS_ID to "001",
                    MeasurementConstant.Key.KEY_TITLE to "radioStation",
                    MeasurementConstant.Key.KEY_CONTENT_TYPE to "radio",
                    MeasurementConstant.Key.KEY_SHELF_NAME to "shelfName",
                    MeasurementConstant.Key.KEY_ITEM_INDEX to 1,
                    MeasurementConstant.Key.KEY_SHELF_INDEX to 8
                )
            )
        }
    }
}
