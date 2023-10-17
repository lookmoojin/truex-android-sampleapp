package com.truedigital.features.music.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.truedigital.features.music.presentation.addsong.AddSongViewModel
import com.truedigital.features.music.presentation.addtomyplaylist.AddToMyPlaylistViewModel
import com.truedigital.features.music.presentation.createnewplaylist.CreateNewPlaylistViewModel
import com.truedigital.features.music.presentation.forceloginbanner.ForceLoginBannerViewModel
import com.truedigital.features.music.presentation.landing.MusicLandingActionViewModel
import com.truedigital.features.music.presentation.landing.MusicLandingTrackFAViewModel
import com.truedigital.features.music.presentation.landing.MusicLandingViewModel
import com.truedigital.features.music.presentation.musicshare.MusicShareViewModel
import com.truedigital.features.music.presentation.mylibrary.MusicMyLibraryNavigationViewModel
import com.truedigital.features.music.presentation.mylibrary.MusicMyLibraryViewModel
import com.truedigital.features.music.presentation.mylibrary.mymusic.MyMusicViewModel
import com.truedigital.features.music.presentation.myplaylist.MyPlaylistImageViewModel
import com.truedigital.features.music.presentation.myplaylist.MyPlaylistViewModel
import com.truedigital.features.music.presentation.player.MusicPlayerAdsViewModel
import com.truedigital.features.music.presentation.player.MusicPlayerNavigationViewModel
import com.truedigital.features.music.presentation.player.MusicPlayerStateViewModel
import com.truedigital.features.music.presentation.player.MusicPlayerViewModel
import com.truedigital.features.music.presentation.search.MusicSearchViewModel
import com.truedigital.features.music.presentation.searchtrending.MusicSearchTrendingViewModel
import com.truedigital.features.music.widget.ads.MusicAdsBannerViewModel
import com.truedigital.foundation.di.scopes.ViewModelKey
import com.truedigital.foundation.presentations.ViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface MusicViewModelsModule {

    @Binds
    fun bindsViewModelFactory(
        viewModelFactory: ViewModelFactory
    ): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(AddSongViewModel::class)
    fun bindsAddSongViewModel(
        addSongViewModel: AddSongViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AddToMyPlaylistViewModel::class)
    fun bindsAddToMyPlaylistViewModel(
        addToMyPlaylistViewModel: AddToMyPlaylistViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ForceLoginBannerViewModel::class)
    fun bindsForceLoginBannerViewModel(
        forceLoginBannerViewModel: ForceLoginBannerViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MusicPlayerAdsViewModel::class)
    fun bindsMusicPlayerAdsViewModel(
        musicPlayerAdsViewModel: MusicPlayerAdsViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MusicPlayerStateViewModel::class)
    fun bindsMusicPlayerStateViewModel(
        musicPlayerStateViewModel: MusicPlayerStateViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MusicPlayerViewModel::class)
    fun bindsMusicPlayerViewModel(
        musicPlayerViewModel: MusicPlayerViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MusicPlayerNavigationViewModel::class)
    fun bindsMusicPlayerNavigationViewModel(
        musicPlayerNavigationViewModel: MusicPlayerNavigationViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MyMusicViewModel::class)
    fun bindsMyMusicViewModel(
        myMusicViewModel: MyMusicViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MusicMyLibraryNavigationViewModel::class)
    fun bindsMusicMyLibraryNavigationViewModel(
        musicMyLibraryNavigationViewModel: MusicMyLibraryNavigationViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MusicMyLibraryViewModel::class)
    fun bindsMusicMyLibraryViewModel(
        musicMyLibraryViewModel: MusicMyLibraryViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MusicLandingViewModel::class)
    fun bindsMusicLandingViewModel(
        musicLandingViewModel: MusicLandingViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MusicLandingTrackFAViewModel::class)
    fun bindsMusicLandingTrackFAViewModel(
        musicLandingTrackFAViewModel: MusicLandingTrackFAViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MyPlaylistViewModel::class)
    fun bindsMyPlaylistViewModel(
        myPlaylistViewModel: MyPlaylistViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MyPlaylistImageViewModel::class)
    fun bindsMyPlaylistImageViewModel(
        myPlaylistImageViewModel: MyPlaylistImageViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CreateNewPlaylistViewModel::class)
    fun bindsCreateNewPlaylistViewModel(
        createNewPlaylistViewModel: CreateNewPlaylistViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MusicSearchViewModel::class)
    fun bindsMusicSearchViewModel(
        musicSearchViewModel: MusicSearchViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MusicShareViewModel::class)
    fun bindsMusicShareViewModel(
        musicShareViewModel: MusicShareViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MusicSearchTrendingViewModel::class)
    fun bindsMusicSearchTrendingViewModel(
        musicSearchTrendingViewModel: MusicSearchTrendingViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MusicAdsBannerViewModel::class)
    fun bindsMusicAdsBannerViewModel(
        musicAdsBannerViewModel: MusicAdsBannerViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MusicLandingActionViewModel::class)
    fun bindsMusicLandingActionViewModel(
        musicLandingActionViewModelImpl: MusicLandingActionViewModel
    ): ViewModel
}
