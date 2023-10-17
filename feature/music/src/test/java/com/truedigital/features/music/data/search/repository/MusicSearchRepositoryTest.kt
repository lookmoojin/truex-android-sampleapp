package com.truedigital.features.music.data.search.repository

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.music.api.SearchMusicApi
import com.truedigital.features.music.data.search.model.response.MusicSearchResponse
import com.truedigital.features.music.data.search.model.response.MusicSearchResponseItem
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.ResponseBody.Companion.toResponseBody
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertNull

@ExtendWith(InstantTaskExecutorExtension::class)
class MusicSearchRepositoryTest {

    private lateinit var musicSearchRepository: MusicSearchRepository
    private val musicSearchApi: SearchMusicApi = mock()
    private val response201 = Response.success(201, MusicSearchResponse())
    private val responseBody =
        "{\"code\":400,\"status\":400,\"error\":null,\"message\":\"unexpected error\"}".toResponseBody(
            "application/json".toMediaTypeOrNull()
        )
    private val responseRaw = okhttp3.Response.Builder()
        .code(400)
        .message("unexpected error")
        .request(Request.Builder().url("http://example.com").build())
        .protocol(Protocol.HTTP_1_0)
        .build()

    @BeforeEach
    fun setUp() {
        musicSearchRepository = MusicSearchRepositoryImpl(musicSearchApi)
    }

    // ---------------------getSearchQuery----------------------------------------------------------
    @Test
    fun `test getSearchQuery success should return data`() = runTest {
        val response = MusicSearchResponse().apply {
            this.add(MusicSearchResponseItem(key = "song"))
            this.add(MusicSearchResponseItem(key = "album"))
        }

        whenever(musicSearchApi.getSearchQuery(any(), any()))
            .thenReturn(Response.success(response))

        val flow = musicSearchRepository.getSearchQuery("query")

        flow.collect {
            assertThat(it?.size, `is`(2))
            assertThat(it?.get(0)?.key, `is`("song"))
        }
    }

    @Test
    fun `test getSearchQuery success but body null should return error`() = runTest {
        whenever(musicSearchApi.getSearchQuery(any(), any()))
            .thenReturn(Response.success(null))

        val flow = musicSearchRepository.getSearchQuery("query")

        flow.catch {
            assertThat(
                it.localizedMessage,
                containsString("Retrieving Music data is fail or data 404 not found")
            )
        }.collect()
    }

    @Test
    fun `test getSearchQuery success code not 200 should return error`() = runTest {
        whenever(musicSearchApi.getSearchQuery(any(), any())).thenReturn(response201)

        val flow = musicSearchRepository.getSearchQuery("query")

        flow.catch {
            assertThat(
                it.localizedMessage,
                containsString("Retrieving Music data is fail or data 404 not found")
            )
        }.collect()
    }

    @Test
    fun `test getSearchQuery error should return error`() = runTest {
        whenever(musicSearchApi.getSearchQuery(any(), any()))
            .thenReturn(Response.error(responseBody, responseRaw))

        val flow = musicSearchRepository.getSearchQuery("query")

        flow.catch {
            assertThat(
                it.localizedMessage,
                containsString("Retrieving Music data is fail or data 404 not found")
            )
        }.collect()
    }

    // ---------------------getSearchArtistQuery----------------------------------------------------
    @Test
    fun `test getSearchArtistQuery success should return data`() = runTest {
        val response = MusicSearchResponse().apply {
            this.add(MusicSearchResponseItem(key = "artist"))
            this.add(MusicSearchResponseItem(key = "song"))
        }

        whenever(musicSearchApi.getArtistQuery(any(), any(), any()))
            .thenReturn(Response.success(response))

        val flow = musicSearchRepository.getSearchArtistQuery("query", "offset", "count")

        flow.collect {
            assertThat(it?.size, `is`(2))
            assertThat(it?.get(0)?.key, `is`("artist"))
        }
    }

    @Test
    fun `test getSearchArtistQuery success but body null should return error`() = runTest {
        whenever(musicSearchApi.getArtistQuery(any(), any(), any()))
            .thenReturn(Response.success(null))

        val flow = musicSearchRepository.getSearchArtistQuery("query", "offset", "count")

        flow.catch {
            assertThat(
                it.localizedMessage,
                containsString("Retrieving Music data is fail or data 404 not found")
            )
        }.collect()
    }

