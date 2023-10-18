package com.truedigital.common.share.componentv3.widget.feedmenutab.data.model

import com.google.gson.annotations.SerializedName

data class CommunityTabConfigModel(
    @SerializedName("community_tab")
    var communityTab: CommunityTab? = null,
    @SerializedName("foryou_tab")
    var forYouTab: CommunityTab? = null,
    @SerializedName("myservices")
    var myServices: MyServiceTodayConfig? = null,
    @SerializedName("sticky_banner")
    val stickyBanner: StickyBannerTodayConfig? = null,
    @SerializedName("today_page")
    var todayPage: TodayPageConfig? = null
) {
    data class CommunityTab(
        @SerializedName("enable")
        var enable: CommunityTabEnableConfig? = null,
        @SerializedName("title")
        val title: CommunityTabTitle? = null
    )

    data class CommunityTabEnableConfig(
        @SerializedName("android")
        val isEnable: Boolean = false
    )

    data class CommunityTabTitle(
        @SerializedName("en")
        val en: String = "",
        @SerializedName("fil")
        val ph: String = "",
        @SerializedName("id")
        val id: String = "",
        @SerializedName("km")
        val kh: String = "",
        @SerializedName("my")
        val my: String = "",
        @SerializedName("th")
        val th: String = "",
        @SerializedName("vi")
        val vi: String = "",
    )

    data class MyServiceTodayConfig(
        @SerializedName("enable")
        var enable: CommunityTabEnableConfig? = null,
        @SerializedName("title")
        val title: CommunityTabTitle? = null,
        @SerializedName("period_day")
        val periodDay: String? = null,
        @SerializedName("hour_cache")
        val hourCache: String? = null
    )

    data class StickyBannerTodayConfig(
        @SerializedName("enable")
        var enable: CommunityTabEnableConfig? = null,
        @SerializedName("shelf_id")
        val shelfId: String? = null,
        @SerializedName("slug")
        val slug: String? = null
    )

    data class TodayPageConfig(
        @SerializedName("ads_preload")
        var adsPreLoad: AdsPreLoad? = null
    )

    data class AdsPreLoad(
        @SerializedName("enable")
        var enable: CommunityTabEnableConfig? = null
    )
}
