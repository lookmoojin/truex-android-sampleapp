package com.truedigital.features.tuned.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.truedigital.features.tuned.data.database.entity.SkipEntity
import io.reactivex.Single

@Dao
interface SkipDao {

    @Query("SELECT * FROM skip_table")
    fun getSkips(): Single<List<SkipEntity>>

    @Query("DELETE FROM skip_table WHERE timestamp < :expireTimestamp")
    fun deleteExpireSkips(expireTimestamp: Long): Single<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSkip(data: SkipEntity): Single<Long>

    @Query("DELETE FROM skip_table")
    suspend fun deleteAllSkips(): Int
}
