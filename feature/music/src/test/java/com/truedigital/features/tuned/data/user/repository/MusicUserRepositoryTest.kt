package com.truedigital.features.tuned.data.user.repository

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.tuned.api.user.AuthenticatedUserApiInterface
import com.truedigital.features.tuned.api.user.BasicUserApiInterface
import com.truedigital.features.tuned.api.user.MetadataGroupApiInterface
import com.truedigital.features.tuned.application.configuration.Configuration
import com.truedigital.features.tuned.data.ObfuscatedKeyValueStoreInterface
import com.truedigital.features.tuned.data.device.model.Device
import com.truedigital.features.tuned.data.get
import com.truedigital.features.tuned.data.put
import com.truedigital.features.tuned.data.user.model.ContentLanguage
import com.truedigital.features.tuned.data.user.model.Login
import com.truedigital.features.tuned.data.user.model.LoginType
import com.truedigital.features.tuned.data.user.model.PromoCode
import com.truedigital.features.tuned.data.user.model.Settings
import com.truedigital.features.tuned.data.user.model.User
import com.truedigital.features.tuned.data.user.repository.MusicUserRepositoryImpl.Companion.CURRENT_USER_SETTINGS_KEY
import com.truedigital.features.tuned.data.util.WrappedValue
import com.truedigital.features.tuned.injection.module.NetworkModule
import com.truedigital.features.utils.MockDataModel
import io.reactivex.Single
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyInt
import retrofit2.HttpException
import retrofit2.Response
import kotlin.test.assertEquals

class MusicUserRepositoryTest {

    private lateinit var musicUserRepository: MusicUserRepository
    private val sharedPreferences: ObfuscatedKeyValueStoreInterface = mock()
    private val basicUserApi: BasicUserApiInterface = mock()
    private val authenticatedUserApi: AuthenticatedUserApiInterface = mock()
    private val metadataGroupApi: MetadataGroupApiInterface = mock()
    private val configuration: Configuration = mock()
    private val mockUser = MockDataModel.mockUserTuned
    private val oneDayMillisecond = 86400000L
    private fun setUpUserNull() {
        whenever(sharedPreferences.get<User>(MusicUserRepositoryImpl.CURRENT_USER_KEY))
            .thenReturn(null)
        initMusicUserRepository()
    }

    private fun setUpUser() {
        whenever(sharedPreferences.get<User>(MusicUserRepositoryImpl.CURRENT_USER_KEY))
            .thenReturn(mockUser)
        initMusicUserRepository()
    }

    private fun initMusicUserRepository() {
        musicUserRepository = MusicUserRepositoryImpl(
            sharedPreferences,
            basicUserApi,
            authenticatedUserApi,
            metadataGroupApi,
            configuration
        )
    }

    @Test
    fun getTrueUserId_userNull_returnNull() {
        setUpUserNull()
        assertEquals(null, musicUserRepository.getTrueUserId())
    }

    @Test
    fun getTrueUserId_userNotNull_loginsEmpty_returnNull() {
        setUpUser()
        assertEquals(null, musicUserRepository.getTrueUserId())
    }

    @Test
    fun getTrueUserId_userNotNull_loginsMatchType_returnValueInt() {
        whenever(sharedPreferences.get<User>(MusicUserRepositoryImpl.CURRENT_USER_KEY))
            .thenReturn(
                mockUser.copy(
                    logins = listOf(
                        Login(LoginType.DEVICE.value, "0"),
                        Login(LoginType.USERNAME.value, "1")
                    )
                )
            )
        initMusicUserRepository()

        assertEquals(1, musicUserRepository.getTrueUserId())
    }

    @Test
    fun getTrueUserId_userNotNull_loginsMatchType_valueEmpty_returnNull() {
        whenever(sharedPreferences.get<User>(MusicUserRepositoryImpl.CURRENT_USER_KEY))
            .thenReturn(
                mockUser.copy(
                    logins = listOf(
                        Login(LoginType.USERNAME.value, "")
                    )
                )
            )
        initMusicUserRepository()

        assertEquals(null, musicUserRepository.getTrueUserId())
    }

