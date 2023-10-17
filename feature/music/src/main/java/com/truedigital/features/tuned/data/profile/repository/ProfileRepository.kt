package com.truedigital.features.tuned.data.profile.repository

import com.truedigital.features.tuned.data.profile.model.Profile
import io.reactivex.Single

interface ProfileRepository {
    fun get(id: Int): Single<Profile>
    fun get(username: String): Single<Profile>
    fun removeFollow(id: Int): Single<Any>
    fun addFollow(id: Int): Single<Any>
    fun isFollowing(id: Int): Single<Boolean>
}
