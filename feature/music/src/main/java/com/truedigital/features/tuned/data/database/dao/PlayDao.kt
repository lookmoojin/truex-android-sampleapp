package com.truedigital.features.tuned.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.truedigital.features.tuned.data.database.entity.PlayEntity
import io.reactivex.Single

@Dao
interface PlayDao {

    @Query("SELECT * FROM play_table")
    fun getPlays(): Single<List<PlayEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlay(data: PlayEntity): Single<Long>

    @Query("DELETE FROM play_table WHERE timestamp < :expireTimestamp")
    fun deleteExpirePlays(expireTimestamp: Long): Single<Int>

    @Query("DELETE FROM play_table")
    suspend fun deleteAllPlays(): Int
}