    @Test
    fun getTrueUserId_userNotNull_loginsNotMatchType_returnNull() {
        whenever(sharedPreferences.get<User>(MusicUserRepositoryImpl.CURRENT_USER_KEY))
            .thenReturn(
                mockUser.copy(
                    logins = listOf(
                        Login(LoginType.DEVICE.value, "0")
                    )
                )
            )
        initMusicUserRepository()

        assertEquals(null, musicUserRepository.getTrueUserId())
    }

    @Test
    fun getUserId_userNull_returnNull() {
        setUpUserNull()
        assertEquals(null, musicUserRepository.getUserId())
    }

    @Test
    fun getUserId_userNotNull_returnUserId() {
        setUpUser()
        assertEquals(1, musicUserRepository.getUserId())
    }

    @Test
    fun getByRefresh_userNull_returnError() {
        setUpUserNull()
        musicUserRepository.get(false)
            .test()
            .assertError(IllegalStateException::class.java)
    }

    @Test
    fun getByRefresh_userNotNull_refreshTrue_returnUser() {
        setUpUser()
        whenever(authenticatedUserApi.getUser(anyInt()))
            .thenReturn(Single.just(mockUser.copy(userId = 2)))
        musicUserRepository.get(true)
            .test()
            .assertNoErrors()
            .assertValue {
                it.userId == 2
            }
    }

    @Test
    fun getByRefresh_userNotNull_refreshFalse_returnUser() {
        setUpUser()
        musicUserRepository.get(false)
            .test()
            .assertNoErrors()
            .assertValue {
                it == mockUser
            }
    }

    @Test
    fun getByUserId_returnUser() {
        setUpUser()
        whenever(authenticatedUserApi.getUser(anyInt()))
            .thenReturn(Single.just(mockUser.copy(userId = 2)))
        musicUserRepository.get(1)
            .test()
            .assertNoErrors()
            .assertValue {
                it.userId == 2
            }
    }

    @Test
    fun exist_userNull_returnFalse() {
        setUpUserNull()
        assertEquals(false, musicUserRepository.exist())
    }

    @Test
    fun exist_userNotNull_returnTrue() {
        setUpUser()
        assertEquals(true, musicUserRepository.exist())
    }

    @Test
    fun registerByType_userNotNull_msisdnNull_returnUser() {
        setUpUser()
        whenever(basicUserApi.registerByType(any())).thenReturn(Single.just(MockDataModel.mockUserTuned))
        whenever(configuration.applicationId).thenReturn(1)

        musicUserRepository.registerByType(
            Device(
                displayName = "displayName",
                uniqueId = "uniqueId",
                token = "token",
                type = "type",
                operatingSystem = "operatingSystem",
                operatingSystemVersion = "1",
                appVersion = "1",
                country = "th",
                language = "th",
                manufacturer = "manufacturer",
                timezoneOffset = 1,
                carrier = "carrier",
                brand = "brand",
                referrer = null
            ),
            LoginType.MSISDN,
            null
        )
            .test()
            .assertValue {
                it == mockUser
            }
    }

    @Test
    fun registerByType_userNotNull_msisdnNotNull_returnUser() {
        setUpUser()
        whenever(basicUserApi.registerByType(any())).thenReturn(Single.just(MockDataModel.mockUserTuned))
        whenever(configuration.applicationId).thenReturn(1)

        musicUserRepository.registerByType(
            Device(
                displayName = "displayName",
                uniqueId = "uniqueId",
                token = "token",
                type = "type",
                operatingSystem = "operatingSystem",
                operatingSystemVersion = "1",
                appVersion = "1",
                country = "th",
                language = "th",
                manufacturer = "manufacturer",
                timezoneOffset = 1,
                carrier = "carrier",
                brand = "brand",
                referrer = "referrer"
            ),
            LoginType.MSISDN,
            "msisdn"
        )
            .test()
            .assertValue {
                it == mockUser
            }
    }

