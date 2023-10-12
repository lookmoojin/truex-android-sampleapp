package com.truedigital.features.truecloudv3.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferType
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.truedigital.features.truecloudv3.data.model.SecureTokenServiceDataResponse
import com.truedigital.features.truecloudv3.injections.TrueCloudV3Component
import com.truedigital.features.truecloudv3.provider.TrueCloudV3TransferUtilityProvider
import com.truedigital.features.truecloudv3.util.DownloadNotification
import org.jetbrains.annotations.VisibleForTesting
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class TrueCloudV3Service : Service() {

    companion object {

        @VisibleForTesting
        const val INTENT_TRANSFER_OPERATION = "transferOperation"

        @VisibleForTesting
        const val ACCESS_TOKEN = "access_token"

        @VisibleForTesting
        const val FILE_PATH = "path"

        @VisibleForTesting
        const val ENDPOINT = "end_point"

        const val SECRET_KEY = "secret_key"
        const val SESSION_KEY = "session_key"
        const val OBJECT_ID = "object_id"
        const val TRANSFER_OPERATION_DOWNLOAD = "download"

        private const val MAX_PROGRESS = 100
        private const val EMPTY_DOWNLOAD_TASK_SIZE = 0
        private const val DEFAULT_DOWNLOAD_NOTIFICATION_ID = 0

        @VisibleForTesting
        var downloadNotification: DownloadNotification? = null

        @VisibleForTesting
        var transferUtility: TransferUtility? = null

        fun callPause(id: Int) {
            Timber.d("true_cloudv3 pause id : " + id)
            val success = transferUtility?.pause(id) ?: false
            if (success) {
                downloadNotification?.downloadPause(id)
            }
        }

        fun callResume(id: Int) {
            val success = transferUtility?.resume(id)
            if (success != null) {
                downloadNotification?.downloadResume(id)
            }
        }
    }

    @Inject
    lateinit var trueCloudV3TransferUtilityProvider: TrueCloudV3TransferUtilityProvider

    override fun onCreate() {
        TrueCloudV3Component.getInstance().inject(this)
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val accessToken: String? = intent.getStringExtra(ACCESS_TOKEN)
        val secretKey: String? = intent.getStringExtra(SECRET_KEY)
        val sessionKey: String? = intent.getStringExtra(SESSION_KEY)
        val objectId: String? = intent.getStringExtra(OBJECT_ID)
        val endpoint: String? = intent.getStringExtra(ENDPOINT)
        val path: String? = intent.getStringExtra(FILE_PATH)
        val transferOperation: String? = intent.getStringExtra(INTENT_TRANSFER_OPERATION)
        val file = File(path)
        val secureTokenServiceDataResponse = SecureTokenServiceDataResponse(
            accessToken = accessToken,
            secretKey = secretKey,
            sessionKey = sessionKey,
            endpoint = endpoint
        )
        transferUtility = trueCloudV3TransferUtilityProvider.getTransferUtility(
            secureTokenServiceDataResponse,
            this
        )

        when (transferOperation) {
            TRANSFER_OPERATION_DOWNLOAD -> {
                val downloadObserver = transferUtility?.download(objectId, file)
                downloadObserver?.setTransferListener(transferlistener)
                downloadNotification = DownloadNotification(this)
                downloadNotification?.show(
                    downloadObserver?.id ?: DEFAULT_DOWNLOAD_NOTIFICATION_ID,
                    objectId,
                    path
                )
                when (downloadObserver?.state) {
                    TransferState.COMPLETED -> {
                        downloadNotification?.downloadComplete(
                            downloadObserver.id
                        )
                    }
                    TransferState.FAILED -> {
                        downloadNotification?.downloadFailed(
                            downloadObserver.id
                        )
                    }
                    else -> {
                        /* Nothing to do */
                    }
                }
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @VisibleForTesting
    val transferlistener = object : TransferListener {
        override fun onError(id: Int, e: Exception) {
            Timber.e(e)
        }

        override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
            val progress =
                ((bytesCurrent.toDouble() / bytesTotal.toDouble()) * MAX_PROGRESS).toInt()
            if (progress == MAX_PROGRESS) {
                downloadNotification?.downloadComplete(id)
            } else {
                downloadNotification?.updateProgress(id, progress)
            }
        }

        override fun onStateChanged(id: Int, state: TransferState) {
            if (state == TransferState.COMPLETED) {
                downloadNotification?.downloadComplete(id)
            } else if (state == TransferState.PAUSED) {
                downloadNotification?.downloadPause(id)
            } else if (state == TransferState.IN_PROGRESS) {
                downloadNotification?.downloadResume(id)
            } else {
                downloadNotification?.downloadFailed(id)
            }
        }
    }

    @VisibleForTesting
    fun closeService() {
        if (transferUtility?.getTransfersWithType(TransferType.DOWNLOAD)?.size == EMPTY_DOWNLOAD_TASK_SIZE) {
            stopSelf()
        }
    }
}
