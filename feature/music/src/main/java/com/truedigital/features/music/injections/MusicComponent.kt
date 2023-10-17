package com.truedigital.features.music.injections

import com.truedigital.common.share.analytics.injections.AnalyticsSubComponent
import com.truedigital.common.share.data.coredata.injections.CoreDataSubComponent
import com.truedigital.common.share.datalegacy.injections.DataLegacySubComponent
import com.truedigital.common.share.nativeshare.injections.LinkGeneratorSubComponent
import com.truedigital.common.share.webview.externalbrowser.injections.ExternalBrowserSubComponent
import com.truedigital.core.injections.CoreSubComponent
import com.truedigital.features.listens.share.injection.ListenShareSubComponent
import com.truedigital.features.music.data.forceloginbanner.repository.MusicConfigRepository
import com.truedigital.features.music.di.MusicAddSongModule
import com.truedigital.features.music.di.MusicAuthenticationBindsModule
import com.truedigital.features.music.di.MusicAuthenticationModule
import com.truedigital.features.music.di.MusicConfigModule
import com.truedigital.features.music.di.MusicForceLoginBannerModule
import com.truedigital.features.music.di.MusicGeoBlockModule
import com.truedigital.features.music.di.MusicLandingBindsModule
import com.truedigital.features.music.di.MusicLandingModule
import com.truedigital.features.music.di.MusicModule
import com.truedigital.features.music.di.MusicMyLibraryModule
import com.truedigital.features.music.di.MusicMyPlaylistModule
import com.truedigital.features.music.di.MusicPlayerModule
import com.truedigital.features.music.di.MusicPlaylistBindsModule
import com.truedigital.features.music.di.MusicPlaylistModule
import com.truedigital.features.music.di.MusicSearchBindsModule
import com.truedigital.features.music.di.MusicSearchModule
import com.truedigital.features.music.di.MusicTrackBindsModule
import com.truedigital.features.music.di.MusicTrackModule
import com.truedigital.features.music.di.MusicTrendingBindsModule
import com.truedigital.features.music.di.MusicTrendingModule
import com.truedigital.features.music.di.MusicViewModelsModule
import com.truedigital.features.music.domain.landing.usecase.ClearCacheMusicLandingUseCase
import com.truedigital.features.music.domain.landing.usecase.GetTagAlbumShelfUseCase
import com.truedigital.features.music.domain.landing.usecase.GetTagPlaylistShelfUseCase
import com.truedigital.features.music.domain.landing.usecase.GetTrackPlaylistShelfUseCase
import com.truedigital.features.music.domain.logout.usecase.LogoutMusicUseCase
import com.truedigital.features.music.domain.player.usecase.GetMusicPlayerVisibleUseCase
import com.truedigital.features.music.domain.player.usecase.SetLandingOnListenScopeUseCase
import com.truedigital.features.music.domain.queue.usecase.ClearCacheTrackQueueUseCase
import com.truedigital.features.music.manager.player.MusicPlayerActionManager
import com.truedigital.features.music.presentation.addsong.AddSongBottomSheetDialogFragment
import com.truedigital.features.music.presentation.addtomyplaylist.AddToMyPlaylistDialogFragment
import com.truedigital.features.music.presentation.createnewplaylist.CreateNewPlaylistBottomSheetDialogFragment
import com.truedigital.features.music.presentation.forceloginbanner.ForceLoginBannerViewModel
import com.truedigital.features.music.presentation.landing.MusicLandingActionViewModel
import com.truedigital.features.music.presentation.landing.MusicLandingFragment
import com.truedigital.features.music.presentation.landing.viewholder.MusicForYouShelfViewHolder
import com.truedigital.features.music.presentation.mylibrary.MusicMyLibraryFragment
import com.truedigital.features.music.presentation.mylibrary.mymusic.MyMusicFragment
import com.truedigital.features.music.presentation.myplaylist.MyPlaylistFragment
import com.truedigital.features.music.presentation.player.MusicPlayerViewModel
import com.truedigital.features.music.presentation.searchlanding.MusicSearchLandingFragment
import com.truedigital.features.music.presentation.searchtrending.MusicSearchTrendingFragment
import com.truedigital.features.music.presentation.searchtrending.viewholder.MusicSearchTrendingSectionAlbumViewHolder
import com.truedigital.features.music.presentation.searchtrending.viewholder.MusicSearchTrendingSectionArtistViewHolder
import com.truedigital.features.music.presentation.searchtrending.viewholder.MusicSearchTrendingSectionPlaylistViewHolder
import com.truedigital.features.music.widget.ads.MusicAdsBannerWidget
import com.truedigital.features.music.widget.player.MusicSeekBarWidget
import com.truedigital.features.tuned.data.ObfuscatedKeyValueStoreInterface
import com.truedigital.features.tuned.injection.component.ApplicationComponent
import com.truedigital.features.tuned.injection.component.InstanceComponent
import com.truedigital.features.tuned.injection.module.ApplicationModule
import com.truedigital.features.tuned.injection.module.ConfigurationModule
import com.truedigital.features.tuned.injection.module.MusicRoomDatabaseModule
import com.truedigital.features.tuned.injection.module.NetworkModule
import com.truedigital.features.tuned.injection.module.RepositoryBindsModule
import com.truedigital.features.tuned.injection.module.RepositoryModule
import com.truedigital.features.tuned.injection.module.SharePreferenceModule
import com.truedigital.features.tuned.injection.module.UseCaseModule
import com.truedigital.features.tuned.injection.module.viewmodel.ViewModelModule
import com.truedigital.navigation.injections.NavigationSubComponent
import com.truedigital.share.data.firestoreconfig.injections.FirestoreConfigSubComponent
import com.truedigital.share.data.geoinformation.injections.GeoInformationSubComponent
import com.truedigital.share.data.prasarn.injections.PrasarnSubComponent
import dagger.Component
import dagger.Subcomponent
import javax.inject.Named
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        MusicModule::class,
        MusicAddSongModule::class,
        MusicAuthenticationModule::class,
        MusicAuthenticationBindsModule::class,
        MusicConfigModule::class,
        MusicForceLoginBannerModule::class,
        MusicGeoBlockModule::class,
        MusicLandingModule::class,
        MusicLandingBindsModule::class,
        MusicMyLibraryModule::class,
        MusicMyPlaylistModule::class,
        MusicPlayerModule::class,
        MusicPlaylistModule::class,
        MusicPlaylistBindsModule::class,
        MusicSearchModule::class,
        MusicSearchBindsModule::class,
        MusicTrackModule::class,
        MusicTrackBindsModule::class,
        MusicTrendingModule::class,
        MusicTrendingBindsModule::class,
        MusicViewModelsModule::class,

        // From Tuned
        ApplicationModule::class,
        ConfigurationModule::class,
        MusicRoomDatabaseModule::class,
        NetworkModule::class,
        RepositoryModule::class,
        RepositoryBindsModule::class,
        SharePreferenceModule::class,
        UseCaseModule::class,
        ViewModelModule::class
    ],
    dependencies = [
        AnalyticsSubComponent::class,
        CoreSubComponent::class,
        CoreDataSubComponent::class,
        DataLegacySubComponent::class,
        ExternalBrowserSubComponent::class,
        FirestoreConfigSubComponent::class,
        GeoInformationSubComponent::class,
        NavigationSubComponent::class,
        LinkGeneratorSubComponent::class,
        PrasarnSubComponent::class,
        ListenShareSubComponent::class
    ]
)
interface MusicComponent {

