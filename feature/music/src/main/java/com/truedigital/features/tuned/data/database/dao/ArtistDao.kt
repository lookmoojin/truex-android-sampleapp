package com.truedigital.features.tuned.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.truedigital.features.tuned.data.database.entity.ArtistEntity
import io.reactivex.Single

@Dao
interface ArtistDao {

    @Query("SELECT * FROM artist_table WHERE id == :artistId")
    fun getArtist(artistId: Int): Single<ArtistEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertArtist(data: ArtistEntity): Single<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertArtists(data: List<ArtistEntity>): Single<List<Long>>
}
