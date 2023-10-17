package com.truedigital.features.tuned.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.truedigital.features.tuned.data.database.entity.StationEntity
import io.reactivex.Single

@Dao
interface StationDao {

    @Query("SELECT * FROM station_table WHERE stationId == :stationId")
    fun getStation(stationId: Int): Single<StationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStation(data: StationEntity): Single<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStations(data: List<StationEntity>): Single<List<Long>>
}
