package com.truedigital.features.tuned.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.truedigital.features.tuned.data.database.entity.TrackHistoryEntity
import io.reactivex.Single

@Dao
interface TrackHistoryDao {

    @Query("SELECT * FROM track_history_table ORDER BY lastPlayedDate DESC")
    fun getTrackHistories(): Single<List<TrackHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTrackHistory(data: TrackHistoryEntity): Single<Long>

    @Query("DELETE FROM track_history_table")
    suspend fun deleteAllTrackHistories(): Int
}
