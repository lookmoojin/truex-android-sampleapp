package com.truedigital.navigation.usecase

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.navigation.data.repository.CountryRepository
import com.truedigital.navigations.share.data.model.CountryResponseItem
import com.truedigital.navigations.share.data.model.Language
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GetCountryUseCaseTest {

    private lateinit var useCase: GetCountryUseCase

    private val countryRepository: CountryRepository = mock()
    private val mockCountryResponseItem: CountryResponseItem = CountryResponseItem(
        clientId = "212",
        appId = "224",
        clientSecret = "236",
        code = "th",
        icon = null,
        languages = listOf(
            Language(
                code = "th",
                name = "Thai",
                isDefault = true
            ),
            Language(
                code = "en",
                name = "English",
                isDefault = false
            ),
        ),
        name = "Thailand",
        welcomeImgS = null,
        welcomeImgXL = null,
        selectCountryView = null,
        getStartView = null,
    )

    @BeforeEach
    fun setUp() {
        useCase = GetCountryUseCaseImpl(
            countryRepository = countryRepository
        )
    }

    @Test
    fun `Execute get country list success`() {
        runTest {
            // arrange
            whenever(countryRepository.getCountryList()).thenReturn(
                flow { listOf(mockCountryResponseItem) }
            )

            // act
            val actual = useCase.execute()

            // assert
            actual.collect {
                assertEquals(mockCountryResponseItem, it)
            }
        }
    }
}
