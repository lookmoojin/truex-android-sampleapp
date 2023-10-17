package com.truedigital.features.music.data.track.repository

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.music.api.MusicTrackApiInterface
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.util.PagedResults
import com.truedigital.features.utils.MockDataModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MusicTrackRepositoryTest {

    private lateinit var musicTrackRepository: MusicTrackRepository
    private val musicTrackApi: MusicTrackApiInterface = mock()

    private val bodyResponse =
        "{\"code\":400,\"status\":400,\"error\":null,\"message\":\"unexpected error\"}"
            .toResponseBody("application/json".toMediaTypeOrNull())
    private val rawResponse = okhttp3.Response.Builder()
        .code(400)
        .message("unexpected error")
        .request(Request.Builder().url("http://example.com").build())
        .protocol(Protocol.HTTP_1_0)
        .build()

    @BeforeEach
    fun setUp() {
        musicTrackRepository = MusicTrackRepositoryImpl(musicTrackApi)
    }

    @Test
    fun getTrackList_success_returnData() = runTest {
        val pageResultsMock = PagedResults(
            1,
            10,
            100,
            listOf(MockDataModel.mockTrack)
        )
        whenever(musicTrackApi.getTrackList(any(), any(), any())).thenReturn(
            Response.success(pageResultsMock)
        )

        val flow = musicTrackRepository.getTrackList(1, 1, 10)

        flow.collect {
            Assertions.assertEquals(it, pageResultsMock.results)
        }
    }

    @Test
    fun getTrackList_resultEmpty_returnError() = runTest {
        val pageResultsMock = PagedResults<Track>(
            1,
            10,
            100,
            emptyList()
        )
        whenever(musicTrackApi.getTrackList(any(), any(), any())).thenReturn(
            Response.success(pageResultsMock)
        )

        val flow = musicTrackRepository.getTrackList(1, 1, 10)

        flow.catch {
            Assertions.assertEquals(
                it.localizedMessage,
                MusicTrackRepositoryImpl.ERROR_LOAD_TRACK_LIST
            )
        }.collect()
    }

    @Test
    fun getTrackList_bodyNull_returnError() = runTest {
        whenever(musicTrackApi.getTrackList(any(), any(), any())).thenReturn(
            Response.success(null)
        )

        val flow = musicTrackRepository.getTrackList(1, 1, 10)

        flow.catch {
            Assertions.assertEquals(
                it.localizedMessage,
                MusicTrackRepositoryImpl.ERROR_LOAD_TRACK_LIST
            )
        }.collect()
    }

    @Test
    fun getTrackList_fail_returnError() = runTest {
        whenever(musicTrackApi.getTrackList(any(), any(), any())).thenReturn(
            Response.error(
                bodyResponse,
                rawResponse
            )
        )

        val flow = musicTrackRepository.getTrackList(1, 1, 10)

        flow.catch {
            Assertions.assertEquals(
                it.localizedMessage,
                MusicTrackRepositoryImpl.ERROR_LOAD_TRACK_LIST
            )
        }.collect()
    }

    @Test
    fun getMyPlaylistTrackList_success_returnData() = runTest {
        val pageResultsMock = PagedResults(
            1,
            10,
            100,
            listOf(MockDataModel.mockTrack)
        )
        whenever(musicTrackApi.getTrackList(any(), any(), any())).thenReturn(
            Response.success(pageResultsMock)
        )

        val result = musicTrackRepository.getMyPlaylistTrackList(1, 1, 10)
        result.collect {
            val trackResponse = it.first()
            assertEquals(trackResponse, pageResultsMock.results.first())
        }
    }

    @Test
    fun getMyPlaylistTrackList_fail_returnEmptyList() = runTest {
        whenever(musicTrackApi.getTrackList(any(), any(), any())).thenReturn(
            Response.error(
                bodyResponse,
                rawResponse
            )
        )

        val result = musicTrackRepository.getMyPlaylistTrackList(1, 1, 10)
        result.collect {
            assertTrue(it.isEmpty())
        }
    }
}
