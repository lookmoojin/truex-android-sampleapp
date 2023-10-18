package com.truedigital.features.tuned.domain.facade.album

import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.album.repository.AlbumRepository
import com.truedigital.features.tuned.data.authentication.repository.AuthenticationTokenRepository
import com.truedigital.features.tuned.data.cache.repository.CacheRepository
import com.truedigital.features.tuned.data.device.repository.DeviceRepository
import com.truedigital.features.tuned.data.setting.repository.SettingRepository
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.track.model.request.TrackRequestType
import com.truedigital.features.tuned.data.track.repository.TrackRepository
import com.truedigital.features.tuned.data.user.repository.MusicUserRepository
import io.reactivex.Single
import javax.inject.Inject

class AlbumFacadeImpl @Inject constructor(
    private val albumRepository: AlbumRepository,
    private val trackRepository: TrackRepository,
    private val musicUserRepository: MusicUserRepository,
    private val deviceRepository: DeviceRepository,
    private val settingRepository: SettingRepository,
    private val cacheRepository: CacheRepository,
    private val authenticationTokenRepository: AuthenticationTokenRepository
) : AlbumFacade {

    override fun loadAlbum(id: Int) =
        albumRepository.get(id)

    override fun loadMoreFromArtist(id: Int) =
        albumRepository.getMoreFromArtist(id)

    override fun loadTrack(id: Int): Single<Track> =
        trackRepository.get(id)

    override fun loadTracks(album: Album, ids: List<Int>): Single<List<Track>> =
        albumRepository.getTracks(album.primaryRelease?.id ?: 0, TrackRequestType.AUDIO)
            .map { listTrack ->
                listTrack.map { track ->
                    track.isCached = cacheRepository.getTrackLocationIfExist(track.id) != null
                    track
                }.sortedBy { it.trackNumber }
            }

    override fun loadFavourited(id: Int) =
        albumRepository.isFavourited(id)

    override fun toggleFavourite(album: Album): Single<Any> =
        loadFavourited(album.primaryRelease?.id ?: 0).flatMap {
            if (it) {
                albumRepository.removeFavourite(album.primaryRelease?.id ?: 0)
            } else {
                albumRepository.addFavourite(album.primaryRelease?.id ?: 0)
            }
        }

    override fun getAlbumNavigationAllowed() =
        musicUserRepository.getSettings()?.allowAlbumNavigation ?: false

    override fun getAllowMobileData() =
        settingRepository.allowMobileDataStreaming() || deviceRepository.isWifiConnected()

    override fun getIsEmulator(): Boolean = deviceRepository.isEmulator()

    override fun getHasAlbumShuffleRight(): Boolean =
        authenticationTokenRepository.getCurrentToken()?.hasAlbumShuffleRight ?: false
}
