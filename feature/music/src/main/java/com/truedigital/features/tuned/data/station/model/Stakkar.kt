package com.truedigital.features.tuned.data.station.model

import com.google.gson.annotations.SerializedName
import com.truedigital.features.tuned.data.util.LocalisedString

data class Stakkar(
    @SerializedName("StakkarId") var id: Int,
    @SerializedName("PublisherImage") var publisherImage: String,
    @SerializedName("PublisherDisplayName") var publisherName: String,
    @SerializedName("Type") var type: MediaType,
    @SerializedName("Links") var links: List<LocalisedString>,
    @SerializedName("BannerURL") var bannerUrl: String?,
    @SerializedName("BannerImage") var bannerImage: List<LocalisedString>?,
    @SerializedName("IsStationIdentity") var hideDialog: Boolean
) {
    enum class MediaType {
        @SerializedName("Video")
        VIDEO,

        @SerializedName("Audio")
        AUDIO
    }
}
