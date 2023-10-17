package com.truedigital.features.tuned.data.profile.repository

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.tuned.api.profile.ProfileMetadataApiInterface
import com.truedigital.features.tuned.api.profile.ProfileServicesApiInterface
import com.truedigital.features.tuned.data.profile.model.Profile
import com.truedigital.features.tuned.data.profile.model.response.ProfileContext
import io.reactivex.Single
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ProfileRepositoryTest {

    private val profileMetadataApiInterface: ProfileMetadataApiInterface = mock()
    private val profileServicesApiInterface: ProfileServicesApiInterface = mock()
    private lateinit var profileRepository: ProfileRepository

    private val mockProfile = Profile(
        id = 1,
        username = "username",
        name = "name",
        image = "image",
        backgroundImage = "backgroundImage",
        isVerified = true,
        contentKey = "contentKey",
        followerCount = 100
    )

    @BeforeEach
    fun setUp() {
        profileRepository = ProfileRepositoryImpl(
            profileMetadataApiInterface,
            profileServicesApiInterface
        )
    }

    @Test
    fun get_sendId_returnProfile() {
        whenever(profileMetadataApiInterface.getProfile(any())).thenReturn(Single.just(mockProfile))

        profileRepository.get(id = 1)
            .test()
            .assertNoErrors()
            .assertValue { profile ->
                profile.id == mockProfile.id &&
                    profile.username == mockProfile.username &&
                    profile.name == mockProfile.name &&
                    profile.image == mockProfile.image &&
                    profile.backgroundImage == mockProfile.backgroundImage &&
                    profile.isVerified == mockProfile.isVerified &&
                    profile.contentKey == mockProfile.contentKey &&
                    profile.followerCount == mockProfile.followerCount
            }

        verify(profileMetadataApiInterface, times(1)).getProfile(any())
    }

    @Test
    fun get_sendUsername_returnProfile() {
        whenever(profileMetadataApiInterface.getProfile(any())).thenReturn(Single.just(mockProfile))

        profileRepository.get(username = "username")
            .test()
            .assertNoErrors()
            .assertValue { profile ->
                profile.id == mockProfile.id &&
                    profile.username == mockProfile.username &&
                    profile.name == mockProfile.name &&
                    profile.image == mockProfile.image &&
                    profile.backgroundImage == mockProfile.backgroundImage &&
                    profile.isVerified == mockProfile.isVerified &&
                    profile.contentKey == mockProfile.contentKey &&
                    profile.followerCount == mockProfile.followerCount
            }

        verify(profileMetadataApiInterface, times(1)).getProfile(any())
    }

    @Test
    fun removeFollow_sendId_unfollowProfileCalledOneTime() {
        whenever(profileServicesApiInterface.unfollowProfile(any())).thenReturn(Single.just(Any()))

        profileRepository.removeFollow(id = 1)

        verify(profileServicesApiInterface, times(1)).unfollowProfile(any())
    }

    @Test
    fun addFollow_sendId_followProfileCalledOneTime() {
        whenever(profileServicesApiInterface.followProfile(any())).thenReturn(Single.just(Any()))

        profileRepository.addFollow(id = 1)

        verify(profileServicesApiInterface, times(1)).followProfile(any())
    }

    @Test
    fun isFollowing_sendId_returnStatusFollowing() {
        val profileContextMock = ProfileContext(isFollowing = true)
        whenever(profileServicesApiInterface.userContext(any())).thenReturn(
            Single.just(profileContextMock)
        )

        profileRepository.isFollowing(id = 1)
            .test()
            .assertNoErrors()
            .assertValue { isFollow ->
                isFollow
            }

        verify(profileServicesApiInterface, times(1)).userContext(any())
    }
}
