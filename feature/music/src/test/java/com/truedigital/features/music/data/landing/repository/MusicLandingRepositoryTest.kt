package com.truedigital.features.music.data.landing.repository

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.common.share.data.coredata.data.api.CmsContentApiInterface
import com.truedigital.common.share.data.coredata.data.api.CmsShelvesApiInterface
import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.CmsShelfResponse
import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.Data
import com.truedigital.features.music.api.MusicLandingApiInterface
import com.truedigital.features.music.data.landing.model.response.MusicForYouShelfResponse
import com.truedigital.features.music.data.landing.model.response.playlisttrack.PlaylistTrackResponse
import com.truedigital.features.music.data.landing.model.response.tagalbum.TagAlbumResponse
import com.truedigital.features.music.data.landing.model.response.tagartist.TagArtistResponse
import com.truedigital.features.music.data.landing.model.response.tagname.TagNameResponse
import com.truedigital.features.music.data.landing.model.response.tagplaylist.TagPlaylistResponse
import com.truedigital.features.music.data.trending.model.response.playlist.Translation
import com.truedigital.features.utils.MockDataModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Response

internal class MusicLandingRepositoryTest {

    private lateinit var musicLandingRepository: MusicLandingRepository
    private val cmsContentApiInterface: CmsContentApiInterface = mock()
    private val musicLandingApiInterface: MusicLandingApiInterface = mock()
    private val cmsShelvesApi: CmsShelvesApiInterface = mock()
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
        musicLandingRepository = MusicLandingRepositoryImpl(
            cmsContentApi = cmsContentApiInterface,
            cmsShelvesApi = cmsShelvesApi,
            musicLandingApi = musicLandingApiInterface
        )
    }

    @Test
    fun getMusicForYouShelf_success_returnData() = runTest {
        // Given
        val musicForYouShelfResponseMock = MusicForYouShelfResponse().apply {
            this.value = "value"
        }
        whenever(musicLandingApiInterface.getMusicForYouShelf(any())).thenReturn(
            Response.success(musicForYouShelfResponseMock)
        )

        // When
        val flow = musicLandingRepository.getMusicForYouShelf("apiPath")

        // Then
        flow.collect {
            assertEquals(it, musicForYouShelfResponseMock.value)
        }
    }

    @Test
    fun getMusicForYouShelf_successValueNull_returnError() = runTest {
        // Given
        val musicForYouShelfResponseMock = MusicForYouShelfResponse().apply {
            this.value = null
        }
        whenever(musicLandingApiInterface.getMusicForYouShelf(any())).thenReturn(
            Response.success(musicForYouShelfResponseMock)
        )

        // When
        val flow = musicLandingRepository.getMusicForYouShelf("apiPath")

        // Then
        flow.catch {
            assertEquals(it.localizedMessage, MusicLandingRepositoryImpl.ERROR_LOAD_SHELF)
        }.collect { }
    }

    @Test
    fun getMusicForYouShelf_successValueEmpty_returnError() = runTest {
        // Given
        val musicForYouShelfResponseMock = MusicForYouShelfResponse().apply {
            this.value = ""
        }
        whenever(musicLandingApiInterface.getMusicForYouShelf(any())).thenReturn(
            Response.success(musicForYouShelfResponseMock)
        )

        // When
        val flow = musicLandingRepository.getMusicForYouShelf("apiPath")

        // Then
        flow.catch {
            assertEquals(it.localizedMessage, MusicLandingRepositoryImpl.ERROR_LOAD_SHELF)
        }.collect { }
    }

    @Test
    fun getMusicForYouShelf_bodyNull_returnError() = runTest {
        // Given
        whenever(
            musicLandingApiInterface.getMusicForYouShelf(
                any()
            )
        ).thenReturn(Response.success(null))

        // When
        val flow = musicLandingRepository.getMusicForYouShelf("apiPath")

        // Then
        flow.catch {
            assertEquals(it.localizedMessage, MusicLandingRepositoryImpl.ERROR_LOAD_SHELF)
        }.collect { }
    }

    @Test
    fun getMusicForYouShelf_fail_returnError() = runTest {
        // Given
        whenever(
            musicLandingApiInterface.getMusicForYouShelf(
                any()
            )
        ).thenReturn(Response.error(bodyResponse, rawResponse))

        // When
        val flow = musicLandingRepository.getMusicForYouShelf("apiPath")

        // Then
        flow.catch {
            assertEquals(it.localizedMessage, MusicLandingRepositoryImpl.ERROR_LOAD_SHELF)
        }.collect { }
    }

    @Test
    fun getTagAlbumShelf_success_returnData() = runTest {
        // Given
        val resultMock = TagAlbumResponse.Result(
            albumId = 99,
            name = "name"
        )
        val tagAlbumResponseMock = TagAlbumResponse(
            count = 10,
            offset = 1,
            total = 100,
            results = listOf(resultMock)
        )

        whenever(
            musicLandingApiInterface.getTagAlbum(
                any(),
                any(),
                any()
            )
        ).thenReturn(Response.success(tagAlbumResponseMock))

        // When
        val flow = musicLandingRepository.getTagAlbumShelf("tag", 1, 10)

        // Then
        flow.collect { tagAlbumResponse ->
            assertEquals(tagAlbumResponse?.count, tagAlbumResponseMock.count)
            assertEquals(tagAlbumResponse?.offset, tagAlbumResponseMock.offset)
            assertEquals(tagAlbumResponse?.total, tagAlbumResponseMock.total)

            val resultResponse = tagAlbumResponse?.results?.first()
            assertEquals(resultResponse?.albumId, resultMock.albumId)
            assertEquals(resultResponse?.name, resultMock.name)
        }
    }

    @Test
    fun getTagAlbumShelf_bodyNull_returnError() = runTest {
        // Given
        whenever(
            musicLandingApiInterface.getTagAlbum(
                any(),
                any(),
                any()
            )
        ).thenReturn(Response.success(null))

        // When
        val flow = musicLandingRepository.getTagAlbumShelf("tag", 1, 10)

        // Then
        flow.catch {
            assertEquals(it.localizedMessage, MusicLandingRepositoryImpl.ERROR_TAG_ALBUM_SHELF)
        }.collect { }
    }

    @Test
    fun getTagAlbumShelf_fail_returnError() = runTest {
        // Given
        whenever(
            musicLandingApiInterface.getTagAlbum(
                any(),
                any(),
                any()
            )
        ).thenReturn(Response.error(bodyResponse, rawResponse))

        // When
        val flow = musicLandingRepository.getTagAlbumShelf("tag", 1, 10)

        // Then
        flow.catch {
            assertEquals(it.localizedMessage, MusicLandingRepositoryImpl.ERROR_TAG_ALBUM_SHELF)
        }.collect { }
    }

    @Test
    fun getTagPlaylistShelf_success_returnData() = runTest {
        // Given
        val resultMock = TagPlaylistResponse.Result(
            playlistId = 1,
            coverImage = listOf(Translation("language", "coverImageValue")),
            name = listOf(Translation("language", "nameValue")),
        )
        val tagPlaylistResponseMock = TagPlaylistResponse(
            count = 10,
            offset = 1,
            total = 100,
            results = listOf(resultMock)
        )

        whenever(
            musicLandingApiInterface.getTagPlaylist(
                any(),
                any(),
                any(),
                any()
            )
        ).thenReturn(Response.success(tagPlaylistResponseMock))

        // When
        val flow = musicLandingRepository.getTagPlaylistShelf("tag", 1, 10, "all")

        // Then
        flow.collect { tagPlaylistResponse ->
            assertEquals(tagPlaylistResponse?.count, tagPlaylistResponseMock.count)
            assertEquals(tagPlaylistResponse?.offset, tagPlaylistResponseMock.offset)
            assertEquals(tagPlaylistResponse?.total, tagPlaylistResponseMock.total)

            val resultResponse = tagPlaylistResponse?.results?.first()
            assertEquals(resultResponse?.playlistId, resultMock.playlistId)
            assertEquals(resultResponse?.coverImage?.first(), resultMock.coverImage?.first())
            assertEquals(resultResponse?.name?.first(), resultMock.name?.first())
        }
    }

    @Test
    fun getTagPlaylistShelf_bodyNull_returnError() = runTest {
        // Given
        whenever(
            musicLandingApiInterface.getTagPlaylist(
                any(),
                any(),
                any(),
                any()
            )
        ).thenReturn(Response.success(null))

        // When
        val flow = musicLandingRepository.getTagPlaylistShelf("tag", 1, 10, "all")

        // Then
        flow.catch {
            assertEquals(it.localizedMessage, MusicLandingRepositoryImpl.ERROR_TAG_PLAYLIST_SHELF)
        }.collect { }
    }

    @Test
    fun getTagPlaylistShelf_fail_returnError() = runTest {
        // Given
        whenever(
            musicLandingApiInterface.getTagPlaylist(
                any(),
                any(),
                any(),
                any()
            )
        ).thenReturn(Response.error(bodyResponse, rawResponse))

        // When
        val flow = musicLandingRepository.getTagPlaylistShelf("tag", 1, 10, "all")

        // Then
        flow.catch {
            assertEquals(it.localizedMessage, MusicLandingRepositoryImpl.ERROR_TAG_PLAYLIST_SHELF)
        }.collect { }
    }

    @Test
    fun getPlaylistTrack_success_returnData() = runTest {
        // Given
        val resultMock = PlaylistTrackResponse.Result(
            playlistTrackId = 1,
            name = "name",
            image = "image",
        )
        val playlistTrackResponseMock = PlaylistTrackResponse(
            count = 10,
            offset = 1,
            total = 100,
            results = listOf(resultMock)
        )

        whenever(
            musicLandingApiInterface.getPlaylistTrack(
                any(),
                any(),
                any()
            )
        ).thenReturn(Response.success(playlistTrackResponseMock))

        // When
        val flow = musicLandingRepository.getPlaylistTrackShelf("id", 1, 10)

        // Then
        flow.collect { tagPlaylistResponse ->
            assertEquals(tagPlaylistResponse?.count, playlistTrackResponseMock.count)
            assertEquals(tagPlaylistResponse?.offset, playlistTrackResponseMock.offset)
            assertEquals(tagPlaylistResponse?.total, playlistTrackResponseMock.total)

            val resultResponse = tagPlaylistResponse?.results?.first()
            assertEquals(resultResponse?.playlistTrackId, resultMock.playlistTrackId)
            assertEquals(resultResponse?.name, resultMock.name)
            assertEquals(resultResponse?.image, resultMock.image)
        }
    }

    @Test
    fun getPlaylistTrack_bodyNull_returnError() = runTest {
        // Given
        whenever(
            musicLandingApiInterface.getPlaylistTrack(
                any(),
                any(),
                any()
            )
        ).thenReturn(Response.success(null))

        // When
        val flow = musicLandingRepository.getPlaylistTrackShelf("id", 1, 10)

        // Then
        flow.catch {
            assertEquals(it.localizedMessage, MusicLandingRepositoryImpl.ERROR_TRACK_PLAYLIST_SHELF)
        }.collect { }
    }

    @Test
    fun getPlaylistTrack_fail_returnError() = runTest {
        // Given
        whenever(
            musicLandingApiInterface.getPlaylistTrack(
                any(),
                any(),
                any()
            )
        ).thenReturn(Response.error(bodyResponse, rawResponse))

        // When
        val flow = musicLandingRepository.getPlaylistTrackShelf("id", 1, 10)

        // Then
        flow.catch {
            assertEquals(it.localizedMessage, MusicLandingRepositoryImpl.ERROR_TRACK_PLAYLIST_SHELF)
        }.collect { }
    }

    @Test
    fun testGetTagByName_success_returnData() = runTest {
        // Given
        val mockId = "id"
        val mockResponse = TagNameResponse().apply {
            displayName = listOf(
                Translation(
                    language = "en",
                    value = mockId
                )
            )
        }

        whenever(musicLandingApiInterface.getTagByName(any()))
            .thenReturn(Response.success(mockResponse))

        // When
        val flow = musicLandingRepository.getTagByName("name")

        // Then
        flow.collect { response ->
            assertEquals(1, response?.displayName?.size)
            assertEquals(mockId, response?.displayName?.first()?.value)
        }
    }

    @Test
    fun testGetTagByName_bodyNull_returnError() = runTest {
        // Given
        whenever(musicLandingApiInterface.getTagByName(any()))
            .thenReturn(Response.success(null))

        // When
        val flow = musicLandingRepository.getTagByName("name")

        // Then
        flow.catch {
            assertEquals(it.localizedMessage, MusicLandingRepositoryImpl.ERROR_TAG_BY_NAME)
        }.collect { }
    }

    @Test
    fun testGetTagByName_fail_returnError() = runTest {
        // Given
        whenever(musicLandingApiInterface.getTagByName(any()))
            .thenReturn(Response.error(bodyResponse, rawResponse))

        // When
        val flow = musicLandingRepository.getTagByName("name")

        // Then
        flow.catch {
            assertEquals(it.localizedMessage, MusicLandingRepositoryImpl.ERROR_TAG_BY_NAME)
        }.collect { }
    }

    @Test
    fun getCmsContentDetails_success_returnData() = runTest {
        // Given

        whenever(cmsContentApiInterface.getCmsContentDetails(any(), any(), any(), any(), any()))
            .thenReturn(Response.success(MockDataModel.mockContentDetailResponse))

        // When
        val flow = musicLandingRepository.getCmsContentDetails(
            cmsId = "cmsId",
            country = "country",
            lang = "lang",
            fields = "fields"
        )

        // Then
        flow.collect { response ->
            assertEquals(MockDataModel.mockContentDetailData.id, response?.data?.id)
            assertEquals(
                MockDataModel.mockContentDetailData.contentType,
                response?.data?.contentType
            )
        }
    }

    @Test
    fun getCmsContentDetails_success_bodyIsNull_returnError() = runTest {
        // Given
        whenever(cmsContentApiInterface.getCmsContentDetails(any(), any(), any(), any(), any()))
            .thenReturn(Response.success(null))

        // When
        val flow = musicLandingRepository.getCmsContentDetails(
            cmsId = "cmsId",
            country = "country",
            lang = "lang",
            fields = "fields"
        )

        // Then
        flow.catch {
            assertEquals(it.localizedMessage, MusicLandingRepositoryImpl.ERROR_CMS_CONTENT_DETAILS)
        }.collect { }
    }

    @Test
    fun getCmsContentDetails_fail_returnError() = runTest {
        // Given
        whenever(cmsContentApiInterface.getCmsContentDetails(any(), any(), any(), any(), any()))
            .thenReturn(Response.error(bodyResponse, rawResponse))

        // When
        val flow = musicLandingRepository.getCmsContentDetails(
            cmsId = "cmsId",
            country = "country",
            lang = "lang",
            fields = "fields"
        )

        // Then
        flow.catch {
            assertEquals(it.localizedMessage, MusicLandingRepositoryImpl.ERROR_CMS_CONTENT_DETAILS)
        }.collect {}
    }

    @Test
    fun getTagArtist_success_returnData() = runTest {
        // Given
        val musicForYouShelfResponseMock = TagArtistResponse(
            offset = 1,
            count = 10,
            results = listOf()
        )
        whenever(musicLandingApiInterface.getTagArtist(any(), any(), any())).thenReturn(
            Response.success(musicForYouShelfResponseMock)
        )

        // When
        val flow = musicLandingRepository.getTagArtist("tag", 1, 10)

        // Then
        flow.collect {
            assertEquals(it?.offset, 1)
            assertEquals(it?.count, 10)
            assertEquals(it?.results?.size, 0)
        }
    }

    @Test
    fun getTagArtist_bodyNull_returnError() = runTest {
        // Given
        whenever(musicLandingApiInterface.getTagArtist(any(), any(), any())).thenReturn(
            Response.success(null)
        )

        // When
        val flow = musicLandingRepository.getTagArtist("tag", 1, 10)

        // Then
        flow.catch {
            assertEquals(it.localizedMessage, MusicLandingRepositoryImpl.ERROR_TAG_ARTIST_SHELF)
        }.collect { }
    }

    @Test
    fun getTagArtist_fail_returnError() = runTest {
        // Given
        whenever(musicLandingApiInterface.getTagArtist(any(), any(), any())).thenReturn(
            Response.error(bodyResponse, rawResponse)
        )

        // When
        val flow = musicLandingRepository.getTagArtist("tag", 1, 10)

        // Then
        flow.catch {
            assertEquals(it.localizedMessage, MusicLandingRepositoryImpl.ERROR_TAG_ARTIST_SHELF)
        }.collect { }
    }

    @Test
    fun getCmsShelfList_success_returnData() = runTest {
        // Given
        val mockData = Data().apply {
            this.id = "id"
            this.detail = "detail"
            this.title = "title"
        }
        val mockCmsShelfResponse = CmsShelfResponse().apply {
            this.data = mockData
        }
        whenever(
            cmsShelvesApi.getCmsShelfList(any(), any(), any(), any(), anyOrNull(), anyOrNull())
        ).thenReturn(
            Response.success(mockCmsShelfResponse)
        )

        // When
        val flow = musicLandingRepository.getCmsShelfList("shelfId", "country", "lang", "fields")

        // Then
        flow.collect {
            assertEquals(mockData.id, it?.id)
            assertEquals(mockData.detail, it?.detail)
            assertEquals(mockData.title, it?.title)
        }
    }

    @Test
    fun getCmsShelfList_bodyNull_returnError() = runTest {
        // Given
        whenever(
            cmsShelvesApi.getCmsShelfList(any(), any(), any(), any(), anyOrNull(), anyOrNull())
        ).thenReturn(
            Response.success(null)
        )

        // When
        val flow = musicLandingRepository.getCmsShelfList("shelfId", "country", "lang", "fields")

        // Then
        flow.catch {
            assertEquals(it.localizedMessage, MusicLandingRepositoryImpl.ERROR_CMS_SHELF)
        }.collect {}
    }

    @Test
    fun getCmsShelfList_fail_returnError() = runTest {
        // Given
        whenever(
            cmsShelvesApi.getCmsShelfList(any(), any(), any(), any(), anyOrNull(), anyOrNull())
        ).thenReturn(
            Response.error(bodyResponse, rawResponse)
        )

        // When
        val flow = musicLandingRepository.getCmsShelfList("shelfId", "country", "lang", "fields")

        // Then
        flow.catch {
            assertEquals(it.localizedMessage, MusicLandingRepositoryImpl.ERROR_CMS_SHELF)
        }.collect {}
    }
}
