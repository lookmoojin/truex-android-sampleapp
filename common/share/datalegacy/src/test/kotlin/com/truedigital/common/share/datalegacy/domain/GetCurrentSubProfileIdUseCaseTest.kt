package com.truedigital.common.share.datalegacy.domain

import com.truedigital.common.share.datalegacy.data.TvsNowCacheSourceRepository
import com.truedigital.common.share.datalegacy.domain.profile.usecase.userdetails.GetCurrentSubProfileUseCase
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class GetCurrentSubProfileIdUseCaseTest {

    private val getCurrentSubProfileUseCase: GetCurrentSubProfileUseCase = mockk()
    private val tvsNowCacheSourceRepository: TvsNowCacheSourceRepository = mockk()
    private val getCurrentSubProfileIdUseCase = GetCurrentSubProfileIdUseCaseImpl(
        getCurrentSubProfileUseCase, tvsNowCacheSourceRepository
    )

    @Test
    fun `test execute when isOpenFromTrueVision is true`() {
        every { tvsNowCacheSourceRepository.isOpenFromTrueVision() } returns true
        every { getCurrentSubProfileUseCase.execute() } returns mockk {
            every { id } returns "123"
        }

        val result = getCurrentSubProfileIdUseCase.execute()

        assertEquals("123", result)
    }

    @Test
    fun `test execute when isOpenFromTrueVision is false`() {
        every { tvsNowCacheSourceRepository.isOpenFromTrueVision() } returns false

        val result = getCurrentSubProfileIdUseCase.execute()

        assertEquals("me", result)
    }

    @Test
    fun `test execute when getCurrentSubProfileUseCase execute() returns null`() {
        every { tvsNowCacheSourceRepository.isOpenFromTrueVision() } returns true
        every { getCurrentSubProfileUseCase.execute() } returns null

        val result = getCurrentSubProfileIdUseCase.execute()

        assertEquals("me", result)
    }

    @Test
    fun `test execute when getCurrentSubProfileUseCase execute() returns empty id`() {
        every { tvsNowCacheSourceRepository.isOpenFromTrueVision() } returns true
        every { getCurrentSubProfileUseCase.execute() } returns mockk {
            every { id } returns ""
        }

        val result = getCurrentSubProfileIdUseCase.execute()

        assertEquals("me", result)
    }

    @Test
    fun `test execute when getCurrentSubProfileUseCase execute() returns blank id`() {
        every { tvsNowCacheSourceRepository.isOpenFromTrueVision() } returns true
        every { getCurrentSubProfileUseCase.execute() } returns mockk {
            every { id } returns "  "
        }

        val result = getCurrentSubProfileIdUseCase.execute()

        assertEquals("me", result)
    }
}
