package com.truedigital.common.share.data.coredata.deeplink.usecase

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class IsDomainDeeplinkUrlUseCaseImplTest {
    private lateinit var isDomainDeeplinkUrlUseCase: IsDomainDeeplinkUrlUseCase

    private val HOST_LIST = listOf("tv.trueid.net", "home.trueid.net")
    private val URL_TEST_SUCCESS = "https://tv.trueid.net/ch-3"
    private val URL_TEST_FAILED = "https://s.trueid-preprod.net/yQ4Y"
    private val URI_TEST_SUEECSS = "trueid://tv.trueid.net/ch-3"
    private val URI_TEST_FAILED = "trueid://sport.trueid.net/detail/yQ4Y"

    @BeforeEach
    fun setUp() {
        isDomainDeeplinkUrlUseCase = IsDomainDeeplinkUrlUseCaseImpl()
    }

    @Test
    fun `test IsDomainDeeplinkUrl URL is module return true`() {
        val result = isDomainDeeplinkUrlUseCase.execute(HOST_LIST, URL_TEST_SUCCESS)
        Assertions.assertEquals(true, result)
    }

    @Test
    fun `test IsDomainDeeplinkUrl URL is not module return false`() {
        val result = isDomainDeeplinkUrlUseCase.execute(HOST_LIST, URL_TEST_FAILED)
        Assertions.assertEquals(false, result)
    }

    @Test
    fun `test IsDomainDeeplinkUrl URI is module return true`() {
        val result = isDomainDeeplinkUrlUseCase.execute(HOST_LIST, URI_TEST_SUEECSS)
        Assertions.assertEquals(true, result)
    }

    @Test
    fun `test IsDomainDeeplinkUrl URI is not module return false`() {
        val result = isDomainDeeplinkUrlUseCase.execute(HOST_LIST, URI_TEST_FAILED)
        Assertions.assertEquals(false, result)
    }

    @Test
    fun `test IsDomainDeeplinkUrl URI is not http or trueid return false`() {
        val result = isDomainDeeplinkUrlUseCase.execute(HOST_LIST, "market://")
        Assertions.assertEquals(false, result)
    }

    @Test
    fun `test IsDomainDeeplinkUrl URI is empty return false`() {
        val result = isDomainDeeplinkUrlUseCase.execute(HOST_LIST, "")
        Assertions.assertEquals(false, result)
    }

    @Test
    fun `test IsDomainDeeplinkUrl HOST_LIST is empty return false`() {
        val result = isDomainDeeplinkUrlUseCase.execute(listOf(), URL_TEST_SUCCESS)
        Assertions.assertEquals(false, result)
    }
}
