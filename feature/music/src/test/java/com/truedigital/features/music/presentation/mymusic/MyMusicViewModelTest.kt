package com.truedigital.features.music.presentation.mymusic

import com.truedigital.common.share.analytics.measurement.AnalyticManager
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant
import com.truedigital.features.music.domain.myplaylist.model.MusicMyPlaylistModel
import com.truedigital.features.music.domain.myplaylist.usecase.GetMyPlaylistShelfUseCase
import com.truedigital.features.music.presentation.mylibrary.mymusic.MyMusicViewModel
import com.truedigital.features.utils.MockDataModel
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.livedata.getOrAwaitValue
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExtendWith(InstantTaskExecutorExtension::class)
class MyMusicViewModelTest {

    @MockK
    private lateinit var getMyPlaylistShelfUseCase: GetMyPlaylistShelfUseCase

    @MockK
    private lateinit var analyticManager: AnalyticManager

    private lateinit var myMusicViewModel: MyMusicViewModel
    private val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()
    private val testScope = TestScope(dispatcher)

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        myMusicViewModel = MyMusicViewModel(
            getMyPlaylistShelfUseCase,
            analyticManager
        )
        Dispatchers.setMain(dispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testFetchMyPlaylist_whenFetch_thenGetList() = testScope.runTest {
        // Given
        every { getMyPlaylistShelfUseCase.execute() } returns flowOf(MockDataModel.mockMyPlaylist)

        // When
        myMusicViewModel.fetchMyPlaylist()

        // Then
        assertTrue(myMusicViewModel.onGetMyPlaylist().getOrAwaitValue().isNotEmpty())
        assertFalse(myMusicViewModel.onPullRefresh().getOrAwaitValue())
    }

    @Test
    fun testTrackFASelectContent_isCreateMyPlaylistModel_trackFAWithoutIndex() {
        // Given
        val mockResult = hashMapOf<String, Any>(
            MeasurementConstant.Key.KEY_EVENT_NAME to MeasurementConstant.Event.EVENT_SELECT_CONTENT,
            MeasurementConstant.Music.Key.MEASUREMENT_MUSIC_MY_PLAYLIST to com.truedigital.features.listens.share.constant.MusicConstant.MyPlaylist.MY_PLAYLIST,
            MeasurementConstant.Key.KEY_TITLE to com.truedigital.features.listens.share.constant.MusicConstant.MyPlaylist.CREATE_A_NEW_LIST
        )

        every { analyticManager.trackEvent(mockResult) } just runs

        // When
        myMusicViewModel.trackFASelectContent(MusicMyPlaylistModel.CreateMyPlaylistModel())

        // Then
        verify { analyticManager.trackEvent(mockResult) }
    }

    @Test
    fun testTrackFASelectContent_isMyPlaylistModel_verifyTrackEvent() {
        // Given

        every { analyticManager.trackEvent(any()) } just runs

        // When
        myMusicViewModel.trackFASelectContent(MusicMyPlaylistModel.MyPlaylistModel(index = 2))

        // Then
        verify { analyticManager.trackEvent(any()) }
    }
}
