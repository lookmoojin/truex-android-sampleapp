package com.truedigital.features.truecloudv3.service

import android.app.NotificationChannel
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.truedigital.features.truecloudv3.provider.TrueCloudV3TransferUtilityProvider
import com.truedigital.features.truecloudv3.util.DownloadNotification
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import kotlin.test.assertEquals

internal class TrueCloudV3ServiceTest {
    private val trueCloudV3TransferUtilityProvider: TrueCloudV3TransferUtilityProvider = mockk()
    private lateinit var trueCloudV3Service: TrueCloudV3Service
    private val file: File = mockk(relaxed = true)
    private val uri: Uri = mockk(relaxed = true)
    private val context: Context = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        trueCloudV3Service = TrueCloudV3Service()
    }

    @Disabled
    @Test
    fun `test TrueCloudV3ServiceTest return correct`() = runTest {
        // arrange
        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 0)
        val transferUtility: TransferUtility = mockk(relaxed = true)
        val intent = mockk<Intent>(relaxed = true)
        val transferObserver = mockk<TransferObserver>(relaxed = true)
        val mockDownloadNotification = mockk<DownloadNotification>()
        val mockFile = mockk<File>(relaxed = true)
        mockkStatic(Uri::class)
        every { intent.getStringExtra(TrueCloudV3Service.ACCESS_TOKEN) } returns "a"
        every { intent.getStringExtra(TrueCloudV3Service.FILE_PATH) } returns "/storage/emulated/0/Download/abc.jpg"
        every { intent.getStringExtra(TrueCloudV3Service.ENDPOINT) } returns "https://www.google.com/"
        every { intent.getStringExtra(TrueCloudV3Service.INTENT_TRANSFER_OPERATION) } returns "download"
        every { mockFile.path } returns "/storage/emulated/0/Download/abc.jpg"

        every { Uri.fromFile(any()) } returns uri
        every { mockFile.toUri() } returns uri
        every {
            trueCloudV3TransferUtilityProvider.getTransferUtility(any(), any())
        } returns transferUtility

        every { mockDownloadNotification.downloadComplete(any()) } returns Unit
        every {
            transferUtility.download(any(), any())
        } returns transferObserver
        every { transferObserver.state } returns TransferState.COMPLETED
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
        val response = trueCloudV3Service.onStartCommand(intent, 0, 0)

        // assert
        assertEquals(1, response)
    }

    @Disabled
    @Test
    fun `test TrueCloudV3ServiceTest startcommand status null return correct`() = runTest {
        // arrange
        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 0)
        val transferUtility: TransferUtility = mockk(relaxed = true)
        val intent = mockk<Intent>(relaxed = true)
        val transferObserver = mockk<TransferObserver>(relaxed = true)
        val mockDownloadNotification = mockk<DownloadNotification>()
        val mockFile = mockk<File>(relaxed = true)
        mockkStatic(Uri::class)
        every { intent.getStringExtra(TrueCloudV3Service.ACCESS_TOKEN) } returns "a"
        every { intent.getStringExtra(TrueCloudV3Service.FILE_PATH) } returns "/storage/emulated/0/Download/abc.jpg"
        every { intent.getStringExtra(TrueCloudV3Service.ENDPOINT) } returns "https://www.google.com/"
        every { intent.getStringExtra(TrueCloudV3Service.INTENT_TRANSFER_OPERATION) } returns "download"
        every { mockFile.path } returns "/storage/emulated/0/Download/abc.jpg"

        every { Uri.fromFile(any()) } returns uri
        every { mockFile.toUri() } returns uri
        every {
            trueCloudV3TransferUtilityProvider.getTransferUtility(any(), any())
        } returns transferUtility

        every { mockDownloadNotification.downloadComplete(any()) } returns Unit
        every {
            transferUtility.download(any(), any())
        } returns transferObserver
        every { transferObserver.state } returns null
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
        val response = trueCloudV3Service.onStartCommand(intent, 0, 0)

        // assert
        assertEquals(1, response)
    }

    @Disabled
    @Test
    fun `test TrueCloudV3ServiceTest return fail`() = runTest {
        // arrange
        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 0)
        val transferUtility: TransferUtility = mockk(relaxed = true)
        val intent = mockk<Intent>(relaxed = true)
        val transferObserver = mockk<TransferObserver>(relaxed = true)
        val mockDownloadNotification = mockk<DownloadNotification>()
        val mockFile = mockk<File>(relaxed = true)
        mockkStatic(Uri::class)
        every { intent.getStringExtra(TrueCloudV3Service.ACCESS_TOKEN) } returns "a"
        every { intent.getStringExtra(TrueCloudV3Service.FILE_PATH) } returns "/storage/emulated/0/Download/abc.jpg"
        every { intent.getStringExtra(TrueCloudV3Service.ENDPOINT) } returns "https://www.google.com/"
        every { intent.getStringExtra(TrueCloudV3Service.INTENT_TRANSFER_OPERATION) } returns "download"
        every { mockFile.path } returns "/storage/emulated/0/Download/abc.jpg"

        every { Uri.fromFile(any()) } returns uri
        every { mockFile.toUri() } returns uri
        every {
            trueCloudV3TransferUtilityProvider.getTransferUtility(any(), any())
        } returns transferUtility

        every { mockDownloadNotification.downloadComplete(any()) } returns Unit
        every { mockDownloadNotification.downloadFailed(any()) } returns Unit
        every {
            transferUtility.download(any(), any())
        } returns transferObserver
        every { transferObserver.state } returns TransferState.FAILED
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
        val response = trueCloudV3Service.onStartCommand(intent, 0, 0)

        // assert
        assertEquals(1, response)
    }

    @Test
    fun testCloseService() {
        // arrange
        val transferUtility = mockk<TransferUtility>()
        every { trueCloudV3TransferUtilityProvider.getDefaultTransferUtility(context) } returns transferUtility
        every { transferUtility.getTransfersWithType(any()) } returns mutableListOf()
        TrueCloudV3Service.transferUtility = transferUtility
        // act
        trueCloudV3Service.closeService()

        // assert
        verify(exactly = 1) { transferUtility.getTransfersWithType(any()) }
    }

    @Test
    fun testTransferlistenerServiceUpdate() {
        // arrange
        val mockDownloadNotification = mockk<DownloadNotification>()

        every { mockDownloadNotification.downloadComplete(any()) } returns Unit
        every { mockDownloadNotification.updateProgress(any(), any()) } returns Unit
        TrueCloudV3Service.downloadNotification = mockDownloadNotification
        // act
        trueCloudV3Service.transferlistener.onProgressChanged(1, 50, 100)

        // assert
        verify(exactly = 1) {
            mockDownloadNotification.updateProgress(any(), any())
        }
    }

    @Test
    fun testTransferlistenerServiceComplete() {
        // arrange
        val mockDownloadNotification = mockk<DownloadNotification>()
        every { mockDownloadNotification.downloadComplete(any()) } returns Unit
        every { mockDownloadNotification.updateProgress(any(), any()) } returns Unit
        TrueCloudV3Service.downloadNotification = mockDownloadNotification
        // act
        trueCloudV3Service.transferlistener.onProgressChanged(1, 100, 100)

        // assert
        verify(exactly = 1) {
            mockDownloadNotification.downloadComplete(any())
        }
    }

    @Test
    fun testTransferlistenerServiceOnStateChangedComplete() {
        // arrange
        val mockDownloadNotification = mockk<DownloadNotification>()
        val transferUtility = mockk<TransferUtility>()
        every { trueCloudV3TransferUtilityProvider.getDefaultTransferUtility(context) } returns transferUtility
        every { transferUtility.getTransfersWithType(any()) } returns mutableListOf()
        TrueCloudV3Service.transferUtility = transferUtility

        every { mockDownloadNotification.downloadComplete(any()) } returns Unit
        every { mockDownloadNotification.updateProgress(any(), any()) } returns Unit
        TrueCloudV3Service.downloadNotification = mockDownloadNotification
        // act
        trueCloudV3Service.transferlistener.onStateChanged(1, TransferState.COMPLETED)

        // assert
        verify(exactly = 1) {
            mockDownloadNotification.downloadComplete(any())
        }
    }

    @Test
    fun testTransferlistenerServiceOnStateChangedFail() {
        // arrange
        val mockDownloadNotification = mockk<DownloadNotification>()
        val transferUtility = mockk<TransferUtility>()
        every { trueCloudV3TransferUtilityProvider.getDefaultTransferUtility(context) } returns transferUtility
        every { transferUtility.getTransfersWithType(any()) } returns mutableListOf()
        TrueCloudV3Service.transferUtility = transferUtility

        every { mockDownloadNotification.downloadComplete(any()) } returns Unit
        every { mockDownloadNotification.updateProgress(any(), any()) } returns Unit
        every { mockDownloadNotification.downloadFailed(any()) } returns Unit
        TrueCloudV3Service.downloadNotification = mockDownloadNotification
        // act
        trueCloudV3Service.transferlistener.onStateChanged(1, TransferState.FAILED)

        // assert
        verify(exactly = 1) {
            mockDownloadNotification.downloadFailed(any())
        }
    }

    @Test
    fun testTransferlistenerServiceOnStateChangedPause() {
        // arrange
        val mockDownloadNotification = mockk<DownloadNotification>()
        val transferUtility = mockk<TransferUtility>()
        every { trueCloudV3TransferUtilityProvider.getDefaultTransferUtility(context) } returns transferUtility
        every { transferUtility.getTransfersWithType(any()) } returns mutableListOf()
        TrueCloudV3Service.transferUtility = transferUtility

        every { mockDownloadNotification.downloadPause(any()) } returns Unit
        every { mockDownloadNotification.updateProgress(any(), any()) } returns Unit
        every { mockDownloadNotification.downloadFailed(any()) } returns Unit
        TrueCloudV3Service.downloadNotification = mockDownloadNotification
        // act
        trueCloudV3Service.transferlistener.onStateChanged(1, TransferState.PAUSED)

        // assert
        verify(exactly = 1) {
            mockDownloadNotification.downloadPause(any())
        }
    }

    @Test
    fun testTransferlistenerServiceOnStateChangedIN_PROGRESS() {
        // arrange
        val mockDownloadNotification = mockk<DownloadNotification>()
        val transferUtility = mockk<TransferUtility>()
        every { trueCloudV3TransferUtilityProvider.getDefaultTransferUtility(context) } returns transferUtility
        every { transferUtility.getTransfersWithType(any()) } returns mutableListOf()
        TrueCloudV3Service.transferUtility = transferUtility

        every { mockDownloadNotification.downloadResume(any()) } returns Unit
        every { mockDownloadNotification.updateProgress(any(), any()) } returns Unit
        every { mockDownloadNotification.downloadFailed(any()) } returns Unit
        TrueCloudV3Service.downloadNotification = mockDownloadNotification
        // act
        trueCloudV3Service.transferlistener.onStateChanged(1, TransferState.IN_PROGRESS)

        // assert
        verify(exactly = 1) {
            mockDownloadNotification.downloadResume(any())
        }
    }

    @Test
    fun testCallResume() {
        // arrange
        val mockDownloadNotification = mockk<DownloadNotification>()
        val transferUtility = mockk<TransferUtility>()
        val transferObserver: TransferObserver = mockk()

        TrueCloudV3Service.transferUtility = transferUtility

        every { mockDownloadNotification.downloadResume(any()) } returns Unit
        TrueCloudV3Service.downloadNotification = mockDownloadNotification
        every {
            TrueCloudV3Service.transferUtility?.resume(any())
        } returns transferObserver

        // act
        TrueCloudV3Service.callResume(0)

        // assert
        verify(exactly = 1) {
            mockDownloadNotification.downloadResume(any())
        }
    }

    @Test
    fun testCallPause() {
        // arrange
        val mockDownloadNotification = mockk<DownloadNotification>()
        val transferUtility = mockk<TransferUtility>()

        TrueCloudV3Service.transferUtility = transferUtility

        every { mockDownloadNotification.downloadPause(any()) } returns Unit
        TrueCloudV3Service.downloadNotification = mockDownloadNotification
        every {
            TrueCloudV3Service.transferUtility?.pause(any())
        } returns true

        // act
        TrueCloudV3Service.callPause(0)

        // assert
        verify(exactly = 1) {
            mockDownloadNotification.downloadPause(any())
        }
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
