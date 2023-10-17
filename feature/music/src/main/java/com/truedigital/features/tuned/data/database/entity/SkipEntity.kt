package com.truedigital.features.tuned.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "skip_table")
class SkipEntity(
    @PrimaryKey
    val timestamp: Long = -1
)
