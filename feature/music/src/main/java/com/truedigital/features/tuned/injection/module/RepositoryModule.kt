package com.truedigital.features.tuned.injection.module

import com.truedigital.common.share.datalegacy.wrapper.AuthManagerWrapper
import com.truedigital.features.tuned.api.MusicApiBuilder
import com.truedigital.features.tuned.application.configuration.Configuration
import com.truedigital.features.tuned.data.ObfuscatedKeyValueStoreInterface
import com.truedigital.features.tuned.data.ad.repository.AdRepository
import com.truedigital.features.tuned.data.ad.repository.AdRepositoryImpl
import com.truedigital.features.tuned.data.album.repository.AlbumRepository
import com.truedigital.features.tuned.data.album.repository.AlbumRepositoryImpl
import com.truedigital.features.tuned.data.artist.repository.ArtistRepository
import com.truedigital.features.tuned.data.artist.repository.ArtistRepositoryImpl
import com.truedigital.features.tuned.data.authentication.repository.AuthenticationTokenRepository
import com.truedigital.features.tuned.data.authentication.repository.AuthenticationTokenRepositoryImpl
import com.truedigital.features.tuned.data.database.repository.MusicRoomRepository
import com.truedigital.features.tuned.data.playlist.repository.PlaylistRepository
import com.truedigital.features.tuned.data.playlist.repository.PlaylistRepositoryImpl
import com.truedigital.features.tuned.data.profile.repository.ProfileRepository
import com.truedigital.features.tuned.data.profile.repository.ProfileRepositoryImpl
import com.truedigital.features.tuned.data.station.repository.StationRepository
import com.truedigital.features.tuned.data.station.repository.StationRepositoryImpl
import com.truedigital.features.tuned.data.stream.repository.StreamRepository
import com.truedigital.features.tuned.data.stream.repository.StreamRepositoryImpl
import com.truedigital.features.tuned.data.tag.repository.TagRepository
import com.truedigital.features.tuned.data.tag.repository.TagRepositoryImpl
import com.truedigital.features.tuned.data.track.repository.TrackRepository
import com.truedigital.features.tuned.data.track.repository.TrackRepositoryImpl
import com.truedigital.features.tuned.data.user.repository.MusicUserRepository
import com.truedigital.features.tuned.data.user.repository.MusicUserRepositoryImpl
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(
        @Named(SharePreferenceModule.KVS_USER) obfuscatedKeyValueStore: ObfuscatedKeyValueStoreInterface,
        @Named(NetworkModule.BASIC_SERVICES_RETROFIT) basicRetrofit: Retrofit,
        @Named(NetworkModule.AUTHENTICATED_SERVICES_RETROFIT) authenticatedRetrofit: Retrofit,
        @Named(NetworkModule.METADATA_RETROFIT) metadataRetrofit: Retrofit,
        config: Configuration
    ): MusicUserRepository =
        MusicUserRepositoryImpl(
            obfuscatedKeyValueStore,
            MusicApiBuilder.build(basicRetrofit),
            MusicApiBuilder.build(authenticatedRetrofit),
            MusicApiBuilder.build(metadataRetrofit),
            config
        )

    @Provides
    @Singleton
    fun provideAuthTokenRepository(
        @Named(SharePreferenceModule.KVS_AUTH_TOKEN) obfuscatedKeyValueStore: ObfuscatedKeyValueStoreInterface,
        @Named(NetworkModule.BASIC_SERVICES_RETROFIT) retrofit: Retrofit,
        config: Configuration,
        authManagerWrapper: AuthManagerWrapper
    ): AuthenticationTokenRepository =
        AuthenticationTokenRepositoryImpl(
            obfuscatedKeyValueStore,
            MusicApiBuilder.build(retrofit),
            config,
            authManagerWrapper
        )

    @Provides
    @Singleton
    fun provideStationRepository(
        @Named(NetworkModule.AUTHENTICATED_SERVICES_RETROFIT) servicesRetrofit: Retrofit,
        @Named(NetworkModule.METADATA_RETROFIT) metadataRetrofit: Retrofit,
        musicRoomRepository: MusicRoomRepository
    ): StationRepository =
        StationRepositoryImpl(
            MusicApiBuilder.build(servicesRetrofit),
            MusicApiBuilder.build(metadataRetrofit),
            musicRoomRepository
        )

    @Provides
    @Singleton
    fun provideStreamRepository(
        @Named(NetworkModule.AUTHENTICATED_SERVICES_RETROFIT) retrofit: Retrofit
    ): StreamRepository =
        StreamRepositoryImpl(MusicApiBuilder.build(retrofit))

    @Provides
    @Singleton
    fun provideArtistRepository(
        @Named(NetworkModule.AUTHENTICATED_SERVICES_RETROFIT) servicesRetrofit: Retrofit,
        @Named(NetworkModule.METADATA_RETROFIT) metadataRetrofit: Retrofit,
        musicRoomRepository: MusicRoomRepository
    ): ArtistRepository =
        ArtistRepositoryImpl(
            MusicApiBuilder.build(servicesRetrofit),
            MusicApiBuilder.build(metadataRetrofit),
            musicRoomRepository
        )

    @Provides
    @Singleton
    fun provideAlbumRepository(
        @Named(NetworkModule.AUTHENTICATED_SERVICES_RETROFIT) servicesRetrofit: Retrofit,
        @Named(NetworkModule.METADATA_RETROFIT) metadataRetrofit: Retrofit,
        musicRoomRepository: MusicRoomRepository
    ): AlbumRepository =
        AlbumRepositoryImpl(
            MusicApiBuilder.build(servicesRetrofit),
            MusicApiBuilder.build(metadataRetrofit),
            musicRoomRepository
        )

    @Provides
    @Singleton
    fun providePlaylistRepository(
        @Named(NetworkModule.AUTHENTICATED_SERVICES_RETROFIT) servicesRetrofit: Retrofit,
        @Named(NetworkModule.METADATA_RETROFIT) metadataRetrofit: Retrofit,
        musicRoomRepository: MusicRoomRepository
    ): PlaylistRepository =
        PlaylistRepositoryImpl(
            MusicApiBuilder.build(servicesRetrofit),
            MusicApiBuilder.build(metadataRetrofit),
            musicRoomRepository
        )

    @Provides
    @Singleton
    fun provideTrackRepository(
        @Named(NetworkModule.AUTHENTICATED_SERVICES_RETROFIT) servicesRetrofit: Retrofit,
        @Named(NetworkModule.METADATA_RETROFIT) metadataRetrofit: Retrofit
    ): TrackRepository =
        TrackRepositoryImpl(
            MusicApiBuilder.build(metadataRetrofit),
            MusicApiBuilder.build(servicesRetrofit)
        )

    @Provides
    @Singleton
    fun provideProfileRepository(
        @Named(NetworkModule.METADATA_RETROFIT) metadataRetrofit: Retrofit,
        @Named(NetworkModule.AUTHENTICATED_SERVICES_RETROFIT) servicesRetrofit: Retrofit
    ): ProfileRepository =
        ProfileRepositoryImpl(
            MusicApiBuilder.build(metadataRetrofit),
            MusicApiBuilder.build(servicesRetrofit)
        )

    @Provides
    @Singleton
    fun provideTagRepository(
        @Named(NetworkModule.METADATA_RETROFIT) metadataRetrofit: Retrofit,
        musicRoomRepository: MusicRoomRepository
    ): TagRepository =
        TagRepositoryImpl(
            MusicApiBuilder.build(metadataRetrofit),
            musicRoomRepository
        )

    @Provides
    @Singleton
    fun provideAdRepository(
        @Named(NetworkModule.AUTHENTICATED_SERVICES_RETROFIT) servicesRetrofit: Retrofit
    ): AdRepository =
        AdRepositoryImpl(
            MusicApiBuilder.build(servicesRetrofit)
        )
}
