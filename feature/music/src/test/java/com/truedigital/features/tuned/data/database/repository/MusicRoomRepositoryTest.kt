package com.truedigital.features.tuned.data.database.repository

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
import com.truedigital.features.utils.MockDataModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import io.reactivex.Single
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class MusicRoomRepositoryTest {

    @MockK
    lateinit var albumDao: AlbumDao

    @MockK
    lateinit var artistDao: ArtistDao

    @MockK
    lateinit var playbackStateDao: PlaybackStateDao

    @MockK
    lateinit var playDao: PlayDao

    @MockK
    lateinit var playlistDao: PlaylistDao

    @MockK
    lateinit var skipDao: SkipDao

    @MockK
    lateinit var stationDao: StationDao

    @MockK
    lateinit var trackHistoryDao: TrackHistoryDao

    private lateinit var musicRoomRepository: MusicRoomRepository

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        musicRoomRepository = MusicRoomRepositoryImpl(
            albumDao = albumDao,
            artistDao = artistDao,
            playbackStateDao = playbackStateDao,
            playDao = playDao,
            playlistDao = playlistDao,
            skipDao = skipDao,
            stationDao = stationDao,
            trackHistoryDao = trackHistoryDao
        )
    }

    @Test
    fun getAlbum_returnAlbumFromRoom() {
        // Given
        every { albumDao.getAlbum(any()) } returns Single.just(MockDataModel.mockAlbum.toAlbumEntity())

        // When
        val result = musicRoomRepository.getAlbum(1)

        // Then
        result.test().assertNoErrors().assertValue {
            it == MockDataModel.mockAlbum
        }
        verify(exactly = 1) { albumDao.getAlbum(any()) }
    }

    @Test
    fun insertAlbum_albumIsSavedIntoRoom() {
        // Given
        val mockInsertId = 1L
        every { albumDao.insertAlbum(any()) } returns Single.just(mockInsertId)

        // When
        val result = musicRoomRepository.insertAlbum(MockDataModel.mockAlbum)

        // Then
        result.test().assertNoErrors().assertValue {
            it == mockInsertId
        }
        verify(exactly = 1) { albumDao.insertAlbum(any()) }
    }

    @Test
    fun insertAlbums_albumsAreSavedIntoRoom() {
        // Given
        val mockInsertId = 1L
        every { albumDao.insertAlbums(any()) } returns Single.just(listOf(mockInsertId))

        // When
        val result = musicRoomRepository.insertAlbums(listOf(MockDataModel.mockAlbum))

        // Then
        result.test().assertNoErrors().assertValue {
            it.first() == mockInsertId
        }
        verify(exactly = 1) { albumDao.insertAlbums(any()) }
    }

    @Test
    fun getArtist_returnArtistFromRoom() {
        // Given
        every { artistDao.getArtist(any()) } returns Single.just(MockDataModel.mockArtist.toArtistEntity())

        // When
        val result = musicRoomRepository.getArtist(1)

        // Then
        result.test().assertNoErrors().assertValue {
            it == MockDataModel.mockArtist
        }
        verify(exactly = 1) { artistDao.getArtist(any()) }
    }

    @Test
    fun insertArtist_artistIsSavedIntoRoom() {
        // Given
        val mockInsertId = 1L
        every { artistDao.insertArtist(any()) } returns Single.just(mockInsertId)

        // When
        val result = musicRoomRepository.insertArtist(MockDataModel.mockArtist)

        // Then
        result.test().assertNoErrors().assertValue {
            it == mockInsertId
        }
        verify(exactly = 1) { artistDao.insertArtist(any()) }
    }

    @Test
    fun insertArtists_artistsAreSavedIntoRoom() {
        // Given
        val mockInsertId = 1L
        every { artistDao.insertArtists(any()) } returns Single.just(listOf(mockInsertId))

        // When
        val result = musicRoomRepository.insertArtists(listOf(MockDataModel.mockArtist))

        // Then
        result.test().assertNoErrors().assertValue {
            it.first() == mockInsertId
        }
        verify(exactly = 1) { artistDao.insertArtists(any()) }
    }

    @Test
    fun getPlaybackStates_returnPlaybackStatesFromRoom() {
        // Given
        val mockTimestamp = 1000L
        every { playbackStateDao.getPlaybackStates() } returns Single.just(
            listOf(MockDataModel.mockPlaybackState.toPlaybackStateEntity(mockTimestamp))
        )

        // When
        val result = musicRoomRepository.getPlaybackStates()

        // Then
        result.test().assertNoErrors().assertValue {
            val offlinePlaybackStateResult = it.first()
            offlinePlaybackStateResult.playbackState == MockDataModel.mockPlaybackState &&
                offlinePlaybackStateResult.date.time == mockTimestamp
        }
        verify(exactly = 1) { playbackStateDao.getPlaybackStates() }
    }

    @Test
    fun insertPlaybackState_playbackStateIsSavedIntoRoom() {
        // Given
        val mockInsertId = 1L
        every { playbackStateDao.insertPlaybackState(any()) } returns Single.just(mockInsertId)

        // When
        val result = musicRoomRepository.insertPlaybackState(MockDataModel.mockPlaybackState, 100L)

        // Then
        result.test().assertNoErrors().assertValue {
            it == mockInsertId
        }
        verify(exactly = 1) { playbackStateDao.insertPlaybackState(any()) }
    }

    @Test
    fun deletePlaybackState_playbackStateIsDeleted() {
        // Given
        val mockDeleteId = 1
        every { playbackStateDao.deleteAllPlaybackStates() } returns Single.just(mockDeleteId)

        // When
        val result = musicRoomRepository.deleteAllPlaybackStates()

        // Then
        result.test().assertNoErrors().assertValue {
            it == mockDeleteId
        }
        verify(exactly = 1) { playbackStateDao.deleteAllPlaybackStates() }
    }

    @Test
    fun getPlays_returnPlaysFromRoom() {
        // Given
        val mockTimestamp = 2000L
        every { playDao.getPlays() } returns Single.just(listOf(PlayEntity(mockTimestamp)))

        // When
        val result = musicRoomRepository.getPlays()

        // Then
        result.test().assertNoErrors().assertValue {
            it.first() == mockTimestamp
        }
        verify(exactly = 1) { playDao.getPlays() }
    }

    @Test
    fun insertPlay_playIsSavedIntoRoom() {
        // Given
        val mockInsertId = 1L
        every { playDao.insertPlay(any()) } returns Single.just(mockInsertId)

        // When
        val result = musicRoomRepository.insertPlay(1000L)

        // Then
        result.test().assertNoErrors().assertValue {
            it == mockInsertId
        }
        verify(exactly = 1) { playDao.insertPlay(any()) }
    }

    @Test
    fun deleteExpirePlays_expirePlaysAreDeleted() {
        // Given
        val mockDeleteCount = 1
        every { playDao.deleteExpirePlays(any()) } returns Single.just(mockDeleteCount)

        // When
        val result = musicRoomRepository.deleteExpirePlays(1000L)

        // Then
        result.test().assertNoErrors().assertValue {
            it == mockDeleteCount
        }
        verify(exactly = 1) { playDao.deleteExpirePlays(any()) }
    }

    @Test
    fun getPlaylist_returnPlaylistFromRoom() {
        // Given
        every { playlistDao.getPlaylist(any()) } returns Single.just(MockDataModel.mockPlaylist.toPlaylistEntity())

        // When
        val result = musicRoomRepository.getPlaylist(1)

        // Then
        result.test().assertNoErrors().assertValue {
            it == MockDataModel.mockPlaylist
        }
        verify(exactly = 1) { playlistDao.getPlaylist(any()) }
    }

    @Test
    fun insertPlaylist_playlistIsSavedIntoRoom() {
        // Given
        val mockInsertId = 1L
        every { playlistDao.insertPlaylist(any()) } returns Single.just(mockInsertId)

        // When
        val result = musicRoomRepository.insertPlaylist(MockDataModel.mockPlaylist)

        // Then
        result.test().assertNoErrors().assertValue {
            it == mockInsertId
        }
        verify(exactly = 1) { playlistDao.insertPlaylist(any()) }
    }

    @Test
    fun insertPlaylists_playlistsIsSavedIntoRoom() {
        // Given
        val mockInsertId = 1L
        every { playlistDao.insertPlaylists(any()) } returns Single.just(listOf(mockInsertId))

        // When
        val result = musicRoomRepository.insertPlaylists(listOf(MockDataModel.mockPlaylist))

        // Then
        result.test().assertNoErrors().assertValue {
            it.first() == mockInsertId
        }
        verify(exactly = 1) { playlistDao.insertPlaylists(any()) }
    }

    @Test
    fun getSkips_returnSkipFromRoom() {
        // Given
        val mockTimestamp = 2000L
        every { skipDao.getSkips() } returns Single.just(listOf(SkipEntity(mockTimestamp)))

        // When
        val result = musicRoomRepository.getSkips()

        // Then
        result.test().assertNoErrors().assertValue {
            it.first().timestamp == mockTimestamp
        }
        verify(exactly = 1) { skipDao.getSkips() }
    }

    @Test
    fun insertSkip_skipIsSavedIntoRoom() {
        // Given
        val mockInsertId = 1L
        every { skipDao.insertSkip(any()) } returns Single.just(mockInsertId)

        // When
        val result = musicRoomRepository.insertSkip(1000L)

        // Then
        result.test().assertNoErrors().assertValue {
            it == mockInsertId
        }
        verify(exactly = 1) { skipDao.insertSkip(any()) }
    }

    @Test
    fun deleteExpireSkips_expireSkipsAreDeleted() {
        // Given
        val mockDeleteCount = 1
        every { skipDao.deleteExpireSkips(any()) } returns Single.just(mockDeleteCount)

        // When
        val result = musicRoomRepository.deleteExpireSkips(1000L)

        // Then
        result.test().assertNoErrors().assertValue {
            it == mockDeleteCount
        }
        verify(exactly = 1) { skipDao.deleteExpireSkips(any()) }
    }

    @Test
    fun getStation_returnStationFromRoom() {
        // Given
        every { stationDao.getStation(any()) } returns Single.just(MockDataModel.mockStation.toStationEntity())

        // When
        val result = musicRoomRepository.getStation(1)

        // Then
        result.test().assertNoErrors().assertValue {
            it == MockDataModel.mockStation
        }
        verify(exactly = 1) { stationDao.getStation(any()) }
    }

    @Test
    fun insertStation_stationIsSavedIntoRoom() {
        // Given
        val mockInsertId = 1L
        every { stationDao.insertStation(any()) } returns Single.just(mockInsertId)

        // When
        val result = musicRoomRepository.insertStation(MockDataModel.mockStation)

        // Then
        result.test().assertNoErrors().assertValue {
            it == mockInsertId
        }
        verify(exactly = 1) { stationDao.insertStation(any()) }
    }

    @Test
    fun insertStations_stationsAreSavedIntoRoom() {
        // Given
        val mockInsertId = 1L
        every { stationDao.insertStations(any()) } returns Single.just(listOf(mockInsertId))

        // When
        val result = musicRoomRepository.insertStations(listOf(MockDataModel.mockStation))

        // Then
        result.test().assertNoErrors().assertValue {
            it.first() == mockInsertId
        }
        verify(exactly = 1) { stationDao.insertStations(any()) }
    }

    @Test
    fun getTrackHistories_returnTrackHistoriesFromRoom() {
        // Given
        val mockTrackHistoryEntity = TrackHistoryEntity(
            trackId = 10,
            lastPlayedDate = 2000L
        )
        every { trackHistoryDao.getTrackHistories() } returns Single.just(
            listOf(
                mockTrackHistoryEntity
            )
        )

        // When
        val result = musicRoomRepository.getTrackHistories()

        // Then
        result.test().assertNoErrors().assertValue {
            it.first() == mockTrackHistoryEntity
        }
        verify(exactly = 1) { trackHistoryDao.getTrackHistories() }
    }

    @Test
    fun insertTrackHistory_trackHistoryIsSavedIntoRoom() {
        // Given
        val mockInsertId = 1L
        every { trackHistoryDao.insertTrackHistory(any()) } returns Single.just(mockInsertId)

        // When
        val result = musicRoomRepository.insertTrackHistory(1, 2000L)

        // Then
        result.test().assertNoErrors().assertValue {
            it == mockInsertId
        }
        verify(exactly = 1) { trackHistoryDao.insertTrackHistory(any()) }
    }

    @Test
    fun deleteData_allDataInRoomAreDeleted() = runTest {
        // Given
        coEvery { playDao.deleteAllPlays() } returns 1
        coEvery { skipDao.deleteAllSkips() } returns 1
        coEvery { trackHistoryDao.deleteAllTrackHistories() } returns 1

        // When
        musicRoomRepository.deleteData()

        // Then
        coVerify(exactly = 1) { playDao.deleteAllPlays() }
        coVerify(exactly = 1) { skipDao.deleteAllSkips() }
        coVerify(exactly = 1) { trackHistoryDao.deleteAllTrackHistories() }
    }
}
