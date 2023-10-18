package com.truedigital.core.domain.usecase

import com.truedigital.core.constant.FireBaseConstant
import com.truedigital.core.domain.usecase.model.DataRocketModel
import com.truedigital.core.utils.SharedPrefsUtils
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetRocketUseCaseTest {
    private lateinit var sharedPrefsUtils: SharedPrefsUtils
    private lateinit var getRocketUseCase: GetRocketUseCase

    @Before
    fun setUp() {
        sharedPrefsUtils = mockk()
        getRocketUseCase = GetRocketUseCaseImpl(sharedPrefsUtils)
    }

    @Test
    fun `execute should return valid DataAnimalModel`() {
        // Arrange
        val jsonString = "{\n" +
            "  \"control_list\": [\n" +
            "    \"PHRESm5Gyf5HnR5JZMt2ExTYZ3uJniLY0m5FJMqr2F8=\"\n" +
            "  ],\n" +
            "  \"blackbox\": [\n" +
            "    \"4a6cPehI7OG6cuDZka5NDZ7FR8a60d3auda+sKfg4Ng=\"\n" +
            "  ]\n" +
            "}"
        val expectedModel = DataRocketModel(
            arrayListOf("PHRESm5Gyf5HnR5JZMt2ExTYZ3uJniLY0m5FJMqr2F8="),
            arrayListOf("4a6cPehI7OG6cuDZka5NDZ7FR8a60d3auda+sKfg4Ng=")
        )

        every { sharedPrefsUtils.get(FireBaseConstant.FIREBASE_ROCKET, "") } returns jsonString

        // Act
        val result = getRocketUseCase.execute()

        // Assert
        assertEquals(expectedModel, result)
    }

    @Test
    fun `execute should return empty DataAnimalModel if JSON parsing fails`() {
        // Arrange
        val jsonString = "{\"animalName\": \"Lion\", \"animalType\": \"Mammal\"}"

        every { sharedPrefsUtils.get(FireBaseConstant.FIREBASE_ROCKET, "") } returns jsonString

        // Act
        val result = getRocketUseCase.execute()

        // Assert
        assertEquals(DataRocketModel(), result)
    }

    @Test
    fun `execute should return empty DataAnimalModel if JSON string is empty`() {
        // Arrange
        val jsonString = ""
        every { sharedPrefsUtils.get(FireBaseConstant.FIREBASE_ROCKET, "") } returns jsonString

        // Act
        val result = getRocketUseCase.execute()

        // Assert
        assertEquals(DataRocketModel(), result)
    }
}
