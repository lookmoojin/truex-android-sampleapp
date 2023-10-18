package com.truedigital.share.data.firestoreconfig.domainconfig.usecase

import android.content.Context
import com.truedigital.share.data.firestoreconfig.domainconfig.repository.secure.FireBaseSecureRepository
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GetFireBaseSecureUseCaseTest {

    private val mockContext = mockk<Context>(relaxed = true)
    private val mockFireBaseSecureRepository = mockk<FireBaseSecureRepository>(relaxed = true)

    private lateinit var getFireBaseSecureUseCase: GetFireBaseSecureUseCaseImpl

    @BeforeEach
    fun setUp() {
        getFireBaseSecureUseCase = GetFireBaseSecureUseCaseImpl(mockFireBaseSecureRepository)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `test execute calls fireBaseSecureRepository getFireBaseSecure`() {
        // Call the function to be tested
        getFireBaseSecureUseCase.execute(mockContext)

        // Verify that fireBaseSecureRepository.getFireBaseSecure() was called with the correct context
        verify { mockFireBaseSecureRepository.getFireBaseSecure(mockContext) }
    }
}
