package com.truedigital.features.tuned.domain.facade

import com.truedigital.common.share.analytics.measurement.AnalyticManager
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant
import com.truedigital.core.extensions.ifIsNullOrEmpty
import com.truedigital.features.tuned.common.extensions.getDeviceId
import com.truedigital.features.tuned.data.ad.model.AdProvider
import com.truedigital.features.tuned.data.ad.model.AdSourceType
import com.truedigital.features.tuned.data.ad.repository.AdRepository
import com.truedigital.features.tuned.data.authentication.repository.AuthenticationTokenRepository
import com.truedigital.features.tuned.data.cache.repository.CacheRepository
import com.truedigital.features.tuned.data.database.repository.MusicRoomRepository
import com.truedigital.features.tuned.data.device.repository.DeviceRepository
import com.truedigital.features.tuned.data.player.PlayerSource
import com.truedigital.features.tuned.data.setting.repository.SettingRepository
import com.truedigital.features.tuned.data.station.model.Rating
import com.truedigital.features.tuned.data.station.model.Stakkar
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.station.model.TrackExtras
import com.truedigital.features.tuned.data.station.model.Vote
import com.truedigital.features.tuned.data.station.model.request.PlaybackState
import com.truedigital.features.tuned.data.station.repository.StationRepository
import com.truedigital.features.tuned.data.stream.repository.StreamRepository
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.track.repository.TrackRepository
import com.truedigital.features.tuned.data.user.model.User
import com.truedigital.features.tuned.data.user.repository.MusicUserRepository
import com.truedigital.features.tuned.service.music.facade.MusicPlayerFacade
import com.truedigital.foundation.player.model.MediaAsset
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

