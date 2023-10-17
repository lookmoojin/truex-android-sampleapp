package com.truedigital.features.tuned.injection.component

import com.truedigital.features.music.presentation.forceloginbanner.ForceLoginBannerFragment
import com.truedigital.features.music.presentation.landing.MusicLandingFragment
import com.truedigital.features.music.presentation.myplaylist.MyPlaylistFragment
import com.truedigital.features.music.presentation.player.MusicPlayerFragment
import com.truedigital.features.music.presentation.search.MusicSearchFragment
import com.truedigital.features.music.widget.favorite.MusicFavoriteWidget
import com.truedigital.features.music.widget.player.MusicPlayPauseWidget
import com.truedigital.features.music.widget.player.MusicRepeatWidget
import com.truedigital.features.music.widget.player.MusicShuffleWidget
import com.truedigital.features.tuned.presentation.album.view.AlbumActivity
import com.truedigital.features.tuned.presentation.artist.view.ArtistActivity
import com.truedigital.features.tuned.presentation.bottomsheet.view.BottomSheetProductPicker
import com.truedigital.features.tuned.presentation.common.TunedActivity
import com.truedigital.features.tuned.presentation.common.TunedFragment
import com.truedigital.features.tuned.presentation.main.view.MyMusicView
import com.truedigital.features.tuned.presentation.player.view.PlayerQueueActivity
import com.truedigital.features.tuned.presentation.player.view.PlayerSettingActivity
import com.truedigital.features.tuned.presentation.playlist.view.PlaylistActivity
import com.truedigital.features.tuned.presentation.popups.view.FullScreenImageDialog
import com.truedigital.features.tuned.presentation.popups.view.LossOfNetworkDialog
import com.truedigital.features.tuned.presentation.popups.view.UpgradePremiumDialog
import com.truedigital.features.tuned.presentation.productlist.view.ProductListActivity
import com.truedigital.features.tuned.presentation.station.view.StationActivity
import com.truedigital.features.tuned.presentation.station.view.StationOverviewView
import com.truedigital.features.tuned.presentation.station.view.TuningView
import com.truedigital.features.tuned.presentation.tag.view.TagActivity
import com.truedigital.features.tuned.service.music.MusicPlayerServiceImpl
import dagger.Subcomponent

@Subcomponent
interface InstanceComponent {

    // region Activities
    fun inject(activity: StationActivity)
    fun inject(activity: ArtistActivity)
    fun inject(activity: AlbumActivity)
    fun inject(activity: PlayerQueueActivity)
    fun inject(activity: PlayerSettingActivity)
    fun inject(activity: PlaylistActivity)
    fun inject(activity: ProductListActivity)
    fun inject(activity: TagActivity)
    fun inject(activity: TunedActivity)

    // region Fragments
    fun inject(fragment: ForceLoginBannerFragment)
    fun inject(musicLandingFragment: MusicLandingFragment)
    fun inject(musicPlayerFragment: MusicPlayerFragment)
    fun inject(musicSearchFragment: MusicSearchFragment)
    fun inject(myPlaylistFragment: MyPlaylistFragment)
    fun inject(tunedFragment: TunedFragment)

    // region Services
    fun inject(service: MusicPlayerServiceImpl)

    // region Views
    fun inject(view: MyMusicView)
    fun inject(view: StationOverviewView)
    fun inject(view: TuningView)

    // region Dialogs
    fun inject(dialog: UpgradePremiumDialog)
    fun inject(dialog: LossOfNetworkDialog)
    fun inject(dialog: BottomSheetProductPicker)
    fun inject(dialog: FullScreenImageDialog)

    // region Widget
    fun inject(widget: MusicFavoriteWidget)
    fun inject(widget: MusicPlayPauseWidget)
    fun inject(widget: MusicRepeatWidget)
    fun inject(widget: MusicShuffleWidget)
}
