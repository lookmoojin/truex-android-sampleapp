package com.truedigital.features.tuned.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "play_table")
class PlayEntity(
    @PrimaryKey
    var timestamp: Long = -1L
)
