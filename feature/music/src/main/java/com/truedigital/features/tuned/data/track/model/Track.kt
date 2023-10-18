package com.truedigital.features.tuned.data.track.model

import com.google.gson.annotations.SerializedName
import com.truedigital.features.listens.share.constant.MusicConstant
import com.truedigital.features.music.data.trending.model.response.playlist.Translation
import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.artist.model.ArtistInfo
import com.truedigital.features.tuned.data.player.PlayerSource
import com.truedigital.features.tuned.data.player.model.MediaType
import com.truedigital.features.tuned.data.playlist.model.Playlist
import com.truedigital.features.tuned.data.product.model.Product
import com.truedigital.features.tuned.data.station.model.Rating
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.util.LocalisedString

data class Track(
    @SerializedName("TrackId") override var id: Int,
    @SerializedName("PlaylistTrackId") var playlistTrackId: Int,
    @SerializedName("SongId") var songId: Int,
    @SerializedName("ReleaseId") var releaseId: Int,
    @SerializedName("Artists") var artists: List<ArtistInfo>,
    @SerializedName("Name") var name: String,
    @SerializedName("OriginalCredit") var originalCredit: String?,
    @SerializedName("IsExplicit") var isExplicit: Boolean,
    @SerializedName("TrackNumber") var trackNumber: Int,
    @SerializedName("TrackNumberInVolume") var trackNumberInVolume: Int,
    @SerializedName("VolumeNumber") var volumeNumber: Int,
    @SerializedName("ReleaseArtists") var releaseArtists: List<ArtistInfo>,
    @SerializedName("Sample") var sample: String,
    @SerializedName("IsOnCompilation") var isOnCompilation: Boolean,
    @SerializedName("ReleaseName") var releaseName: String,
    @SerializedName("AllowDownload") var allowDownload: Boolean,
    @SerializedName("AllowStream") var allowStream: Boolean,
    @SerializedName("Duration") var duration: Long,
    @SerializedName("Image") var image: String,
    @SerializedName("HasLyrics") var hasLyrics: Boolean,
    @SerializedName("Video") var video: Track?,
    @SerializedName("IsVideo") var isVideo: Boolean,
    @SerializedName("Translations") var translationsList: List<Translation> = emptyList(),
    @SerializedName("Owner") var owner: String? = null,
    @SerializedName("Label") var label: String? = null,
    @SerializedName("ContentLanguage") var contentLanguage: String? = null,
    @SerializedName("Genre") var genreList: List<String>? = null,
    var vote: Rating?,
    var isDownloaded: Boolean,
    var syncProgress: Float,
    var isCached: Boolean,
    var isPlayingTrack: Boolean = false
) : Product, PlayerSource {

    companion object {
        private const val MINUTES_10 = 10
        private const val MINUTES_60 = 60
        private const val SECOND_3600 = 3600
    }

    var playerSource: PlayerSource? = null

    val nameTranslations: String
        get() = this.translationsList.find {
            it.language == com.truedigital.features.listens.share.constant.MusicConstant.Language.LANG_TH
        }?.value?.ifEmpty { this.name } ?: this.name

    fun getArtistString(variousString: String): String =
        if (artists.isNotEmpty()) artistString else variousString

    val artistString: String
        get() = artists.joinToString(",") { it.name }

    val genreString: String
        get() = if (genreList.isNullOrEmpty()) {
            " "
        } else {
            genreList?.joinToString("|") { it } ?: " "
        }

    val formattedDuration: String
        // Example formats 1:03:29, 11:31, 2:12, 0:59
        get() {
            val hours = duration / SECOND_3600
            val minutes = duration / MINUTES_60 - (hours * MINUTES_60)
            val seconds = duration - (minutes * MINUTES_60) - (hours * SECOND_3600)
            val durationStringBuilder = StringBuilder()
            if (hours > 0) {
                durationStringBuilder.append(hours)
                durationStringBuilder.append(":")
            }
            if (minutes >= MINUTES_10) {
                durationStringBuilder.append(minutes)
            } else {
                if (durationStringBuilder.isNotEmpty()) {
                    durationStringBuilder.append(0)
                }
                durationStringBuilder.append(minutes)
            }

            durationStringBuilder.append(":")
            if (seconds < MINUTES_10) {
                durationStringBuilder.append(0)
            }
            durationStringBuilder.append(seconds)
            return durationStringBuilder.toString()
        }

    fun getDurationString(): String {
        if (duration <= 0) {
            return "0m"
        }

        // Example formats 1h23m
        val hours = duration / SECOND_3600
        val minutes = duration / MINUTES_60 - (hours * MINUTES_60)
        val seconds = duration - (minutes * MINUTES_60) - (hours * SECOND_3600)
        val durationStringBuilder = StringBuilder()
        if (hours > 0) {
            durationStringBuilder.append(hours)
            durationStringBuilder.append("h")
        }
        if (minutes > 0) {
            durationStringBuilder.append(minutes)
            durationStringBuilder.append("m")
        }
        if (seconds > 0) {
            durationStringBuilder.append(seconds)
            durationStringBuilder.append("s")
        }

        return durationStringBuilder.toString()
    }

    // region PlayerSource
    override var sourceId = id
    override var sourceImage = listOf(LocalisedString("en", image))
    override var sourceType = if (isVideo) MediaType.VIDEO.name else MediaType.SONGS.name
    override var sourceAlbum: Album? = null
    override var sourceArtist: Artist? = null
    override var sourceStation: Station? = null
    override var sourcePlaylist: Playlist? = null
    override var sourceTrack: Track? = this
    override var isOffline = false

    override fun resetPlayerSource(isOffline: Boolean) {
        sourceId = id
        sourceImage = listOf(LocalisedString("en", image))
        sourceType = if (isVideo) MediaType.VIDEO.name else MediaType.SONGS.name
        sourceAlbum = null
        sourceArtist = null
        sourceStation = null
        sourcePlaylist = null
        sourceTrack = this
        this.isOffline = isOffline
    }
}
