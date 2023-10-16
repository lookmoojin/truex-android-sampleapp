package com.truedigital.common.share.data.coredata.deeplink.usecase

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class IsDynamicUrlUseCaseImplTest {

    private lateinit var isDynamicUrlUseCase: IsDynamicUrlUseCase

    @BeforeEach
    fun setUp() {
        isDynamicUrlUseCase = IsDynamicUrlUseCaseImpl()
    }

    @Test
    fun testIsDynamicUrl_urlIsEmpty_returnFalse() {
        val result = isDynamicUrlUseCase.execute("")
        Assertions.assertFalse(result)
    }

    @Test
    fun testIsDynamicUrl_urlIsTrueIdProtocol_returnFalse() {
        val result = isDynamicUrlUseCase.execute("trueid://")
        Assertions.assertFalse(result)
    }

    @Test
    fun testIsDynamicUrl_urlIsDynamicLink_dev_returnTrue() {
        val result = isDynamicUrlUseCase.execute("https://s.trueid-dev.net/yQ4Y")
        Assertions.assertTrue(result)
    }

    @Test
    fun testIsDynamicUrl_urlIsDynamicLink_preprod_returnTrue() {
        val result = isDynamicUrlUseCase.execute("https://s.trueid-preprod.net/yQ4Y")
        Assertions.assertTrue(result)
    }

    @Test
    fun testIsDynamicUrl_urlIsDynamicLink_prod_returnTrue() {
        val result = isDynamicUrlUseCase.execute("https://s.trueid.net/yQ4Y")
        Assertions.assertTrue(result)
    }

    @Test
    fun testIsDynamicUrl_urlIsDynamicLink_app_goo_returnTrue() {
        val result = isDynamicUrlUseCase.execute("https://e22s5.app.goo.gl/q1Ptm")
        Assertions.assertTrue(result)
    }

    @Test
    fun testIsDynamicUrl_urlIsWebUrl_returnFalse() {
        val result = isDynamicUrlUseCase.execute("https://news.trueid.net/detail/Q7r1rRzzvxV2")
        Assertions.assertFalse(result)
    }
}
