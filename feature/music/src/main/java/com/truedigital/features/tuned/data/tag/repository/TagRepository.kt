package com.truedigital.features.tuned.data.tag.repository

import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.playlist.model.Playlist
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.tag.model.Tag
import com.truedigital.features.tuned.data.track.model.request.TrackRequestType
import io.reactivex.Single

interface TagRepository {
    fun getTagByName(name: String): Single<Tag>
    fun getTags(names: String): Single<List<Tag>>

    fun getPlaylistsByTag(
        tag: String,
        offset: Int,
        count: Int,
        type: TrackRequestType
    ): Single<List<Playlist>>

    fun getArtistsByTag(tag: String, offset: Int, count: Int): Single<List<Artist>>
    fun getAlbumsByTag(tag: String, offset: Int, count: Int): Single<List<Album>>
    fun getStationsByTag(tag: String, offset: Int, count: Int): Single<List<Station>>

    fun getAlbumsByTagGroup(
        tagsString: String,
        tagTypeString: String,
        offset: Int,
        count: Int
    ): Single<List<Album>>
}
