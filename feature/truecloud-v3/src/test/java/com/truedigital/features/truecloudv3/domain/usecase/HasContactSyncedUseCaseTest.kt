package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.core.extensions.collectSafe
import com.truedigital.features.truecloudv3.data.repository.ContactRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class HasContactSyncedUseCaseTest {
    private val contactRepository: ContactRepository = mockk()
    private lateinit var hasContactSyncedUseCase: HasContactSyncedUseCase

    @BeforeEach
    fun setup() {
        hasContactSyncedUseCase = HasContactSyncedUseCaseImpl(
            contactRepository = contactRepository
        )
    }

    @Test
    fun `test execute success`() = runTest {
        // arrange
        coEvery {
            contactRepository.hasContactSynced()
        } returns true

        // act
        val flowResponse = hasContactSyncedUseCase.execute()

        // assert
        flowResponse.collectSafe {
            assertTrue(it)
        }
        coVerify(exactly = 1) {
            contactRepository.hasContactSynced()
        }
    }
}
