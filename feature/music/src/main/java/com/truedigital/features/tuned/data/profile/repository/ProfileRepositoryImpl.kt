package com.truedigital.features.tuned.data.profile.repository

import com.truedigital.features.tuned.api.profile.ProfileMetadataApiInterface
import com.truedigital.features.tuned.api.profile.ProfileServicesApiInterface
import com.truedigital.features.tuned.data.profile.model.Profile
import io.reactivex.Single

class ProfileRepositoryImpl(
    private val profileMetadataApiInterface: ProfileMetadataApiInterface,
    private val profileServicesApiInterface: ProfileServicesApiInterface
) : ProfileRepository {

    override fun get(id: Int): Single<Profile> =
        profileMetadataApiInterface.getProfile(id.toString())

    override fun get(username: String): Single<Profile> =
        profileMetadataApiInterface.getProfile(username)

    override fun removeFollow(id: Int): Single<Any> =
        profileServicesApiInterface.unfollowProfile(id)

    override fun addFollow(id: Int): Single<Any> =
        profileServicesApiInterface.followProfile(id)

    override fun isFollowing(id: Int): Single<Boolean> =
        profileServicesApiInterface.userContext(id).map { it.isFollowing }
}
