package com.truedigital.core.domain.usecase

import com.truedigital.core.domain.usecase.model.DataPinnedDomainsModel
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class MapPinnedDomainsUseCaseTest {

    private lateinit var getPinnedDomainsUseCase: GetPinnedDomainsUseCase
    private lateinit var mapPinnedDomainsUseCase: MapPinnedDomainsUseCase

    @Before
    fun setUp() {
        getPinnedDomainsUseCase = mockk()
        mapPinnedDomainsUseCase = MapPinnedDomainsUseCaseImpl(getPinnedDomainsUseCase)
    }

    @Test
    fun `test execute returns correctly mapped domains`() {
        // Given
        val dummyPinnedDomain = DataPinnedDomainsModel(
            urls = listOf("http://example.com", "http://test.com"),
            controlList = listOf("sha256/testControl1", "sha256/testControl2"),
            blackBox = listOf("sha256/testBlack1", "sha256/testBlack2")
        )
        every { getPinnedDomainsUseCase.execute() } returns listOf(dummyPinnedDomain)

        val expectedMap = mapOf(
            "sha256/testControl1" to listOf("http://example.com", "http://test.com"),
            "sha256/testControl2" to listOf("http://example.com", "http://test.com"),
            "sha256/testBlack1" to listOf("http://example.com", "http://test.com"),
            "sha256/testBlack2" to listOf("http://example.com", "http://test.com")
        )

        // When
        val result = mapPinnedDomainsUseCase.execute()

        // Then
        assertEquals(expectedMap, result)
    }

    @Test
    fun `test execute returns empty map when PinnedDomain list is empty`() {
        // Given
        every { getPinnedDomainsUseCase.execute() } returns emptyList()

        // When
        val result = mapPinnedDomainsUseCase.execute()

        // Then
        assertEquals(emptyMap(), result)
    }

    @Test
    fun `test execute handles null URLs correctly`() {
        // Given
        val dummyPinnedDomain = DataPinnedDomainsModel(
            urls = null,
            controlList = listOf("sha256/testControl1"),
            blackBox = listOf("sha256/testBlack1")
        )
        every { getPinnedDomainsUseCase.execute() } returns listOf(dummyPinnedDomain)

        // When
        val result = mapPinnedDomainsUseCase.execute()

        // Then
        assertEquals(emptyMap(), result)
    }

    @Test
    fun `test execute handles empty controlList or blackBox correctly`() {
        // Given
        val dummyPinnedDomain = DataPinnedDomainsModel(
            urls = listOf("http://example.com"),
            controlList = emptyList(),
            blackBox = emptyList()
        )
        every { getPinnedDomainsUseCase.execute() } returns listOf(dummyPinnedDomain)

        // When
        val result = mapPinnedDomainsUseCase.execute()

        // Then
        assertEquals(emptyMap(), result)
    }
}