    @Test
    fun registerByType_userNotNull_loginTypeDevice_returnUser() {
        setUpUser()
        whenever(basicUserApi.registerByType(any())).thenReturn(Single.just(MockDataModel.mockUserTuned))
        whenever(configuration.applicationId).thenReturn(1)

        musicUserRepository.registerByType(
            Device(
                displayName = "displayName",
                uniqueId = "uniqueId",
                token = "token",
                type = "type",
                operatingSystem = "operatingSystem",
                operatingSystemVersion = "1",
                appVersion = "1",
                country = "th",
                language = "th",
                manufacturer = "manufacturer",
                timezoneOffset = 1,
                carrier = "carrier",
                brand = "brand",
                referrer = null
            ),
            LoginType.DEVICE,
            ""
        )
            .test()
            .assertValue {
                it == mockUser
            }
    }

    @Test
    fun addLogin_returnBoolean() {
        setUpUser()
        whenever(authenticatedUserApi.addLogin(any())).thenReturn(Single.just(true))

        musicUserRepository.addLogin("userbane", "password", LoginType.USERNAME)
            .test()
            .assertValue {
                it
            }
        verify(authenticatedUserApi, times(1)).addLogin(any())
    }

    @Test
    fun addDevice_returnBoolean() {
        setUpUser()
        whenever(authenticatedUserApi.authDevice(any())).thenReturn(
            Single.just(
                WrappedValue(
                    true
                )
            )
        )

        musicUserRepository.addDevice(
            Device(
                displayName = "displayName",
                uniqueId = "uniqueId",
                token = "token",
                type = "type",
                operatingSystem = "operatingSystem",
                operatingSystemVersion = "1",
                appVersion = "1",
                country = "th",
                language = "th",
                manufacturer = "manufacturer",
                timezoneOffset = 1,
                carrier = "carrier",
                brand = "brand",
                referrer = null
            )
        )
            .test()
            .assertValue {
                it
            }
        verify(authenticatedUserApi, times(1)).authDevice(any())
    }

    @Test
    fun getSettings_currentUserSettingNull_returnNull() {
        setUpUser()
        whenever(sharedPreferences.get<Settings>(CURRENT_USER_SETTINGS_KEY)).thenReturn(null)

        assertEquals(null, musicUserRepository.getSettings())
        verify(sharedPreferences, times(1)).get<Settings>(CURRENT_USER_SETTINGS_KEY)
    }

    @Test
    fun getSettings_currentUserSettingNotNull_returnSetting() {
        setUpUser()
        whenever(sharedPreferences.get<Settings>(CURRENT_USER_SETTINGS_KEY)).thenReturn(
            MockDataModel.mockSetting
        )

        assertEquals(MockDataModel.mockSetting, musicUserRepository.getSettings())
        verify(sharedPreferences, times(1)).get<Settings>(CURRENT_USER_SETTINGS_KEY)
    }

    @Test
    fun syncLanguage_userNull_returnError() {
        setUpUserNull()

        musicUserRepository.syncLanguage("en")
            .test()
            .assertError(IllegalStateException::class.java)
    }

    @Test
    fun syncLanguage_userNotNull_equalsLanguage_returnUser() {
        setUpUser()

        musicUserRepository.syncLanguage("th")
            .test()
            .assertNoErrors()
            .assertValue {
                it == mockUser
            }
    }

    @Test
    fun syncLanguage_userNotNull_notEqualsLanguage_returnUser() {
        setUpUser()
        whenever(authenticatedUserApi.updateMyUserDetails(any())).thenReturn(
            Single.just(
                WrappedValue(false)
            )
        )
        musicUserRepository.syncLanguage("en")
            .test()
            .assertNoErrors()
            .assertValue {
                it == mockUser
            }
    }

    @Test
    fun refreshSettings_userNull_returnError() {
        setUpUserNull()

        musicUserRepository.refreshSettings()
            .test()
            .assertError(IllegalStateException::class.java)
    }

    @Test
    fun refreshSettings_userNotNull_returnSetting() {
        setUpUser()
        whenever(configuration.applicationId).thenReturn(1)
        whenever(authenticatedUserApi.getSettings(anyInt(), anyInt())).thenReturn(
            Single.just(
                MockDataModel.mockSetting
            )
        )

        musicUserRepository.refreshSettings()
            .test()
            .assertValue {
                it.allowAlbumNavigation && it.offlineMaximumDuration == oneDayMillisecond
            }
        verify(sharedPreferences, times(1)).put(
            CURRENT_USER_SETTINGS_KEY,
            MockDataModel.mockSetting
        )
    }

