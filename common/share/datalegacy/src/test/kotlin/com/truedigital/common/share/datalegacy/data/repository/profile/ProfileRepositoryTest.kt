package com.truedigital.common.share.datalegacy.data.repository.profile

import com.truedigital.common.share.datalegacy.data.api.graph.GraphApiInterface
import com.truedigital.common.share.datalegacy.data.base.Failure
import com.truedigital.common.share.datalegacy.data.base.Success
import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.Setting
import com.truedigital.common.share.datalegacy.data.repository.profile.model.request.UpdateSettingProfileRequest
import com.truedigital.common.share.datalegacy.data.repository.profile.model.response.ProfileData
import com.truedigital.common.share.datalegacy.data.repository.profile.model.response.ProfileResponse
import com.truedigital.common.share.datalegacy.data.repository.profile.model.response.ProfileSettingsResponse
import com.truedigital.common.share.datalegacy.data.repository.profile.model.response.SegmentData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Response
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class ProfileRepositoryTest {

    private val mockGraphApi: GraphApiInterface = mockk()
    private val mockUserRepository: UserRepository = mockk()
    private lateinit var profileRepository: ProfileRepository

    @BeforeEach
    fun setup() {
        profileRepository = ProfileRepositoryImpl(mockGraphApi, mockUserRepository)
    }

    @Test
    fun `test getProfile returns cached data`() = runTest {
        // given
        val mockProfileData = ProfileData().apply {
            avatar = "https://avatar.com"
            displayName = "John Doe"
            firstName = "John"
            lastName = "Doe"
            settings = Setting()
        }
        every { mockUserRepository.getSsoId() } returns "1234"
        coEvery {
            mockGraphApi.getProfile(
                "1234",
                "avatar,display_name,first_name,last_name,settings,segment"
            )
        } returns Response.success(
            ProfileResponse().apply {
                data = mockProfileData
            }
        )

        // when
        val result = profileRepository.getProfile().toList()
        delay(100) // simulate delay for withTimeout
        assertEquals(1, result.size)

        // then
        verify(exactly = 1) { mockUserRepository.getSsoId() }
        coVerify(exactly = 1) {
            mockGraphApi.getProfile(
                "1234",
                "avatar,display_name,first_name,last_name,settings,segment"
            )
        }
    }

    @Test
    fun `test getProfile returns default data`() = runTest {
        // given
        every { mockUserRepository.getSsoId() } returns "1234"
        every { mockUserRepository.getSsoAvatar() } returns "1234"
        every { mockUserRepository.getSsoDisplayName() } returns "1234"
        every { mockUserRepository.getBio() } returns "1234"
        coEvery {
            mockGraphApi.getProfile(
                "1234",
                "avatar,display_name,first_name,last_name,settings,segment"
            )
        } returns Response.success(
            ProfileResponse()
        )

        // when
        val result = profileRepository.getProfile().toList()
        delay(100) // simulate delay for withTimeout
        assertEquals(1, result.size)

        // then
        verify(exactly = 2) { mockUserRepository.getSsoId() }
        coVerify(exactly = 1) {
            mockGraphApi.getProfile(
                "1234",
                "avatar,display_name,first_name,last_name,settings,segment"
            )
        }
    }

    @Test
    fun `test getNonCachedProfile with successful response`() = runTest {
        // Setup mock response
        val mockProfileData = ProfileData().apply {
            avatar = "mock_avatar"
            displayName = "mock_display_name"
            firstName = "mock_first_name"
            lastName = "mock_last_name"
            settings = Setting()
            segment = SegmentData().apply {
                segmentPersonaHashMap = hashMapOf(
                    "mock_primary" to "mock_persona",
                    "mock_secondary" to "mock_persona"
                )
            }
        }

        val mockResponse = Response.success(ProfileResponse().apply { data = mockProfileData })

        coEvery {
            mockGraphApi.getProfile(any(), any())
        } returns mockResponse
        coEvery {
            mockUserRepository.getSsoId()
        } returns "mock_sso_id"

        // Call method under test
        val result = profileRepository.getNonCachedProfile().toList()

        // Verify response
        assertEquals(1, result.size)
        assertEquals(mockProfileData, (result[0] as Success).data)
    }

    @Test
    fun `test getNonCachedProfile with unsuccessful response`() = runTest {
        // Setup mock response
        val mockResponse = Response.error<ProfileResponse>(400, "null".toResponseBody())
        coEvery {
            mockGraphApi.getProfile(any(), any())
        } returns mockResponse
        every { mockUserRepository.getSsoId() } returns "mock_sso_id"
        every { mockUserRepository.getSsoAvatar() } returns "1234"
        every { mockUserRepository.getSsoDisplayName() } returns "1234"
        every { mockUserRepository.getBio() } returns "1234"

        // Call method under test
        val result = profileRepository.getNonCachedProfile().toList()

        // Verify response
        assertEquals(1, result.size)
        assertNotNull((result[0] as Success).data)
    }

    @Test
    fun `getProfileByUsername should return success when graphApi returns success`() =
        runTest {
            // Given
            val username = "testuser"
            val fields = "email,phone"
            val countryCode = "US"
            val profileData = ProfileData().apply {
                avatar = "mock_avatar"
                displayName = "mock_display_name"
                firstName = "mock_first_name"
                lastName = "mock_last_name"
                settings = Setting()
                segment = SegmentData().apply {
                    segmentPersonaHashMap = hashMapOf(
                        "mock_primary" to "mock_persona",
                        "mock_secondary" to "mock_persona"
                    )
                }
            }
            val mockProfile = ProfileResponse().apply {
                data = profileData
            }
            coEvery {
                mockGraphApi.getProfileByUsername(
                    username = username,
                    fields = fields,
                    countryCode = countryCode
                )
            } returns Response.success(mockProfile)

            // When
            val flow = profileRepository.getProfileByUsername(username, fields, countryCode)

            // Then
            flow.collect { result ->
                assertTrue((result is Success))
                assertEquals(mockProfile.data?.avatar, result.data.avatar)
                assertEquals(mockProfile.data?.displayName, result.data.displayName)
                assertEquals(mockProfile.data?.firstName, result.data.firstName)
                assertEquals(mockProfile.data?.lastName, result.data.lastName)
                assertEquals(2, result.data.segment?.segmentPersonaHashMap?.size)
            }
        }

    @Test
    fun `getProfileByUsername should return failure when graphApi returns error`() =
        runTest {
            // Given
            val username = "testuser"
            val fields = "email,phone"
            val countryCode = "US"
            val errorCode = 404
            val errorType = "not_found"
            val errorMessage = "User not found"
            coEvery {
                mockGraphApi.getProfileByUsername(
                    username = username,
                    fields = fields,
                    countryCode = countryCode
                )
            } returns Response.error(
                errorCode,
                """
            {
                "error": {
                    "type": "$errorType",
                    "message": "$errorMessage"
                }
            }
                """.trimIndent().toResponseBody("application/json".toMediaTypeOrNull())
            )

            // When
            val flow = profileRepository.getProfileByUsername(username, fields, countryCode)

            // Then
            flow.collect { result ->
                assertTrue((result is Failure))
                assertEquals(
                    "NOT_FOUND - 404 - failed to get profile by username",
                    result.exception.message
                )
            }
        }

    @Test
    fun `getOtherProfile should return success when graphApi returns success`() = runTest {
        // Given
        val ssoid = "testuser123"
        val fields = "email,phone"
        val countryCode = "US"
        val profileData = ProfileData().apply {
            avatar = "mock_avatar"
            displayName = "mock_display_name"
            firstName = "mock_first_name"
            lastName = "mock_last_name"
            settings = Setting()
        }
        val mockProfile = ProfileResponse().apply {
            data = profileData
        }
        coEvery {
            mockGraphApi.getOtherProfile(
                ssoId = ssoid,
                fields = fields,
                countryCode = countryCode
            )
        } returns Response.success(mockProfile)

        // When
        val flow = profileRepository.getOtherProfile(ssoid, fields, countryCode)

        // Then
        flow.collect { result ->
            assertTrue((result is Success))
            assertEquals(mockProfile.data?.avatar, result.data.avatar)
            assertEquals(mockProfile.data?.displayName, result.data.displayName)
            assertEquals(mockProfile.data?.firstName, result.data.firstName)
            assertEquals(mockProfile.data?.lastName, result.data.lastName)
        }
    }

    @Test
    fun `getOtherProfile should return failure when graphApi returns error`() = runTest {
        // Given
        val ssoid = "testuser123"
        val fields = "email,phone"
        val countryCode = "US"
        val errorCode = 401
        val errorType = "unauthorized"
        val errorMessage = "Invalid credentials"
        coEvery {
            mockGraphApi.getOtherProfile(
                ssoId = ssoid,
                fields = fields,
                countryCode = countryCode
            )
        } returns Response.error(
            errorCode,
            """
            {
                "error": {
                    "type": "$errorType",
                    "message": "$errorMessage"
                }
            }
            """.trimIndent()
                .toResponseBody("application/json".toMediaTypeOrNull())
        )

        // When
        val flow = profileRepository.getOtherProfile(ssoid, fields, countryCode)

        // Then
        flow.collect { result ->
            assertTrue((result is Failure))
            assertEquals(
                "UNAUTHORIZED - 401 - failed to get profile by ssoid",
                result.exception.message
            )
        }
    }

    @Test
    fun `updateProfileWatch with successful response`() = runTest {
        // Given
        val updateSettingProfileRequest = UpdateSettingProfileRequest()
        coEvery { mockUserRepository.getSsoId() } returns "123"
        coEvery {
            mockGraphApi.updateProfileWatch(
                ssoId = any(),
                updateProfileSettingsRequest = any()
            )
        } returns Response.success(
            ProfileSettingsResponse().apply {
                code = 10001
            }
        )

        // When
        val result = profileRepository.updateProfileWatch(updateSettingProfileRequest).first()

        // Then
        assertTrue((result is Success))
    }

    @Test
    fun `updateProfileWatch with null body`() = runTest {
        // Given
        val updateSettingProfileRequest = UpdateSettingProfileRequest()
        coEvery { mockUserRepository.getSsoId() } returns "123"
        coEvery {
            mockGraphApi.updateProfileWatch(
                ssoId = any(),
                updateProfileSettingsRequest = any()
            )
        } returns Response.success(null)

        // When
        val result = profileRepository.updateProfileWatch(updateSettingProfileRequest).first()

        // Then
        assertTrue((result is Failure))
        assertEquals(
            "NULL_BODY - 200 - failed to update profile setting cause response body is null",
            result.exception.message
        )
    }

    @Test
    fun `updateProfileWatch with non-successful response`() = runTest {
        // Given
        val updateSettingProfileRequest = UpdateSettingProfileRequest()
        coEvery { mockUserRepository.getSsoId() } returns "123"
        coEvery {
            mockGraphApi.updateProfileWatch(
                ssoId = any(),
                updateProfileSettingsRequest = any()
            )
        } returns Response.success(
            ProfileSettingsResponse().apply {
                code = 99999
            }
        )

        // When
        val result = profileRepository.updateProfileWatch(updateSettingProfileRequest).first()

        // Then
        assertTrue((result is Failure))
        assertEquals("FAILED - 200 - failed to update profile setting", result.exception.message)
    }
}
