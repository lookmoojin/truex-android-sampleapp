package com.truedigital.features.tuned.service.music.controller

import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.VisibleForTesting
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.features.music.domain.landing.model.MusicForYouItemModel
import com.truedigital.features.tuned.common.extensions.cacheOnMainThread
import com.truedigital.features.tuned.common.extensions.emptySubscribe
import com.truedigital.features.tuned.common.extensions.isNotDisposed
import com.truedigital.features.tuned.common.extensions.successSubscribe
import com.truedigital.features.tuned.common.extensions.tunedSubscribe
import com.truedigital.features.tuned.data.ad.model.Ad
import com.truedigital.features.tuned.data.player.PlayerSource
import com.truedigital.features.tuned.data.player.model.MediaType
import com.truedigital.features.tuned.data.station.model.Rating
import com.truedigital.features.tuned.data.station.model.Stakkar
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.station.model.TrackExtras
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.util.LocalisedString
import com.truedigital.features.tuned.injection.module.NetworkModule
import com.truedigital.features.tuned.presentation.common.Presenter
import com.truedigital.features.tuned.service.music.facade.MusicPlayerFacade
import com.truedigital.features.tuned.service.music.model.AdQueue
import com.truedigital.features.tuned.service.music.model.StakkarQueue
import com.truedigital.features.tuned.service.music.model.TrackQueue
import com.truedigital.features.tuned.service.music.model.TrackQueueInfo
import com.truedigital.features.tuned.service.util.PlayQueue
import com.truedigital.foundation.player.model.MediaAsset
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

