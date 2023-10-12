package com.truedigital.common.share.datalegacy.domain.verifydevice.usecase

import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class VerifyTrustDeviceOwnerUseCaseTest {

    private val userRepository = mockk<UserRepository>()
    private val useCase = VerifyTrustDeviceOwnerUseCaseImpl(userRepository)

    @Test
    fun `when userRepository returns trusted owner, execute() returns the value`() {
        val expectedOwner = "123"
        every { userRepository.getTrustedOwner() } returns expectedOwner

        val result = useCase.execute()

        assertEquals(expectedOwner, result)
    }

    @Test
    fun `when userRepository returns empty, execute() returns 0`() {
        every { userRepository.getTrustedOwner() } returns ""

        val result = useCase.execute()

        assertEquals("0", result)
    }
}
