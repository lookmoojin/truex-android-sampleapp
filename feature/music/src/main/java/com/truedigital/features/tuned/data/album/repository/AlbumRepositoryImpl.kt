package com.truedigital.features.tuned.data.album.repository

import com.truedigital.features.tuned.api.album.AlbumMetadataApiInterface
import com.truedigital.features.tuned.api.album.AlbumReleaseApiInterface
import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.album.model.Release
import com.truedigital.features.tuned.data.database.repository.MusicRoomRepository
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.track.model.request.TrackRequestType
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class AlbumRepositoryImpl(
    private val albumReleaseApi: AlbumReleaseApiInterface,
    private val albumMetadataApi: AlbumMetadataApiInterface,
    private val musicRoomRepository: MusicRoomRepository
) : AlbumRepository {

    override fun get(id: Int, refresh: Boolean): Single<Album> {
        return Single.just(refresh)
            .subscribeOn(Schedulers.io())
            .flatMap {
                if (it) throw IllegalStateException("Refresh Required")
                else musicRoomRepository.getAlbum(id)
            }
            .onErrorResumeNext {
                albumMetadataApi.album(id)
                    .flatMap { album ->
                        musicRoomRepository.insertAlbum(album).map { album }
                    }
            }
    }

    override fun getTracks(releaseId: Int, type: TrackRequestType): Single<List<Track>> {
        return albumMetadataApi.releaseTracks(releaseId, type.value)
            .map { trackList ->
                trackList.map { track ->
                    track.copy(name = track.nameTranslations)
                }
            }
    }

    override fun getTrending(offset: Int, count: Int): Single<List<Album>> {
        return albumMetadataApi.trendingAlbums(offset, count)
            .map {
                it.results
            }
            .flatMap { albums ->
                musicRoomRepository.insertAlbums(albums).map { albums }
            }
    }

    override fun getNewReleases(offset: Int, count: Int): Single<List<Album>> {
        return albumMetadataApi.newReleases(offset, count)
            .map {
                it.results
            }
            .flatMap { albums ->
                musicRoomRepository.insertAlbums(albums).map { albums }
            }
    }

    override fun getMoreFromArtist(id: Int): Single<List<Album>> {
        return albumMetadataApi.artistAlbums(id)
            .map {
                it.results
            }
            .flatMap { albums ->
                musicRoomRepository.insertAlbums(albums).map { albums }
            }
    }

    override fun isFavourited(id: Int): Single<Boolean> =
        albumReleaseApi.userContext(id).map { it.isFavourited }

    override fun removeFavourite(id: Int): Single<Any> =
        albumReleaseApi.unFavourite(id)

    override fun addFavourite(id: Int): Single<Any> =
        albumReleaseApi.favourite(id)

    override fun getFavourited(offset: Int, count: Int): Single<List<Release>> =
        albumReleaseApi.favourited(offset, count).map { it.results }

    override fun getFavouritedCount(): Single<Int> =
        albumReleaseApi.favourited(1, 1).map { it.total }
}
