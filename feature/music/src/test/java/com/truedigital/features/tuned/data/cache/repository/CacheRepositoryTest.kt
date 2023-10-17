package com.truedigital.features.tuned.data.cache.repository

import android.content.Context
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class CacheRepositoryTest {

    private lateinit var cacheRepository: CacheRepository
    private val context: Context = mock()
    private val mockPathCache = "pathCache"
    private val mockPathFile = "pathFile"

    @BeforeEach
    fun setup() {
        whenever(context.cacheDir).thenReturn(File(mockPathCache))
        whenever(context.filesDir).thenReturn(File(mockPathFile))
        cacheRepository = CacheRepositoryImpl(context)
    }

    @Test
    fun testIsDownloaded_returnFalse() {
        assertEquals(false, cacheRepository.isDownloaded(1))
    }

    @Test
    fun testGetTrackLocationIfExist_notExists_returnNull() {
        assertNull(cacheRepository.getTrackLocationIfExist(1))
    }

    @Test
    fun testGetTrackFileLocation_isTempTrue_returnFileLocation() {
        val mockId = 1
        val pathCacheValue = File("$mockPathCache/songs")
        assertEquals(
            "$pathCacheValue/$mockId",
            cacheRepository.getTrackFileLocation(mockId, true)
        )
    }

    @Test
    fun testGetTrackFileLocation_isTempFalse_returnFileLocation() {
        val mockId = 1
        val pathFileValue = File("$mockPathFile/songs")

        assertEquals(
            "${pathFileValue.path}/$mockId",
            cacheRepository.getTrackFileLocation(mockId, false)
        )
    }

    @Test
    fun testMoveToDownloadIfExist_notExists_returnFalse() {
        assertFalse(cacheRepository.moveToDownloadIfExist(1))
    }

    @Test
    fun testMoveToCacheIfExist_notExists_returnFalse() {
        assertFalse(cacheRepository.moveToCacheIfExist(1))
    }

    @Test
    fun testGetCacheSize_returnNoError() {
        cacheRepository.getCacheSize()
            .test()
            .assertNoErrors()
    }

    @Test
    fun testGetDownloadSize_returnNoError() {
        cacheRepository.getDownloadSize()
            .test()
            .assertNoErrors()
    }

    @Test
    fun testGetAlarmSampleFile_isTempTrue_returnFileLocation() {
        val mockFileName = "name"
        val pathFileValue = File("$mockPathFile/alarms/$mockFileName")

        assertEquals(
            pathFileValue.path,
            cacheRepository.getAlarmSampleFile(mockFileName).path
        )
    }
}
