package com.truedigital.features.tuned.data.user.model

import com.google.gson.annotations.SerializedName
import com.truedigital.features.tuned.data.ad.model.AdProvider

data class Settings(
    @SerializedName("AllowStreams") var allowStreams: Boolean,
    @SerializedName("LimitSkips") var limitSkips: Boolean,
    @SerializedName("AdFirstMinutes") var adFirstMinutes: Int,
    @SerializedName("AdIntervalMinutes") var adIntervalMinutes: Int,
    @SerializedName("AllowPurchase") var allowPurchase: Boolean,
    @SerializedName("AllowAlbumNavigation") var allowAlbumNavigation: Boolean,
    @SerializedName("AllowOffline") var allowOffline: Boolean,
    @SerializedName("AllowSync") var allowSync: Boolean,
    @SerializedName("SyncCutOffDays") var syncCutOffDays: Int,
    @SerializedName("MaxSkipsPerHour") var maxSkipsPerHour: Int,
    @SerializedName("LimitPlays") var limitPlays: Boolean,
    @SerializedName("MonthlyPlayLimit") var monthlyPlayLimit: Int,
    @SerializedName("AdProvider") var adProvider: AdProvider,
    @SerializedName("TracksPerAd") var tracksPerAd: Int,
    @SerializedName("InterstitialId") var interstitialId: String,
    @SerializedName("FacebookUrl") var facebookUrl: String?,
    @SerializedName("TwitterUrl") var twitterUrl: String?,
    @SerializedName("InstagramUrl") var instagramUrl: String?,
    @SerializedName("YoutubeUrl") var youtubeUrl: String?,
    @SerializedName("SupportEmail") var supportEmail: String,
    @SerializedName("DMCARules") var dmcaEnabled: Boolean,
    var offlineMaximumDuration: Long
)
