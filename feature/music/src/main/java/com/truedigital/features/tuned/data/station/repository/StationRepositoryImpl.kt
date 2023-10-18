package com.truedigital.features.tuned.data.station.repository

import com.truedigital.features.tuned.api.station.StationMetadataApiInterface
import com.truedigital.features.tuned.api.station.StationServiceApiInterface
import com.truedigital.features.tuned.data.ad.model.AdProvider
import com.truedigital.features.tuned.data.database.repository.MusicRoomRepository
import com.truedigital.features.tuned.data.station.model.LikedTrack
import com.truedigital.features.tuned.data.station.model.Rating
import com.truedigital.features.tuned.data.station.model.Stakkar
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.station.model.StationVote
import com.truedigital.features.tuned.data.station.model.TrackExtras
import com.truedigital.features.tuned.data.station.model.Vote
import com.truedigital.features.tuned.data.station.model.VoteCategory
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.injection.module.NetworkModule
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

class StationRepositoryImpl @Inject constructor(
    @Named(NetworkModule.AUTHENTICATED_SERVICES_RETROFIT) private val stationServiceApi: StationServiceApiInterface,
    @Named(NetworkModule.METADATA_RETROFIT) private val stationMetadataApi: StationMetadataApiInterface,
    private val musicRoomRepository: MusicRoomRepository
) : StationRepository {

    companion object {
        private const val TRACKS_PER_REQUEST = 5
        private const val REQUEST_TIMEOUT_SECONDS = 30L
        private const val SEED_TYPE_ARTIST_AND_SIMILAR = "Artist"
        private const val SEED_TYPE_USER = "User"

        private const val VOTE_TYPE_LIKE = "Like"
        private const val VOTE_TYPE_DISLIKE = "Dislike"
        private const val VOTE_TYPE_DELETE = "Delete"
    }

    override fun get(id: Int, refresh: Boolean): Single<Station> {
        return Single.just(refresh)
            .subscribeOn(Schedulers.io())
            .flatMap {
                if (it) throw IllegalStateException("Refresh Required")
                else musicRoomRepository.getStation(id)
            }
            .onErrorResumeNext {
                stationMetadataApi.station(id)
                    .flatMap { station ->
                        musicRoomRepository.insertStation(station).map { station }
                    }
            }
    }

    override fun getLocalMix(userId: Int): Single<Station> =
        stationServiceApi.addBySeed(userId, SEED_TYPE_USER)

    override fun getByArtist(artistId: Int): Single<Station> {
        return stationServiceApi.addBySeed(artistId, SEED_TYPE_ARTIST_AND_SIMILAR)
            .flatMap { station ->
                musicRoomRepository.insertStation(station).map { station }
            }
            .timeout(
                REQUEST_TIMEOUT_SECONDS,
                TimeUnit.SECONDS
            )
        // set time out to 30 seconds on Reactive Stream. Otherwise,
        // the app will silently retry 3 times due to OKHTTP features which results in a 2 mins timeout
    }

    override fun getContainingArtist(id: Int): Single<List<Station>> {
        return stationMetadataApi.containingArtist(id)
            .flatMap { stations ->
                musicRoomRepository.insertStations(stations).map { stations }
            }
    }

    override fun getSimilar(stationId: Int): Single<List<Station>> {
        return stationMetadataApi.similarStations(stationId)
            .flatMap { stations ->
                musicRoomRepository.insertStations(stations).map { stations }
            }
    }

    override fun getPopular(): Single<List<Station>> {
        return stationMetadataApi.trendingStations()
            .flatMap { stations ->
                musicRoomRepository.insertStations(stations).map { stations }
            }
    }

    override fun getRecent(): Single<List<Station>> {
        return stationServiceApi.lastPlayed()
            .flatMap { stations ->
                musicRoomRepository.insertStations(stations).map { stations }
            }
    }

    override fun getFavourited(offset: Int, count: Int): Single<List<Station>> {
        return stationServiceApi.favouritedStations(offset, count)
            .map { it.results }
            .flatMap { stations ->
                musicRoomRepository.insertStations(stations).map { stations }
            }
    }

    override fun getSuggested(offset: Int, count: Int): Single<List<Station>> {
        return stationMetadataApi.presetStations(offset, count)
            .map { it.results }
            .flatMap { stations ->
                musicRoomRepository.insertStations(stations).map { stations }
            }
    }

    override fun getSyncTracks(stationId: Int): Single<List<Track>> {
        return stationServiceApi.stationSyncTracks(stationId).map {
            it.map { stationTrack ->
                stationTrack.vote?.let {
                    stationTrack.track.vote = it
                }
                stationTrack.track
            }
        }
    }

    override fun getStakkar(stakkarId: Int): Single<Stakkar> {
        return stationServiceApi.stakkar(stakkarId).map {
            Stakkar(
                it.id,
                it.publisher.profile.image,
                it.publisher.profile.name,
                it.type,
                it.links,
                it.bannerUrl,
                it.bannerImage,
                it.hideDialog
            )
        }
    }

    override fun getTracks(stationId: Int, trackHash: String?): Single<List<Track>> {
        return stationServiceApi.stationTracks(stationId, TRACKS_PER_REQUEST, trackHash).map {
            it.map { stationTrack ->
                stationTrack.vote?.let { rating ->
                    stationTrack.track.vote = rating
                }
                stationTrack.track
            }
        }
    }

    override fun getTrackExtras(
        uniqueId: Int,
        stationId: Int,
        nextTrackId: Int?,
        previousTrackId: Int?,
        includeAds: Boolean,
        adProvider: AdProvider,
        lsid: String
    ): Single<TrackExtras> {
        return stationServiceApi.stationTrackExtras(
            stationId,
            uniqueId,
            nextTrackId,
            previousTrackId,
            includeAds,
            adProvider.name,
            lsid
        ).map { trackExtras ->
            TrackExtras(
                trackExtras.stakkars,
                trackExtras.campaignAds.map {
                    val ad = it.ad
                    ad.vast = it.xml
                    ad
                }
            )
        }
    }

    override fun getVotes(stationId: Int, userId: Int): Single<List<LikedTrack>> =
        stationServiceApi.stationUserVotes(stationId, userId, VoteCategory.TRACK.type)

    override fun deleteVote(stationId: Int, trackId: Int): Single<List<StationVote>> {
        val vote = Vote(trackId, VOTE_TYPE_DELETE, VoteCategory.TRACK.type, Date())
        return stationServiceApi.stationVote(stationId, listOf(vote))
    }

    override fun deleteVotes(stationId: Int): Single<Any> = stationServiceApi.deleteVotes(stationId)

    override fun putVote(stationId: Int, trackId: Int, rating: Rating): Single<Vote> {
        val vote = Vote(
            trackId,
            if (rating == Rating.LIKED) VOTE_TYPE_LIKE else VOTE_TYPE_DISLIKE,
            VoteCategory.TRACK.type,
            Date()
        )
        return stationServiceApi.stationVote(stationId, listOf(vote)).map { it.first().vote }
    }

    override fun isFavourited(stationId: Int): Single<Boolean> =
        stationServiceApi.userContext(stationId).map { it.isFavourited }

    override fun removeFavourite(stationId: Int): Single<Any> =
        stationServiceApi.unfavouriteStation(stationId)

    override fun addFavourite(stationId: Int): Single<Any> =
        stationServiceApi.favouriteStation(stationId)

    override fun getFavouritedCount(): Single<Int> =
        stationServiceApi.favouritedStations(1, 1).map { it.total }
}
