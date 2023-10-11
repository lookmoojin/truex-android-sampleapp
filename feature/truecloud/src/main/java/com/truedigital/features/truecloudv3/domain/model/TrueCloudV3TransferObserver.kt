package com.truedigital.features.truecloudv3.domain.model

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.truedigital.features.truecloudv3.common.TrueCloudV3TransferState
import org.jetbrains.annotations.VisibleForTesting

class TrueCloudV3TransferObserver(val transferObserver: TransferObserver) {

    fun getId(): Int {
        return transferObserver.id
    }

    fun getState(): TrueCloudV3TransferState {
        return getTrueCloudV3State(transferObserver.state)
    }

    fun cleanTransferListener() {
        transferObserver.cleanTransferListener()
    }

    fun setTransferListener(trueCloudV3TransferListener: TrueCloudV3TransferListener) {
        transferObserver.setTransferListener(getListener(trueCloudV3TransferListener))
    }

    @VisibleForTesting
    fun getListener(trueCloudV3TransferListener: TrueCloudV3TransferListener): TransferListener {
        return object : TransferListener {
            override fun onStateChanged(id: Int, state: TransferState?) {
                trueCloudV3TransferListener.onStateChanged(id, getTrueCloudV3State(state))
            }

            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                trueCloudV3TransferListener.onProgressChanged(id, bytesCurrent, bytesTotal)
            }

            override fun onError(id: Int, ex: java.lang.Exception?) {
                trueCloudV3TransferListener.onError(id, ex)
            }
        }
    }

    @VisibleForTesting
    fun getTrueCloudV3State(transferState: TransferState?): TrueCloudV3TransferState {
        return when (transferState) {
            TransferState.WAITING -> TrueCloudV3TransferState.WAITING
            TransferState.IN_PROGRESS -> TrueCloudV3TransferState.IN_PROGRESS
            TransferState.PAUSED -> TrueCloudV3TransferState.PAUSED
            TransferState.RESUMED_WAITING -> TrueCloudV3TransferState.RESUMED_WAITING
            TransferState.COMPLETED -> TrueCloudV3TransferState.COMPLETED
            TransferState.CANCELED -> TrueCloudV3TransferState.CANCELED
            TransferState.FAILED -> TrueCloudV3TransferState.FAILED
            TransferState.WAITING_FOR_NETWORK -> TrueCloudV3TransferState.WAITING_FOR_NETWORK
            TransferState.PART_COMPLETED -> TrueCloudV3TransferState.PART_COMPLETED
            TransferState.PENDING_CANCEL -> TrueCloudV3TransferState.PENDING_CANCEL
            TransferState.PENDING_PAUSE -> TrueCloudV3TransferState.PENDING_PAUSE
            TransferState.PENDING_NETWORK_DISCONNECT -> TrueCloudV3TransferState.PENDING_NETWORK_DISCONNECT
            TransferState.UNKNOWN -> TrueCloudV3TransferState.UNKNOWN
            else -> TrueCloudV3TransferState.UNKNOWN
        }
    }

    interface TrueCloudV3TransferListener {
        fun onStateChanged(id: Int, state: TrueCloudV3TransferState?)
        fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long)
        fun onError(id: Int, ex: Exception?)
    }
}
