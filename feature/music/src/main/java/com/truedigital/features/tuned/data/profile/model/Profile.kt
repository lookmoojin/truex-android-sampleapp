package com.truedigital.features.tuned.data.profile.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.truedigital.features.tuned.data.product.model.Product
import kotlinx.parcelize.Parcelize
import java.util.Objects
import kotlin.math.floor

@Parcelize
data class Profile(
    @SerializedName("UserId") override var id: Int,
    @SerializedName("UserName") var username: String?,
    @SerializedName("DisplayName") var name: String,
    @SerializedName("Image") var image: String?,
    @SerializedName("BackgroundImage") var backgroundImage: String?,
    @SerializedName("IsVerified") var isVerified: Boolean,
    @SerializedName("ContentKey") var contentKey: String?,
    @SerializedName("FollowerCount") var followerCount: Int = 0
) : Parcelable, Product {

    companion object {
        private const val FOLLOWER_1000 = 1000
        private const val FOLLOWER_500 = 500
        private const val FOLLOWER_100 = 100
        private const val FLOAT_100 = 100F
        private const val INT_100 = 100
    }

    override fun equals(other: Any?): Boolean = other is Profile && other.id == id

    override fun hashCode(): Int {
        return Objects.hash(id)
    }

    fun getFollowerCountString() =
        when {
            followerCount >= FOLLOWER_1000 -> "1000+"
            followerCount >= FOLLOWER_500 -> "500+"
            followerCount < FOLLOWER_100 -> "<100"
            else -> "${(floor(followerCount / FLOAT_100) * INT_100).toInt()}+"
        }
}
