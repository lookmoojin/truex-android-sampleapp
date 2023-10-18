package com.truedigital.common.share.datalegacy.data.repository.profile

import com.truedigital.common.share.datalegacy.data.api.graph.GraphApiInterface
import com.truedigital.common.share.datalegacy.data.base.Failure
import com.truedigital.common.share.datalegacy.data.base.ResultResponse
import com.truedigital.common.share.datalegacy.data.base.Success
import com.truedigital.common.share.datalegacy.data.repository.cmsfnshelf.model.Setting
import com.truedigital.common.share.datalegacy.data.repository.profile.model.request.UpdateSettingProfileRequest
import com.truedigital.common.share.datalegacy.data.repository.profile.model.response.ProfileData
import com.truedigital.common.share.datalegacy.exception.TrueIdHandleExceptionType
import com.truedigital.common.share.datalegacy.exception.TrueIdMessageExceptionHelper
import com.truedigital.common.share.datalegacy.exception.createExceptionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

/***************************************************************************************************
 *
 * After UserRepository's dataManager is removed
 * These all method below will be moved into UserRepository
 *
 **************************************************************************************************/
interface ProfileRepository {
    fun getProfile(): Flow<ResultResponse<ProfileData>>
    fun getProfileByUsername(
        username: String,
        fields: String?,
        countryCode: String?
    ): Flow<ResultResponse<ProfileData>>
    fun getNonCachedProfile(): Flow<ResultResponse<ProfileData>>
    fun getOtherProfile(
        ssoid: String,
        fields: String?,
        countryCode: String?
    ): Flow<ResultResponse<ProfileData>>
    fun updateProfileWatch(updateSettingProfileRequest: UpdateSettingProfileRequest): Flow<ResultResponse<Unit>>
    fun clearCache()
}

