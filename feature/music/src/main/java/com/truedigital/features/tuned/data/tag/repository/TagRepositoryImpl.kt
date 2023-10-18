package com.truedigital.features.tuned.data.tag.repository

import com.truedigital.features.tuned.api.tag.TagMetadataApiInterface
import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.database.repository.MusicRoomRepository
import com.truedigital.features.tuned.data.playlist.model.Playlist
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.track.model.request.TrackRequestType
import io.reactivex.Single

class TagRepositoryImpl(
    private val tagMetadataApi: TagMetadataApiInterface,
    private val musicRoomRepository: MusicRoomRepository
) : TagRepository {

    override fun getTagByName(name: String) =
        tagMetadataApi.getTagByName(name)

    override fun getTags(names: String) =
        tagMetadataApi.getMultipleByName(names)

    override fun getPlaylistsByTag(
        tag: String,
        offset: Int,
        count: Int,
        type: TrackRequestType
    ): Single<List<Playlist>> =
        tagMetadataApi.getPlaylistsByTag(tag, offset, count, type.value)
            .map { it.results }
            .flatMap { playlists ->
                musicRoomRepository.insertPlaylists(playlists).map { playlists }
            }

    override fun getArtistsByTag(tag: String, offset: Int, count: Int): Single<List<Artist>> =
        tagMetadataApi.getArtistByTag(tag, offset, count)
            .map { it.results }
            .flatMap { artists ->
                musicRoomRepository.insertArtists(artists).map { artists }
            }

    override fun getAlbumsByTag(tag: String, offset: Int, count: Int): Single<List<Album>> {
        return tagMetadataApi.getAlbumsByTag(tag, offset, count)
            .map {
                it.results
            }
            .flatMap { albums ->
                musicRoomRepository.insertAlbums(albums).map { albums }
            }
    }

    override fun getStationsByTag(tag: String, offset: Int, count: Int): Single<List<Station>> =
        tagMetadataApi.getStationsByTag(tag, offset, count)
            .map { it.results }
            .flatMap { stations ->
                musicRoomRepository.insertStations(stations).map { stations }
            }

    override fun getAlbumsByTagGroup(
        tagsString: String,
        tagTypeString: String,
        offset: Int,
        count: Int
    ): Single<List<Album>> {
        return tagMetadataApi.getAlbumsByTagGroup(tagsString, tagTypeString, offset, count)
            .map {
                it.results
            }
            .flatMap { albums ->
                musicRoomRepository.insertAlbums(albums).map { albums }
            }
    }
}
