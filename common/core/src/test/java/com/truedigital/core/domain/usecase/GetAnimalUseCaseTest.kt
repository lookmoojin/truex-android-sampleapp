package com.truedigital.core.domain.usecase

import com.truedigital.core.constant.FireBaseConstant
import com.truedigital.core.domain.usecase.model.DataAnimalModel
import com.truedigital.core.utils.SharedPrefsUtils
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetAnimalUseCaseTest {
    private lateinit var sharedPrefsUtils: SharedPrefsUtils
    private lateinit var getAnimalUseCase: GetAnimalUseCase

    @Before
    fun setUp() {
        sharedPrefsUtils = mockk()
        getAnimalUseCase = GetAnimalUseCaseImpl(sharedPrefsUtils)
    }

    @Test
    fun `execute should return valid DataAnimalModel`() {
        // Arrange
        val jsonString = "{\"cat\":true,\"whale\":[\"115b090a4b144aed\",\"e6b18e617e751702\"]}"
        val expectedModel =
            DataAnimalModel(true, arrayListOf("115b090a4b144aed", "e6b18e617e751702"))

        every { sharedPrefsUtils.get(FireBaseConstant.FIREBASE_ANIMALS, "") } returns jsonString

        // Act
        val result = getAnimalUseCase.execute()

        // Assert
        assertEquals(expectedModel, result)
    }

    @Test
    fun `execute should return empty DataAnimalModel if JSON parsing fails`() {
        // Arrange
        val jsonString = "{\"animalName\": \"Lion\", \"animalType\": \"Mammal\"}"

        every { sharedPrefsUtils.get(FireBaseConstant.FIREBASE_ANIMALS, "") } returns jsonString

        // Act
        val result = getAnimalUseCase.execute()

        // Assert
        assertEquals(DataAnimalModel(), result)
    }

    @Test
    fun `execute should return empty DataAnimalModel if JSON string is empty`() {
        // Arrange
        val jsonString = ""
        every { sharedPrefsUtils.get(FireBaseConstant.FIREBASE_ANIMALS, "") } returns jsonString

        // Act
        val result = getAnimalUseCase.execute()

        // Assert
        assertEquals(DataAnimalModel(), result)
    }
}
