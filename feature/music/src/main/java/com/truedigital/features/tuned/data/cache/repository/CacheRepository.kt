package com.truedigital.features.tuned.data.cache.repository

import com.truedigital.features.tuned.data.database.entity.TrackHistoryEntity
import io.reactivex.Single
import java.io.File

interface CacheRepository {
    fun getTrackLocationIfExist(trackId: Int): String?
    fun getTrackFileLocation(trackId: Int, isTemp: Boolean): String
    fun moveToDownloadIfExist(trackId: Int): Boolean
    fun moveToCacheIfExist(trackId: Int): Boolean
    fun pruneCache(trackHistories: List<TrackHistoryEntity>)

    fun isDownloaded(trackId: Int): Boolean
    fun getCacheSize(): Single<Long>
    fun clearCache(): Single<Any>
    fun getDownloadSize(): Single<Long>
    fun clearDownload(): Single<Any>

    fun getAlarmSampleFile(fileName: String): File
}