    companion object {

        private lateinit var musicComponent: MusicComponent

        fun initialize(musicComponent: MusicComponent) {
            this.musicComponent = musicComponent
        }

        fun getInstance(): MusicComponent {
            if (!(::musicComponent.isInitialized)) {
                error("MusicComponent not initialize")
            }
            return musicComponent
        }
    }

    @Component.Factory
    interface Factory {
        fun create(
            analyticsSubComponent: AnalyticsSubComponent,
            coreSubComponent: CoreSubComponent,
            coreDataSubComponent: CoreDataSubComponent,
            dataLegacySubComponent: DataLegacySubComponent,
            externalBrowserSubComponent: ExternalBrowserSubComponent,
            firestoreConfigSubComponent: FirestoreConfigSubComponent,
            geoInformationSubComponent: GeoInformationSubComponent,
            navigationSubComponent: NavigationSubComponent,
            linkGeneratorSubComponent: LinkGeneratorSubComponent,
            prasarnSubComponent: PrasarnSubComponent,
            listenShareSubComponent: ListenShareSubComponent
        ): MusicComponent
    }

    fun inject(fragment: AddSongBottomSheetDialogFragment)
    fun inject(fragment: MusicMyLibraryFragment)
    fun inject(fragment: MusicLandingFragment)
    fun inject(fragment: MyPlaylistFragment)
    fun inject(fragment: CreateNewPlaylistBottomSheetDialogFragment)
    fun inject(fragment: AddToMyPlaylistDialogFragment)
    fun inject(fragment: MusicSearchTrendingFragment)
    fun inject(fragment: MyMusicFragment)
    fun inject(fragment: MusicSearchLandingFragment)

    fun inject(widget: MusicAdsBannerWidget)
    fun inject(widget: MusicSeekBarWidget)

    fun inject(holder: MusicForYouShelfViewHolder)
    fun inject(holder: MusicSearchTrendingSectionArtistViewHolder)
    fun inject(holder: MusicSearchTrendingSectionPlaylistViewHolder)
    fun inject(holder: MusicSearchTrendingSectionAlbumViewHolder)

    fun getInstanceComponent(): InstanceComponent
    fun getApplicationComponent(): ApplicationComponent
    fun getMusicSubComponent(): MusicSubComponent
}

@Subcomponent
interface MusicSubComponent {
    @Named(SharePreferenceModule.KVS_USER)
    fun getObfuscatedKeyValueStoreInterface(): ObfuscatedKeyValueStoreInterface

    fun getMusicConfigRepository(): MusicConfigRepository

    fun getClearCacheTrackQueueUseCase(): ClearCacheTrackQueueUseCase
    fun getClearCacheMusicLandingUseCase(): ClearCacheMusicLandingUseCase
    fun getLogoutMusicUseCase(): LogoutMusicUseCase
    fun getGetTagPlaylistShelfUseCase(): GetTagPlaylistShelfUseCase
    fun getGetTrackPlaylistShelfUseCase(): GetTrackPlaylistShelfUseCase
    fun getSetLandingOnListenScopeUseCase(): SetLandingOnListenScopeUseCase
    fun getGetTagAlbumShelfUseCase(): GetTagAlbumShelfUseCase
    fun getGetMusicPlayerVisibleUseCase(): GetMusicPlayerVisibleUseCase

    fun getMusicPlayerViewModel(): MusicPlayerViewModel
    fun getForceLoginBannerViewModel(): ForceLoginBannerViewModel
    fun getMusicLandingActionViewModel(): MusicLandingActionViewModel

    fun getMusicPlayerActionManager(): MusicPlayerActionManager
}
