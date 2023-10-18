package com.truedigital.features.tuned.data.player

import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.playlist.model.Playlist
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.util.LocalisedString

interface PlayerSource {
    var sourceId: Int
    var sourceImage: List<LocalisedString>
    var sourceType: String
    var sourceStation: Station?
    var sourceAlbum: Album?
    var sourceArtist: Artist?
    var sourcePlaylist: Playlist?
    var sourceTrack: Track?
    var isOffline: Boolean

    // for stations, playersource info might lost during parcel, call this to restore it
    fun resetPlayerSource(isOffline: Boolean = false)
}
