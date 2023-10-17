package com.truedigital.features.tuned.domain.facade

import android.util.Base64
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.truedigital.features.tuned.data.authentication.model.AuthenticationToken
import com.truedigital.features.tuned.data.authentication.repository.AuthenticationTokenRepository
import com.truedigital.features.tuned.data.user.repository.MusicUserRepository
import com.truedigital.features.tuned.domain.facade.authentication.AuthenticationThirdPartyJWTUseCase
import com.truedigital.features.tuned.domain.facade.authentication.AuthenticationThirdPartyJWTUseCaseImpl
import com.truedigital.features.utils.MockDataModel
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import io.reactivex.Single
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class AuthenticationThirdPartyJWTUseCaseTest {

    private lateinit var authenticationThirdPartyJWTUseCase: AuthenticationThirdPartyJWTUseCase

    @MockK
    private lateinit var musicUserRepository: MusicUserRepository

    @MockK
    private lateinit var authenticationTokenRepository: AuthenticationTokenRepository

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        authenticationThirdPartyJWTUseCase = AuthenticationThirdPartyJWTUseCaseImpl(
            musicUserRepository,
            authenticationTokenRepository
        )
    }

    @Test
    fun test_getUserSuccess_userIdNull_returnError() {
        every {
            authenticationTokenRepository.getTokenByJwt(MockDataModel.mockDevice.uniqueId, "jwt")
        } returns Single.just(
            AuthenticationToken(
                "refreshToken",
                10000L,
                null
            )
        )

        authenticationThirdPartyJWTUseCase.execute(MockDataModel.mockDevice, "jwt")
            .test()
            .assertError(NullPointerException::class.java)
    }

    @Test
    fun test_getUserSuccess_userIdNotNull_returnUser() {
        val mockTokenPayload = "token"
        val mockUserId = 123456
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

        every {
            authenticationTokenRepository.getTokenByJwt(MockDataModel.mockDevice.uniqueId, "jwt")
        } returns Single.just(
            AuthenticationToken(
                "refreshToken",
                10000L,
                "abc.$mockTokenPayload.f"
            )
        )
        every { musicUserRepository.get(mockUserId) } returns Single.just(MockDataModel.mockUserTuned)
        every { musicUserRepository.refreshSettings() } returns Single.just(MockDataModel.mockSetting)

        authenticationThirdPartyJWTUseCase.execute(MockDataModel.mockDevice, "jwt")
            .test()
            .assertValue {
                it == MockDataModel.mockUserTuned
            }
    }
}