class MusicPlayerController @Inject constructor(private val musicPlayerFacade: MusicPlayerFacade) :
    Presenter {
    companion object {
        const val ACTION_PLAY = "action_play"
        const val ACTION_PAUSE = "action_pause"
        const val ACTION_SKIP_NEXT = "action_next"
        const val ACTION_SKIP_PREVIOUS = "action_previous"
        const val ACTION_STOP = "action_stop"
        const val ACTION_SEEK = "action_seek"
        const val ACTION_LIKE = "action_like"
        const val ACTION_DISLIKE = "action_dislike"
        const val ACTION_REMOVE_RATING = "action_remove_rating"
        const val ACTION_TOGGLE_SHUFFLE = "action_toggle_shuffle"
        const val ACTION_TOGGLE_REPEAT = "action_toggle_repeat"
        const val ACTION_REPLAY = "action_replay"

        const val METADATA_KEY_TYPE = "type"
        const val METADATA_KEY_CLICK_URI = "click_uri"
        const val METADATA_KEY_HIDE_DIALOG = "hide_dialog"
        const val METADATA_KEY_HAS_LYRICS = "has_lyrics"
        const val METADATA_KEY_SKIP_LIMIT_REACHED = "skip_limit_reached"
        const val METADATA_KEY_IS_VIDEO = "is_video"
        const val METADATA_KEY_IS_EXPLICIT = "is_explicit"
        const val METADATA_KEY_AD_VAST_XML = "ad_vast_xml"
        const val METADATA_KEY_IS_FIRST_TRACK = "is_first_track"
        const val METADATA_KEY_IS_LAST_TRACK = "is_last_track"

        const val SESSION_EVENT_QUEUE_CHANGE = "event_queue_change"
        const val SKIP_TO_PREVIOUS_THRESHOLD = 5000L
        const val FILE_PREFIX = "file://"

        private const val S_TO_MS = 1000
        private const val VOLUME = 0.3f
    }

    private var loadStreamSubscription: Disposable? = null
    private var loadTracksObservable: Single<List<Track>>? = null
    private var loadTracksSubscription: Disposable? = null
    private var loadTrackExtrasObservable: Single<TrackExtras>? = null
    private var loadTrackExtrasSubscription: Disposable? = null

    private var playQueue: TrackQueue? = null
    private var currentSource: PlayerSource? = null
    private var previousSource: PlayerSource? = null
    private var currentMedia: MediaAsset? = null

    private var adQueue: AdQueue? = null
    private var currentStakkar: Stakkar? = null
    private var stakkarQueue: StakkarQueue? = null

    private var hijackedMedia: MediaAsset? = null
    private var enqueuedAsset: MediaAsset? = null

    private var lastKnownPosition: Long = 0L
    private var playing = false

    private var lostFocusWhilstPlaying: Boolean = false
    private var isDucking: Boolean = false
    private var playlistHasNoMoreTracks = false
    private var blockSkipPress = false
    private var playLimitReached = false
    private var availableSkips: Int = 0

    // Enable this flag and related logic will trigger an auto skip to
    // simulate the user skip a track while pre-buffering the next track.
    // private var autoSkipForPreBufferingDebug = false
    private var shouldDoPreBufferImmediately = false
    private var playWhenReady = true

    private val adDateFormatter = SimpleDateFormat("HH:mm:ss.SSSSSS", Locale.getDefault())
    private val languageCodeEN = LocalizationRepository.Localization.EN.languageCode

    private lateinit var player: PlayerSurface

    init {
        adDateFormatter.timeZone = TimeZone.getTimeZone("GMT")
    }

    fun onInject(player: PlayerSurface) {
        this.player = player
    }

    override fun onStop() {
        onStopAudio()
        loadStreamSubscription?.dispose()
        loadTracksSubscription?.dispose()
        loadTrackExtrasSubscription?.dispose()
    }

    // region Action Callback

    fun onPlayAudio() {
        if (currentMedia != null && currentMedia is AdMediaAsset) return

        currentMedia?.let { _currentMedia ->
            if (_currentMedia.location != null &&
                _currentMedia.location?.startsWith(FILE_PREFIX) != true &&
                !musicPlayerFacade.isStreamingEnabled()
            ) {
                player.setStreamingDisabled()
                playing = false
            } else {
                playing = true
                player.resume(_currentMedia)
            }
        } ?: run {
            playNext()
        }
    }

    fun onPauseAudio() {
        if (currentMedia != null && currentMedia is AdMediaAsset) return
        playing = false
        player.pause()
    }

    fun onStopAudio() {
        if (currentMedia != null && currentMedia is AdMediaAsset) return
        playing = false
        player.stop()
    }

    fun onSkipToPrevious() {
        if (currentMedia != null && currentMedia is AdMediaAsset) return

        // when playlimit reached or end of queue, the source will get cleared
        if (currentSource == null) {
            currentSource = previousSource
            previousSource = null
        }

        if (lastKnownPosition >= SKIP_TO_PREVIOUS_THRESHOLD ||
            playQueue?.hasPrevious() != true
        ) {
            lastKnownPosition = 0L
            seekTo(0L)
        } else {
            // change repeat mode when user presses previous button and repeat mode is on repeat song
            if (musicPlayerFacade.getRepeatMode() == PlayQueue.REPEAT_MODE_ONE) {
                setRepeatMode(PlayQueue.REPEAT_MODE_ALL)
            }
            playing = false
            player.pause()
            playQueue?.let { trackQueue ->
                trackQueue.currentIndex = when {
                    musicPlayerFacade.getRepeatMode() == PlayQueue.REPEAT_MODE_ALL &&
                        trackQueue.currentIndex == 0 -> trackQueue.getTrackListSize() - 2

                    trackQueue.currentIndex == 0 -> -1
                    else -> trackQueue.currentIndex - 2
                }
            }
            playNext()
        }
    }

    fun onSkipToNext() {
        if (currentMedia != null && currentMedia is AdMediaAsset) return
        Timber.d("--------> try skip to next track")

        if (blockSkipPress) {
            Timber.d("Skip press blocked")
            return
        }

        // when playlimit reached or end of queue, the source will get cleared
        if (currentSource == null) {
            currentSource = previousSource
            previousSource = null
        }

        if (currentSource != null) {
            val source = currentSource
            val media = currentMedia

            // change repeat mode when user presses skip next button and repeat mode is on repeat song,
            // don't toggle when playing station as station doesn't have repeat functionality
            if (source?.sourceStation == null &&
                musicPlayerFacade.getRepeatMode() == PlayQueue.REPEAT_MODE_ONE
            ) {
                setRepeatMode(PlayQueue.REPEAT_MODE_ALL)
            }

            if (musicPlayerFacade.isSkipLimitEnabled()) {
                if (hijackedMedia != null || availableSkips > 0) {
                    blockSkipPress = true
                    musicPlayerFacade.addLocalSkip()
                        .subscribeOn(Schedulers.io())
                        .doOnSuccess {
                            --availableSkips
                            resumeHijackedTrackOrPlayNext()
                        }
                        .flatMap {
                            if (media != null && media is TrackMediaAsset) {
                                source?.let { playerSource ->
                                    musicPlayerFacade.logTrackSkip(
                                        playerSource,
                                        media.track,
                                        lastKnownPosition,
                                        media.sessionId
                                    )
                                }
                            } else {
                                Single.just(Any())
                            }
                        }
                        .observeOn(AndroidSchedulers.mainThread())
                        .emptySubscribe()
                } else {
                    player.setSkipLimitReached()
                }
            } else {
                if (media != null && media is TrackMediaAsset) {
                    blockSkipPress = true
                    source?.let { playerSource ->
                        musicPlayerFacade.logTrackSkip(
                            playerSource,
                            media.track,
                            lastKnownPosition,
                            media.sessionId
                        )
                            .observeOn(AndroidSchedulers.mainThread())
                            .successSubscribe {
                                resumeHijackedTrackOrPlayNext()
                            }
                    }
                } else {
                    resumeHijackedTrackOrPlayNext()
                }
            }
        } else {
            player.stop()
            player.resetMetaData()
        }
    }

    fun onLike() {
        val track = (currentMedia as? TrackMediaAsset)?.track
        if (track != null && currentSource != null) {
            currentSource?.sourceStation?.let { station ->
                if (!station.isOffline) {
                    musicPlayerFacade.likeTrack(station, track)
                        .observeOn(AndroidSchedulers.mainThread())
                        .successSubscribe {
                            if (currentMedia?.id == track.id) {
                                track.vote = Rating.LIKED
                                setMetadata(track)
                            }
                        }
                }
            }
        }
    }

    fun onDislike() {
        val track = (currentMedia as? TrackMediaAsset)?.track
        if (track != null && currentSource != null) {
            currentSource?.sourceStation?.let { station ->
                if (!station.isOffline) {
                    dislikeTrack(station, track)
                }
            }
        }
    }

    private fun dislikeTrack(station: Station, track: Track) {
        musicPlayerFacade.dislikeTrack(station, (currentMedia as TrackMediaAsset).track)
            .observeOn(AndroidSchedulers.mainThread())
            .successSubscribe {
                track.vote = Rating.DISLIKED
                if ((currentMedia as TrackMediaAsset).track.id == track.id) {
                    setMetadata(track)
                }
                if (availableSkips > 0 || !musicPlayerFacade.isSkipLimitEnabled()) {
                    onSkipToNext()
                }
            }
    }

    fun onRemoveRating() {
        if (currentMedia != null &&
            currentMedia is TrackMediaAsset &&
            isOnlineStation() &&
            currentSource != null
        ) {
            val track = (currentMedia as TrackMediaAsset).track
            currentSource?.sourceStation?.let {
                musicPlayerFacade.removeRating(it, (currentMedia as TrackMediaAsset).track)
                    .observeOn(AndroidSchedulers.mainThread())
                    .successSubscribe {
                        track.vote = null
                        if ((currentMedia as TrackMediaAsset).track.id == track.id) {
                            setMetadata(track)
                        }
                    }
            }
        }
    }

    fun onToggleShuffle(enabled: Boolean? = null) {
        musicPlayerFacade.setShufflePlay(enabled ?: !musicPlayerFacade.isShufflePlayEnabled())
        player.setPlayMode(
            musicPlayerFacade.isShufflePlayEnabled(),
            musicPlayerFacade.getRepeatMode()
        )

        if (musicPlayerFacade.isShufflePlayEnabled()) {
            playQueue?.enableShuffle()
        } else {
            playQueue?.disableShuffle()
        }
        onQueueUpdated()
    }

    fun onToggleRepeat() {
        val newRepeatMode = when (musicPlayerFacade.getRepeatMode()) {
            PlayQueue.REPEAT_MODE_NONE -> PlayQueue.REPEAT_MODE_ALL
            PlayQueue.REPEAT_MODE_ALL -> PlayQueue.REPEAT_MODE_ONE
            PlayQueue.REPEAT_MODE_ONE -> PlayQueue.REPEAT_MODE_NONE
            else -> PlayQueue.REPEAT_MODE_NONE
        }
        setRepeatMode(newRepeatMode)
        onQueueUpdated()
    }

    fun onSeekTo(position: Long) {
        if (currentMedia != null && currentMedia is AdMediaAsset) return
        lastKnownPosition = position
        seekTo(position)
    }

    fun onReplay() {
        if (currentMedia != null && currentMedia is AdMediaAsset) return
        seekTo(0L)
        // when playlimit reached or end of queue, the source will get cleared
        if (currentSource == null) {
            currentSource = previousSource
            previousSource = null
        }
        onPlayAudio()
    }

    fun onPlaybackError(error: Exception) {
        // try to power through
        Timber.e(error)
        playNext()
    }

    fun onPlaybackTick(position: Long) {
        if (currentMedia != null && currentMedia is TrackMediaAsset) {
            currentSource?.let {
                musicPlayerFacade.logTrackPlay(
                    it,
                    (currentMedia as TrackMediaAsset).track,
                    position,
                    (currentMedia as TrackMediaAsset).sessionId
                )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .successSubscribe { isTrack ->
                        if (isTrack is Boolean) {
                            Timber.d("--------> preload trackextra")
                            loadTrackExtrasSubscription?.dispose()
                            loadTrackExtrasSubscription = getEnqueueTrackExtrasObservable()
                                ?.observeOn(AndroidSchedulers.mainThread())
                                ?.tunedSubscribe(
                                    {
                                        loadTrackExtrasObservable = null
                                    },
                                    {
                                        loadTrackExtrasObservable = null
                                    }
                                )
                        }
                    }
            }
        }

        player.setPlaybackPosition(position)
        lastKnownPosition = position

        // to stop automatically playing a song when u remove the current paused song in queue
        if (!playWhenReady) {
            playing = false
            player.pause()
            playWhenReady = true
        }
    }

    fun onPlaybackEnded() {
        if (currentSource != null &&
            currentMedia != null &&
            currentMedia is TrackMediaAsset
        ) {
            musicPlayerFacade.logTrackFinish(
                currentSource!!,
                (currentMedia as TrackMediaAsset).track,
                lastKnownPosition,
                (currentMedia as TrackMediaAsset).sessionId
            )
                .observeOn(AndroidSchedulers.mainThread())
                .emptySubscribe()
        }

        if (currentSource != null) {
            resumeHijackedTrackOrPlayNext()
        } else {
            player.resetMetaData()
        }
    }

    fun onLostAudioFocus(canDuck: Boolean = false) {
        if (canDuck) {
            isDucking = true
            player.setVolume(VOLUME)
        } else {
            if (playing) {
                lostFocusWhilstPlaying = true
            }
            onPauseAudio()
        }
    }

    fun onGainedAudioFocus() {
        if (currentMedia != null && lostFocusWhilstPlaying) {
            lostFocusWhilstPlaying = false
            playing = true
            currentMedia?.let { currentMedia ->
                player.resume(currentMedia)
            }
        } else if (isDucking) {
            isDucking = false
            player.setVolume(1f)
        }
    }

    fun onBecomingNoisy() {
        if (playing) {
            onPauseAudio()
        }
    }

    fun onSourceLoaded() {
        if (enqueuedAsset == null) {
            // After a normal track is loaded, pre-buffer next one
            if (currentSource != null && currentMedia is TrackMediaAsset) {
                Timber.d("--------> start pre buffer after track loaded")
                preBufferTrack()
            }
        } else {
            // If a track has been pre-buffered, set the flag to true to allow an immediate pre-buffer
            Timber.d("--------> pre-buffered track ready ${enqueuedAsset?.id}")
            shouldDoPreBufferImmediately = true
        }
    }

    // endregion

    // region Source Callback

    fun onPlayStakkar(stakkar: Stakkar) {
        if (!playlistHasNoMoreTracks) {
            hijackedMedia = currentMedia
        }
        play(stakkar)
    }

    fun onPlayStakkar(stakkarId: Int) {
        if (currentStakkar?.id == stakkarId) {
            currentStakkar?.let { onPlayStakkar(it) }
        } else {
            musicPlayerFacade.loadStakkar(stakkarId)
                .observeOn(AndroidSchedulers.mainThread())
                .successSubscribe {
                    onPlayStakkar(it)
                }
        }
    }

    fun onPlayRadio(radio: MusicForYouItemModel.RadioShelfItem) {
        onPauseAudio()
        resetState()

        val radioMediaAsset = RadioMediaAsset(radio)
        currentMedia = radioMediaAsset

        if (!isDucking) {
            player.requestAudioFocus()
            player.setVolume(1f)
        }

        player.play(radioMediaAsset)
        player.setMetadata(
            title = radio.title,
            albumArtUri = listOf(LocalisedString(languageCodeEN, radio.thumbnail)),
            artUri = listOf(LocalisedString(languageCodeEN, radio.thumbnail)),
            type = MediaType.RADIO,
            artist = radio.description
        )
    }

    fun onPlayAd(adId: Int, url: String?) {
        if (currentMedia != null &&
            currentMedia is AdMediaAsset &&
            adId == currentMedia?.id &&
            url != null
        ) {
            currentMedia?.location = url
            player.play(currentMedia as AdMediaAsset)
            playing = true
        } else {
            playNext()
        }
    }

    fun onDismissAd(adId: Int) {
        if (currentMedia != null &&
            currentMedia is AdMediaAsset &&
            adId == currentMedia?.id
        ) {
            playNext()
        }
    }

    // stations
    fun playRadio(source: PlayerSource, trackHash: String? = null, tracks: List<Track>? = null) {
        if (!musicPlayerFacade.hasTrackPlayRight()) {
            player.setUpgradeRequired()
            return
        }

        if (isCurrentSource(source.sourceId)) {
            onPlayAudio()
            return
        }
        onPauseAudio()
        resetState()
        currentSource = source
        val station = currentSource?.sourceStation
        setPlayerQueueTitle()
        setAvailableActions(isRadio = true)
        setAvailableSkipIfEnabled()
        setPlayLimitIfEnabled()

        // artist mix & station
        if (tracks == null) {
            station?.let { _station ->
                musicPlayerFacade.enqueueStation(_station, trackHash)
                    .observeOn(AndroidSchedulers.mainThread())
                    .tunedSubscribe(
                        { listTrack ->
                            listTrack.map { track -> track.playerSource = currentSource }
                            Timber.d("--------> loaded ${listTrack.size} tracks")
                            playQueue = TrackQueue(listTrack)
                            playQueue?.disableShuffle()
                            playQueue?.repeatMode = PlayQueue.REPEAT_MODE_NONE
                            Timber.d("--------> playQueue has ${playQueue?.getTrackListSize()} tracks")
                            playNext()
                        },
                        {
                            Timber.d("--------> no more tracks")
                            currentSource = null
                            player.setUnknownError()
                        }
                    )
            }
        } else {
            // offline station
            playQueue = TrackQueue(tracks)
            playQueue?.enableShuffle()
            playQueue?.repeatMode = PlayQueue.REPEAT_MODE_NONE

            playNext()
        }
    }

    // artist shuffle & album & playlist
    fun playFullStream(
        initialSource: PlayerSource,
        tracks: List<Track>,
        startIndex: Int?,
        forceShuffle: Boolean,
        forceSequential: Boolean
    ) {
        if (!musicPlayerFacade.hasTrackPlayRight()) {
            player.setUpgradeRequired()
            return
        }

        if (isCurrentSource(initialSource.sourceId) &&
            tracks.isNotEmpty() &&
            startIndex != null &&
            isCurrentMedia(tracks[startIndex].id)
        ) {
            onPlayAudio()
            return
        }
        onPauseAudio()
        resetState()
        currentSource = initialSource
        setPlayerQueueTitle()
        setAvailableActions()
        setAvailableSkipIfEnabled()
        setPlayLimitIfEnabled()

        playQueue = TrackQueue(tracks)
        onQueueUpdated()
        // change repeat mode to all if last session is on repeat song or else use last repeat mode
        if (musicPlayerFacade.getRepeatMode() == PlayQueue.REPEAT_MODE_ONE)
            setRepeatMode(PlayQueue.REPEAT_MODE_ALL)
        else
            setRepeatMode(musicPlayerFacade.getRepeatMode())

        if (musicPlayerFacade.hasSequentialPlaybackRight() && forceSequential) {
            onToggleShuffle(false)
            playQueue?.currentIndex = (startIndex ?: 0) - 1
        } else if (forceShuffle) {
            onToggleShuffle(true)
            if (startIndex != null) {
                playQueue?.enableShuffle(startIndex)
                playQueue?.currentIndex?.minus(1)
            }
        } else {
            // if no force option, init the queue as previous mode
            if (!musicPlayerFacade.isShufflePlayEnabled()) {
                onToggleShuffle(false)
                playQueue?.currentIndex = (startIndex ?: 0) - 1
            } else {
                onToggleShuffle(true)
                if (startIndex != null) {
                    playQueue?.enableShuffle(startIndex)
                    playQueue?.currentIndex?.minus(1)
                }
            }
        }

        playNext()
    }

    fun playVideo(video: Track) {
        if (!musicPlayerFacade.hasTrackPlayRight()) {
            player.setUpgradeRequired()
            return
        }
        setPlayLimitIfEnabled()
        if (musicPlayerFacade.isPlayLimitEnabled() && playLimitReached) {
            onPlayLimitReached()
            return
        }

        if (!isDucking) {
            player.requestAudioFocus()
            player.setVolume(1f)
        }

        resetState()
        currentSource = if (video.playerSource != null)
            video.playerSource
        else {
            video.resetPlayerSource()
            video
        }
        setPlayerQueueTitle()
        setAvailableActions(isVideo = true)

        currentMedia = TrackMediaAsset(video)

        loadStreamSubscription?.dispose()
        loadStreamSubscription = musicPlayerFacade.streamTrack(video)
            .doOnSuccess { mediaAsset ->
                // video does not need to be cached so cache path is not added to currentMedia
                currentMedia?.location = mediaAsset.location
                currentMedia?.sessionId = mediaAsset.sessionId
            }
            .flatMap { mediaAsset ->
                if (mediaAsset.location != null &&
                    mediaAsset.location?.startsWith(FILE_PREFIX) != true
                ) {
                    Single.just(musicPlayerFacade.isStreamingEnabled())
                } else {
                    Single.just(true)
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .tunedSubscribe(
                { canStream ->
                    setMetadata(video)
                    if (canStream) {
                        playing = true
                        player.play(currentMedia as TrackMediaAsset)
                        musicPlayerFacade.resetPlayTrackLog()
                    } else {
                        playing = false
                        player.setStreamingDisabled()
                    }
                },
                { throwable ->
                    setMetadata(video)
                    if (throwable is HttpException &&
                        (
                            throwable.code() == NetworkModule.HTTP_CODE_UPGRADE_REQUIRED ||
                                throwable.code() == NetworkModule.HTTP_CODE_FORBIDDEN
                            )
                    ) {
                        playLimitReached = true
                        onPlayLimitReached()
                    } else {
                        playNext()
                    }
                }
            )
    }

    fun playVideo(videoId: Int) {
        if (currentMedia is TrackMediaAsset &&
            (currentMedia as TrackMediaAsset).track.id == videoId
        ) {
            playVideo((currentMedia as TrackMediaAsset).track)
        } else {
            musicPlayerFacade.loadTrack(videoId)
                .observeOn(AndroidSchedulers.mainThread())
                .successSubscribe { track ->
                    playVideo(track)
                }
        }
    }

    // endregion

    // region PlayQueue Callback

    fun onGetAvailableSkip(): Int = availableSkips

    fun onGetCurrentQueue(): TrackQueueInfo? = playQueue?.generateTrackQueueInfo()

    fun onPlayTrack(index: Int) {
        val isRepeatSong = musicPlayerFacade.getRepeatMode() == PlayQueue.REPEAT_MODE_ONE
        if (!musicPlayerFacade.isShufflePlayEnabled()) {
            playQueue?.currentIndex = if (isRepeatSong) index else index - 1
        } else {
            playQueue?.enableShuffle(index)
            if (!isRepeatSong) playQueue?.currentIndex?.minus(1)
        }
        playNext()
    }

    fun onSkipToTrack(index: Int) {
        if (index != playQueue?.currentIndex) {
            // change repeat mode when repeat mode is on repeat song
            if (musicPlayerFacade.getRepeatMode() == PlaybackStateCompat.REPEAT_MODE_ONE) {
                setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL)
            }
            playQueue?.currentIndex = index - 1
            playNext()
        }
    }

    fun onAddTracks(tracks: List<Track>) {
        playQueue?.addToEnd(tracks)
        onQueueUpdated()
    }

    fun onRemoveTrack(index: Int) {
        val isRemovingCurrentTrack = playQueue?.getIndexInDisplayOrder() == index
        playQueue?.remove(index)
        onQueueUpdated()
        if (!playing) playWhenReady = false
        if (isRemovingCurrentTrack) {
            if (musicPlayerFacade.getRepeatMode() == PlayQueue.REPEAT_MODE_ONE) {
                setRepeatMode(PlayQueue.REPEAT_MODE_ALL)
            }
            playNext()
        }
    }

    fun onMoveTrack(oldIndex: Int, newIndex: Int) {
        playQueue?.move(oldIndex, newIndex)
        onQueueUpdated()
    }

    fun onClearQueue() {
        player.stop()
        resetState()
        player.resetMetaData()
        onQueueUpdated()
    }

    fun updateQueue(trackList: List<Track>) {
        playQueue?.updateTrackList(trackList)
    }

    // endregion

    private fun setRepeatMode(repeatMode: Int) {
        musicPlayerFacade.setRepeatMode(repeatMode)
        player.setPlayMode(musicPlayerFacade.isShufflePlayEnabled(), repeatMode)

        playQueue?.repeatMode = repeatMode
    }

    private fun isCurrentSource(sourceId: Int) =
        currentSource != null && sourceId == currentSource?.sourceId

    private fun isCurrentMedia(mediaId: Int) = currentMedia != null && mediaId == currentMedia?.id

    private fun setPlayerQueueTitle() {
        when {
            currentSource?.sourceStation != null -> currentSource?.sourceStation?.name?.let {
                player.setQueueTitle(
                    it,
                    true
                )
            }

            currentSource?.sourceAlbum != null -> player.setQueueTitle(
                listOf(
                    LocalisedString(
                        languageCodeEN,
                        currentSource?.sourceAlbum?.primaryRelease?.name
                    )
                )
            )

            currentSource?.sourceArtist != null -> player.setQueueTitle(
                listOf(
                    LocalisedString(
                        languageCodeEN,
                        currentSource?.sourceArtist?.name
                    )
                )
            )

            currentSource?.sourcePlaylist != null -> currentSource?.sourcePlaylist?.name?.let {
                player.setQueueTitle(
                    it
                )
            }

            currentSource?.sourceTrack != null -> player.setQueueTitle(
                listOf(
                    LocalisedString(
                        languageCodeEN,
                        currentSource?.sourceTrack?.name
                    )
                )
            )
        }
    }

    private fun setAvailableSkipIfEnabled() {
        if (musicPlayerFacade.isSkipLimitEnabled()) {
            Timber.d("--------> onPlayStation get skip")
            musicPlayerFacade.getTotalSkips()
                .observeOn(AndroidSchedulers.mainThread())
                .successSubscribe {
                    this.availableSkips = it
                }
        }
    }

    private fun setPlayLimitIfEnabled() {
        if (musicPlayerFacade.isPlayLimitEnabled()) {
            playLimitReached = musicPlayerFacade.getTotalPlays().blockingGet() <= 0
        }
    }

    private fun setAvailableActions(isRadio: Boolean = false, isVideo: Boolean = false) {
        val actions = mutableListOf<Long>()
        actions.add(PlaybackStateCompat.ACTION_PLAY_PAUSE)
        when {
            isRadio -> {
                actions.add(PlaybackStateCompat.ACTION_SET_RATING)
                actions.add(PlaybackStateCompat.ACTION_SKIP_TO_NEXT)
            }

            isVideo ->
                actions.add(PlaybackStateCompat.ACTION_SEEK_TO)

            else -> {
                actions.add(PlaybackStateCompat.ACTION_SEEK_TO)
                actions.add(PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
                actions.add(PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE)
                actions.add(PlaybackStateCompat.ACTION_SET_REPEAT_MODE)
                actions.add(PlaybackStateCompat.ACTION_SKIP_TO_NEXT)
            }
        }
        player.setAvailableActions(actions)
    }

    private fun resetState() {
        playQueue?.clear()
        playQueue = null
        playlistHasNoMoreTracks = false
        playLimitReached = false
        currentSource = null
        currentMedia = null
        enqueuedAsset = null
        availableSkips = 0
    }

    private fun resumeHijackedTrackOrPlayNext() {
        hijackedMedia?.let { media ->
            currentMedia = media

            when (currentMedia) {
                is AdMediaAsset -> {
                    setMetadata((currentMedia as AdMediaAsset).ad)
                }

                is StakkarMediaAsset -> {
                    setMetadata((currentMedia as StakkarMediaAsset).stakkar)
                }

                else -> {
                    setMetadata((currentMedia as TrackMediaAsset).track)
                }
            }

            playing = true
            player.resume(media)
            hijackedMedia = null
        } ?: run {
            playNext()
        }
    }

    // Play the next track in the playQueue (if one exists)
    private fun playNext() {

        loadStreamSubscription?.dispose()

        if (currentSource == null) {
            return
        }

        if (playQueue == null) {
            setPlaylistHasNoMoreTracks()
            return
        }

        // If we have enough track, try to play its ads and then play it
        if (playQueue?.requireMoreTracks() != true ||
            !isOnlineStation() || playlistHasNoMoreTracks
        ) {
            val peekedTrack = playQueue?.peekNext()
            if (peekedTrack == null) {
                setPlaylistHasNoMoreTracks()
                return
            }
            if (adQueue != null && adQueue?.attachedTrackId == peekedTrack.id &&
                adQueue?.hasNext() == true
            ) {
                // if we have an ad for the current track, play it
                adQueue?.next()?.let { play(it) }
            } else if (stakkarQueue != null && stakkarQueue?.attachedTrackId == peekedTrack.id &&
                stakkarQueue?.hasNext() == true
            ) {
                // if we have a stakkar for the current track, play it
                stakkarQueue?.next()?.let { play(it) }
            } else if (adQueue == null || adQueue?.attachedTrackId != peekedTrack.id ||
                stakkarQueue == null || stakkarQueue?.attachedTrackId != peekedTrack.id
            ) {
                // if we dont have an ad or stakkar queue, or theyre for the wrong track, get the track extras
                loadTrackExtrasSubscription?.dispose()
                loadTrackExtrasSubscription = getEnqueueTrackExtrasObservable()?.tunedSubscribe(
                    {
                        Timber.d("--------> track extra got")
                        loadTrackExtrasObservable = null
                        playNext()
                    },
                    {
                        loadTrackExtrasObservable = null
                        playQueue?.next()?.let { it1 -> play(it1) }
                    }
                )
            } else {
                playQueue?.next()?.let { play(it) }
            }
        } else if (loadTracksObservable != null) {
            // if we were downloading tracks, finish the download and act appropriately
            if (isOnlineStation()) {
                loadTracksSubscription?.dispose()
                loadTracksSubscription = getEnqueueTracksObservable()?.tunedSubscribe(
                    { trackList ->
                        loadTracksObservable = null

                        if (trackList.isNotEmpty()) {
                            playNext()
                        } else {
                            setPlaylistHasNoMoreTracks()
                        }
                    },
                    {
                        loadTracksObservable = null
                        setPlaylistHasNoMoreTracks()
                    }
                )

                if (loadTracksSubscription == null) {
                    playNext()
                }
            }
        } else {
            if (isOnlineStation()) {
                if (!playlistHasNoMoreTracks) {
                    loadTracksSubscription?.dispose()
                    loadTracksSubscription = getEnqueueTracksObservable()?.tunedSubscribe(
                        {
                            loadTracksObservable = null
                            playNext()
                        },
                        {
                            loadTracksObservable = null
                            setPlaylistHasNoMoreTracks()
                        }
                    )
                } else {
                    setPlaylistHasNoMoreTracks()
                }
            }
        }
    }

    private fun seekTo(position: Long) {
        if (position == -1L) return
        if (currentMedia == null || currentMedia is RadioMediaAsset) return
        player.seekTo(position, (currentMedia as TrackMediaAsset).track.isVideo)
    }

    private fun play(track: Track) {
        if (musicPlayerFacade.isPlayLimitEnabled()) {
            setPlayLimitIfEnabled()
            if (playLimitReached) {
                onPlayLimitReached()
                return
            }
        }

        if (!isDucking) {
            player.requestAudioFocus()
            player.setVolume(1f)
        }

        blockSkipPress = false
        currentSource = if (track.playerSource != null)
            track.playerSource
        else {
            track.resetPlayerSource()
            track
        }
        setPlayerQueueTitle()
        currentMedia = TrackMediaAsset(track)
        if (enqueuedAsset != null && enqueuedAsset?.id == track.id) {
            Timber.d("--------> do pre buffer play ${track.id}")

            player.setPlaybackBuffering()

            currentMedia?.location = enqueuedAsset?.location
            currentMedia?.cachePath = enqueuedAsset?.cachePath
            currentMedia?.sessionId = enqueuedAsset?.sessionId

            enqueuedAsset = null

            player.play(currentMedia as TrackMediaAsset)

            musicPlayerFacade.resetPlayTrackLog()

            if (musicPlayerFacade.isSkipLimitEnabled()) {
                musicPlayerFacade.checkLocalSkipCount()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .successSubscribe {
                        availableSkips += it
                        if (availableSkips > 0) player.setSkipAvailable()
                    }
            }

            playing = true
            setMetadata(track)

            // force pre-buffer next track or wait for the current track finish buffering based on the flag
            if (shouldDoPreBufferImmediately) {
                shouldDoPreBufferImmediately = false
                Timber.d("--------> force a pre buffer")
                preBufferTrack()
            }
        } else {
            Timber.d("--------> do normal buffer play ${track.id}")
            loadStreamSubscription?.dispose()
            val loadStreamObservable = musicPlayerFacade.streamTrack(track)
                .subscribeOn(Schedulers.io())
                .doOnSuccess { mediaAsset ->
                    currentMedia?.location = mediaAsset.location
                    currentMedia?.cachePath = mediaAsset.cachePath
                    currentMedia?.sessionId = mediaAsset.sessionId
                }
                .flatMap { mediaAsset ->
                    if (mediaAsset.location != null &&
                        mediaAsset.location?.startsWith(FILE_PREFIX) != true
                    ) {
                        Single.just(musicPlayerFacade.isStreamingEnabled())
                    } else {
                        Single.just(true)
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess { canStream ->
                    setMetadata(track)
                    if (canStream) {
                        playing = true
                        player.play(currentMedia as TrackMediaAsset)
                        musicPlayerFacade.resetPlayTrackLog()
                    } else {
                        playing = false
                        player.setStreamingDisabled()
                    }
                }
                .doOnError { throwable ->
                    setMetadata(track)
                    if (throwable is HttpException &&
                        (
                            throwable.code() == NetworkModule.HTTP_CODE_UPGRADE_REQUIRED ||
                                throwable.code() == NetworkModule.HTTP_CODE_FORBIDDEN
                            )
                    ) {
                        playLimitReached = true
                        onPlayLimitReached()
                    } else {
                        playNext()
                    }
                }

            loadStreamSubscription = if (musicPlayerFacade.isSkipLimitEnabled()) {
                loadStreamObservable
                    .flatMap {
                        musicPlayerFacade.checkLocalSkipCount()
                    }.successSubscribe {
                        availableSkips += it
                        if (availableSkips > 0) player.setSkipAvailable()
                    }
            } else {
                loadStreamObservable.emptySubscribe()
            }
        }
    }

    private fun setMetadata(track: Track) {
        currentSource?.let { source ->
            player.setMetadata(
                title = track.name,
                type = MediaType.valueOf(source.sourceType),
                id = "${source.sourceId}:${track.id}",
                artist = track.artistString,
                duration = track.duration * S_TO_MS,
                artUri = source.sourceImage,
                albumArtUri = listOf(LocalisedString(languageCodeEN, track.image)),
                rating = track.vote,
                hasLyrics = track.hasLyrics,
                isVideo = track.isVideo,
                isExplicit = track.isExplicit,
                isFirstTrack = playQueue?.hasPrevious()?.not() == true,
                isLastTrack = playQueue?.hasNext()?.not() == true
            )
        }
    }

    private fun onPlayLimitReached() {
        playlistHasNoMoreTracks = true
        player.setPlayLimitReached()
        previousSource = currentSource
        currentSource = null
    }

    private fun setPlaylistHasNoMoreTracks() {
        playlistHasNoMoreTracks = true
        if (playQueue != null && playQueue?.hasNext() == true) {
            Timber.d("--------> is about to reach playQueue end")
            playNext()
        } else {
            Timber.d("--------> playQueue ended")
            player.setEndOfQueue()
            blockSkipPress = false
            lastKnownPosition = 0L
            previousSource = currentSource
        }
    }

    private fun play(ad: Ad) {
        if (musicPlayerFacade.isPlayLimitEnabled()) {
            setPlayLimitIfEnabled()
            if (playLimitReached) {
                onPlayLimitReached()
                return
            }
        }

        if (!isDucking) {
            player.requestAudioFocus()
            player.setVolume(1f)
        }

        if (musicPlayerFacade.isStreamingEnabled()) {
            currentMedia = AdMediaAsset(ad)
            setMetadata(ad)
            player.pause()
            playing = false
        } else {
            playing = false
            playNext()
        }
    }

    private fun setMetadata(ad: Ad) {
        val duration = (adDateFormatter.parse(ad.duration) ?: Date()).time
        player.setMetadata(
            title = ad.title,
            type = MediaType.AD,
            id = "${currentSource?.sourceId}:${ad.hashCode()}",
            artUri = listOf(LocalisedString(languageCodeEN, ad.image)),
            albumArtUri = currentSource?.sourceImage,
            duration = duration,
            clickUri = ad.clickUrl,
            isAd = true,
            adVastXML = ad.vast
        )
    }

    private fun play(stakkar: Stakkar) {
        if (!isDucking) {
            player.requestAudioFocus()
            player.setVolume(1f)
        }

        currentMedia = StakkarMediaAsset(stakkar)

        loadStreamSubscription?.dispose()
        loadStreamSubscription = musicPlayerFacade.streamStakkar(stakkar)
            .doOnSuccess { mediaAsset ->
                currentMedia?.location = mediaAsset.location
            }
            .flatMap { mediaAsset ->
                if (mediaAsset.location?.startsWith(FILE_PREFIX) != true) {
                    Single.just(musicPlayerFacade.isStreamingEnabled())
                } else {
                    Single.just(true)
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .tunedSubscribe(
                { canStream ->
                    currentStakkar = stakkar
                    setMetadata(stakkar)

                    if (canStream) {
                        playing = true
                        player.play(currentMedia as StakkarMediaAsset)
                    } else {
                        playing = false
                        player.setStreamingDisabled()
                    }
                },
                {
                    playNext()
                }
            )
    }

    private fun setMetadata(stakkar: Stakkar) {
        player.setMetadata(
            title = "Stakkar",
            type = if (stakkar.type == Stakkar.MediaType.VIDEO) {
                MediaType.VIDEO_STAKKAR
            } else {
                MediaType.AUDIO_STAKKAR
            },
            id = "${currentSource?.sourceId ?: 0}:${stakkar.id}",
            artist = stakkar.publisherName,
            artUri = stakkar.bannerImage,
            albumArtUri = listOf(LocalisedString(languageCodeEN, stakkar.publisherImage)),
            duration = 0L,
            clickUri = stakkar.bannerUrl,
            isAd = false,
            hideDialog = stakkar.hideDialog
        )
    }

    private fun preBufferTrack() {
        if (loadStreamSubscription != null &&
            loadStreamSubscription?.isNotDisposed == true
        ) return

        val nextTrack = playQueue?.peekNext()
        if (nextTrack != null) {
            // Don't pre buffer a track that has already preloaded or during preload
            if (enqueuedAsset != null && enqueuedAsset?.id == nextTrack.id) return

            loadStreamSubscription?.dispose()
            loadStreamSubscription = musicPlayerFacade.streamTrack(nextTrack)
                .flatMap { track ->
                    if (track.location != null && track.location?.startsWith(FILE_PREFIX) != true) {
                        Single.just(Pair(track, musicPlayerFacade.isStreamingEnabled()))
                    } else {
                        Single.just(Pair(track, true))
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .tunedSubscribe(
                    { (track, canStream) ->
                        if (canStream) {
                            enqueuedAsset = track
                            enqueuedAsset?.let { player.enqueue(it) }

//                            autoSkipForPreBufferingDebug = if(autoSkipForPreBufferingDebug){
//                                Handler().postDelayed({
//                                    Timber.d("--------> Auto skip")
//                                    onSkipToNext()
//                                }, 500)
//                                false
//                            } else {
//                                true
//                            }
                        }
                    },
                    { throwable ->
                        if (throwable is HttpException && throwable.code() ==
                            NetworkModule.HTTP_CODE_UPGRADE_REQUIRED
                        ) {
                            playLimitReached = true
                        }
                    }
                )
        }
    }

    private fun getEnqueueTracksObservable(): Single<List<Track>>? {
        if (loadTracksObservable == null) {
            loadTracksObservable = currentSource?.sourceStation?.let { station ->
                musicPlayerFacade.enqueueStation(station)
                    .doOnSuccess { tracks ->
                        tracks.map { it.playerSource = currentSource }
                        if (tracks.isNotEmpty()) {
                            Timber.d("loaded ${tracks.size} tracks")
                            playQueue?.addToEnd(tracks)
                            Timber.d("playQueue has ${playQueue?.getTrackListSize()} tracks")
                        }
                    }.doOnError {
                        Timber.d("no more tracks")
                    }.cacheOnMainThread()
            }
        }

        return loadTracksObservable
    }

    private fun getEnqueueTrackExtrasObservable(): Single<TrackExtras>? {
        if (loadTrackExtrasObservable != null) return loadTrackExtrasObservable
        if (currentSource?.isOffline == true || currentSource == null) {

            return Single.error(
                IllegalStateException("Skip track extra")
            )
        }

        currentSource?.let { currentSource ->
            val nextTrack = playQueue?.peekNext()
            if (nextTrack != null) {
                loadTrackExtrasObservable = musicPlayerFacade.enqueueTrackExtras(
                    currentSource,
                    playQueue?.peekNext(),
                    playQueue?.current(),
                    musicPlayerFacade.checkAdCount()
                )
                    .doOnSuccess { trackExtras ->
                        if (musicPlayerFacade.checkAdCount()) musicPlayerFacade.resetAdCount()
                        stakkarQueue = StakkarQueue(trackExtras.stakkars, nextTrack.id)
                        adQueue = AdQueue(trackExtras.ads, nextTrack.id)
                    }.cacheOnMainThread()
            }
        }
        return loadTrackExtrasObservable
    }

    private fun isOnlineStation(): Boolean =
        currentSource?.sourceStation != null && currentSource?.isOffline != true

    private fun onQueueUpdated() {
        player.setQueueUpdated()
    }

    @VisibleForTesting
    fun setSubscription(
        loadStreamSubscription: Disposable? = null,
        loadTracksSubscription: Disposable? = null,
        loadTrackExtrasSubscription: Disposable? = null,
        loadTracksObservable: Single<List<Track>>? = null
    ) {
        this.loadStreamSubscription = loadStreamSubscription
        this.loadTracksSubscription = loadTracksSubscription
        this.loadTrackExtrasSubscription = loadTrackExtrasSubscription
        this.loadTracksObservable = loadTracksObservable
    }

    @VisibleForTesting
    fun setPrivateDataFirstSection(
        currentMedia: MediaAsset? = null,
        lastKnownPosition: Long = 0L,
        playQueue: TrackQueue? = null,
        blockSkipPress: Boolean = false,
        currentSource: PlayerSource? = null
    ) {
        this.currentMedia = currentMedia
        this.lastKnownPosition = lastKnownPosition
        this.playQueue = playQueue
        this.blockSkipPress = blockSkipPress
        this.currentSource = currentSource
    }

    @VisibleForTesting
    fun setPrivateDataSecondSection(
        hijackedMedia: MediaAsset? = null,
        availableSkips: Int = 0,
        playing: Boolean = false,
        lostFocusWhilstPlaying: Boolean = false,
        isDucking: Boolean = false
    ) {
        this.hijackedMedia = hijackedMedia
        this.availableSkips = availableSkips
        this.playing = playing
        this.lostFocusWhilstPlaying = lostFocusWhilstPlaying
        this.isDucking = isDucking
    }

    @VisibleForTesting
    fun setPrivateDataThirdSection(
        enqueuedAsset: MediaAsset? = null,
        currentStakkar: Stakkar? = null
    ) {
        this.enqueuedAsset = enqueuedAsset
        this.currentStakkar = currentStakkar
    }

    class TrackMediaAsset(val track: Track) : MediaAsset(track.id)
    class AdMediaAsset(val ad: Ad) : MediaAsset(ad.hashCode(), ad.mediaFile)
    class StakkarMediaAsset(val stakkar: Stakkar) : MediaAsset(stakkar.id)
    class RadioMediaAsset(val radio: MusicForYouItemModel.RadioShelfItem) : MediaAsset(radio.id)

    interface PlayerSurface {
        fun play(mediaAsset: MediaAsset)
        fun resume(mediaAsset: MediaAsset)
        fun enqueue(mediaAsset: MediaAsset)
        fun pause()
        fun stop()
        fun seekTo(position: Long, isVideo: Boolean)
        fun setVolume(volume: Float)
        fun requestAudioFocus()
        fun setQueueTitle(title: List<LocalisedString>, isStation: Boolean = false)
        fun setAvailableActions(actions: List<Long>)
        fun setPlayMode(isShuffle: Boolean, repeatMode: Int)
        fun setMetadata(
            title: String,
            type: MediaType,
            id: String = "",
            artist: String? = null,
            duration: Long = 0L,
            artUri: List<LocalisedString>? = null,
            albumArtUri: List<LocalisedString>? = null,
            rating: Rating? = null,
            clickUri: String? = null,
            isAd: Boolean = false,
            adVastXML: String? = null,
            hideDialog: Boolean = false,
            hasLyrics: Boolean = false,
            isVideo: Boolean = false,
            isExplicit: Boolean = false,
            isFirstTrack: Boolean = false,
            isLastTrack: Boolean = false
        )

        fun resetMetaData()
        fun setPlaybackPosition(position: Long)
        fun setPlaybackBuffering()
        fun setEndOfQueue()
        fun setSkipLimitReached()
        fun setSkipAvailable()
        fun setPlayLimitReached()
        fun setUnknownError()
        fun setStreamingDisabled()
        fun setUpgradeRequired()
        fun setQueueUpdated()
    }
}
