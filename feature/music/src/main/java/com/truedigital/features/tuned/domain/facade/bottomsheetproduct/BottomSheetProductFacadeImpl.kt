package com.truedigital.features.tuned.domain.facade.bottomsheetproduct

import com.truedigital.features.tuned.common.Constants.INT_3
import com.truedigital.features.tuned.common.Constants.INT_99
import com.truedigital.features.tuned.data.album.repository.AlbumRepository
import com.truedigital.features.tuned.data.artist.repository.ArtistRepository
import com.truedigital.features.tuned.data.cache.repository.CacheRepository
import com.truedigital.features.tuned.data.playlist.repository.PlaylistRepository
import com.truedigital.features.tuned.data.product.model.Product
import com.truedigital.features.tuned.data.profile.repository.ProfileRepository
import com.truedigital.features.tuned.data.station.repository.StationRepository
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.track.model.request.TrackRequestType
import com.truedigital.features.tuned.data.track.repository.TrackRepository
import com.truedigital.features.tuned.data.user.repository.MusicUserRepository
import com.truedigital.features.tuned.presentation.bottomsheet.ProductPickerType
import io.reactivex.Single
import javax.inject.Inject

class BottomSheetProductFacadeImpl @Inject constructor(
    private val stationRepository: StationRepository,
    private val artistRepository: ArtistRepository,
    private val albumRepository: AlbumRepository,
    private val playlistRepository: PlaylistRepository,
    private val trackRepository: TrackRepository,
    private val musicUserRepository: MusicUserRepository,
    private val cacheRepository: CacheRepository,
    private val profileRepository: ProfileRepository
) : BottomSheetProductFacade {

    companion object {
        private const val PRODUCT_NOT_SUPPORT_API = "This product type does not support this api"
    }

    override fun getProduct(id: Int, type: ProductPickerType?): Single<Product> =
        when (type) {
            ProductPickerType.ALBUM -> albumRepository.get(id)
            ProductPickerType.ARTIST -> artistRepository.get(id)
            ProductPickerType.MIX -> stationRepository.get(id)
            ProductPickerType.PLAYLIST,
            ProductPickerType.PLAYLIST_VERIFIED_OWNER -> playlistRepository.get(id)
            ProductPickerType.PLAYLIST_OWNER -> playlistRepository.get(id, true)
            ProductPickerType.PROFILE -> profileRepository.get(id)
            ProductPickerType.SONG,
            ProductPickerType.VIDEO,
            ProductPickerType.ARTIST_SONG,
            ProductPickerType.ALBUM_SONG,
            ProductPickerType.PLAYLIST_SONG,
            ProductPickerType.SONG_PLAYER,
            ProductPickerType.SONG_QUEUE,
            ProductPickerType.VIDEO_PLAYER -> trackRepository.get(id)
            else ->
                Single.error(IllegalStateException(PRODUCT_NOT_SUPPORT_API))
        }.map { it as Product }

    override fun addToCollection(id: Int, type: ProductPickerType?): Single<Any> =
        when (type) {
            ProductPickerType.ALBUM -> albumRepository.addFavourite(id)
            ProductPickerType.ARTIST -> artistRepository.addFollow(id)
            ProductPickerType.MIX -> stationRepository.addFavourite(id)
            ProductPickerType.PLAYLIST,
            ProductPickerType.PLAYLIST_OWNER,
            ProductPickerType.PLAYLIST_VERIFIED_OWNER -> playlistRepository.addFavourite(id)
            ProductPickerType.PROFILE -> profileRepository.addFollow(id)
            ProductPickerType.SONG,
            ProductPickerType.VIDEO,
            ProductPickerType.ARTIST_SONG,
            ProductPickerType.ALBUM_SONG,
            ProductPickerType.PLAYLIST_SONG,
            ProductPickerType.SONG_PLAYER,
            ProductPickerType.SONG_QUEUE,
            ProductPickerType.MY_PLAYLIST_SONG,
            ProductPickerType.VIDEO_PLAYER -> trackRepository.addFavourite(id)
            else ->
                Single.error(IllegalStateException(PRODUCT_NOT_SUPPORT_API))
        }

    override fun removeFromCollection(id: Int, type: ProductPickerType?): Single<Any> =
        when (type) {
            ProductPickerType.ALBUM -> albumRepository.removeFavourite(id)
            ProductPickerType.ARTIST -> artistRepository.removeFollow(id)
            ProductPickerType.MIX -> stationRepository.removeFavourite(id)
            ProductPickerType.PLAYLIST,
            ProductPickerType.PLAYLIST_OWNER,
            ProductPickerType.PLAYLIST_VERIFIED_OWNER -> playlistRepository.removeFavourite(id)
            ProductPickerType.PROFILE -> profileRepository.removeFollow(id)
            ProductPickerType.SONG,
            ProductPickerType.VIDEO,
            ProductPickerType.ARTIST_SONG,
            ProductPickerType.ALBUM_SONG,
            ProductPickerType.PLAYLIST_SONG,
            ProductPickerType.SONG_PLAYER,
            ProductPickerType.SONG_QUEUE,
            ProductPickerType.MY_PLAYLIST_SONG,
            ProductPickerType.VIDEO_PLAYER -> trackRepository.removeFavourite(id)
            else ->
                Single.error(IllegalStateException(PRODUCT_NOT_SUPPORT_API))
        }

    override fun isInCollection(id: Int, type: ProductPickerType?): Single<Boolean> =
        when (type) {
            ProductPickerType.ALBUM -> albumRepository.isFavourited(id)
            ProductPickerType.ARTIST -> artistRepository.isFollowing(id)
            ProductPickerType.MIX -> stationRepository.isFavourited(id)
            ProductPickerType.PLAYLIST,
            ProductPickerType.PLAYLIST_OWNER,
            ProductPickerType.PLAYLIST_VERIFIED_OWNER -> playlistRepository.isFavourited(id)
            ProductPickerType.PROFILE -> profileRepository.isFollowing(id)
            ProductPickerType.SONG,
            ProductPickerType.VIDEO,
            ProductPickerType.ARTIST_SONG,
            ProductPickerType.ALBUM_SONG,
            ProductPickerType.MY_PLAYLIST_SONG,
            ProductPickerType.PLAYLIST_SONG,
            ProductPickerType.SONG_PLAYER,
            ProductPickerType.SONG_QUEUE,
            ProductPickerType.VIDEO_PLAYER -> trackRepository.isFavourited(id)
            else -> Single.error(IllegalStateException(PRODUCT_NOT_SUPPORT_API))
        }

    override fun getHasArtistAndSimilarStation(artistId: Int): Single<Boolean> =
        artistRepository.getSimilar(artistId)
            .map { it.size > INT_3 }

    override fun checkPlaylistType(creatorId: Int): Single<ProductPickerType> =
        musicUserRepository.get().map { user ->
            when {
                user.userId == creatorId && user.isVerified -> ProductPickerType.PLAYLIST_VERIFIED_OWNER
                user.userId == creatorId -> ProductPickerType.PLAYLIST_OWNER
                else -> ProductPickerType.PLAYLIST
            }
        }

    override fun getTracksForProduct(id: Int, type: ProductPickerType?): Single<List<Track>> =
        when (type) {
            ProductPickerType.ALBUM ->
                albumRepository.getTracks(id, TrackRequestType.AUDIO)
                    .map { list ->
                        list.sortedBy { it.trackNumber }
                    }
            ProductPickerType.MIX -> stationRepository.getSyncTracks(id)
            ProductPickerType.PLAYLIST,
            ProductPickerType.PLAYLIST_OWNER,
            ProductPickerType.PLAYLIST_VERIFIED_OWNER -> playlistRepository.getTracks(id, 1, INT_99)
            else -> Single.error(IllegalStateException("Cannot get tracks for this product"))
        }.map { list ->
            list.map { track ->
                track.isCached = cacheRepository.getTrackLocationIfExist(track.id) != null
                track
            }
        }

    override fun clearVotes(
        productId: Int,
        productPickerType: ProductPickerType?,
        voteType: String
    ): Single<Any> =
        when (productPickerType) {
            ProductPickerType.MIX -> stationRepository.deleteVotes(productId)
            ProductPickerType.ARTIST -> artistRepository.clearArtistVotes(productId, voteType)
            else -> {
                Single.error(IllegalStateException("This product type does not support clear votes"))
            }
        }
}
