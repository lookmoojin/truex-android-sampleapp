package com.truedigital.core.data.repository

import com.truedigital.core.BuildConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BuildVariantRepositoryTest {

    private lateinit var buildVariantRepository: BuildVariantRepository

    @BeforeEach
    fun setUp() {
        buildVariantRepository = BuildVariantRepositoryImpl()
    }

    @Test
    fun `getFlavorPlatform should return the correct flavor platform`() {
        // Given
        val expectedPlatform = BuildConfig.FLAVOR_platform

        // When
        val result = buildVariantRepository.getFlavorPlatform()

        // Then
        assertEquals(expectedPlatform, result)
    }

    @Test
    fun `getVersionName should return the correct version name`() {
        // Given
        val expectedVersionName = BuildConfig.VERSION_NAME

        // When
        val result = buildVariantRepository.getVersionName()

        // Then
        assertEquals(expectedVersionName, result)
    }

    @Test
    fun `getVersionCode should return the correct version code`() {
        // Given
        val expectedVersionCode = BuildConfig.VERSION_CODE

        // When
        val result = buildVariantRepository.getVersionCode()

        // Then
        assertEquals(expectedVersionCode, result)
    }
}
