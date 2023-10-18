package com.truedigital.core.domain.usecase

import com.truedigital.core.constant.FireBaseConstant
import com.truedigital.core.domain.usecase.model.DataPinnedDomainsModel
import com.truedigital.core.utils.SharedPrefsUtils
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetPinnedDomainsUseCaseImplTest {
    private lateinit var sharedPrefsUtils: SharedPrefsUtils
    private lateinit var getPinnedDomainsUseCase: GetPinnedDomainsUseCase

    @Before
    fun setUp() {
        sharedPrefsUtils = mockk()
        getPinnedDomainsUseCase = GetPinnedDomainsUseCaseImpl(sharedPrefsUtils)
    }

    @Test
    fun `execute should return valid DataPinnedDomainsModel`() {
        // Given
        val jsonString = "[{\"urls\": [\"http://example.com\"]}]"
        val expectedDomains = listOf(DataPinnedDomainsModel(urls = listOf("http://example.com")))

        every {
            sharedPrefsUtils.get(
                FireBaseConstant.FIREBASE_PINNED_DOMAINS,
                ""
            )
        } returns jsonString

        // Act
        val result = getPinnedDomainsUseCase.execute()

        // Assert
        assertEquals(expectedDomains, result)
    }

    @Test
    fun `execute should return empty DataPinnedDomainsModel if JSON parsing fails`() {
        // Arrange
        val jsonString = "{\"animalName\": \"Lion\", \"animalType\": \"Mammal\"}"

        every {
            sharedPrefsUtils.get(
                FireBaseConstant.FIREBASE_PINNED_DOMAINS,
                ""
            )
        } returns jsonString

        // Act
        val result = getPinnedDomainsUseCase.execute()

        // Assert
        assertEquals(listOf<DataPinnedDomainsModel>(), result)
    }

    @Test
    fun `execute should return empty DataPinnedDomainsModel if JSON string is empty`() {
        // Arrange
        val jsonString = ""
        every {
            sharedPrefsUtils.get(
                FireBaseConstant.FIREBASE_PINNED_DOMAINS,
                ""
            )
        } returns jsonString

        // Act
        val result = getPinnedDomainsUseCase.execute()

        // Assert
        assertEquals(listOf<DataPinnedDomainsModel>(), result)
    }
}
