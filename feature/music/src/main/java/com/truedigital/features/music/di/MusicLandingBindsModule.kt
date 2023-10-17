package com.truedigital.features.music.di

import com.truedigital.features.music.data.landing.repository.CacheMusicLandingRepository
import com.truedigital.features.music.data.landing.repository.CacheMusicLandingRepositoryImpl
import com.truedigital.features.music.data.landing.repository.MusicLandingRepository
import com.truedigital.features.music.data.landing.repository.MusicLandingRepositoryImpl
import com.truedigital.features.music.domain.landing.usecase.ClearCacheMusicLandingUseCase
import com.truedigital.features.music.domain.landing.usecase.ClearCacheMusicLandingUseCaseImpl
import com.truedigital.features.music.domain.landing.usecase.DecodeMusicHeroBannerDeeplinkUseCase
import com.truedigital.features.music.domain.landing.usecase.DecodeMusicHeroBannerDeeplinkUseCaseImpl
import com.truedigital.features.music.domain.landing.usecase.GetCacheMusicShelfDataUseCase
import com.truedigital.features.music.domain.landing.usecase.GetCacheMusicShelfDataUseCaseImpl
import com.truedigital.features.music.domain.landing.usecase.GetContentBaseShelfUseCase
import com.truedigital.features.music.domain.landing.usecase.GetContentBaseShelfUseCaseImpl
import com.truedigital.features.music.domain.landing.usecase.GetContentItemUseCase
import com.truedigital.features.music.domain.landing.usecase.GetContentItemUseCaseImpl
import com.truedigital.features.music.domain.landing.usecase.GetDataForTrackFAMusicLandingPageUseCase
import com.truedigital.features.music.domain.landing.usecase.GetDataForTrackFAMusicLandingPageUseCaseImpl
import com.truedigital.features.music.domain.landing.usecase.GetMusicBaseShelfUseCase
import com.truedigital.features.music.domain.landing.usecase.GetMusicBaseShelfUseCaseImpl
import com.truedigital.features.music.domain.landing.usecase.GetMusicForYouShelfUseCase
import com.truedigital.features.music.domain.landing.usecase.GetMusicForYouShelfUseCaseImpl
import com.truedigital.features.music.domain.landing.usecase.GetMusicUserByTagShelfUseCase
import com.truedigital.features.music.domain.landing.usecase.GetMusicUserByTagShelfUseCaseImpl
import com.truedigital.features.music.domain.landing.usecase.GetRadioMediaAssetIdUseCase
import com.truedigital.features.music.domain.landing.usecase.GetRadioMediaAssetIdUseCaseImpl
import com.truedigital.features.music.domain.landing.usecase.GetRadioUseCase
import com.truedigital.features.music.domain.landing.usecase.GetRadioUseCaseImpl
import com.truedigital.features.music.domain.landing.usecase.GetTagAlbumShelfUseCase
import com.truedigital.features.music.domain.landing.usecase.GetTagAlbumShelfUseCaseImpl
import com.truedigital.features.music.domain.landing.usecase.GetTagArtistShelfUseCase
import com.truedigital.features.music.domain.landing.usecase.GetTagArtistShelfUseCaseImpl
import com.truedigital.features.music.domain.landing.usecase.GetTagPlaylistShelfUseCase
import com.truedigital.features.music.domain.landing.usecase.GetTagPlaylistShelfUseCaseImpl
import com.truedigital.features.music.domain.landing.usecase.GetTrackPlaylistShelfUseCase
import com.truedigital.features.music.domain.landing.usecase.GetTrackPlaylistShelfUseCaseImpl
import com.truedigital.features.music.domain.landing.usecase.MapProductListTypeUseCase
import com.truedigital.features.music.domain.landing.usecase.MapProductListTypeUseCaseImpl
import com.truedigital.features.music.domain.landing.usecase.MapRadioUseCase
import com.truedigital.features.music.domain.landing.usecase.MapRadioUseCaseImpl
import com.truedigital.features.music.domain.landing.usecase.SaveCacheMusicShelfDataUseCase
import com.truedigital.features.music.domain.landing.usecase.SaveCacheMusicShelfDataUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
interface MusicLandingBindsModule {

    @Binds
    fun bindsCacheMusicLandingRepository(
        cacheMusicLandingRepositoryImpl: CacheMusicLandingRepositoryImpl
    ): CacheMusicLandingRepository

