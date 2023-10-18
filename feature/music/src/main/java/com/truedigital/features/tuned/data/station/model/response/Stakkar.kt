package com.truedigital.features.tuned.data.station.model.response

import com.google.gson.annotations.SerializedName
import com.truedigital.features.tuned.data.station.model.Stakkar
import com.truedigital.features.tuned.data.util.LocalisedString

data class Stakkar(
    @SerializedName("StakkarId") var id: Int,
    @SerializedName("Publisher") var publisher: Publisher,
    @SerializedName("Type") var type: Stakkar.MediaType,
    @SerializedName("Links") var links: List<LocalisedString>,
    @SerializedName("BannerURL") var bannerUrl: String?,
    @SerializedName("BannerImage") var bannerImage: List<LocalisedString>?,
    @SerializedName("IsStationIdentity") var hideDialog: Boolean
) {
    data class Publisher(@SerializedName("UserProfile") var profile: Profile) {
        data class Profile(
            @SerializedName("DisplayName") var name: String,
            @SerializedName("Image") var image: String
        )
    }
}
