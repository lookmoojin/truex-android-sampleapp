package com.truedigital.features.tuned.api.user

import com.truedigital.features.tuned.data.profile.model.Profile
import com.truedigital.features.tuned.data.user.model.PromoCode
import com.truedigital.features.tuned.data.user.model.Settings
import com.truedigital.features.tuned.data.user.model.User
import com.truedigital.features.tuned.data.user.model.request.AuthDevice
import com.truedigital.features.tuned.data.user.model.request.UserDetails
import com.truedigital.features.tuned.data.user.model.request.UsernamePasswordType
import com.truedigital.features.tuned.data.util.WrappedValue
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface AuthenticatedUserApiInterface {

    @GET("users/{id}/profile")
    fun getUser(@Path("id") userId: Int): Single<User>

    @POST("users/redeemvoucher")
    fun redeemPromoCode(@Query("code") code: String): Single<PromoCode>

    @POST("users/login")
    fun addLogin(@Body request: UsernamePasswordType): Single<Boolean>

    @PATCH("users/me/details")
    fun updateMyUserDetails(@Body request: UserDetails): Single<WrappedValue<Boolean>>

    @POST("users/me/device")
    fun authDevice(@Body request: AuthDevice): Single<WrappedValue<Boolean>>

    @GET("users/me/suggest/users")
    fun getSuggestedUsers(): Single<List<Profile>>

    @GET("users/me/following")
    fun getFollowedUsers(): Single<List<Profile>>

    @GET("users/{id}/settings")
    fun getSettings(
        @Path("id") userId: Int,
        @Query("applicationId") applicationId: Int
    ): Single<Settings>

    @PUT("users/updatecontentlanguages")
    fun updateContentLanguages(@Body languages: List<String>): Single<Any>
}
