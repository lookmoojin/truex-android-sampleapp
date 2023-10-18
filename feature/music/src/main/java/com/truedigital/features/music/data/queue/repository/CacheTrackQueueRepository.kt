package com.truedigital.features.music.data.queue.repository

import com.truedigital.features.music.data.landing.model.response.playlisttrack.PlaylistTrackResponse
import com.truedigital.features.music.data.trending.model.response.playlist.Translation
import com.truedigital.features.tuned.data.artist.model.ArtistInfo
import com.truedigital.features.tuned.data.track.model.Track
import javax.inject.Inject

interface CacheTrackQueueRepository {
    fun saveCacheTrackQueue(playlistId: Int, trackList: List<Track>)
    fun getCacheTrackQueue(playlistId: Int): List<Track>
    fun saveCachePlaylistTrackQueue(
        playlistId: Int,
        playlistTrackList: List<PlaylistTrackResponse.Result>
    )

    fun getCachePlaylistTrackQueue(playlistId: Int): List<Track>
    fun clearCacheTrackQueue()
}

class CacheTrackQueueRepositoryImpl @Inject constructor() : CacheTrackQueueRepository {

    companion object {
        private val cacheTrackQueue = mutableListOf<HashMap<Int, List<Track>>>()
        private val cachePlaylistTrackQueue = mutableListOf<HashMap<Int, List<Track>>>()
    }

    override fun saveCacheTrackQueue(playlistId: Int, trackList: List<Track>) {
        cacheTrackQueue.save(playlistId, trackList)
    }

    override fun getCacheTrackQueue(playlistId: Int): List<Track> {
        return cacheTrackQueue.query(playlistId)
    }

    override fun saveCachePlaylistTrackQueue(
        playlistId: Int,
        playlistTrackList: List<PlaylistTrackResponse.Result>
    ) {
        val trackList = playlistTrackList.map { result ->
            Track(
                id = result.trackId ?: 0,
                playlistTrackId = result.playlistTrackId ?: 0,
                songId = result.songId ?: 0,
                releaseId = result.releaseId ?: 0,
                artists = result.artist?.map { artist ->
                    ArtistInfo(
                        id = artist.artistId ?: 0,
                        name = artist.name.orEmpty()
                    )
                } ?: listOf(),
                name = result.name.orEmpty(),
                originalCredit = null,
                isExplicit = result.isExplicit ?: false,
                trackNumber = 0,
                trackNumberInVolume = 0,
                volumeNumber = 0,
                releaseArtists = listOf(),
                sample = "",
                isOnCompilation = false,
                releaseName = result.releaseName.orEmpty(),
                allowDownload = false,
                allowStream = true,
                duration = result.duration ?: 0L,
                image = result.image.orEmpty(),
                hasLyrics = result.hasLyrics ?: false,
                video = null,
                isVideo = result.isVideo ?: false,
                translationsList = result.translations.map { translations ->
                    Translation(
                        language = translations.language,
                        value = translations.value
                    )
                },
                owner = result.owner,
                label = result.label,
                contentLanguage = result.contentLanguage,
                genreList = result.genreList,
                vote = null,
                isDownloaded = false,
                syncProgress = 0F,
                isCached = false
            )
        }.map { track ->
            track.copy(name = track.nameTranslations)
        }
        cachePlaylistTrackQueue.save(playlistId, trackList)
    }

    override fun getCachePlaylistTrackQueue(playlistId: Int): List<Track> {
        return cachePlaylistTrackQueue.query(playlistId)
    }

    override fun clearCacheTrackQueue() {
        cacheTrackQueue.clear()
        cachePlaylistTrackQueue.clear()
    }

    private fun MutableList<HashMap<Int, List<Track>>>.save(
        playlistId: Int,
        trackList: List<Track>
    ) {
        this.find { hashMap ->
            hashMap.containsKey(playlistId)
        }?.let { hashMap ->
            hashMap[playlistId] = trackList
        } ?: run {
            add(hashMapOf(playlistId to trackList))
        }
    }

    private fun MutableList<HashMap<Int, List<Track>>>.query(
        playlistId: Int
    ): List<Track> {
        return this.find { hashMap ->
            hashMap.containsKey(playlistId)
        }?.let { hashMap ->
            hashMap[playlistId] ?: emptyList()
        } ?: run {
            emptyList()
        }
    }
}
