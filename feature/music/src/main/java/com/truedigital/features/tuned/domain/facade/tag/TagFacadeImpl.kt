package com.truedigital.features.tuned.domain.facade.tag

import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.playlist.model.Playlist
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.tag.model.Tag
import com.truedigital.features.tuned.data.tag.repository.TagRepository
import com.truedigital.features.tuned.data.track.model.request.TrackRequestType
import io.reactivex.Single
import javax.inject.Inject

class TagFacadeImpl @Inject constructor(private val tagRepository: TagRepository) : TagFacade {

    override fun getTagByName(name: String): Single<Tag> =
        tagRepository.getTagByName(name)

    override fun loadStationsWithTag(tag: String, offset: Int, count: Int): Single<List<Station>> =
        tagRepository.getStationsByTag(tag, offset, count)

    override fun loadArtistsWithTag(tag: String, offset: Int, count: Int): Single<List<Artist>> =
        tagRepository.getArtistsByTag(tag, offset, count)

    override fun loadAlbumsWithTag(tag: String, offset: Int, count: Int): Single<List<Album>> =
        tagRepository.getAlbumsByTag(tag, offset, count)

    override fun loadPlaylistsWithTag(
        tag: String,
        offset: Int,
        count: Int
    ): Single<List<Playlist>> =
        tagRepository.getPlaylistsByTag(tag, offset, count, TrackRequestType.ALL)
}