    @Test
    fun `test getSearchArtistQuery success code not 200 should return error`() = runTest {
        whenever(musicSearchApi.getArtistQuery(any(), any(), any())).thenReturn(response201)

        val flow = musicSearchRepository.getSearchArtistQuery("query", "offset", "count")

        flow.catch {
            assertThat(
                it.localizedMessage,
                containsString("Retrieving Music data is fail or data 404 not found")
            )
        }.collect()
    }

    @Test
    fun `test getSearchArtistQuery error should return error`() = runTest {
        whenever(musicSearchApi.getArtistQuery(any(), any(), any()))
            .thenReturn(Response.error(responseBody, responseRaw))

        val flow = musicSearchRepository.getSearchArtistQuery("query", "offset", "count")

        flow.catch {
            assertThat(
                it.localizedMessage,
                containsString("Retrieving Music data is fail or data 404 not found")
            )
        }.collect()
    }

    // ---------------------getSearchPlaylistQuery--------------------------------------------------
    @Test
    fun `test getSearchPlaylistQuery success should return data`() = runTest {
        val response = MusicSearchResponse().apply {
            this.add(MusicSearchResponseItem(key = "playlist"))
            this.add(MusicSearchResponseItem(key = "song"))
        }

        whenever(musicSearchApi.getPlaylistQuery(any(), any(), any()))
            .thenReturn(Response.success(response))

        val flow = musicSearchRepository.getSearchPlaylistQuery("query", "offset", "count")

        flow.collect {
            assertThat(it?.size, `is`(2))
            assertThat(it?.get(0)?.key, `is`("playlist"))
        }
    }

    @Test
    fun `test getSearchPlaylistQuery success but body null should return error`() = runTest {
        whenever(musicSearchApi.getPlaylistQuery(any(), any(), any()))
            .thenReturn(Response.success(null))

        val flow = musicSearchRepository.getSearchPlaylistQuery("query", "offset", "count")

        flow.catch {
            assertThat(
                it.localizedMessage,
                containsString("Retrieving Music data is fail or data 404 not found")
            )
        }.collect()
    }

    @Test
    fun `test getSearchPlaylistQuery success code not 200 should return error`() = runTest {
        whenever(musicSearchApi.getPlaylistQuery(any(), any(), any())).thenReturn(response201)

        val flow = musicSearchRepository.getSearchPlaylistQuery("query", "offset", "count")

        flow.catch {
            assertThat(
                it.localizedMessage,
                containsString("Retrieving Music data is fail or data 404 not found")
            )
        }.collect()
    }

    @Test
    fun `test getSearchPlaylistQuery error should return error`() = runTest {
        whenever(musicSearchApi.getPlaylistQuery(any(), any(), any()))
            .thenReturn(Response.error(responseBody, responseRaw))

        val flow = musicSearchRepository.getSearchPlaylistQuery("query", "offset", "count")

        flow.catch {
            assertThat(
                it.localizedMessage,
                containsString("Retrieving Music data is fail or data 404 not found")
            )
        }.collect()
    }

    // ---------------------getSongQuery------------------------------------------------------------
    @Test
    fun `test getSongQuery success should return data`() = runTest {
        val response = MusicSearchResponse().apply {
            this.add(MusicSearchResponseItem(key = "playlist"))
            this.add(MusicSearchResponseItem(key = "song"))
        }

        whenever(musicSearchApi.getSongQuery(any(), any(), any()))
            .thenReturn(Response.success(response))

        val flow = musicSearchRepository.getSongQuery("query", "offset", "count")

        flow.collect {
            assertThat(it?.size, `is`(2))
            assertThat(it?.get(0)?.key, `is`("playlist"))
        }
    }

    @Test
    fun `test getSongQuery success but body null should return error`() = runTest {
        whenever(musicSearchApi.getSongQuery(any(), any(), any()))
            .thenReturn(Response.success(null))

        val flow = musicSearchRepository.getSongQuery("query", "offset", "count")

        flow.catch {
            assertThat(
                it.localizedMessage,
                containsString("Retrieving Music data is fail or data 404 not found")
            )
        }.collect()
    }

