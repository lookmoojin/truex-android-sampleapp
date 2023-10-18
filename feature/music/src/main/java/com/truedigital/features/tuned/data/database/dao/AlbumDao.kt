package com.truedigital.features.tuned.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.truedigital.features.tuned.data.database.entity.AlbumEntity
import io.reactivex.Single

@Dao
interface AlbumDao {

    @Query("SELECT * FROM album_table WHERE albumId == :albumId")
    fun getAlbum(albumId: Int): Single<AlbumEntity>

    @Query("SELECT * FROM album_table")
    fun getAlbums(): Single<List<AlbumEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAlbum(data: AlbumEntity): Single<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAlbums(data: List<AlbumEntity>): Single<List<Long>>
}
