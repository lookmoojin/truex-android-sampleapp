package com.truedigital.features.tuned.injection.module

import com.truedigital.features.music.domain.track.usecase.GetTrackUseCase
import com.truedigital.features.music.domain.track.usecase.GetTrackUseCaseImpl
import com.truedigital.features.tuned.domain.facade.MusicPlayerFacadeImpl
import com.truedigital.features.tuned.domain.facade.MyMusicFacadeImpl
import com.truedigital.features.tuned.domain.facade.PlayerSettingFacadeImpl
import com.truedigital.features.tuned.domain.facade.StationFacadeImpl
import com.truedigital.features.tuned.domain.facade.StationOverviewFacadeImpl
import com.truedigital.features.tuned.domain.facade.TuningFacadeImpl
import com.truedigital.features.tuned.domain.facade.album.AlbumFacade
import com.truedigital.features.tuned.domain.facade.album.AlbumFacadeImpl
import com.truedigital.features.tuned.domain.facade.artist.ArtistFacade
import com.truedigital.features.tuned.domain.facade.artist.ArtistFacadeImpl
import com.truedigital.features.tuned.domain.facade.authentication.AuthenticationThirdPartyJWTUseCase
import com.truedigital.features.tuned.domain.facade.authentication.AuthenticationThirdPartyJWTUseCaseImpl
import com.truedigital.features.tuned.domain.facade.authentication.GetAuthenticatedUserUseCase
import com.truedigital.features.tuned.domain.facade.authentication.GetAuthenticatedUserUseCaseImpl
import com.truedigital.features.tuned.domain.facade.authentication.MusicAuthenticationFacadeImpl
import com.truedigital.features.tuned.domain.facade.bottomsheetproduct.BottomSheetProductFacade
import com.truedigital.features.tuned.domain.facade.bottomsheetproduct.BottomSheetProductFacadeImpl
import com.truedigital.features.tuned.domain.facade.lostnetwork.LossOfNetworkFacade
import com.truedigital.features.tuned.domain.facade.lostnetwork.LossOfNetworkFacadeImpl
import com.truedigital.features.tuned.domain.facade.playerqueue.PlayerQueueFacade
import com.truedigital.features.tuned.domain.facade.playerqueue.PlayerQueueFacadeImpl
import com.truedigital.features.tuned.domain.facade.playlist.PlaylistFacade
import com.truedigital.features.tuned.domain.facade.playlist.PlaylistFacadeImpl
import com.truedigital.features.tuned.domain.facade.productlist.ProductListFacade
import com.truedigital.features.tuned.domain.facade.productlist.ProductListFacadeImpl
import com.truedigital.features.tuned.domain.facade.tag.TagFacade
import com.truedigital.features.tuned.domain.facade.tag.TagFacadeImpl
import com.truedigital.features.tuned.domain.usecase.data.DeleteRoomDataUseCase
import com.truedigital.features.tuned.domain.usecase.data.DeleteRoomDataUseCaseImpl
import com.truedigital.features.tuned.presentation.main.facade.MusicAuthenticationFacade
import com.truedigital.features.tuned.presentation.main.facade.MyMusicFacade
import com.truedigital.features.tuned.presentation.player.facade.PlayerSettingFacade
import com.truedigital.features.tuned.presentation.station.facade.StationFacade
import com.truedigital.features.tuned.presentation.station.facade.StationOverviewFacade
import com.truedigital.features.tuned.presentation.station.facade.TuningFacade
import com.truedigital.features.tuned.service.music.facade.MusicPlayerFacade
import dagger.Binds
import dagger.Module

@Module
interface UseCaseModule {

    @Binds
    fun bindsMusicAuthenticationFacade(
        musicAuthenticationFacadeImpl: MusicAuthenticationFacadeImpl
    ): MusicAuthenticationFacade

    @Binds
    fun bindsAuthenticationThirdPartyJWTUseCase(
        authenticationThirdPartyJWTUseCaseImpl: AuthenticationThirdPartyJWTUseCaseImpl
    ): AuthenticationThirdPartyJWTUseCase

    @Binds
    fun bindsGetAuthenticatedUserUseCase(
        getAuthenticatedUserUseCaseImpl: GetAuthenticatedUserUseCaseImpl
    ): GetAuthenticatedUserUseCase

    @Binds
    fun bindsMyMusicFacade(
        myMusicFacadeImpl: MyMusicFacadeImpl
    ): MyMusicFacade

    @Binds
    fun bindsStationOverviewFacade(
        stationOverviewFacadeImpl: StationOverviewFacadeImpl
    ): StationOverviewFacade

    @Binds
    fun bindsArtistFacade(
        artistFacadeImpl: ArtistFacadeImpl
    ): ArtistFacade

    @Binds
    fun bindsAlbumFacade(
        albumFacadeImpl: AlbumFacadeImpl
    ): AlbumFacade

    @Binds
    fun bindsStationFacade(
        stationFacadeImpl: StationFacadeImpl
    ): StationFacade

    @Binds
    fun bindsTuningFacade(
        tuningFacadeImpl: TuningFacadeImpl
    ): TuningFacade

    @Binds
    fun bindsMusicPlayerFacade(
        musicPlayerFacadeImpl: MusicPlayerFacadeImpl
    ): MusicPlayerFacade

    @Binds
    fun bindsLossOfNetworkFacade(
        lossOfNetworkFacadeImpl: LossOfNetworkFacadeImpl
    ): LossOfNetworkFacade

    @Binds
    fun bindsPlayerQueueFacade(
        playerQueueFacadeImpl: PlayerQueueFacadeImpl
    ): PlayerQueueFacade

    @Binds
    fun bindsPlayerSettingFacade(
        playerSettingFacadeImpl: PlayerSettingFacadeImpl
    ): PlayerSettingFacade

    @Binds
    fun bindsPlaylistFacade(
        playlistFacadeImpl: PlaylistFacadeImpl
    ): PlaylistFacade

    @Binds
    fun bindsBottomSheetProductFacade(
        bottomSheetProductFacadeImpl: BottomSheetProductFacadeImpl
    ): BottomSheetProductFacade

    @Binds
    fun bindsProductListFacade(
        productListFacadeImpl: ProductListFacadeImpl
    ): ProductListFacade

    @Binds
    fun bindsTagFacade(
        tagFacadeImpl: TagFacadeImpl
    ): TagFacade

    @Binds
    fun bindsGetTrackUseCase(
        getTrackUseCaseImpl: GetTrackUseCaseImpl
    ): GetTrackUseCase

    @Binds
    fun bindsDeleteRoomDataUseCase(
        deleteRoomDataUseCaseImpl: DeleteRoomDataUseCaseImpl
    ): DeleteRoomDataUseCase
}
