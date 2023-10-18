package com.truedigital.features.tuned.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.truedigital.features.tuned.data.database.entity.PlaybackStateEntity
import io.reactivex.Single

@Dao
interface PlaybackStateDao {

    @Query("SELECT * FROM playback_state_table")
    fun getPlaybackStates(): Single<List<PlaybackStateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlaybackState(data: PlaybackStateEntity): Single<Long>

    @Query("DELETE FROM playback_state_table")
    fun deleteAllPlaybackStates(): Single<Int>
}
