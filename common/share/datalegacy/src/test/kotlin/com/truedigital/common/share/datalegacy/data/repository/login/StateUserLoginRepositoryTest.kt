package com.truedigital.common.share.datalegacy.data.repository.login

import com.truedigital.common.share.datalegacy.wrapper.AuthManagerWrapper
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StateUserLoginRepositoryTest {

    private val authManagerWrapper: AuthManagerWrapper = mockk()

    private lateinit var repository: StateUserLoginRepository

    @BeforeEach
    fun setup() {
        repository = StateUserLoginRepositoryImpl(authManagerWrapper)
    }

    @Test
    fun `Given isOpenByPass is true When isOpenByPass Then return true`() = runTest {
        mockkObject(authManagerWrapper)

        authManagerWrapper.setOpenByPass(true)

        val result = repository.isOpenByPass()

        assertTrue(result)
    }

    @Test
    fun `Given isOpenByPass is false When isOpenByPass Then return false`() = runTest {
        mockkObject(authManagerWrapper)

        authManagerWrapper.setOpenByPass(false)

        val result = repository.isOpenByPass()

        assertFalse(result)
    }

    @Test
    fun `Given number When setMobileNumber Then store number`() = runTest {
        val number = "number"

        repository.setMobileNumber(number)

        assertEquals("number", repository.getMobileNumber())
    }

    @Test
    fun `Given deeplink When setMobileNumber Then store number`() = runTest {
        val deeplink = "deeplink"

        repository.setDeeplink(deeplink)

        assertEquals("deeplink", repository.getDeeplink())
    }
}
