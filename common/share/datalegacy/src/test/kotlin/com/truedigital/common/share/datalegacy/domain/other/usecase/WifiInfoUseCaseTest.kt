package com.truedigital.common.share.datalegacy.domain.other.usecase

import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class WifiInfoUseCaseTest {

    private lateinit var networkInfoWrapperUseCase: NetworkInfoWrapperUseCaseImpl
    private val networkInfoUseCase: NetworkInfoUseCase = mockk()

    @BeforeEach
    fun setUp() {
        networkInfoWrapperUseCase = NetworkInfoWrapperUseCaseImpl(networkInfoUseCase)
    }

    @Test
    fun `execute should return the result from the networkInfoUseCase`() = runTest {
        // given
        val networkInfoList = listOf("networkInfo1", "networkInfo2")
        coEvery { networkInfoUseCase.getNetworkInfo() } returns networkInfoList

        // when
        val result = networkInfoWrapperUseCase.execute().toList()

        // then
        assertIterableEquals(listOf(networkInfoList), result)
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }
}
