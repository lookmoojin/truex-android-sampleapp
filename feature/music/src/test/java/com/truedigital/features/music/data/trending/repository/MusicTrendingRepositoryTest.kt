package com.truedigital.features.music.data.trending.repository

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.music.api.MusicTrendingApi
import com.truedigital.features.music.data.trending.model.response.album.AlbumResponse
import com.truedigital.features.music.data.trending.model.response.album.MusicTrendingAlbumResponse
import com.truedigital.features.music.data.trending.model.response.artist.ArtistResponse
import com.truedigital.features.music.data.trending.model.response.artist.MusicTrendingArtistResponse
import com.truedigital.features.music.data.trending.model.response.playlist.MusicTrendingPlaylistResponse
import com.truedigital.features.music.data.trending.model.response.playlist.PlaylistResponse
import com.truedigital.features.music.data.trending.model.response.playlist.Translation
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

@ExtendWith(InstantTaskExecutorExtension::class)
class MusicTrendingRepositoryTest {

    private lateinit var musicTrendingRepository: MusicTrendingRepository
    private val musicTrendingApi: MusicTrendingApi = mock()

    @BeforeEach
    fun setUp() {
        musicTrendingRepository = MusicTrendingRepositoryImpl(musicTrendingApi)
    }

    @Test
    fun getMusicTrendingArtists_success_returnData() = runTest {
        val results = arrayListOf(ArtistResponse(artistId = 99, name = "name", image = "image"))
        val responseData =
            MusicTrendingArtistResponse(offset = 1, count = 10, total = 100, results = results)

        whenever(musicTrendingApi.getMusicTrendingArtists())
            .thenReturn(Response.success(responseData))

        val flow = musicTrendingRepository.getMusicTrendingArtists()
        flow.collect { musicTrendingArtist ->
            assertThat(musicTrendingArtist?.offset, `is`(responseData.offset))
            assertThat(musicTrendingArtist?.count, `is`(responseData.count))
            assertThat(musicTrendingArtist?.total, `is`(responseData.total))

            val actualResult = musicTrendingArtist?.results?.firstOrNull()
            assertThat(actualResult?.artistId, `is`(results.firstOrNull()?.artistId))
            assertThat(actualResult?.name, `is`(results.firstOrNull()?.name))
            assertThat(actualResult?.image, `is`(results.firstOrNull()?.image))
        }
    }

    @Test
    fun getMusicTrendingArtists_bodyNull_returnError() = runTest {
        whenever(musicTrendingApi.getMusicTrendingArtists())
            .thenReturn(Response.success(null))

        val flow = musicTrendingRepository.getMusicTrendingArtists()
        flow.catch {
            assertThat(
                it.localizedMessage,
                containsString(MusicTrendingRepositoryImpl.ERROR_TRENDING_ARTIST_API)
            )
        }.collect()
    }

    @Test
    fun getMusicTrendingArtists_fail_returnError() = runTest {
        val responseBody =
            "{\"code\":400,\"status\":400,\"error\":null,\"message\":\"unexpected error\"}"
                .toResponseBody("application/json".toMediaTypeOrNull())

        val responseRaw = okhttp3.Response.Builder()
            .code(400)
            .message("unexpected error")
            .request(Request.Builder().url("http://example.com").build())
            .protocol(Protocol.HTTP_1_0)
            .build()

        whenever(musicTrendingApi.getMusicTrendingArtists())
            .thenReturn(Response.error(responseBody, responseRaw))

        val flow = musicTrendingRepository.getMusicTrendingArtists()
        flow.catch {
            assertThat(
                it.localizedMessage,
                containsString(MusicTrendingRepositoryImpl.ERROR_TRENDING_ARTIST_API)
            )
        }.collect()
    }

    @Test
    fun getMusicTrendingPlaylist_success_returnData() = runTest {
        val name = listOf(Translation(language = "th", value = "nameTh"))
        val coverImage = listOf(Translation(language = "th", value = "imageTh"))
        val results =
            arrayListOf(PlaylistResponse(playlistId = 99, name = name, coverImage = coverImage))
        val responseData =
            MusicTrendingPlaylistResponse(offset = 1, count = 10, total = 100, results = results)

        whenever(musicTrendingApi.getMusicTrendingPlaylist())
            .thenReturn(Response.success(responseData))

        val flow = musicTrendingRepository.getMusicTrendingPlaylist()
        flow.collect { musicTrendingPlaylist ->
            assertThat(musicTrendingPlaylist?.offset, `is`(responseData.offset))
            assertThat(musicTrendingPlaylist?.count, `is`(responseData.count))
            assertThat(musicTrendingPlaylist?.total, `is`(responseData.total))

            val actualResult = musicTrendingPlaylist?.results?.firstOrNull()
            assertThat(actualResult?.playlistId, `is`(results.firstOrNull()?.playlistId))
            assertThat(actualResult?.name, `is`(results.firstOrNull()?.name))
            assertThat(actualResult?.coverImage, `is`(results.firstOrNull()?.coverImage))

            val actualName = actualResult?.name?.firstOrNull()
            assertThat(actualName?.language, `is`(name.firstOrNull()?.language))
            assertThat(actualName?.value, `is`(name.firstOrNull()?.value))

            val actualCoverImage = actualResult?.coverImage?.firstOrNull()
            assertThat(actualCoverImage?.language, `is`(coverImage.firstOrNull()?.language))
            assertThat(actualCoverImage?.value, `is`(coverImage.firstOrNull()?.value))
        }
    }

