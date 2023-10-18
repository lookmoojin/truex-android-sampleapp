package com.truedigital.features.tuned.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "track_history_table")
class TrackHistoryEntity(
    @PrimaryKey
    val trackId: Int = -1,
    val lastPlayedDate: Long = -1
)
