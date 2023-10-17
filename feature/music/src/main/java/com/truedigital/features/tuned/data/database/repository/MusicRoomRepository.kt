package com.truedigital.features.tuned.data.database.repository

import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.database.dao.AlbumDao
import com.truedigital.features.tuned.data.database.dao.ArtistDao
import com.truedigital.features.tuned.data.database.dao.PlayDao
import com.truedigital.features.tuned.data.database.dao.PlaybackStateDao
import com.truedigital.features.tuned.data.database.dao.PlaylistDao
import com.truedigital.features.tuned.data.database.dao.SkipDao
import com.truedigital.features.tuned.data.database.dao.StationDao
import com.truedigital.features.tuned.data.database.dao.TrackHistoryDao
import com.truedigital.features.tuned.data.database.entity.PlayEntity
import com.truedigital.features.tuned.data.database.entity.SkipEntity
import com.truedigital.features.tuned.data.database.entity.TrackHistoryEntity
import com.truedigital.features.tuned.data.playlist.model.Playlist
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.station.model.request.OfflinePlaybackState
import com.truedigital.features.tuned.data.station.model.request.PlaybackState
import io.reactivex.Single
import javax.inject.Inject

interface MusicRoomRepository {
    fun getAlbum(albumId: Int): Single<Album>
    fun insertAlbum(album: Album): Single<Long>
    fun insertAlbums(albums: List<Album>): Single<List<Long>>

    fun getArtist(artistId: Int): Single<Artist>
    fun insertArtist(artist: Artist): Single<Long>
    fun insertArtists(artists: List<Artist>): Single<List<Long>>

    fun getPlaybackStates(): Single<List<OfflinePlaybackState>>
    fun insertPlaybackState(playbackState: PlaybackState, timestamp: Long): Single<Long>
    fun deleteAllPlaybackStates(): Single<Int>

    fun getPlays(): Single<List<Long>>
    fun insertPlay(timestamp: Long): Single<Long>
    fun deleteExpirePlays(expireTimestamp: Long): Single<Int>

    fun getPlaylist(playlistId: Int): Single<Playlist>
    fun insertPlaylist(playlist: Playlist): Single<Long>
    fun insertPlaylists(playlists: List<Playlist>): Single<List<Long>>

    fun getSkips(): Single<List<SkipEntity>>
    fun insertSkip(timestamp: Long): Single<Long>
    fun deleteExpireSkips(expireTimestamp: Long): Single<Int>

    fun getStation(stationId: Int): Single<Station>
    fun insertStation(station: Station): Single<Long>
    fun insertStations(stations: List<Station>): Single<List<Long>>

    fun getTrackHistories(): Single<List<TrackHistoryEntity>>
    fun insertTrackHistory(trackId: Int, lastPlayedDate: Long): Single<Long>

    suspend fun deleteData()
}

class MusicRoomRepositoryImpl @Inject constructor(
    private val albumDao: AlbumDao,
    private val artistDao: ArtistDao,
    private val playbackStateDao: PlaybackStateDao,
    private val playDao: PlayDao,
    private val playlistDao: PlaylistDao,
    private val skipDao: SkipDao,
    private val stationDao: StationDao,
    private val trackHistoryDao: TrackHistoryDao
) : MusicRoomRepository {

    override fun getAlbum(albumId: Int): Single<Album> {
        return albumDao.getAlbum(albumId).map { it.toAlbum() }
    }

    override fun insertAlbum(album: Album): Single<Long> {
        return albumDao.insertAlbum(album.toAlbumEntity())
    }

    override fun insertAlbums(albums: List<Album>): Single<List<Long>> {
        val albumEntityList = albums.map { it.toAlbumEntity() }
        return albumDao.insertAlbums(albumEntityList)
    }

    override fun getArtist(artistId: Int): Single<Artist> {
        return artistDao.getArtist(artistId).map { it.toArtist() }
    }

    override fun insertArtist(artist: Artist): Single<Long> {
        return artistDao.insertArtist(artist.toArtistEntity())
    }

    override fun insertArtists(artists: List<Artist>): Single<List<Long>> {
        val artistEntityList = artists.map { it.toArtistEntity() }
        return artistDao.insertArtists(artistEntityList)
    }

    override fun getPlaybackStates(): Single<List<OfflinePlaybackState>> {
        return playbackStateDao.getPlaybackStates().map { playbackStateEntityList ->
            playbackStateEntityList.map { it.toOfflinePlaybackState() }
        }
    }

    override fun insertPlaybackState(playbackState: PlaybackState, timestamp: Long): Single<Long> {
        return playbackStateDao.insertPlaybackState(playbackState.toPlaybackStateEntity(timestamp))
    }

    override fun deleteAllPlaybackStates(): Single<Int> {
        return playbackStateDao.deleteAllPlaybackStates()
    }

    override fun getPlays(): Single<List<Long>> {
        return playDao.getPlays().map { playlistEntityList ->
            playlistEntityList.map { it.timestamp }
        }
    }

    override fun insertPlay(timestamp: Long): Single<Long> {
        return playDao.insertPlay(PlayEntity(timestamp))
    }

    override fun deleteExpirePlays(expireTimestamp: Long): Single<Int> {
        return playDao.deleteExpirePlays(expireTimestamp)
    }

    override fun getPlaylist(playlistId: Int): Single<Playlist> {
        return playlistDao.getPlaylist(playlistId).map { it.toPlaylist() }
    }

    override fun insertPlaylist(playlist: Playlist): Single<Long> {
        return playlistDao.insertPlaylist(playlist.toPlaylistEntity())
    }

    override fun insertPlaylists(playlists: List<Playlist>): Single<List<Long>> {
        val playlistEntityList = playlists.map { it.toPlaylistEntity() }
        return playlistDao.insertPlaylists(playlistEntityList)
    }

    override fun getSkips(): Single<List<SkipEntity>> {
        return skipDao.getSkips()
    }

    override fun insertSkip(timestamp: Long): Single<Long> {
        return skipDao.insertSkip(SkipEntity(timestamp))
    }

    override fun deleteExpireSkips(expireTimestamp: Long): Single<Int> {
        return skipDao.deleteExpireSkips(expireTimestamp)
    }

    override fun getStation(stationId: Int): Single<Station> {
        return stationDao.getStation(stationId).map { it.toStation() }
    }

    override fun insertStation(station: Station): Single<Long> {
        return stationDao.insertStation(station.toStationEntity())
    }

    override fun insertStations(stations: List<Station>): Single<List<Long>> {
        val stationEntityList = stations.map { it.toStationEntity() }
        return stationDao.insertStations(stationEntityList)
    }

    override fun getTrackHistories(): Single<List<TrackHistoryEntity>> {
        return trackHistoryDao.getTrackHistories()
    }

    override fun insertTrackHistory(trackId: Int, lastPlayedDate: Long): Single<Long> {
        return trackHistoryDao.insertTrackHistory(TrackHistoryEntity(trackId, lastPlayedDate))
    }

    override suspend fun deleteData() {
        playDao.deleteAllPlays()
        skipDao.deleteAllSkips()
        trackHistoryDao.deleteAllTrackHistories()
    }
}
