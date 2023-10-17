package com.truedigital.features.music.presentation.mylibrary

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.truedigital.features.music.domain.usecase.router.MusicRouterUseCase
import com.truedigital.features.music.navigation.router.MusicCreateNewPlaylist
import com.truedigital.features.music.navigation.router.MusicMyLibraryToMyPlaylist
import com.truedigital.features.music.presentation.myplaylist.MyPlaylistFragment
import com.truedigital.navigation.domain.usecase.SetRouterSecondaryToNavControllerUseCase
import javax.inject.Inject

class MusicMyLibraryViewModel @Inject constructor(
    private val router: MusicRouterUseCase,
    private val setRouterSecondaryToNavControllerUseCase: SetRouterSecondaryToNavControllerUseCase,
) : ViewModel() {

    fun navigateToMyPlaylist(playlistId: Int) {
        val bundle = Bundle().apply {
            putInt(MyPlaylistFragment.PLAYLIST_ID_SLUG, playlistId)
        }
        router.execute(MusicMyLibraryToMyPlaylist, bundle)
    }

    fun navigateToCreateNewPlaylist() {
        router.execute(MusicCreateNewPlaylist)
    }

    fun setRouterSecondaryToNavController(navController: NavController) {
        setRouterSecondaryToNavControllerUseCase.execute(navController = navController)
    }
}
