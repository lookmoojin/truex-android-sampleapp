package com.truedigital.features.music.data.landing.repository

import com.truedigital.common.share.data.coredata.data.api.CmsContentApiInterface
import com.truedigital.common.share.data.coredata.data.api.CmsShelvesApiInterface
import com.truedigital.common.share.data.coredata.data.model.response.ContentDetailResponse
import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.Data
import com.truedigital.features.music.api.MusicLandingApiInterface
import com.truedigital.features.music.data.landing.model.response.playlisttrack.PlaylistTrackResponse
import com.truedigital.features.music.data.landing.model.response.tagalbum.TagAlbumResponse
import com.truedigital.features.music.data.landing.model.response.tagartist.TagArtistResponse
import com.truedigital.features.music.data.landing.model.response.tagname.TagNameResponse
import com.truedigital.features.music.data.landing.model.response.tagplaylist.TagPlaylistResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface MusicLandingRepository {
    fun getMusicForYouShelf(apiPath: String): Flow<String>

    fun getTagAlbumShelf(
        tag: String,
        offset: Int,
        count: Int
    ): Flow<TagAlbumResponse?>

    fun getTagArtist(
        tag: String,
        offset: Int,
        count: Int
    ): Flow<TagArtistResponse?>

    fun getTagPlaylistShelf(
        tag: String,
        offset: Int,
        count: Int,
        type: String
    ): Flow<TagPlaylistResponse?>

    fun getPlaylistTrackShelf(
        id: String,
        offset: Int,
        count: Int
    ): Flow<PlaylistTrackResponse?>

    fun getTagByName(name: String): Flow<TagNameResponse?>

    fun getCmsContentDetails(
        cmsId: String,
        country: String,
        lang: String,
        fields: String
    ): Flow<ContentDetailResponse?>

    fun getCmsShelfList(
        shelfId: String,
        country: String,
        lang: String,
        fields: String
    ): Flow<Data?>
}

class MusicLandingRepositoryImpl @Inject constructor(
    private val cmsContentApi: CmsContentApiInterface,
    private val cmsShelvesApi: CmsShelvesApiInterface,
    private val musicLandingApi: MusicLandingApiInterface
) : MusicLandingRepository {

    companion object {
        const val ERROR_LOAD_SHELF = "Music ForYou shelf is fail or data not found"
        const val ERROR_TAG_ALBUM_SHELF = "Music Tag Album is fail or data not found"
        const val ERROR_TAG_ARTIST_SHELF = "Music Tag Artist is fail or data not found"
        const val ERROR_TAG_PLAYLIST_SHELF = "Music Tag Playlist is fail or data not found"
        const val ERROR_TAG_BY_NAME = "Music Tag By Name is fail or data not found"
        const val ERROR_TRACK_PLAYLIST_SHELF = "Music Track Playlist is fail or data not found"
        const val ERROR_CMS_CONTENT_DETAILS = "Cms content details is fail or data not found"
        const val ERROR_CMS_SHELF = "Cms shelf is fail or data not found"
    }

    override fun getMusicForYouShelf(apiPath: String): Flow<String> {
        return flow {
            val response = musicLandingApi.getMusicForYouShelf(apiPath)
            val result = if (response.isSuccessful &&
                response.body()?.value.isNullOrEmpty().not()
            ) {
                response.body()?.value.orEmpty()
            } else {
                error(ERROR_LOAD_SHELF)
            }
            emit(result)
        }
    }

    override fun getTagAlbumShelf(tag: String, offset: Int, count: Int): Flow<TagAlbumResponse?> {
        return flow {
            val response = musicLandingApi.getTagAlbum(
                tag = tag,
                offset = offset,
                count = count
            )
            val result = if (response.isSuccessful && response.body() != null) {
                response.body()
            } else {
                error(ERROR_TAG_ALBUM_SHELF)
            }
            emit(result)
        }
    }

    override fun getTagArtist(tag: String, offset: Int, count: Int): Flow<TagArtistResponse?> {
        return flow {
            val response = musicLandingApi.getTagArtist(
                tag = tag,
                offset = offset,
                count = count
            )
            val result = if (response.isSuccessful && response.body() != null) {
                response.body()
            } else {
                error(ERROR_TAG_ARTIST_SHELF)
            }
            emit(result)
        }
    }

    override fun getTagPlaylistShelf(
        tag: String,
        offset: Int,
        count: Int,
        type: String
    ): Flow<TagPlaylistResponse?> {
        return flow {
            val response = musicLandingApi.getTagPlaylist(
                tag = tag,
                offset = offset,
                count = count,
                type = type
            )
            val result = if (response.isSuccessful && response.body() != null) {
                response.body()
            } else {
                error(ERROR_TAG_PLAYLIST_SHELF)
            }
            emit(result)
        }
    }

    override fun getPlaylistTrackShelf(
        id: String,
        offset: Int,
        count: Int
    ): Flow<PlaylistTrackResponse?> {
        return flow {
            val response = musicLandingApi.getPlaylistTrack(
                id = id,
                offset = offset,
                count = count
            )
            val result = if (response.isSuccessful && response.body() != null) {
                response.body()
            } else {
                error(ERROR_TRACK_PLAYLIST_SHELF)
            }
            emit(result)
        }
    }

    override fun getTagByName(name: String): Flow<TagNameResponse?> {
        return flow {
            val response = musicLandingApi.getTagByName(name)
            val result = if (response.isSuccessful && response.body() != null) {
                response.body()
            } else {
                error(ERROR_TAG_BY_NAME)
            }
            emit(result)
        }
    }

    override fun getCmsContentDetails(
        cmsId: String,
        country: String,
        lang: String,
        fields: String
    ): Flow<ContentDetailResponse?> {
        return flow {
            val response = cmsContentApi.getCmsContentDetails(
                cmsId = cmsId,
                country = country,
                lang = lang,
                fields = fields,
                expand = ""
            )
            val result = if (response.isSuccessful && response.body() != null) {
                response.body()
            } else {
                error(ERROR_CMS_CONTENT_DETAILS)
            }
            emit(result)
        }
    }

    override fun getCmsShelfList(
        shelfId: String,
        country: String,
        lang: String,
        fields: String
    ): Flow<Data?> {
        return flow {
            val response = cmsShelvesApi.getCmsShelfList(
                shelfId = shelfId,
                country = country,
                lang = lang,
                fields = fields
            )

            val result = if (response.isSuccessful && response.body() != null) {
                response.body()?.data
            } else {
                error(ERROR_CMS_SHELF)
            }
            emit(result)
        }
    }
}
