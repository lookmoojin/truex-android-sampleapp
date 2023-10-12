package com.truedigital.common.share.datalegacy.data.endpoint

import com.truedigital.common.share.datalegacy.domain.endpoint.usecase.GetApiConfigurationUseCase
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ApiConfigurationManagerTest {

    private val getApiConfigurationUseCase = mockk<GetApiConfigurationUseCase>()
    private val apiConfigurationManager = ApiConfigurationManager(getApiConfigurationUseCase)

    @Test
    fun `should return correct url when get url with service name`() {
        // given
        val serviceName = "my-service"
        val expectedUrl = "https://my-service.com"
        every { getApiConfigurationUseCase.getServiceUrl(serviceName) } returns expectedUrl

        // when
        val result = apiConfigurationManager.getUrl(serviceName)

        // then
        assertEquals(expectedUrl, result)
    }

    @Test
    fun `should return correct url when get url with login status and service name`() {
        // given
        val serviceName = "my-service"
        val expectedUrl = "https://my-service.com"
        val isLoggedIn = true
        every { getApiConfigurationUseCase.getServiceUrl(isLoggedIn, serviceName) } returns expectedUrl

        // when
        val result = apiConfigurationManager.getUrl(isLoggedIn, serviceName)

        // then
        assertEquals(expectedUrl, result)
    }

    @Test
    fun `should return correct token when get token with service name`() {
        // given
        val serviceName = "my-service"
        val expectedToken = "my-token"
        every { getApiConfigurationUseCase.getServiceToken(serviceName) } returns expectedToken

        // when
        val result = apiConfigurationManager.getToken(serviceName)

        // then
        assertEquals(expectedToken, result)
    }

    @Test
    fun `should return true when has api configuration with service name`() {
        // given
        val serviceName = "my-service"
        every { getApiConfigurationUseCase.hasApiConfiguration(serviceName) } returns true

        // when
        val result = apiConfigurationManager.hasApiConfiguration(serviceName)

        // then
        assertEquals(true, result)
    }

    @Test
    fun `should return true when is api support jwt with service name`() {
        // given
        val serviceName = "my-service"
        every { getApiConfigurationUseCase.isApiSupportJwt(serviceName) } returns true

        // when
        val result = apiConfigurationManager.isApiSupportJwt(serviceName)

        // then
        assertEquals(true, result)
    }
}