class ProfileRepositoryImpl @Inject constructor(
    private val graphApi: GraphApiInterface,
    private val userRepository: UserRepository
) : ProfileRepository {

    companion object {
        private const val FIELDS = "avatar,display_name,first_name,last_name,settings,segment"
        const val MESSAGE_GET_PROFILE_SETTINGS_FAILED = "failed to get profile setting"
        const val MESSAGE_UPDATE_PROFILE_SETTINGS_FAILED_BODY_IS_NULL =
            "failed to update profile setting cause response body is null"
        const val MESSAGE_UPDATE_PROFILE_SETTINGS_FAILED = "failed to update profile setting"
        const val MESSAGE_GET_PROFILE_BY_USERNAME_FAILED = "failed to get profile by username"
        const val MESSAGE_GET_PROFILE_BY_SSOID_FAILED = "failed to get profile by ssoid"
    }

    private var profileData: ProfileData? = null

    override fun getProfile() = flow {
        emit(
            withTimeout(3000) {
                profileData?.let { _profileData ->
                    Success(data = _profileData)
                } ?: run {
                    val responseProfile = graphApi.getProfile(
                        ssoId = userRepository.getSsoId(),
                        fields = FIELDS
                    )

                    responseProfile.run {
                        when {
                            isSuccessful -> {
                                body()?.data?.let {
                                    profileData = it
                                    Success(data = it)
                                } ?: run {
                                    Success(data = generateProfileDataDefault())
                                }
                            }

                            else -> {
                                Success(data = generateProfileDataDefault())
                            }
                        }
                    }
                }
            }
        )
    }

    override fun getNonCachedProfile() = flow {
        emit(
            withTimeout(3000) {
                run {
                    graphApi.getProfile(
                        ssoId = userRepository.getSsoId(),
                        fields = FIELDS
                    ).run {
                        when {
                            isSuccessful -> {
                                body()?.data?.let {
                                    profileData = it
                                    Success(data = it)
                                } ?: run {
                                    Success(data = generateProfileDataDefault())
                                }
                            }

                            else -> {
                                Success(data = generateProfileDataDefault())
                            }
                        }
                    }
                }
            }
        )
    }

    override fun getProfileByUsername(
        profileUserName: String,
        fields: String?,
        countryCode: String?
    ) = flow {
        emit(
            withTimeout(3000) {
                run {
                    graphApi.getProfileByUsername(
                        username = profileUserName,
                        fields = fields,
                        countryCode = countryCode
                    ).run {
                        when {
                            isSuccessful -> {
                                body()?.data?.let {
                                    Success(data = it)
                                } ?: run {
                                    Failure(
                                        TrueIdMessageExceptionHelper(
                                            errorCode = code(),
                                            errorType = createExceptionType(code = code()).toString(),
                                            errorMessages = MESSAGE_GET_PROFILE_BY_USERNAME_FAILED
                                        )
                                    )
                                }
                            }

                            else -> {
                                Failure(
                                    TrueIdMessageExceptionHelper(
                                        errorCode = code(),
                                        errorType = createExceptionType(code = code()).toString(),
                                        errorMessages = MESSAGE_GET_PROFILE_BY_USERNAME_FAILED
                                    )
                                )
                            }
                        }
                    }
                }
            }
        )
    }

    override fun getOtherProfile(ssoid: String, fields: String?, countryCode: String?) = flow {
        emit(
            withTimeout(3000) {
                run {
                    graphApi.getOtherProfile(
                        ssoId = ssoid,
                        fields = fields,
                        countryCode = countryCode
                    ).run {
                        when {
                            isSuccessful -> {
                                body()?.data?.let {
                                    Success(data = it)
                                } ?: run {
                                    Failure(
                                        TrueIdMessageExceptionHelper(
                                            errorCode = code(),
                                            errorType = createExceptionType(code = code()).toString(),
                                            errorMessages = MESSAGE_GET_PROFILE_BY_SSOID_FAILED
                                        )
                                    )
                                }
                            }

                            else -> {
                                Failure(
                                    TrueIdMessageExceptionHelper(
                                        errorCode = code(),
                                        errorType = createExceptionType(code = code()).toString(),
                                        errorMessages = MESSAGE_GET_PROFILE_BY_SSOID_FAILED
                                    )
                                )
                            }
                        }
                    }
                }
            }
        )
    }

    override fun updateProfileWatch(updateSettingProfileRequest: UpdateSettingProfileRequest): Flow<ResultResponse<Unit>> =
        flow {
            emit(
                graphApi.updateProfileWatch(
                    ssoId = userRepository.getSsoId(),
                    updateProfileSettingsRequest = updateSettingProfileRequest
                )
                    .run {
                        when {
                            body() == null -> {
                                Failure(
                                    TrueIdMessageExceptionHelper(
                                        errorCode = code(),
                                        errorType = TrueIdHandleExceptionType.NULL_BODY.toString(),
                                        errorMessages = MESSAGE_UPDATE_PROFILE_SETTINGS_FAILED_BODY_IS_NULL
                                    )
                                )
                            }

                            body()?.code != 10001 -> {
                                Failure(
                                    TrueIdMessageExceptionHelper(
                                        errorCode = code(),
                                        errorType = TrueIdHandleExceptionType.FAILED.toString(),
                                        errorMessages = MESSAGE_UPDATE_PROFILE_SETTINGS_FAILED
                                    )
                                )
                            }

                            isSuccessful -> {
                                body()?.let {
                                    Success(data = Unit)
                                } ?: run {
                                    Failure(
                                        TrueIdMessageExceptionHelper(
                                            errorCode = code(),
                                            errorType = TrueIdHandleExceptionType.FAILED.toString(),
                                            errorMessages = MESSAGE_UPDATE_PROFILE_SETTINGS_FAILED
                                        )
                                    )
                                }
                            }

                            else -> {
                                Failure(
                                    TrueIdMessageExceptionHelper(
                                        errorCode = code(),
                                        errorType = TrueIdHandleExceptionType.FAILED.toString(),
                                        errorMessages = MESSAGE_UPDATE_PROFILE_SETTINGS_FAILED
                                    )
                                )
                            }
                        }
                    }
            )
        }

    override fun clearCache() {
        profileData = null
    }

    private fun generateProfileDataDefault() = ProfileData().apply {
        ssoid = userRepository.getSsoId()
        avatar = userRepository.getSsoAvatar()
        displayName = userRepository.getSsoDisplayName()
        bio = userRepository.getBio()
        settings = Setting().apply {
            language = "-"
        }
    }
}
