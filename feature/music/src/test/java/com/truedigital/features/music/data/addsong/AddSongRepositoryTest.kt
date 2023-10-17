package com.truedigital.features.music.data.addsong

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.music.api.MusicPlaylistApiInterface
import com.truedigital.features.music.data.addsong.repository.AddSongRepository
import com.truedigital.features.music.data.addsong.repository.AddSongRepositoryImpl
import com.truedigital.features.utils.MockDataModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Response
import kotlin.test.assertEquals

class AddSongRepositoryTest {
    private lateinit var addSongRepository: AddSongRepository
    private val apiInterface: MusicPlaylistApiInterface = mock()

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
    fun setup() {
        addSongRepository = AddSongRepositoryImpl(
            apiInterface
        )
    }

    @Test
    fun testAddSong_success_returnData() = runTest {
        whenever(apiInterface.addTracksToPlaylist(any(), any())).thenReturn(
            Response.success(MockDataModel.mockPlaylist)
        )
        val flow = addSongRepository.addTracksToPlaylist("1", listOf(123, 234))

        flow.collect {
            assertEquals(MockDataModel.mockPlaylist.id, 1)
        }
    }

    @Test
    fun testAddSong_fail_returnError() = runTest {
        whenever(apiInterface.addTracksToPlaylist(any(), any())).thenReturn(
            Response.error(
                bodyResponse,
                rawResponse
            )
        )
        val flow = addSongRepository.addTracksToPlaylist("1", emptyList())

        flow.catch {
            assertEquals(AddSongRepositoryImpl.ERROR_ADD_SONGS_TO_PLAYLIST, it.message)
        }.collect()
    }
}