    @Test
    fun getMusicTrendingPlaylist_bodyNull_returnError() = runTest {
        whenever(musicTrendingApi.getMusicTrendingPlaylist())
            .thenReturn(Response.success(null))

        val flow = musicTrendingRepository.getMusicTrendingPlaylist()
        flow.catch {
            assertThat(
                it.localizedMessage,
                containsString(MusicTrendingRepositoryImpl.ERROR_TRENDING_PLAYLIST_API)
            )
        }.collect()
    }

    @Test
    fun getMusicTrendingPlaylist_fail_returnError() = runTest {
        val responseBody =
            "{\"code\":400,\"status\":400,\"error\":null,\"message\":\"unexpected error\"}"
                .toResponseBody("application/json".toMediaTypeOrNull())

        val responseRaw = okhttp3.Response.Builder()
            .code(400)
            .message("unexpected error")
            .request(Request.Builder().url("http://example.com").build())
            .protocol(Protocol.HTTP_1_0)
            .build()

        whenever(musicTrendingApi.getMusicTrendingPlaylist())
            .thenReturn(Response.error(responseBody, responseRaw))

        val flow = musicTrendingRepository.getMusicTrendingPlaylist()
        flow.catch {
            assertThat(
                it.localizedMessage,
                containsString(MusicTrendingRepositoryImpl.ERROR_TRENDING_PLAYLIST_API)
            )
        }.collect()
    }

    @Test
    fun getMusicTrendingAlbum_success_returnData() = runTest {
        val results = arrayListOf(AlbumResponse(albumId = 99, name = "name", albumType = "type"))
        val responseData =
            MusicTrendingAlbumResponse(offset = 1, count = 10, total = 100, results = results)

        whenever(musicTrendingApi.getMusicTrendingAlbum())
            .thenReturn(Response.success(responseData))

        val flow = musicTrendingRepository.getMusicTrendingAlbum()
        flow.collect { musicTrendingAlbum ->
            assertThat(musicTrendingAlbum?.offset, `is`(responseData.offset))
            assertThat(musicTrendingAlbum?.count, `is`(responseData.count))
            assertThat(musicTrendingAlbum?.total, `is`(responseData.total))

            val actualResult = musicTrendingAlbum?.results?.firstOrNull()
            assertThat(actualResult?.albumId, `is`(results.firstOrNull()?.albumId))
            assertThat(actualResult?.name, `is`(results.firstOrNull()?.name))
            assertThat(actualResult?.albumType, `is`(results.firstOrNull()?.albumType))
        }
    }

    @Test
    fun getMusicTrendingAlbum_bodyNull_returnError() = runTest {
        whenever(musicTrendingApi.getMusicTrendingAlbum())
            .thenReturn(Response.success(null))

        val flow = musicTrendingRepository.getMusicTrendingAlbum()
        flow.catch {
            assertThat(
                it.localizedMessage,
                containsString(MusicTrendingRepositoryImpl.ERROR_TRENDING_ALBUM_API)
            )
        }.collect()
    }

    @Test
    fun getMusicTrendingAlbum_fail_returnError() = runTest {
        val responseBody =
            "{\"code\":400,\"status\":400,\"error\":null,\"message\":\"unexpected error\"}"
                .toResponseBody("application/json".toMediaTypeOrNull())

        val responseRaw = okhttp3.Response.Builder()
            .code(400)
            .message("unexpected error")
            .request(Request.Builder().url("http://example.com").build())
            .protocol(Protocol.HTTP_1_0)
            .build()

        whenever(musicTrendingApi.getMusicTrendingAlbum())
            .thenReturn(Response.error(responseBody, responseRaw))

        val flow = musicTrendingRepository.getMusicTrendingAlbum()
        flow.catch {
            assertThat(
                it.localizedMessage,
                containsString(MusicTrendingRepositoryImpl.ERROR_TRENDING_ALBUM_API)
            )
        }.collect()
    }
}
