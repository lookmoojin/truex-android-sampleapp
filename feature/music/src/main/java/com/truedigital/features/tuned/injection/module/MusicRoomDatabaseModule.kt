package com.truedigital.features.tuned.injection.module

import android.content.Context
import androidx.room.Room
import com.truedigital.features.tuned.data.database.TrueIDMusicDatabase
import com.truedigital.features.tuned.data.database.dao.AlbumDao
import com.truedigital.features.tuned.data.database.dao.ArtistDao
import com.truedigital.features.tuned.data.database.dao.PlayDao
import com.truedigital.features.tuned.data.database.dao.PlaybackStateDao
import com.truedigital.features.tuned.data.database.dao.PlaylistDao
import com.truedigital.features.tuned.data.database.dao.SkipDao
import com.truedigital.features.tuned.data.database.dao.StationDao
import com.truedigital.features.tuned.data.database.dao.TrackHistoryDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object MusicRoomDatabaseModule {

    @Provides
    @Singleton
    fun provideTrueIdMusicDatabase(
        context: Context
    ): TrueIDMusicDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            TrueIDMusicDatabase::class.java,
            TrueIDMusicDatabase.DB_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideAlbumDao(db: TrueIDMusicDatabase): AlbumDao {
        return db.albumDao()
    }

    @Provides
    @Singleton
    fun provideArtistDao(db: TrueIDMusicDatabase): ArtistDao {
        return db.artistDao()
    }

    @Provides
    @Singleton
    fun providePlaybackStateDao(db: TrueIDMusicDatabase): PlaybackStateDao {
        return db.playbackStateDao()
    }

    @Provides
    @Singleton
    fun providePlayDao(db: TrueIDMusicDatabase): PlayDao {
        return db.playDao()
    }

    @Provides
    @Singleton
    fun providePlaylistDao(db: TrueIDMusicDatabase): PlaylistDao {
        return db.playlistDao()
    }

    @Provides
    @Singleton
    fun provideSkipDao(db: TrueIDMusicDatabase): SkipDao {
        return db.skipDao()
    }

    @Provides
    @Singleton
    fun provideStationDao(db: TrueIDMusicDatabase): StationDao {
        return db.stationDao()
    }

    @Provides
    @Singleton
    fun provideTrackHistoryDao(db: TrueIDMusicDatabase): TrackHistoryDao {
        return db.trackHistoryDao()
    }
}
