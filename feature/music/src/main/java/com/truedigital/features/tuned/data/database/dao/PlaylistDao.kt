package com.truedigital.features.tuned.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.truedigital.features.tuned.data.database.entity.PlaylistEntity
import io.reactivex.Single

@Dao
interface PlaylistDao {

    @Query("SELECT * FROM playlist_table WHERE playlistId == :playlistId")
    fun getPlaylist(playlistId: Int): Single<PlaylistEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlaylist(data: PlaylistEntity): Single<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlaylists(data: List<PlaylistEntity>): Single<List<Long>>
}
