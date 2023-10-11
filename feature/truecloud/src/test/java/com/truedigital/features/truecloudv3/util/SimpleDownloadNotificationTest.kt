package com.truedigital.features.truecloudv3.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
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

internal class SimpleDownloadNotificationTest {
    lateinit var simpleDownloadNotification: SimpleDownloadNotification
    private var context: Context = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        // setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 1)
        simpleDownloadNotification = SimpleDownloadNotification(context)
    }

    @Disabled
    @Test
    fun `show download notification test`() {
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
        simpleDownloadNotification.show(10, "key", "path")

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
    fun `update notification progress test`() {
        // arrange
        val mockNotificationManagerCompat = mockk<NotificationManagerCompat>()
        val mockNotificationChannel = mockk<NotificationChannel>()
        val mockNotificationManager = mockk<NotificationManager>()
        mockkStatic(NotificationManagerCompat::class)
        mockkStatic(NotificationManager::class)
        mockkStatic(mockNotificationChannel::class)
        every { NotificationManagerCompat.from(any()) } returns mockNotificationManagerCompat
        every { mockNotificationManagerCompat.notificationChannels } returns listOf(
            mockNotificationChannel
        )
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
        simpleDownloadNotification.updateProgress(10, 30)

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
    fun `create notification channel test`() {
        // arrange
        // setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 30)
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
        val response = simpleDownloadNotification.createNotificationChannel(context)

        // assert
        assertEquals(
            "com.truedigital.features.truecloudv3.util.SimpleDownloadNotification",
            response
        )
    }

    @Disabled
    @Test
    fun `download notification success test`() {
        // arrange
        val mockNotificationManagerCompat = mockk<NotificationManagerCompat>()
        val mockNotificationChannel = mockk<NotificationChannel>()
        val mockNotificationManager = mockk<NotificationManager>()
        mockkStatic(NotificationManagerCompat::class)
        mockkStatic(NotificationManager::class)
        mockkStatic(mockNotificationChannel::class)

        every { NotificationManagerCompat.from(any()) } returns mockNotificationManagerCompat
        every { mockNotificationManagerCompat.notificationChannels } returns listOf(
            mockNotificationChannel
        )
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
        simpleDownloadNotification.downloadComplete(10)

        // assert
        verify(exactly = 0) {
            mockNotificationManagerCompat.notify(
                10,
                com.nhaarman.mockitokotlin2.any()
            )
        }
    }

    @Disabled
    @Throws(Exception::class)
    fun setFinalStatic(field: Field, newValue: Any?) {
        field.isAccessible = true
        val modifiersField: Field = Field::class.java.getDeclaredField("modifiers")
        modifiersField.isAccessible = true
        modifiersField.setInt(field, field.modifiers and Modifier.FINAL.inv())
        field.set(null, newValue)
    }
}