class MusicPlayerFacadeImpl @Inject constructor(
    private val analyticManager: AnalyticManager,
    private val streamRepository: StreamRepository,
    private val authenticationTokenRepository: AuthenticationTokenRepository,
    private val stationRepository: StationRepository,
    private val musicUserRepository: MusicUserRepository,
    private val deviceRepository: DeviceRepository,
    private val trackRepository: TrackRepository,
    private val cacheRepository: CacheRepository,
    private val settingRepository: SettingRepository,
    private val adRepository: AdRepository,
    private val musicRoomRepository: MusicRoomRepository
) : MusicPlayerFacade {

    companion object {
        const val SKIP_RESTORE_TIME = 60 * 60 * 1000L
        const val PLAY_RESTORE_TIME = 30 * 24 * 60 * 60 * 1000L
        const val PROGRESS_FIRST = 10L
        const val PROGRESS_SECOND = 30L
        private const val MILL_SEC_1000 = 1000
        private const val LONG_20L = 20L
    }

    private var hasSentStartUpdate = false
    private var hasSentTenSecondUpdate = false
    private var hasSentThirtySecondUpdate = false
    private var hasCalledTrackExtra = false

    override fun loadStakkar(stakkarId: Int) =
        stationRepository.getStakkar(stakkarId)

    override fun loadTrack(trackId: Int) = trackRepository.get(trackId)

    override fun enqueueStation(station: Station, trackHash: String?): Single<List<Track>> =
        stationRepository.getTracks(station.id, trackHash)

    override fun streamTrack(track: Track): Single<MediaAsset> {
        val location = cacheRepository.getTrackLocationIfExist(track.id)
        val sessionId = UUID.randomUUID().toString()
        return if (location != null) {
            Single.just(MediaAsset(track.id, location, null, sessionId))
        } else {
            // if streaming a new track, make sure we clean the old tracks out if reaching the space limit
            musicRoomRepository.getTrackHistories()
                .subscribeOn(Schedulers.io())
                .doOnSuccess {
                    cacheRepository.pruneCache(it)
                }.flatMap {
                    musicUserRepository.get().flatMap {
                        streamRepository.get(getUniqueId(it), track.id, sessionId)
                    }
                }.map {
                    // streaming track should be cached to a temp cache location
                    it.cachePath = cacheRepository.getTrackFileLocation(it.id, true)
                    it
                }
        }
    }

    override fun streamStakkar(stakkar: Stakkar): Single<MediaAsset> =
        musicUserRepository.get().flatMap {
            streamRepository.get(stakkar.id)
        }

    override fun enqueueTrackExtras(
        source: PlayerSource,
        nextTrack: Track?,
        previousTrack: Track?,
        includeAds: Boolean
    ): Single<TrackExtras> {
        val adProvider = musicUserRepository.getSettings()?.adProvider ?: AdProvider.NONE
        val adSourceType = when {
            source.sourceStation != null -> AdSourceType.STATION
            source.sourceAlbum != null -> AdSourceType.ALBUM
            source.sourcePlaylist != null -> AdSourceType.PLAYLIST
            else -> AdSourceType.NONE
        }
        return if (adSourceType != AdSourceType.STATION) {
            when {
                includeAds && adProvider == AdProvider.TRITON ->
                    musicUserRepository.get()
                        .flatMap { user ->
                            if (user.hasActiveSub) {
                                Single.just(TrackExtras(listOf(), listOf()))
                            } else {
                                deviceRepository.getLSID()
                                    .flatMap { lsid ->
                                        adRepository.getTritonAds(
                                            lsid,
                                            adSourceType,
                                            source.sourceId
                                        ).map { ad ->
                                            TrackExtras(listOf(), listOf(ad))
                                        }
                                    }
                            }
                        }

                else ->
                    Single.just(TrackExtras(listOf(), listOf()))
            }
        } else {
            musicUserRepository.get()
                .flatMap { user -> deviceRepository.getLSID().map { Pair(it, user) } }
                .flatMap {
                    stationRepository.getTrackExtras(
                        getUniqueId(it.second),
                        source.sourceId,
                        nextTrack?.id,
                        previousTrack?.id,
                        includeAds && !it.second.hasActiveSub,
                        adProvider,
                        it.first
                    )
                }
        }
    }

    override fun checkAdCount(): Boolean =
        settingRepository.getAdCounter() >= (musicUserRepository.getSettings()?.tracksPerAd ?: 0)

    override fun resetAdCount() = settingRepository.resetAdCounter()

    override fun addTrackHistory(track: Track): Single<Long> {
        return musicRoomRepository.insertTrackHistory(track.id, System.currentTimeMillis())
    }

    override fun getTotalSkips(): Single<Int> {
        return musicRoomRepository.getSkips()
            .subscribeOn(Schedulers.io())
            .map {
                (musicUserRepository.getSettings()?.maxSkipsPerHour ?: 0) - it.size
            }
            .flatMap { remainingSkips ->
                musicRoomRepository.deleteExpireSkips(System.currentTimeMillis() - SKIP_RESTORE_TIME)
                    .map { remainingSkips + it }
            }
    }

    override fun checkLocalSkipCount(): Single<Int> {
        return musicRoomRepository.deleteExpireSkips(expireTimestamp = System.currentTimeMillis() - SKIP_RESTORE_TIME)
    }

    override fun addLocalSkip(): Single<Long> {
        return musicRoomRepository.insertSkip(timestamp = System.currentTimeMillis())
    }

    override fun getTotalPlays(): Single<Int> {
        return musicRoomRepository.getPlays()
            .subscribeOn(Schedulers.io())
            .map {
                (musicUserRepository.getSettings()?.monthlyPlayLimit ?: 0) - it.size
            }.flatMap { remainingPlays ->
                musicRoomRepository.deleteExpirePlays(
                    System.currentTimeMillis() - PLAY_RESTORE_TIME
                ).map {
                    remainingPlays + it
                }
            }.onErrorReturn {
                1
            }
    }

    override fun addLocalPlay(): Single<Long> {
        return musicRoomRepository.insertPlay(System.currentTimeMillis())
    }

    override fun resetPlayTrackLog() {
        hasSentStartUpdate = false
        hasSentTenSecondUpdate = false
        hasSentThirtySecondUpdate = false
        hasCalledTrackExtra = false
    }

    override fun logTrackSkip(
        source: PlayerSource,
        track: Track,
        elapsed: Long,
        sessionId: String?
    ): Single<Any> {
        val elapsedSeconds = elapsed / MILL_SEC_1000

        // a safety check that skip point should not excceed total duration
        if (elapsedSeconds > track.duration) {
            return Single.just(Any())
        }

        return musicUserRepository.get().flatMap {
            logPlaybackState(
                getUniqueId(it),
                source,
                track.id,
                sessionId,
                StationRepository.PlaybackAction.SKIP,
                elapsedSeconds
            )
        }
    }

    override fun logTrackFinish(
        source: PlayerSource,
        track: Track,
        elapsed: Long,
        sessionId: String?
    ): Single<Any> {
        return musicUserRepository.get().flatMap {
            logPlaybackState(
                getUniqueId(it),
                source,
                track.id,
                sessionId,
                StationRepository.PlaybackAction.END,
                track.duration
            )
        }
    }

    // return true if we can getTrackExtra before current track finishes
    override fun logTrackPlay(
        source: PlayerSource,
        track: Track,
        elapsed: Long,
        sessionId: String?
    ): Single<Any> =
        musicUserRepository.get().flatMap {
            val elapsedSeconds = elapsed / MILL_SEC_1000
            if (elapsedSeconds > 0L && !hasSentStartUpdate) {
                Timber.d("--------> Log play start")
                hasSentStartUpdate = true
                trackFirebase(source, track)
                logPlaybackState(
                    getUniqueId(it),
                    source,
                    track.id,
                    sessionId,
                    StationRepository.PlaybackAction.START
                ).flatMap {
                    addTrackHistory(track)
                }
            } else if (elapsedSeconds >= PROGRESS_FIRST && !hasSentTenSecondUpdate) {
                hasSentTenSecondUpdate = true
                logPlaybackState(
                    getUniqueId(it),
                    source,
                    track.id,
                    sessionId,
                    StationRepository.PlaybackAction.PROGRESS,
                    PROGRESS_FIRST
                )
            } else if (elapsedSeconds >= PROGRESS_SECOND && !hasSentThirtySecondUpdate) {
                hasSentThirtySecondUpdate = true
                settingRepository.addAdCounter()
                logPlaybackState(
                    getUniqueId(it),
                    source,
                    track.id,
                    sessionId,
                    StationRepository.PlaybackAction.PROGRESS,
                    PROGRESS_SECOND
                )
                    .flatMap { addLocalPlay() }
            } else if ((track.duration - elapsedSeconds) <= LONG_20L && !hasCalledTrackExtra) {
                hasCalledTrackExtra = true
                Single.just(true)
            } else {
                Single.just(Any())
            }
        }

    private fun trackFirebase(source: PlayerSource, track: Track) {
        val playlistName = source.sourcePlaylist?.let { playlist ->
            playlist.name[0].value ?: ""
        }
        val hasMapEvent = HashMap<String, Any>().apply {
            put(MeasurementConstant.Key.KEY_EVENT_NAME, MeasurementConstant.Event.EVENT_MUSIC_PLAY)
            put(MeasurementConstant.Key.KEY_CONTENT_TYPE, "music")
            put(MeasurementConstant.Key.KEY_CMS_ID, track.songId.toString())
            put(MeasurementConstant.Music.Key.MEASUREMENT_MUSIC_TITLE, track.name)
            put(MeasurementConstant.Music.Key.MEASUREMENT_MUSIC_ARTIST, track.artistString)
            put(MeasurementConstant.Music.Key.MEASUREMENT_MUSIC_ALBUM, track.releaseName)
            if (playlistName != null) {
                put(MeasurementConstant.Music.Key.MEASUREMENT_MUSIC_PLAYLIST, playlistName)
            }
            put(
                MeasurementConstant.Music.Key.MEASUREMENT_MUSIC_OWNER,
                track.owner.ifIsNullOrEmpty { " " }
            )
            put(
                MeasurementConstant.Music.Key.MEASUREMENT_MUSIC_LABEL,
                track.label.ifIsNullOrEmpty { " " }
            )
            put(MeasurementConstant.Music.Key.MEASUREMENT_MUSIC_GENRE, track.genreString)
            put(
                MeasurementConstant.Music.Key.MEASUREMENT_MUSIC_CONTENT_LANGUAGE,
                track.contentLanguage.ifIsNullOrEmpty { " " }
            )
        }
        Timber.w("event $hasMapEvent")
        analyticManager.trackEvent(hasMapEvent)
    }

    private fun logPlaybackState(
        uniqueId: Int,
        source: PlayerSource,
        trackId: Int,
        sessionId: String?,
        action: StationRepository.PlaybackAction,
        elapsedSeconds: Long = 0
    ): Single<Any> {
        val fileSource = if (cacheRepository.isDownloaded(trackId)) "Download" else "Stream"
        val playerSource = when {
            source.sourceStation != null -> "Station"
            source.sourceArtist != null -> "Artist"
            source.sourceAlbum != null -> "Album"
            source.sourcePlaylist != null -> "Playlist"
            else -> "Queue"
        }
        val playbackState = PlaybackState(
            trackId,
            action.action,
            fileSource,
            elapsedSeconds,
            playerSource,
            source.sourceId,
            sessionId
        )

        // if app online, try send log, if fails add to db
        // after sending any log play, also check if we have offline log and send them as well
        return streamRepository.putPlaybackState(uniqueId, playbackState)
            .flatMap {
                musicRoomRepository.getPlaybackStates()
                    .flatMap { offlinePlaybackStates ->
                        if (offlinePlaybackStates.isNotEmpty()) {
                            musicUserRepository.get()
                                .subscribeOn(Schedulers.io())
                                .flatMap {
                                    streamRepository.putOfflinePlaybackState(
                                        it.devices.getDeviceId(deviceRepository.getUniqueId()) ?: 0,
                                        offlinePlaybackStates
                                    )
                                }
                                .flatMap {
                                    musicRoomRepository.deleteAllPlaybackStates()
                                }
                        } else {
                            Single.just(Any())
                        }
                    }.onErrorResumeNext {
                        Single.just(Any())
                    }
            }
            .onErrorResumeNext {
                musicRoomRepository.insertPlaybackState(
                    playbackState,
                    System.currentTimeMillis()
                )
            }
    }

    override fun likeTrack(station: Station, track: Track) =
        stationRepository.putVote(station.id, track.id, Rating.LIKED)

    override fun dislikeTrack(station: Station, track: Track) =
        stationRepository.putVote(station.id, track.id, Rating.DISLIKED)

    override fun removeRating(station: Station, track: Track): Single<Vote> =
        stationRepository.deleteVote(station.id, track.id)
            .map { it.first().vote }

    private fun getUniqueId(user: User): Int {
        return user.devices.getDeviceId(deviceRepository.getUniqueId()) ?: 0
    }

    override fun setShufflePlay(enabled: Boolean) {
        settingRepository.setShufflePlay(enabled)
    }

    override fun isShufflePlayEnabled(): Boolean = settingRepository.isShufflePlayEnabled()

    override fun setRepeatMode(mode: Int) {
        settingRepository.setRepeatMode(mode)
    }

    override fun getRepeatMode(): Int = settingRepository.getRepeatMode()

    override fun isStreamingEnabled(): Boolean =
        settingRepository.allowMobileDataStreaming() || deviceRepository.isWifiConnected()

    override fun hasTrackPlayRight(): Boolean =
        authenticationTokenRepository.getCurrentToken()?.hasTrackPlayRight
            ?: false

    override fun hasSequentialPlaybackRight(): Boolean =
        authenticationTokenRepository.getCurrentToken()?.hasSequentialPlaybackRight ?: false

    override fun isPlayLimitEnabled(): Boolean =
        musicUserRepository.getSettings()?.limitPlays ?: true

    override fun isSkipLimitEnabled() = musicUserRepository.getSettings()?.limitSkips ?: true

    override fun getLikesCount(): Int = deviceRepository.getLikesCount()
}
