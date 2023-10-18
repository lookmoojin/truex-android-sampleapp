package com.truedigital.features.tuned.data.cache.repository

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.cache.DiskCache
import com.truedigital.features.tuned.data.database.entity.TrackHistoryEntity
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.io.File
import javax.inject.Inject

class CacheRepositoryImpl @Inject constructor(private val context: Context) : CacheRepository {

    companion object {
        // Maximum temp cache size in bytes
        private const val MAX_TEMP_CACHE_SIZE = 1024 * 1024 * 1024
    }

    private val downloadDir = File("${context.filesDir}/songs/")
    private val alarmSampleDir = File("${context.filesDir}/alarms/")
    private val cacheDir = File("${context.cacheDir}/songs/")
    private val imageDownloadDir = File("${context.filesDir}/images/")

    // default glide disk cache location, limited to 250 MB at most
    private val imageCacheDir = File(context.cacheDir, DiskCache.Factory.DEFAULT_DISK_CACHE_DIR)

    init {
        if (!downloadDir.exists()) downloadDir.mkdirs()
        if (!cacheDir.exists()) cacheDir.mkdirs()
        if (!alarmSampleDir.exists()) alarmSampleDir.mkdirs()
        if (!imageDownloadDir.exists()) imageDownloadDir.mkdirs()
    }

    override fun isDownloaded(trackId: Int): Boolean =
        File(downloadDir, trackId.toString()).exists()

    override fun getTrackLocationIfExist(trackId: Int): String? {
        val cacheFile = File(downloadDir, trackId.toString())
        val tempCacheFile = File(cacheDir, trackId.toString())
        return when {
            cacheFile.exists() -> "file://${cacheFile.path}"
            tempCacheFile.exists() -> "file://${tempCacheFile.path}"
            else -> null
        }
    }

    override fun getTrackFileLocation(trackId: Int, isTemp: Boolean): String =
        if (isTemp) {
            "${cacheDir.path}/$trackId"
        } else {
            "${downloadDir.path}/$trackId"
        }

    override fun moveToDownloadIfExist(trackId: Int): Boolean {
        val cacheFile = File(downloadDir, trackId.toString())
        val tempCacheFile = File(cacheDir, trackId.toString())
        return when {
            cacheFile.exists() -> true
            tempCacheFile.exists() -> if (!tempCacheFile.renameTo(cacheFile)) throw IllegalStateException(
                "move file failed"
            ) else {
                true
            }
            else -> false
        }
    }

    override fun moveToCacheIfExist(trackId: Int): Boolean {
        val cacheFile = File(downloadDir, trackId.toString())
        val tempCacheFile = File(cacheDir, trackId.toString())
        return when {
            tempCacheFile.exists() -> true
            cacheFile.exists() -> if (!cacheFile.renameTo(tempCacheFile)) throw IllegalStateException(
                "move file failed"
            ) else {
                true
            }
            else -> false
        }
    }

    override fun pruneCache(trackHistories: List<TrackHistoryEntity>) {
        var runningSize = 0L
        // trackHistories is a descending list based on last played date
        for (trackHistory in trackHistories) {
            val tempFile = File(cacheDir, trackHistory.trackId.toString())
            if (tempFile.exists()) {
                runningSize += tempFile.length()
                if (runningSize > MAX_TEMP_CACHE_SIZE) { // Cache is too big, remove it
                    deleteFile(tempFile)
                }
            }
        }
    }

    override fun getCacheSize(): Single<Long> =
        Single.just(
            cacheDir.walkTopDown().map { it.length() }.sum() +
                imageCacheDir.walkTopDown().map { it.length() }.sum()
        )
            .subscribeOn(Schedulers.io())

    override fun clearCache(): Single<Any> =
        Single.just(cacheDir).map {
            deleteDirectory(it)
            Glide.get(context).clearDiskCache()
        }

    override fun getDownloadSize(): Single<Long> =
        Single.just(
            downloadDir.walkTopDown().map { it.length() }.sum() +
                imageDownloadDir.walkTopDown().map { it.length() }.sum()
        )
            .subscribeOn(Schedulers.io())

    override fun clearDownload(): Single<Any> = Single.just(downloadDir).map {
        deleteDirectory(it)
        deleteDirectory(imageDownloadDir)
    }

    override fun getAlarmSampleFile(fileName: String) = File(alarmSampleDir, fileName)

    private fun deleteDirectory(dir: File) {
        if (dir.isDirectory) {
            dir.list()?.let { children ->
                for (child in children) {
                    deleteFile(File(dir, child))
                }
            }
        }
    }

    private fun deleteFile(file: File): Boolean {
        return file.delete()
    }
}
