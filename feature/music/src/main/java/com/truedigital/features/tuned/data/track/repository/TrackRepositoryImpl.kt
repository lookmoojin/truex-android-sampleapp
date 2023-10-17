package com.truedigital.features.tuned.data.track.repository

import com.truedigital.features.tuned.api.track.TrackMetadataApiInterface
import com.truedigital.features.tuned.api.track.TrackServiceApiInterface
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.track.model.request.TrackRequestType
import com.truedigital.features.tuned.data.track.model.response.RawLyricString
import io.reactivex.Single

class TrackRepositoryImpl(
    private val trackMetadataApi: TrackMetadataApiInterface,
    private val trackServiceApi: TrackServiceApiInterface
) : TrackRepository {

    override fun get(id: Int): Single<Track> {
        return trackMetadataApi.get(id).map { track ->
            track.copy(name = track.nameTranslations)
        }
    }

    override fun getList(ids: List<Int>): Single<List<Track>> {
        return trackMetadataApi.getMultiple(ids).map { trackList ->
            trackList.map { track ->
                track.copy(name = track.nameTranslations)
            }
        }
    }

    override fun validateTracks(ids: List<Int>): Single<List<Int>> {
        return trackMetadataApi.validateTracks(ids)
    }

    override fun isFavourited(id: Int): Single<Boolean> {
        return trackServiceApi.userContext(id).map {
            it.isFavourited
        }
    }

    override fun removeFavourite(id: Int): Single<Any> = trackServiceApi.unfavourite(id)

    override fun addFavourite(id: Int): Single<Any> = trackServiceApi.favourite(id)

    override fun getFavourited(
        offset: Int,
        count: Int,
        type: TrackRequestType
    ): Single<List<Track>> {
        return trackServiceApi.favourited(offset, count, type.value)
            .map { pageResults ->
                pageResults.results
            }
            .map { trackList ->
                trackList.map { track ->
                    track.copy(name = track.nameTranslations)
                }
            }
    }

    override fun getFavouritedSongsCount(): Single<Int> {
        return trackServiceApi.favourited(type = TrackRequestType.AUDIO.value).map { it.total }
    }

    override fun getFavouritedVideosCount(): Single<Int> {
        return trackServiceApi.favourited(type = TrackRequestType.VIDEO.value).map { it.total }
    }

    override fun getLyric(id: Int): Single<RawLyricString> = trackMetadataApi.getLyrics(id)
}