    @Binds
    fun bindsMusicLandingRepository(
        musicLandingRepositoryImpl: MusicLandingRepositoryImpl
    ): MusicLandingRepository

    @Binds
    fun bindsDecodeMusicHeroBannerDeeplinkUseCase(
        decodeMusicHeroBannerDeeplinkUseCaseImpl: DecodeMusicHeroBannerDeeplinkUseCaseImpl
    ): DecodeMusicHeroBannerDeeplinkUseCase

    @Binds
    fun bindsGetMusicBaseShelfUseCase(
        getMusicBaseShelfUseCaseImpl: GetMusicBaseShelfUseCaseImpl
    ): GetMusicBaseShelfUseCase

    @Binds
    fun bindsGetMusicForYouShelfUseCase(
        getMusicForYouShelfUseCaseImpl: GetMusicForYouShelfUseCaseImpl
    ): GetMusicForYouShelfUseCase

    @Binds
    fun bindsGetMusicUserByTagShelfUseCase(
        getMusicUserByTagShelfUseCaseImpl: GetMusicUserByTagShelfUseCaseImpl
    ): GetMusicUserByTagShelfUseCase

    @Binds
    fun bindsGetTrackPlaylistShelfUseCase(
        getTrackPlaylistShelfUseCaseImpl: GetTrackPlaylistShelfUseCaseImpl
    ): GetTrackPlaylistShelfUseCase

    @Binds
    fun bindsGetTagAlbumShelfUseCase(
        getTagAlbumShelfUseCaseImpl: GetTagAlbumShelfUseCaseImpl
    ): GetTagAlbumShelfUseCase

    @Binds
    fun bindsGetTagArtistShelfUseCase(
        getTagArtistShelfUseCaseImpl: GetTagArtistShelfUseCaseImpl
    ): GetTagArtistShelfUseCase

    @Binds
    fun bindsGetTagPlaylistShelfUseCase(
        getTagPlaylistShelfUseCaseImpl: GetTagPlaylistShelfUseCaseImpl
    ): GetTagPlaylistShelfUseCase

    @Binds
    fun bindsMapProductListTypeUseCase(
        mapProductListTypeUseCaseImpl: MapProductListTypeUseCaseImpl
    ): MapProductListTypeUseCase

    @Binds
    fun bindsSaveCacheMusicShelfDataUseCase(
        saveCacheMusicShelfDataUseCaseImpl: SaveCacheMusicShelfDataUseCaseImpl
    ): SaveCacheMusicShelfDataUseCase

    @Binds
    fun bindsGetCacheMusicShelfDataUseCase(
        getCacheMusicShelfDataUseCaseImpl: GetCacheMusicShelfDataUseCaseImpl
    ): GetCacheMusicShelfDataUseCase

    @Binds
    fun bindsClearCacheMusicLandingUseCase(
        clearCacheMusicLandingUseCaseImpl: ClearCacheMusicLandingUseCaseImpl
    ): ClearCacheMusicLandingUseCase

    @Binds
    fun bindsGetDataForTrackFAMusicLandingPageUseCase(
        getDataForTrackFAMusicLandingPageUseCaseImpl: GetDataForTrackFAMusicLandingPageUseCaseImpl
    ): GetDataForTrackFAMusicLandingPageUseCase

    @Binds
    fun bindsGetRadioUseCase(
        getRadioUseCaseImpl: GetRadioUseCaseImpl
    ): GetRadioUseCase

    @Binds
    fun bindsMapRadioUseCase(
        mapRadioUseCaseImpl: MapRadioUseCaseImpl
    ): MapRadioUseCase

    @Binds
    fun bindsGetRadioMediaAssetIdUseCase(
        getRadioMediaAssetIdUseCaseImpl: GetRadioMediaAssetIdUseCaseImpl
    ): GetRadioMediaAssetIdUseCase

    @Binds
    fun bindsGetContentBaseShelfUseCase(
        getContentBaseShelfUseCaseImpl: GetContentBaseShelfUseCaseImpl
    ): GetContentBaseShelfUseCase

    @Binds
    fun bindsGetContentItemUseCase(
        getContentItemUseCaseImpl: GetContentItemUseCaseImpl
    ): GetContentItemUseCase
}
