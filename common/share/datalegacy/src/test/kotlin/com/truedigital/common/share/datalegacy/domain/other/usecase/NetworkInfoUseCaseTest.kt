package com.truedigital.common.share.datalegacy.domain.other.usecase

import android.telephony.CellInfoLte
import android.telephony.TelephonyManager
import com.truedigital.core.provider.ContextDataProvider
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class NetworkInfoUseCaseTest {

    private lateinit var useCase: NetworkInfoUseCaseImpl
    private lateinit var contextDataProvider: ContextDataProvider
    private lateinit var telephonyManager: TelephonyManager

    @BeforeEach
    fun setup() {
        contextDataProvider = mockk(relaxed = true)
        telephonyManager = mockk(relaxed = true)
        useCase = NetworkInfoUseCaseImpl(contextDataProvider, telephonyManager)
    }

    @Test
    fun `getLTE returns CellInfoLte`() {
        // Arrange
        val cellInfoLte = mockk<CellInfoLte>()
        every {
            telephonyManager.allCellInfo
        } returns listOf(cellInfoLte)

        // Act
        val result = useCase.getLTE()

        // Assert
        assertEquals(cellInfoLte, result)
    }

    @Test
    fun `getNetworkInfo returns expected list`() {
        // Arrange
        val cellInfoLte = mockk<CellInfoLte>(relaxed = true)
        every {
            telephonyManager.allCellInfo
        } returns listOf(cellInfoLte)

        // Act
        val result = useCase.getNetworkInfo()

        // Assert
        assertEquals(3, result.size)

        val jsonOutputNetworkCellular1 =
            "{\"mRegistered\":\"NO\",\"mMcc\":0,\"mTimeStamp\":0,\"mMnc\":0,\"mTimeStampType\":\"\"}"
        assertEquals(jsonOutputNetworkCellular1, result[0])

        /*val jsonOutputNetworkCellular2 =
            "{\"mCi\":0,\"mPci\":0,\"mTac\":0}"*/

        val jsonOutputNetworkCellular2 =
            "{\"ss\":\"\",\"mCi\":0,\"rsrq\":0,\"rsrp\":0,\"rssnr\":0,\"mPci\":0,\"mTac\":0}"

        assertEquals(jsonOutputNetworkCellular2, result[1])

/*        val jsonOutputNetworkCellular3 =
            "{}"*/

        val jsonOutputNetworkCellular3 =
            "{\"cpi\":0,\"ta\":0}"

        assertEquals(jsonOutputNetworkCellular3, result[2])
    }
}
