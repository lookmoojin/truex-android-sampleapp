package com.truedigital.common.share.datalegacy.data.repository.profile

import com.truedigital.authentication.domain.model.ProfileModel
import com.truedigital.common.share.datalegacy.wrapper.AuthManagerWrapper
import com.truedigital.core.constant.FireBaseConstant
import com.truedigital.core.utils.SharedPrefsUtils
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class UserRepositoryTest {

    private lateinit var authManagerWrapper: AuthManagerWrapper
    private lateinit var sharedPrefsUtils: SharedPrefsUtils
    private lateinit var userRepository: UserRepository

    @BeforeEach
    fun setup() {
        authManagerWrapper = mockk()
        sharedPrefsUtils = mockk()
        userRepository = UserRepositoryImpl(authManagerWrapper, sharedPrefsUtils)
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `test getConfigState`() {
        every { sharedPrefsUtils.get("key", "any") } returns "true"

        val result = userRepository.getConfigState("key")

        assertEquals(true, result)
    }

    @Test
    fun `test setConfigState`() {
        every { sharedPrefsUtils.put("key", true) } returns mockk()

        userRepository.setConfigState("key", true)
    }

    @Test
    fun `test getDebugMode`() {
        every { sharedPrefsUtils.get(FireBaseConstant.DEBUG_MODE, "any") } returns "debug"

        val result = userRepository.getDebugMode()

        assertEquals("debug", result)
    }

    @Test
    fun `test setDebugMode`() {
        every { sharedPrefsUtils.put(FireBaseConstant.DEBUG_MODE, "debug") } returns mockk()

        userRepository.setDebugMode("debug")
    }

    @Test
    fun `test getProfile`() {
        val profileModel = ProfileModel()
        every { authManagerWrapper.getProfileCache() } returns profileModel

        val result = userRepository.getProfile()

        assertEquals(profileModel, result)
    }

    @Test
    fun `getAccessToken should call authManagerWrapper getAccessToken`() {
        every { authManagerWrapper.getAccessToken() } returns "access_token"

        val result = userRepository.getAccessToken()

        assertEquals("access_token", result)
        verify { authManagerWrapper.getAccessToken() }
    }

    @Test
    fun `getEmail should return email from ProfileModel`() {
        val profileModel = ProfileModel().apply {
            more = ProfileModel.MoreModel()
        }
        every { userRepository.getProfile() } returns profileModel

        val result = userRepository.getEmail()

        assertEquals("", result)
        verify { userRepository.getProfile() }
    }

    @Test
    fun `getLoginAccount should return loginByAccount from ProfileModel`() {
        val profileModel = ProfileModel().apply {
            more = ProfileModel.MoreModel()
        }
        every { userRepository.getProfile() } returns profileModel

        val result = userRepository.getLoginAccount()

        assertEquals("", result)
        verify { userRepository.getProfile() }
    }
}