    @Test
    fun update_userNull_returnError() {
        setUpUserNull()

        musicUserRepository.update()
            .test()
            .assertError(IllegalStateException::class.java)
    }

    @Test
    fun update_userNotNull_returnUser() {
        setUpUser()
        whenever(authenticatedUserApi.updateMyUserDetails(any())).thenReturn(
            Single.just(
                WrappedValue(true)
            )
        )
        musicUserRepository.update(
            userModel = MockDataModel.mockUserModel,
            tags = listOf("tags1", "tags2"),
            audioQuality = "audioQuality",
            language = "th"
        ).test().assertValue {
            it == mockUser
        }
        verify(sharedPreferences, times(1)).put(
            MusicUserRepositoryImpl.CURRENT_USER_KEY, mockUser
        )
    }

    @Test
    fun update_userNotNull_ageEmpty_returnUser() {
        setUpUser()
        whenever(authenticatedUserApi.updateMyUserDetails(any())).thenReturn(
            Single.just(
                WrappedValue(true)
            )
        )

        val mockUserValue = MockDataModel.mockUserModel.copy(age = "")
        musicUserRepository.update(
            userModel = mockUserValue,
            tags = listOf("tags1", "tags2"),
            audioQuality = "audioQuality",
            language = "th"
        )
            .test()
            .assertValue {
                it == mockUser
            }
        verify(sharedPreferences, times(1)).put(
            MusicUserRepositoryImpl.CURRENT_USER_KEY, mockUser
        )
    }

    @Test
    fun logout_userNull_notVerifyRealmBackup() {
        setUpUserNull()

        musicUserRepository.logout()

        verify(sharedPreferences, times(1)).remove(CURRENT_USER_SETTINGS_KEY)
        verify(sharedPreferences, times(1)).remove(MusicUserRepositoryImpl.MSISDN_KEY)
    }

    @Test
    fun logout_userNotNull_verifyRealmBackup() {
        setUpUser()

        musicUserRepository.logout()

        verify(sharedPreferences, times(1)).remove(CURRENT_USER_SETTINGS_KEY)
        verify(sharedPreferences, times(1)).remove(MusicUserRepositoryImpl.MSISDN_KEY)
    }

    @Test
    fun getAction_returnAction() {
        setUpUserNull()
        val mockAction = "action"
        whenever(authenticatedUserApi.redeemPromoCode(any())).thenReturn(
            Single.just(
                PromoCode(mockAction)
            )
        )

        musicUserRepository.getAction("123")
            .test()
            .assertNoErrors()
            .assertValue {
                it == mockAction
            }
    }

    @Test
    fun updateContentLanguages_verifyUpdateContentLanguagesApi() {
        setUpUserNull()
        whenever(authenticatedUserApi.updateContentLanguages(any())).thenReturn(
            Single.just(Any())
        )

        musicUserRepository.updateContentLanguages(listOf("th", "en"))
            .test()
            .assertNoErrors()
        verify(authenticatedUserApi, times(1)).updateContentLanguages(any())
    }

    @Test
    fun getContentLanguages_lastUpdateLessThanOneDay_notContains_apiSuccess_returnValue() {
        setUpUserNull()
        whenever(
            sharedPreferences.get(
                MusicUserRepositoryImpl.CONTENT_LANGUAGES_LAST_UPDATE_KEY,
                0L
            )
        ).thenReturn(System.currentTimeMillis() - oneDayMillisecond + 10000000L)
        whenever(sharedPreferences.contains(MusicUserRepositoryImpl.CONTENT_LANGUAGES_KEY))
            .thenReturn(false)
        whenever(metadataGroupApi.getContentLanguages()).thenReturn(Single.just(listOf()))

        musicUserRepository.getContentLanguages()
            .test()
            .assertNoErrors()
            .assertValue {
                it.isEmpty()
            }
        verify(sharedPreferences, times(1)).put(
            MusicUserRepositoryImpl.CONTENT_LANGUAGES_KEY,
            listOf<ContentLanguage>()
        )
    }

