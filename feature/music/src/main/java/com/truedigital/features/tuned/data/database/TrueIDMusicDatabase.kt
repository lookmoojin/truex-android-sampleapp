package com.truedigital.features.tuned.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.truedigital.features.tuned.data.database.TrueIDMusicDatabase.Companion.VERSION
import com.truedigital.features.tuned.data.database.dao.AlbumDao
import com.truedigital.features.tuned.data.database.dao.ArtistDao
import com.truedigital.features.tuned.data.database.dao.PlayDao
import com.truedigital.features.tuned.data.database.dao.PlaybackStateDao
import com.truedigital.features.tuned.data.database.dao.PlaylistDao
import com.truedigital.features.tuned.data.database.dao.SkipDao
import com.truedigital.features.tuned.data.database.dao.StationDao
import com.truedigital.features.tuned.data.database.dao.TrackHistoryDao
import com.truedigital.features.tuned.data.database.entity.AlbumEntity
import com.truedigital.features.tuned.data.database.entity.ArtistEntity
import com.truedigital.features.tuned.data.database.entity.PlayEntity
import com.truedigital.features.tuned.data.database.entity.PlaybackStateEntity
import com.truedigital.features.tuned.data.database.entity.PlaylistEntity
import com.truedigital.features.tuned.data.database.entity.SkipEntity
import com.truedigital.features.tuned.data.database.entity.StationEntity
import com.truedigital.features.tuned.data.database.entity.TrackHistoryEntity

@Database(
    entities = [
        AlbumEntity::class,
        ArtistEntity::class,
        PlaybackStateEntity::class,
        PlayEntity::class,
        PlaylistEntity::class,
        SkipEntity::class,
        StationEntity::class,
        TrackHistoryEntity::class
    ],
    version = VERSION,
    exportSchema = false
)
abstract class TrueIDMusicDatabase : RoomDatabase() {

    companion object {
        const val DB_NAME = "TrueIDMusicDatabase.db"
        const val VERSION = 1
    }

    abstract fun albumDao(): AlbumDao

    abstract fun artistDao(): ArtistDao

    abstract fun playbackStateDao(): PlaybackStateDao

    abstract fun playDao(): PlayDao

    abstract fun playlistDao(): PlaylistDao

    abstract fun skipDao(): SkipDao

    abstract fun stationDao(): StationDao

    abstract fun trackHistoryDao(): TrackHistoryDao
}
