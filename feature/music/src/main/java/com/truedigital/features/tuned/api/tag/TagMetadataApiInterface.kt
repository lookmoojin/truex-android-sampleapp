package com.truedigital.features.tuned.api.tag

import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.playlist.model.Playlist
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.tag.model.Tag
import com.truedigital.features.tuned.data.util.PagedResults
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface TagMetadataApiInterface {
    @GET("tags")
    fun getTagByName(@Query("name") name: String): Single<Tag>

    @GET("tags/multiple")
    fun getMultipleByName(@Query("names") names: String): Single<List<Tag>>

    @GET("tags/playlists")
    fun getPlaylistsByTag(
        @Query("tag") tag: String,
        @Query("offset") offset: Int,
        @Query("count") count: Int,
        @Query("type") type: String
    ): Single<PagedResults<Playlist>>

    @GET("tags/artists")
    fun getArtistByTag(
        @Query("tag") tag: String,
        @Query("offset") offset: Int,
        @Query("count") count: Int
    ): Single<PagedResults<Artist>>

    @GET("tags/albums")
    fun getAlbumsByTag(
        @Query("tag") tag: String,
        @Query("offset") offset: Int,
        @Query("count") count: Int
    ): Single<PagedResults<Album>>

    @GET("tags/stations")
    fun getStationsByTag(
        @Query("tag") tag: String,
        @Query("offset") offset: Int,
        @Query("count") count: Int
    ): Single<PagedResults<Station>>

    @GET("tags/groups/albums")
    fun getAlbumsByTagGroup(
        @Query("groups") groups: String,
        @Query("types") types: String,
        @Query("offset") offset: Int,
        @Query("count") count: Int
    ): Single<PagedResults<Album>>
}
