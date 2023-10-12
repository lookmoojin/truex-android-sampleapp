package com.truedigital.core.data

data class ShelfTypeModel(
    var id: String = "",
    val adsModel: ShelfAdsModel = ShelfAdsModel(),
    val category: String = "",
    val communityId: String = "",
    val contentType: String = "",
    val index: Int = 0,
    val isDarkTheme: Boolean = false,
    val isTabForYou: Boolean = false,
    val leagueCode: String = "",
    val limit: String = "",
    val logoTitle: String = "",
    val navigate: String = "",
    val productCode: String = "",
    val relativeDate: String? = null,
    val relateTeam: String? = null,
    val shelfCode: String = "",
    val shelfId: String = "",
    val shelfSlug: String = "",
    val shelfType: String = "",
    val title: String = "",
    val titleEn: String = "",
    val viewType: String = "",
    val matchId: String = "",
    val homeTeamId: String = "",
    val awayTeamId: String = "",
    val sortByTeamIds: List<String> = listOf()
)

data class ShelfAdsModel(
    val adsTagsUrl: String = "",
    val mobileSize: String = "",
    val tabletSize: String = ""
)
