package com.truedigital.features.music.navigation.config

import androidx.collection.arrayMapOf
import com.truedigital.features.music.navigation.router.MusicAddSong
import com.truedigital.features.music.navigation.router.MusicCreateNewPlaylist
import com.truedigital.features.music.navigation.router.MusicCreateNewPlaylistToMyPlaylist
import com.truedigital.features.music.navigation.router.MusicLandingToAlbum
import com.truedigital.features.music.navigation.router.MusicLandingToArtist
import com.truedigital.features.music.navigation.router.MusicLandingToPlaylist
import com.truedigital.features.music.navigation.router.MusicLandingToSeeAll
import com.truedigital.features.music.navigation.router.MusicMyLibraryToMyPlaylist
import com.truedigital.features.tuned.R
import com.truedigital.navigation.router.Destination

object ConfigMusicNavigate : Map<Destination, Int> by arrayMapOf(
    MusicAddSong to R.id.action_global_addSongBottomSheet,
    MusicLandingToAlbum to R.id.action_musicLanding_to_albumActivity,
    MusicLandingToArtist to R.id.action_musicLanding_to_artistActivity,
    MusicLandingToPlaylist to R.id.action_musicLanding_to_playlistActivity,
    MusicLandingToSeeAll to R.id.action_musicLanding_to_seeAllActivity,
    MusicMyLibraryToMyPlaylist to R.id.action_musicMyLibraryFragment_to_myPlaylistFragment,
    MusicCreateNewPlaylist to R.id.action_global_createNewPlaylistBottomSheet,
    MusicCreateNewPlaylistToMyPlaylist to R.id.action_createNewPlaylistBottomSheet_to_myPlaylistFragment
)