    @Test
    fun `test getSongQuery success code not 200 should return error`() = runTest {
        whenever(musicSearchApi.getSongQuery(any(), any(), any())).thenReturn(response201)

        val flow = musicSearchRepository.getSongQuery("query", "offset", "count")

        flow.catch {
            assertThat(
                it.localizedMessage,
                containsString("Retrieving Music data is fail or data 404 not found")
            )
        }.collect()
    }

    @Test
    fun `test getSongQuery error should return error`() = runTest {
        whenever(musicSearchApi.getSongQuery(any(), any(), any()))
            .thenReturn(Response.error(responseBody, responseRaw))

        val flow = musicSearchRepository.getSongQuery("query", "offset", "count")

        flow.catch {
            assertThat(
                it.localizedMessage,
                containsString("Retrieving Music data is fail or data 404 not found")
            )
        }.collect()
    }

    // ---------------------getSongQueryStream------------------------------------------------------
    @Test
    fun getSongQueryStream_success_bodyIsNotNull_returnData() = runTest {
        val mockMusicSearchResponseItem = MusicSearchResponseItem(key = "playlist")

        val response = MusicSearchResponse().apply {
            add(mockMusicSearchResponseItem)
        }

        whenever(musicSearchApi.getSongQuery(any(), any(), any()))
            .thenReturn(Response.success(response))

        val result = musicSearchRepository.getSongQueryStream("query", "offset", "count")
        assertEquals(mockMusicSearchResponseItem.key, result?.first()?.key)
        verify(musicSearchApi, times(1)).getSongQuery(any(), any(), any())
    }

    @Test
    fun getSongQueryStream_success_bodyIsNull_returnNull() = runTest {
        whenever(musicSearchApi.getSongQuery(any(), any(), any()))
            .thenReturn(Response.success(null))

        val result = musicSearchRepository.getSongQueryStream("query", "offset", "count")
        assertNull(result)
        verify(musicSearchApi, times(1)).getSongQuery(any(), any(), any())
    }

    @Test
    fun getSongQueryStream_fail_returnNull() = runTest {
        whenever(musicSearchApi.getSongQuery(any(), any(), any()))
            .thenReturn(Response.error(responseBody, responseRaw))

        val result = musicSearchRepository.getSongQueryStream("query", "offset", "count")
        assertNull(result)
        verify(musicSearchApi, times(1)).getSongQuery(any(), any(), any())
    }

    // ---------------------getAlbumQuery-----------------------------------------------------------
    @Test
    fun `test getAlbumQuery success should return data`() = runTest {
        val response = MusicSearchResponse().apply {
            this.add(MusicSearchResponseItem(key = "playlist"))
            this.add(MusicSearchResponseItem(key = "song"))
        }

        whenever(musicSearchApi.getAlbumQuery(any(), any(), any()))
            .thenReturn(Response.success(response))

        val flow = musicSearchRepository.getAlbumQuery("query", "offset", "count")

        flow.collect {
            assertThat(it?.size, `is`(2))
            assertThat(it?.get(0)?.key, `is`("playlist"))
        }
    }

    @Test
    fun `test getAlbumQuery success but body null should return error`() = runTest {
        whenever(musicSearchApi.getAlbumQuery(any(), any(), any()))
            .thenReturn(Response.success(null))

        val flow = musicSearchRepository.getAlbumQuery("query", "offset", "count")

        flow.catch {
            assertThat(
                it.localizedMessage,
                containsString("Retrieving Music data is fail or data 404 not found")
            )
        }.collect()
    }

    @Test
    fun `test getAlbumQuery success code not 200 should return error`() = runTest {
        whenever(musicSearchApi.getAlbumQuery(any(), any(), any())).thenReturn(response201)

        val flow = musicSearchRepository.getAlbumQuery("query", "offset", "count")

        flow.catch {
            assertThat(
                it.localizedMessage,
                containsString("Retrieving Music data is fail or data 404 not found")
            )
        }.collect()
    }

    @Test
    fun `test getAlbumQuery error should return error`() = runTest {
        whenever(musicSearchApi.getAlbumQuery(any(), any(), any()))
            .thenReturn(Response.error(responseBody, responseRaw))

        val flow = musicSearchRepository.getAlbumQuery("query", "offset", "count")

        flow.catch {
            assertThat(
                it.localizedMessage,
                containsString("Retrieving Music data is fail or data 404 not found")
            )
        }.collect()
    }
}
