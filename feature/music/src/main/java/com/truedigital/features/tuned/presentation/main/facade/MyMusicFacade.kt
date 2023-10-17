package com.truedigital.features.tuned.presentation.main.facade

import io.reactivex.Single

interface MyMusicFacade {
    fun getFollowedArtistCount(): Single<Int>
    fun getFavouritedAlbumCount(): Single<Int>
    fun getFavouritedSongCount(): Single<Int>
    fun getFavouritedPlaylistCount(): Single<Int>
}
