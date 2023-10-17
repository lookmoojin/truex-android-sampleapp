package com.truedigital.features.music.data.playlist.repository

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.music.api.MusicPlaylistApiInterface
import com.truedigital.features.music.data.playlist.model.CreateNewPlaylistRequest
import com.truedigital.features.music.data.playlist.model.response.RemoveTrackResponse
import com.truedigital.features.music.data.playlist.repository.MusicPlaylistRepositoryImpl.Companion.ERROR_CREATE_NEW_PLAYLIST
import com.truedigital.features.music.data.playlist.repository.MusicPlaylistRepositoryImpl.Companion.ERROR_DELETE_PLAYLIST
import com.truedigital.features.music.data.playlist.repository.MusicPlaylistRepositoryImpl.Companion.ERROR_LOAD_PLAYLIST_SHELF
import com.truedigital.features.music.data.playlist.repository.MusicPlaylistRepositoryImpl.Companion.ERROR_REMOVE_TRACK
import com.truedigital.features.music.data.playlist.repository.MusicPlaylistRepositoryImpl.Companion.ERROR_UPLOAD_COVER_IMAGE
import com.truedigital.features.music.data.trending.model.response.playlist.Translation
import com.truedigital.features.utils.MockDataModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class MusicPlaylistRepositoryTest {

    private lateinit var musicPlaylistRepository: MusicPlaylistRepository
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
        musicPlaylistRepository = MusicPlaylistRepositoryImpl(apiInterface)
    }

    @Test
    fun testGetPlaylist_successDataNotNull_returnPlaylist() = runTest {
        // Given
        whenever(apiInterface.getPlaylist(any()))
            .thenReturn(Response.success(MockDataModel.mockPlaylist))

        // When
        val result = musicPlaylistRepository.getPlaylist("1")

        // Then
        result.collect {
            assertNotNull(it)
        }
        verify(apiInterface, times(1)).getPlaylist(any())
    }

    @Test
    fun testGetPlaylist_successDataNull_returnError() = runTest {
        // Given
        whenever(apiInterface.getPlaylist(any())).thenReturn(Response.success(null))

        // When
        val result = musicPlaylistRepository.getPlaylist("1")

        // Then
        result.catch { error ->
            assertEquals(MusicPlaylistRepositoryImpl.ERROR_LOAD_PLAYLIST, error.localizedMessage)
        }.collect()
        verify(apiInterface, times(1)).getPlaylist(any())
    }

    @Test
    fun testGetPlaylist_fail_returnError() = runTest {
        // Given
        whenever(apiInterface.getPlaylist(any()))
            .thenReturn(Response.error(bodyResponse, rawResponse))

        // When
        val result = musicPlaylistRepository.getPlaylist("1")

        // Then
        result.catch { error ->
            assertEquals(MusicPlaylistRepositoryImpl.ERROR_LOAD_PLAYLIST, error.localizedMessage)
        }.collect()
        verify(apiInterface, times(1)).getPlaylist(any())
    }

    @Test
    fun testGetMyPlaylist_success_returnPlaylist() = runTest {
        // Given
        whenever(apiInterface.getMyPlaylists())
            .thenReturn(Response.success(MockDataModel.mockPlaylistPage))

        // When
        val result = musicPlaylistRepository.getMyPlaylists()

        // Then
        result.collect {
            assertNotNull(it)
        }
        verify(apiInterface, times(1)).getMyPlaylists()
    }

    @Test
    fun testGetMyPlaylist_success_bodyNull_returnError() = runTest {
        // Given
        whenever(apiInterface.getMyPlaylists())
            .thenReturn(Response.success(null))

        // When
        val result = musicPlaylistRepository.getMyPlaylists()

        // Then
        result.catch { error ->
            assertEquals(ERROR_LOAD_PLAYLIST_SHELF, error.localizedMessage)
        }.collect()
        verify(apiInterface, times(1)).getMyPlaylists()
    }

    @Test
    fun testGetMyPlaylist_fail_returnError() = runTest {
        // Given
        whenever(apiInterface.getMyPlaylists())
            .thenReturn(Response.error(bodyResponse, rawResponse))

        // When
        val result = musicPlaylistRepository.getMyPlaylists()

        // Then
        result.catch { error ->
            assertEquals(ERROR_LOAD_PLAYLIST_SHELF, error.localizedMessage)
        }.collect()
        verify(apiInterface, times(1)).getMyPlaylists()
    }

    @Test
    fun testPostNewPlaylist_fail_returnError() = runTest {
        // Given
        whenever(apiInterface.postCreateNewPlaylist(any()))
            .thenReturn(Response.error(bodyResponse, rawResponse))
        val request = CreateNewPlaylistRequest(
            name = listOf(
                Translation(
                    language = "en",
                    value = "Playlist 02"
                )
            )
        )

        // When
        val result = musicPlaylistRepository.postNewPlaylist(request)

        // Then
        result.catch { exception ->
            assertTrue {
                exception.message == ERROR_CREATE_NEW_PLAYLIST
            }
        }.collect()
        verify(apiInterface, times(1)).postCreateNewPlaylist(request)
    }

    @Test
    fun testPostNewPlaylist_success_returnPlaylist() = runTest {
        // Given
        whenever(apiInterface.postCreateNewPlaylist(any()))
            .thenReturn(Response.success(MockDataModel.mockPlaylist))
        val request = CreateNewPlaylistRequest(
            name = listOf(
                Translation(
                    language = "en",
                    value = "Playlist 02"
                )
            )
        )

        // When
        val result = musicPlaylistRepository.postNewPlaylist(request)

        // Then
        result.collect {
            assertEquals(MockDataModel.mockPlaylist, it)
        }
        verify(apiInterface, times(1)).postCreateNewPlaylist(request)
    }

    @Test
    fun testPostNewPlaylist_success_bodyNull_returnError() = runTest {
        // Given
        whenever(apiInterface.postCreateNewPlaylist(any()))
            .thenReturn(Response.success(null))
        val request = CreateNewPlaylistRequest(
            name = listOf(
                Translation(
                    language = "en",
                    value = "Playlist 02"
                )
            )
        )

        // When
        val result = musicPlaylistRepository.postNewPlaylist(request)

        // Then
        result.catch { exception ->
            assertTrue {
                exception.message == ERROR_CREATE_NEW_PLAYLIST
            }
        }.collect()
        verify(apiInterface, times(1)).postCreateNewPlaylist(request)
    }

    @Test
    fun testUploadCoverImage_success_verifyUploadCoverImage() = runTest {
        // Given
        whenever(apiInterface.uploadCoverImage(any(), any(), any()))
            .thenReturn(Response.success(Any()))

        // When
        val multiPart = MultipartBody.Part.createFormData("upload", "name")
        val result = musicPlaylistRepository.uploadCoverImage(1, "en", multiPart)

        // Then
        result.collect()
        verify(apiInterface, times(1)).uploadCoverImage(any(), any(), any())
    }

    @Test
    fun testUploadCoverImage_fail_returnError() = runTest {
        // Given
        whenever(apiInterface.uploadCoverImage(any(), any(), any()))
            .thenReturn(Response.error(bodyResponse, rawResponse))

        // When
        val multiPart = MultipartBody.Part.createFormData("upload", "name")
        val result = musicPlaylistRepository.uploadCoverImage(1, "en", multiPart)

        // Then
        result.catch { error ->
            assertEquals(ERROR_UPLOAD_COVER_IMAGE, error.localizedMessage)
        }.collect()
        verify(apiInterface, times(1)).uploadCoverImage(any(), any(), any())
    }

    @Test
    fun testDeletePlaylist_success_verifyDeletePlaylist() = runTest {
        // Given
        whenever(apiInterface.deletePlaylist(any())).thenReturn(Response.success(Any()))

        // When
        val result = musicPlaylistRepository.deletePlaylist(1)

        // Then
        result.collect()
        verify(apiInterface, times(1)).deletePlaylist(any())
    }

    @Test
    fun testDeletePlaylist_fail_returnError() = runTest {
        // Given
        whenever(apiInterface.deletePlaylist(any())).thenReturn(
            Response.error(
                bodyResponse,
                rawResponse
            )
        )

        // When
        val result = musicPlaylistRepository.deletePlaylist(1)

        // Then
        result.catch { error ->
            assertEquals(ERROR_DELETE_PLAYLIST, error.localizedMessage)
        }.collect()
        verify(apiInterface, times(1)).deletePlaylist(any())
    }

    @Test
    fun removeTrack_success_returnData() = runTest {
        val mockRemoveTrackResponse = RemoveTrackResponse(value = true)
        whenever(apiInterface.removeTrack(any(), any())).thenReturn(
            Response.success(mockRemoveTrackResponse)
        )

        val result = musicPlaylistRepository.removeTrack(1, listOf(1, 2))

        result.collect { value ->
            assertEquals(mockRemoveTrackResponse, value)
        }
        verify(apiInterface, times(1)).removeTrack(any(), any())
    }

    @Test
    fun removeTrack_fail_returnError() = runTest {
        whenever(apiInterface.removeTrack(any(), any())).thenReturn(
            Response.error(bodyResponse, rawResponse)
        )

        val result = musicPlaylistRepository.removeTrack(1, listOf(1, 2))

        result.catch { error ->
            assertEquals(ERROR_REMOVE_TRACK, error.localizedMessage)
        }.collect()
        verify(apiInterface, times(1)).removeTrack(any(), any())
    }
}