    @Test
    fun getContentLanguages_lastUpdateMoreThanOneDay_contains_apiRuntimeException_returnThrow() {
        setUpUserNull()
        whenever(
            sharedPreferences.get(
                MusicUserRepositoryImpl.CONTENT_LANGUAGES_LAST_UPDATE_KEY,
                0L
            )
        ).thenReturn(System.currentTimeMillis() - oneDayMillisecond - 10000000L)
        whenever(sharedPreferences.contains(MusicUserRepositoryImpl.CONTENT_LANGUAGES_KEY))
            .thenReturn(true)
        whenever(metadataGroupApi.getContentLanguages()).thenReturn(
            Single.error(RuntimeException())
        )

        musicUserRepository.getContentLanguages()
            .test()
            .assertNotComplete()

        verify(sharedPreferences, times(0)).put(
            MusicUserRepositoryImpl.CONTENT_LANGUAGES_KEY,
            listOf<ContentLanguage>()
        )
    }

    @Test
    fun getContentLanguages_lastUpdateMoreThanOneDay_contains_apiCodeForBidden_returnThrow() {
        setUpUserNull()
        whenever(
            sharedPreferences.get(
                MusicUserRepositoryImpl.CONTENT_LANGUAGES_LAST_UPDATE_KEY,
                0L
            )
        ).thenReturn(System.currentTimeMillis() - oneDayMillisecond - 10000000L)
        whenever(sharedPreferences.contains(MusicUserRepositoryImpl.CONTENT_LANGUAGES_KEY))
            .thenReturn(true)
        whenever(metadataGroupApi.getContentLanguages()).thenReturn(
            Single.error(
                HttpException(
                    Response.error<Any>(
                        NetworkModule.HTTP_CODE_FORBIDDEN,
                        "".toResponseBody("application/json".toMediaTypeOrNull())
                    )
                )
            )
        )

        musicUserRepository.getContentLanguages()
            .test()
            .assertNotComplete()

        verify(sharedPreferences, times(0)).put(
            MusicUserRepositoryImpl.CONTENT_LANGUAGES_KEY,
            listOf<ContentLanguage>()
        )
    }

    @Test
    fun getContentLanguages_lastUpdateMoreThanOneDay_contains_apiCodeNotFound_returnEmptyList() {
        setUpUserNull()
        whenever(
            sharedPreferences.get(
                MusicUserRepositoryImpl.CONTENT_LANGUAGES_LAST_UPDATE_KEY,
                0L
            )
        ).thenReturn(System.currentTimeMillis() - oneDayMillisecond - 10000000L)
        whenever(sharedPreferences.contains(MusicUserRepositoryImpl.CONTENT_LANGUAGES_KEY))
            .thenReturn(true)
        whenever(metadataGroupApi.getContentLanguages()).thenReturn(
            Single.error(
                HttpException(
                    Response.error<Any>(
                        NetworkModule.HTTP_CODE_RESOURCE_NOT_FOUND,
                        "".toResponseBody("application/json".toMediaTypeOrNull())
                    )
                )
            )
        )

        musicUserRepository.getContentLanguages()
            .test()
            .assertNoErrors()
            .assertValue {
                it.isEmpty()
            }

        verify(sharedPreferences, times(1)).put(
            MusicUserRepositoryImpl.CONTENT_LANGUAGES_KEY,
            listOf<ContentLanguage>()
        )
    }

    @Test
    fun getSuggestedProfiles_verifyGetSuggestedUsers() {
        setUpUser()

        musicUserRepository.getSuggestedProfiles()

        verify(authenticatedUserApi, times(1)).getSuggestedUsers()
    }

    @Test
    fun getFollowedProfiles_verifyGetFollowedUsers() {
        setUpUser()

        musicUserRepository.getFollowedProfiles()

        verify(authenticatedUserApi, times(1)).getFollowedUsers()
    }
}
