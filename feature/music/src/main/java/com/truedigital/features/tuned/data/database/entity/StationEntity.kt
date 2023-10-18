package com.truedigital.features.tuned.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.truedigital.features.tuned.data.database.converter.StationConverters
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.util.LocalisedString

@Entity(tableName = "station_table")
@TypeConverters(StationConverters::class)
class StationEntity(
    @PrimaryKey
    var stationId: Int = -1,
    var stationType: String = "",
    var name: List<LocalisedString> = emptyList(),
    var description: List<LocalisedString> = emptyList(),
    var coverImage: List<LocalisedString> = emptyList(),
    var bannerImage: List<LocalisedString> = emptyList(),
    var bannerUrl: String? = null,
    var isActive: Boolean = false
) {

    fun toStation(): Station {
        return Station(
            id = this.stationId,
            type = Station.StationType.fromString(this.stationType),
            name = this.name,
            description = this.description,
            coverImage = this.coverImage,
            bannerImage = this.bannerImage,
            bannerURL = this.bannerUrl,
            isActive = this.isActive
        )
    }
}
