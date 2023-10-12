package com.truedigital.features.truecloudv3.domain.model

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.truedigital.features.truecloudv3.common.TrueCloudV3TransferState
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class TrueCloudV3TransferObserverTest {
    val transferObserver: TransferObserver = mockk()
    val trueCloudV3TransferListener: TrueCloudV3TransferObserver.TrueCloudV3TransferListener =
        mockk()

    @Test
    fun setTransferObserverTest() {
        // Arrange
        val trueCloudV3TransferObserver = TrueCloudV3TransferObserver(transferObserver)
        val mockTransferListener = mockk<TransferListener>()
        every { transferObserver.setTransferListener(any()) } returns Unit
        // Act
        trueCloudV3TransferObserver.setTransferListener(trueCloudV3TransferListener)

        // Assert
        verify(exactly = 1) { transferObserver.setTransferListener(any()) }
    }

    @Test
    fun getTransferStateINPROGRESSTest() {
        // Arrange
        val trueCloudV3TransferObserver = TrueCloudV3TransferObserver(transferObserver)

        every { transferObserver.state } returns TransferState.IN_PROGRESS
        // Act
        val state = trueCloudV3TransferObserver.getState()

        // Assert
        assertEquals(TrueCloudV3TransferState.IN_PROGRESS, state)
    }
    @Test
    fun getTransferStatePAUSEDTest() {
        // Arrange
        val trueCloudV3TransferObserver = TrueCloudV3TransferObserver(transferObserver)

        every { transferObserver.state } returns TransferState.PAUSED
        // Act
        val state = trueCloudV3TransferObserver.getState()

        // Assert
        assertEquals(TrueCloudV3TransferState.PAUSED, state)
    }
    @Test
    fun getTransferStateCOMPLETEDTest() {
        // Arrange
        val trueCloudV3TransferObserver = TrueCloudV3TransferObserver(transferObserver)

        every { transferObserver.state } returns TransferState.COMPLETED
        // Act
        val state = trueCloudV3TransferObserver.getState()

        // Assert
        assertEquals(TrueCloudV3TransferState.COMPLETED, state)
    }
    @Test
    fun getTransferStateUNKNOWNTest() {
        // Arrange
        val trueCloudV3TransferObserver = TrueCloudV3TransferObserver(transferObserver)

        every { transferObserver.state } returns TransferState.UNKNOWN
        // Act
        val state = trueCloudV3TransferObserver.getState()

        // Assert
        assertEquals(TrueCloudV3TransferState.UNKNOWN, state)
    }
    @Test
    fun getTransferStateFAILEDTest() {
        // Arrange
        val trueCloudV3TransferObserver = TrueCloudV3TransferObserver(transferObserver)

        every { transferObserver.state } returns TransferState.FAILED
        // Act
        val state = trueCloudV3TransferObserver.getState()

        // Assert
        assertEquals(TrueCloudV3TransferState.FAILED, state)
    }
    @Test
    fun getTransferStateRESUMEDWAITINGTest() {
        // Arrange
        val trueCloudV3TransferObserver = TrueCloudV3TransferObserver(transferObserver)

        every { transferObserver.state } returns TransferState.RESUMED_WAITING
        // Act
        val state = trueCloudV3TransferObserver.getState()

        // Assert
        assertEquals(TrueCloudV3TransferState.RESUMED_WAITING, state)
    }
    @Test
    fun getTransferStateCANCELEDTest() {
        // Arrange
        val trueCloudV3TransferObserver = TrueCloudV3TransferObserver(transferObserver)

        every { transferObserver.state } returns TransferState.CANCELED
        // Act
        val state = trueCloudV3TransferObserver.getState()

        // Assert
        assertEquals(TrueCloudV3TransferState.CANCELED, state)
    }
    @Test
    fun getTransferStateWAITINGFORNETWORKTest() {
        // Arrange
        val trueCloudV3TransferObserver = TrueCloudV3TransferObserver(transferObserver)

        every { transferObserver.state } returns TransferState.WAITING_FOR_NETWORK
        // Act
        val state = trueCloudV3TransferObserver.getState()

        // Assert
        assertEquals(TrueCloudV3TransferState.WAITING_FOR_NETWORK, state)
    }
    @Test
    fun getTransferStatePARTCOMPLETEDTest() {
        // Arrange
        val trueCloudV3TransferObserver = TrueCloudV3TransferObserver(transferObserver)

        every { transferObserver.state } returns TransferState.PART_COMPLETED
        // Act
        val state = trueCloudV3TransferObserver.getState()

        // Assert
        assertEquals(TrueCloudV3TransferState.PART_COMPLETED, state)
    }
    @Test
    fun getTransferStatePENDINGCANCELTest() {
        // Arrange
        val trueCloudV3TransferObserver = TrueCloudV3TransferObserver(transferObserver)

        every { transferObserver.state } returns TransferState.PENDING_CANCEL
        // Act
        val state = trueCloudV3TransferObserver.getState()

        // Assert
        assertEquals(TrueCloudV3TransferState.PENDING_CANCEL, state)
    }
    @Test
    fun getTransferStatePENDINGNETWORKDISCONNECTTest() {
        // Arrange
        val trueCloudV3TransferObserver = TrueCloudV3TransferObserver(transferObserver)

        every { transferObserver.state } returns TransferState.PENDING_NETWORK_DISCONNECT
        // Act
        val state = trueCloudV3TransferObserver.getState()

        // Assert
        assertEquals(TrueCloudV3TransferState.PENDING_NETWORK_DISCONNECT, state)
    }
    @Test
    fun getTransferStatePENDINGPAUSETest() {
        // Arrange
        val trueCloudV3TransferObserver = TrueCloudV3TransferObserver(transferObserver)

        every { transferObserver.state } returns TransferState.PENDING_PAUSE
        // Act
        val state = trueCloudV3TransferObserver.getState()

        // Assert
        assertEquals(TrueCloudV3TransferState.PENDING_PAUSE, state)
    }
    @Test
    fun getTransferStateNullTest() {
        // Arrange
        val trueCloudV3TransferObserver = TrueCloudV3TransferObserver(transferObserver)

        every { transferObserver.state } returns null
        // Act
        val state = trueCloudV3TransferObserver.getState()

        // Assert
        assertEquals(TrueCloudV3TransferState.UNKNOWN, state)
    }
}
