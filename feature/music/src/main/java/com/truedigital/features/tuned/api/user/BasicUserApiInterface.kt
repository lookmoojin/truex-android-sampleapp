package com.truedigital.features.tuned.api.user

import com.truedigital.features.tuned.data.user.model.User
import com.truedigital.features.tuned.data.user.model.request.RegisterByType
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.PUT

interface BasicUserApiInterface {
    @PUT("users/registerbydevice")
    fun registerByType(@Body request: RegisterByType): Single<User>
}
