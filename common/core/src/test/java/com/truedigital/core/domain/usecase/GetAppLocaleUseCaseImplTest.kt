package com.truedigital.core.domain.usecase

import com.truedigital.core.data.device.repository.LocalizationRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.Locale

class GetAppLocaleUseCaseImplTest {

    @Test
    fun `when execute is called, it should return the app locale from the repository`() {
        // Arrange
        val expectedLocale = Locale("en", "US")
        val localizationRepository = mockk<LocalizationRepository>()
        every { localizationRepository.getAppLocale() } returns expectedLocale
        val getAppLocaleUseCase = GetAppLocaleUseCaseImpl(localizationRepository)

        // Act
        val result = getAppLocaleUseCase.execute()

        // Assert
        assertEquals(expectedLocale, result)
    }
}
