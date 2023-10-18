package com.truedigital.common.share.datalegacy.domain.endpoint.usecase

import com.truedigital.common.share.datalegacy.login.LoginManagerInterface
import com.truedigital.share.data.firestoreconfig.domainconfig.model.ApiServiceData
import com.truedigital.share.data.firestoreconfig.domainconfig.repository.DomainRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GetApiConfigurationUseCaseTest {

    // Instantiate the class under test
    private lateinit var getApiConfigurationUseCase: GetApiConfigurationUseCase

    // Mock dependencies
    private val apiConfigurationRepository: DomainRepository = mockk()
    private val loginManagerInterface: LoginManagerInterface = mockk()

    @BeforeEach
    fun setUp() {
        // Initialize the class under test with mocked dependencies
        getApiConfigurationUseCase =
            GetApiConfigurationUseCaseImpl(apiConfigurationRepository, loginManagerInterface)
    }

    @Test
    fun `test hasApiConfiguration returns true when service name exists`() {
        // Given
        val serviceName = "some_service_name"
        val apiServiceData = ApiServiceData()
        every { apiConfigurationRepository.getApiServiceData(serviceName) } returns apiServiceData

        // When
        val result = getApiConfigurationUseCase.hasApiConfiguration(serviceName)

        // Then
        assertTrue(result)
    }

    @Test
    fun `test hasApiConfiguration returns false when service name does not exist`() {
        // Given
        val serviceName = "some_service_name"
        every { apiConfigurationRepository.getApiServiceData(serviceName) } returns null

        // When
        val result = getApiConfigurationUseCase.hasApiConfiguration(serviceName)

        // Then
        assertFalse(result)
    }

    @Test
    fun `test isApiSupportJwt returns true when useJwt is true`() {
        // Given
        val serviceName = "some_service_name"
        val apiServiceData = ApiServiceData().apply { useJwt = true }
        every { apiConfigurationRepository.getApiServiceData(serviceName) } returns apiServiceData

        // When
        val result = getApiConfigurationUseCase.isApiSupportJwt(serviceName)

        // Then
        assertTrue(result)
    }

    @Test
    fun `test isApiSupportJwt returns false when useJwt is false`() {
        // Given
        val serviceName = "some_service_name"
        val apiServiceData = ApiServiceData().apply { useJwt = false }
        every { apiConfigurationRepository.getApiServiceData(serviceName) } returns apiServiceData

        // When
        val result = getApiConfigurationUseCase.isApiSupportJwt(serviceName)

        // Then
        assertFalse(result)
    }

    @Test
    fun `test getServiceUrl returns non-login URL when not logged in`() {
        // Given
        val serviceName = "some_service_name"
        val apiServiceData = ApiServiceData().apply { nonLoginUrl = "non_login_url" }
        every { apiConfigurationRepository.getApiServiceData(serviceName) } returns apiServiceData
        every { loginManagerInterface.isLoggedIn() } returns false

        // When
        val result = getApiConfigurationUseCase.getServiceUrl(serviceName)

        // Then
        assertEquals("non_login_url", result)
    }

    @Test
    fun `test getServiceUrl returns login URL when logged in`() {
        // Given
        val serviceName = "some_service_name"
        val apiServiceData = ApiServiceData().apply { loginUrl = "login_url" }
        every { apiConfigurationRepository.getApiServiceData(serviceName) } returns apiServiceData
        every { loginManagerInterface.isLoggedIn() } returns true

        // When
        val result = getApiConfigurationUseCase.getServiceUrl(serviceName)

        // Then
        assertEquals("login_url", result)
    }

    @Test
    fun `test getServiceUrl with isLoggedIn false`() {
        // given
        val serviceName = "service2"
        val apiServiceData = ApiServiceData().apply {
            this.serviceName = serviceName
            nonLoginUrl = "http://example.com/nonlogin"
            loginUrl = "http://example.com/login"
            useJwt = true
            apiToken = "token123"
        }
        every { apiConfigurationRepository.getApiServiceData(serviceName) } returns apiServiceData
        every { loginManagerInterface.isLoggedIn() } returns false

        // when
        val result = getApiConfigurationUseCase.getServiceUrl(false, serviceName)

        // then
        assertEquals(apiServiceData.nonLoginUrl, result)
    }

    @Test
    fun `test getServiceToken`() {
        // given
        val serviceName = "service3"
        val apiServiceData = ApiServiceData().apply {
            this.serviceName = serviceName
            loginUrl = ""
            useJwt = true
            apiToken = "token123"
        }
        every { apiConfigurationRepository.getApiServiceData(serviceName) } returns apiServiceData

        // when
        val result = getApiConfigurationUseCase.getServiceToken(serviceName)

        // then
        assertEquals(apiServiceData.apiToken, result)
    }
}
