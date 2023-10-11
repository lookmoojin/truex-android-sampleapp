package com.truedigital.features.truecloudv3.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import com.truedigital.foundation.NotificationChannelInfo
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import kotlin.test.assertEquals

internal class DownloadNotificationTest {
    lateinit var downloadNotification: DownloadNotification
    private var context: Context = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 1)
        downloadNotification = DownloadNotification(context)
    }

    @Disabled
    @Test
    fun showNotificationTestCaseNotiNull() {
        // arrange
        val mockNotificationManagerCompat = mockk<NotificationManagerCompat>()
        val mockNotificationChannel = mockk<NotificationChannel>()
        val notificationManager = mockk<NotificationManager>()
        val mockIntent = mockk<Intent>()
        mockkStatic(NotificationManagerCompat::class)
        mockkStatic(mockNotificationChannel::class)
        every { NotificationManagerCompat.from(any()) } returns mockNotificationManagerCompat
        every { mockNotificationManagerCompat.notify(any(), any()) } returns Unit
        every {
            context.getSystemService(
                Context.NOTIFICATION_SERVICE
            )
        } returns notificationManager
        every {
            mockIntent.setAction("download_pause")
        } returns mockIntent
        // act
        downloadNotification.show(10, "key", "path")

        // assert
        verify(exactly = 0) {
            mockNotificationManagerCompat.notify(
                10,
                com.nhaarman.mockitokotlin2.any()
            )
        }
    }

    @Disabled
    @Test
    fun updateProgressNotificationTestCaseSuccess() {
        // arrange
        val mockNotificationManagerCompat = mockk<NotificationManagerCompat>()
        val mockNotificationChannel = mockk<NotificationChannel>()
        val mockNotificationManager = mockk<NotificationManager>()
        mockkStatic(NotificationManagerCompat::class)
        mockkStatic(NotificationManager::class)
        mockkStatic(mockNotificationChannel::class)

        every { NotificationManagerCompat.from(any()) } returns mockNotificationManagerCompat
        every { mockNotificationManagerCompat.notificationChannels } returns listOf(mockNotificationChannel)
        every { mockNotificationManagerCompat.areNotificationsEnabled() } returns true
        every { mockNotificationChannel.importance } returns 2
        every { mockNotificationChannel.group } returns null
        every { mockNotificationManagerCompat.getNotificationChannelGroup(any()) } returns null

        every {
            context.getSystemService(
                Context.NOTIFICATION_SERVICE
            )
        } returns mockNotificationManager
        // act
        downloadNotification.updateProgress(10, 30)

        // assert
        verify(exactly = 0) {
            mockNotificationManagerCompat.notify(
                10,
                com.nhaarman.mockitokotlin2.any()
            )
        }
    }

    @Disabled
    @Test
    fun downloadFailedNotificationTestCaseSuccess() {
        // arrange
        val mockNotificationManagerCompat = mockk<NotificationManagerCompat>()
        val mockNotificationChannel = mockk<NotificationChannel>()
        val mockNotificationManager = mockk<NotificationManager>()
        mockkStatic(NotificationManagerCompat::class)
        mockkStatic(NotificationManager::class)
        mockkStatic(mockNotificationChannel::class)

        every { NotificationManagerCompat.from(any()) } returns mockNotificationManagerCompat
        every { mockNotificationManagerCompat.notificationChannels } returns listOf(mockNotificationChannel)
        every { mockNotificationManagerCompat.areNotificationsEnabled() } returns true
        every { mockNotificationChannel.importance } returns 2
        every { mockNotificationChannel.group } returns null
        every { mockNotificationManagerCompat.getNotificationChannelGroup(any()) } returns null

        every {
            context.getSystemService(
                Context.NOTIFICATION_SERVICE
            )
        } returns mockNotificationManager
        // act
        downloadNotification.downloadFailed(10)

        // assert
        verify(exactly = 0) {
            mockNotificationManagerCompat.notify(
                10,
                com.nhaarman.mockitokotlin2.any()
            )
        }
    }

    @Disabled
    @Test
    fun downloadPauseNotificationTestCaseSuccess() {
        // arrange
        val mockNotificationManagerCompat = mockk<NotificationManagerCompat>()
        val mockNotificationChannel = mockk<NotificationChannel>()
        val mockNotificationManager = mockk<NotificationManager>()
        mockkStatic(NotificationManagerCompat::class)
        mockkStatic(NotificationManager::class)
        mockkStatic(mockNotificationChannel::class)

        every { NotificationManagerCompat.from(any()) } returns mockNotificationManagerCompat
        every { mockNotificationManagerCompat.notificationChannels } returns listOf(mockNotificationChannel)
        every { mockNotificationManagerCompat.areNotificationsEnabled() } returns true
        every { mockNotificationChannel.importance } returns 2
        every { mockNotificationChannel.group } returns null
        every { mockNotificationManagerCompat.getNotificationChannelGroup(any()) } returns null
        val mockIntent = mockk<Intent>()
        every {
            mockIntent.setAction("download_resume")
        } returns mockIntent
        every {
            mockIntent.setFlags(10)
        } returns mockIntent
        every {
            context.getSystemService(
                Context.NOTIFICATION_SERVICE
            )
        } returns mockNotificationManager
        // act
        downloadNotification.downloadPause(10)

        // assert
        verify(exactly = 0) {
            mockNotificationManagerCompat.notify(
                10,
                com.nhaarman.mockitokotlin2.any()
            )
        }
    }

    @Disabled
    @Test
    fun downloadResumeNotificationTestCaseSuccess() {
        // arrange
        val mockNotificationManagerCompat = mockk<NotificationManagerCompat>()
        val mockNotificationChannel = mockk<NotificationChannel>()
        val mockNotificationManager = mockk<NotificationManager>()
        mockkStatic(NotificationManagerCompat::class)
        mockkStatic(NotificationManager::class)
        mockkStatic(mockNotificationChannel::class)

        every { NotificationManagerCompat.from(any()) } returns mockNotificationManagerCompat
        every { mockNotificationManagerCompat.notificationChannels } returns listOf(mockNotificationChannel)
        every { mockNotificationManagerCompat.areNotificationsEnabled() } returns true
        every { mockNotificationChannel.importance } returns 2
        every { mockNotificationChannel.group } returns null
        every { mockNotificationManagerCompat.getNotificationChannelGroup(any()) } returns null
        val mockIntent = mockk<Intent>()
        every {
            mockIntent.setAction("download_pause")
        } returns mockIntent
        every {
            mockIntent.setFlags(10)
        } returns mockIntent
        every {
            context.getSystemService(
                Context.NOTIFICATION_SERVICE
            )
        } returns mockNotificationManager
        // act
        downloadNotification.downloadResume(10)

        // assert
        verify(exactly = 0) {
            mockNotificationManagerCompat.notify(
                10,
                com.nhaarman.mockitokotlin2.any()
            )
        }
    }

    @Disabled
    @Test
    fun downloadCompleteNotificationTestCaseSuccess() {
        // arrange
        val mockNotificationManagerCompat = mockk<NotificationManagerCompat>()
        val mockNotificationChannel = mockk<NotificationChannel>()
        val mockNotificationManager = mockk<NotificationManager>()
        mockkStatic(NotificationManagerCompat::class)
        mockkStatic(NotificationManager::class)
        mockkStatic(mockNotificationChannel::class)

        every { NotificationManagerCompat.from(any()) } returns mockNotificationManagerCompat
        every { mockNotificationManagerCompat.notificationChannels } returns listOf(mockNotificationChannel)
        every { mockNotificationManagerCompat.areNotificationsEnabled() } returns true
        every { mockNotificationChannel.importance } returns 2
        every { mockNotificationChannel.group } returns null
        every { mockNotificationManagerCompat.getNotificationChannelGroup(any()) } returns null

        every {
            context.getSystemService(
                Context.NOTIFICATION_SERVICE
            )
        } returns mockNotificationManager
        // act
        downloadNotification.downloadComplete(10)

        // assert
        verify(exactly = 0) {
            mockNotificationManagerCompat.notify(
                10,
                com.nhaarman.mockitokotlin2.any()
            )
        }
    }

    @Disabled
    @Test
    fun createNotificationChannelCaseSuccess() {
        // arrange
        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 30)
        val mockNotificationManagerCompat = mockk<NotificationManagerCompat>()
        mockkStatic(NotificationManagerCompat::class)
        every {
            NotificationManagerCompat.from(context)
        } returns mockNotificationManagerCompat

        mockkConstructor(NotificationChannel::class)
        justRun {
            mockNotificationManagerCompat.createNotificationChannel(any<NotificationChannel>())
        }

        // act
        val response = downloadNotification.createNotificationChannel(context)

        // assert
        assertEquals(NotificationChannelInfo.TRUE_CLOUD_DOWNLOAD_CHANNEL_ID, response)
    }

    @Throws(Exception::class)
    private fun setFinalStatic(field: Field, newValue: Any?) {
        field.isAccessible = true
        val modifiersField: Field = Field::class.java.getDeclaredField("modifiers")
        modifiersField.isAccessible = true
        modifiersField.setInt(field, field.modifiers and Modifier.FINAL.inv())
        field.set(null, newValue)
    }
}
