package com.truedigital.features.tuned.domain.facade

import android.util.Base64
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.truedigital.features.tuned.data.authentication.model.AuthenticationToken
import com.truedigital.features.tuned.data.authentication.repository.AuthenticationTokenRepository
import com.truedigital.features.tuned.data.user.repository.MusicUserRepository
import com.truedigital.features.tuned.domain.facade.authentication.GetAuthenticatedUserUseCase
import com.truedigital.features.tuned.domain.facade.authentication.GetAuthenticatedUserUseCaseImpl
import com.truedigital.features.utils.MockDataModel
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import io.reactivex.Single
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GetAuthenticatedUserUseCaseTest {
    private lateinit var getAuthenticatedUserUseCase: GetAuthenticatedUserUseCase

    @MockK
    private lateinit var musicUserRepository: MusicUserRepository

    @MockK
    private lateinit var authenticationTokenRepository: AuthenticationTokenRepository

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        getAuthenticatedUserUseCase = GetAuthenticatedUserUseCaseImpl(
            musicUserRepository,
            authenticationTokenRepository
        )
    }

    @Test
    fun test_getUserSuccess_userIdNull_returnError() {
        every { musicUserRepository.get(false) } returns Single.just(MockDataModel.mockUserTuned)
        every { musicUserRepository.get(true) } returns Single.just(MockDataModel.mockUserTuned)
        every { musicUserRepository.refreshSettings() } returns Single.just(MockDataModel.mockSetting)
        every {
            authenticationTokenRepository.get(MockDataModel.mockDevice.uniqueId, false)
        } returns Single.just(
            Pair(
                AuthenticationToken(
                    "refreshToken",
                    10000L,
                    null
                ),
                true
            )
        )

        getAuthenticatedUserUseCase.execute(MockDataModel.mockDevice)
            .test()
            .assertError(IllegalStateException::class.java)
    }

    @Test
    fun test_getUserSuccess_equalUserId_returnUser() {
        val mockTokenPayload = "token"
        val mockUserId = MockDataModel.mockUserTuned.userId
        val mockJsonString = "{\"key\":\"music\",\"sub\":\"$mockUserId\"}"
        val mockJsonElement: JsonElement = JsonParser.parseString(mockJsonString)

        mockkStatic(JsonParser::class)
        every { JsonParser.parseString(String(mockJsonString.toByteArray())) } returns mockJsonElement

        mockkStatic(Base64::class)
        every {
            Base64.decode(
                mockTokenPayload,
                Base64.DEFAULT
            )
        } returns mockJsonString.toByteArray()

        every { musicUserRepository.get(false) } returns Single.just(MockDataModel.mockUserTuned)
        every { musicUserRepository.get(true) } returns Single.just(MockDataModel.mockUserTuned)
        every { musicUserRepository.refreshSettings() } returns Single.just(MockDataModel.mockSetting)
        every {
            authenticationTokenRepository.get(MockDataModel.mockDevice.uniqueId, false)
        } returns Single.just(
            Pair(
                AuthenticationToken(
                    "refreshToken",
                    10000L,
                    "abc.$mockTokenPayload.f"
                ),
                true
            )
        )

        getAuthenticatedUserUseCase.execute(MockDataModel.mockDevice)
            .test()
            .assertValue {
                it == MockDataModel.mockUserTuned
            }
    }

    @Test
    fun test_getUserSuccess_notEqualUserId_returnError() {
        val mockTokenPayload = "token"
        val mockUserId = 1234
        val mockJsonString = "{\"key\":\"music\",\"sub\":\"$mockUserId\"}"
        val mockJsonElement: JsonElement = JsonParser.parseString(mockJsonString)

        mockkStatic(JsonParser::class)
        every { JsonParser.parseString(String(mockJsonString.toByteArray())) } returns mockJsonElement

        mockkStatic(Base64::class)
        every {
            Base64.decode(
                mockTokenPayload,
                Base64.DEFAULT
            )
        } returns mockJsonString.toByteArray()

        every { musicUserRepository.get(false) } returns Single.just(MockDataModel.mockUserTuned)
        every { musicUserRepository.get(true) } returns Single.just(MockDataModel.mockUserTuned)
        every { musicUserRepository.refreshSettings() } returns Single.just(MockDataModel.mockSetting)
        every {
            authenticationTokenRepository.get(MockDataModel.mockDevice.uniqueId, false)
        } returns Single.just(
            Pair(
                AuthenticationToken(
                    "refreshToken",
                    10000L,
                    "abc.$mockTokenPayload.f"
                ),
                true
            )
        )

        getAuthenticatedUserUseCase.execute(MockDataModel.mockDevice)
            .test()
            .assertError(IllegalStateException::class.java)
    }
}
