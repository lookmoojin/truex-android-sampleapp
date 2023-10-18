package com.truedigital.features.tuned.domain.facade.tag

import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.playlist.model.Playlist
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.tag.model.Tag
import io.reactivex.Single

interface TagFacade {
    fun getTagByName(name: String): Single<Tag>
    fun loadStationsWithTag(tag: String, offset: Int, count: Int): Single<List<Station>>
    fun loadArtistsWithTag(tag: String, offset: Int, count: Int): Single<List<Artist>>
    fun loadAlbumsWithTag(tag: String, offset: Int, count: Int): Single<List<Album>>
    fun loadPlaylistsWithTag(tag: String, offset: Int, count: Int): Single<List<Playlist>>
}
