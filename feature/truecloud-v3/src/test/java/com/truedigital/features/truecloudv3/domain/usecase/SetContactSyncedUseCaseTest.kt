package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.data.repository.ContactRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class SetContactSyncedUseCaseTest {
    private val contactRepository: ContactRepository = mockk()
    private lateinit var setContactSyncedUseCase: SetContactSyncedUseCase

    @BeforeEach
    fun setup() {
        setContactSyncedUseCase = SetContactSyncedUseCaseImpl(
            contactRepository = contactRepository
        )
    }

    @Test
    fun `test execute success`() = runTest {
        // arrange
        coEvery {
            contactRepository.setContactSynced(any())
        } returns Unit

        // act
        setContactSyncedUseCase.execute()

        // assert
        coVerify(exactly = 1) {
            contactRepository.setContactSynced(any())
        }
    }
}
