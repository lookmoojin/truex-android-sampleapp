package com.truedigital.features.tuned.data.authentication.model.response

import android.util.Base64
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import com.truedigital.features.tuned.data.authentication.model.AuthenticationToken
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class AuthenticationTokenTest {

    private val mockTokenPayload = "token"
    private val mockUserId = 123456
    private val mockAccessToken = "abc.$mockTokenPayload.f"
    private val mockJsonStringSub = "{\"key\":\"music\",\"sub\":\"$mockUserId\"}"
    private fun mockJsonParserAndBase64(jsonString: String) {
        val mockJsonElement: JsonElement = JsonParser.parseString(jsonString)
        mockkStatic(JsonParser::class)
        every { JsonParser.parseString(String(jsonString.toByteArray())) } returns mockJsonElement

        mockkStatic(Base64::class)
        every {
            Base64.decode(
                mockTokenPayload,
                Base64.DEFAULT
            )
        } returns jsonString.toByteArray()
    }

    @Test
    fun testAuthenticationToken_getData_returnAuthenticationToken() {
        val result = AuthenticationToken(
            refreshToken = "refreshToken",
            expiration = 1L,
            accessToken = "accessToken"
        )
        Assertions.assertEquals("refreshToken", result.refreshToken)
        Assertions.assertEquals(1L, result.expiration)
        Assertions.assertEquals("accessToken", result.accessToken)
    }

    @Test
    fun testUserId_accessTokenNull_returnNull() {
        val result = AuthenticationToken(
            refreshToken = "refreshToken",
            expiration = 1L,
            accessToken = null
        )
        Assertions.assertEquals(null, result.userId)
    }

    @Test
    fun testUserId_accessTokenNotNull_returnInt() {
        mockJsonParserAndBase64(mockJsonStringSub)

        val result = AuthenticationToken(
            refreshToken = "refreshToken",
            expiration = 1L,
            accessToken = mockAccessToken
        )

        Assertions.assertEquals(mockUserId, result.userId)
    }

    @Test
    fun testHasAlbumShuffleRight_scopesNull_returnFalse() {
        mockJsonParserAndBase64(mockJsonStringSub)

        val result = AuthenticationToken(
            refreshToken = "refreshToken",
            expiration = 1L,
            accessToken = mockAccessToken
        )

        Assertions.assertEquals(false, result.hasAlbumShuffleRight)
    }

    @Test
    fun testHasTrackPlayRight_jsonSyntaxException_returnFalse() {
        mockkStatic(JsonParser::class)
        every { JsonParser.parseString(String(mockJsonStringSub.toByteArray())) }.throws(
            JsonSyntaxException("error")
        )

        mockkStatic(Base64::class)
        every {
            Base64.decode(
                mockTokenPayload,
                Base64.DEFAULT
            )
        } returns mockJsonStringSub.toByteArray()

        val result = AuthenticationToken(
            refreshToken = "refreshToken",
            expiration = 1L,
            accessToken = mockAccessToken
        )

        Assertions.assertEquals(false, result.hasTrackPlayRight)
    }

    @Test
    fun testHasTrackPlayRight_scopesException_returnFalse() {
        val mockJsonStringScope = "{\"scope\":\"music\"}"
        mockJsonParserAndBase64(mockJsonStringScope)

        val result = AuthenticationToken(
            refreshToken = "refreshToken",
            expiration = 1L,
            accessToken = mockAccessToken
        )

        Assertions.assertEquals(false, result.hasTrackPlayRight)
    }

    @Test
    fun testHasTrackPlayRight_scopesNotNull_equalKey_returnTrue() {
        val mockJsonStringScope = "{\"scope\":[\"track:play2\",\"track:play,song\"]}"
        mockJsonParserAndBase64(mockJsonStringScope)

        val result = AuthenticationToken(
            refreshToken = "refreshToken",
            expiration = 1L,
            accessToken = mockAccessToken
        )

        Assertions.assertEquals(true, result.hasTrackPlayRight)
    }

    @Test
    fun testHasTrackPlayRight_scopesNotNull_notEqualKey_returnFalse() {
        val mockJsonStringScope = "{\"scope\":[\"album:play\"]}"
        mockJsonParserAndBase64(mockJsonStringScope)

        val result = AuthenticationToken(
            refreshToken = "refreshToken",
            expiration = 1L,
            accessToken = mockAccessToken
        )

        Assertions.assertEquals(false, result.hasTrackPlayRight)
    }

    @Test
    fun testHasTrackPlayRight_scopesEmpty_returnFalse() {
        val mockJsonStringScope = "{\"scope\":[]}"
        mockJsonParserAndBase64(mockJsonStringScope)

        val result = AuthenticationToken(
            refreshToken = "refreshToken",
            expiration = 1L,
            accessToken = mockAccessToken
        )

        Assertions.assertEquals(false, result.hasTrackPlayRight)
    }
}
