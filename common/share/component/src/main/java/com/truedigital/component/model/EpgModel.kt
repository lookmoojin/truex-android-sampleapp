package com.truedigital.component.model

import com.truedigital.common.share.datalegacy.domain.multimedia.model.ContentInfo
import kotlinx.parcelize.Parcelize

@Parcelize
class EpgModel(
    var originalId: String? = null,
    var status: String? = null,
    var titleId: String? = null,
    var episodeId: String? = null,
    var episodeName: String? = null,
    var movieType: String? = null,
    var firstRun: String? = null,
    var castType: String? = null,
    var language: String? = null,
    var thumb: String? = null,
    var thumbnailUrl: String? = null,
    var thumbLargeUrl: String? = null,
    var channelName: String? = null,
    var channelId: String? = null,
    var adsEpgUrl: String? = null,
    var isCheckLineFooter: Boolean = false,
    var isCatchUp: Boolean = false,
    var duration: Long? = null,
    var catchUpStartDate: String = "",
    var catchUpStartTime: String = "",
    var total: Int? = null,
    var digitalNo: String = "",
    var isBlackOut: Boolean = false,
    var titleBlackOutButton: String = "",
    override var title: String = "",
    override var startDate: String = "",
    override var endDate: String = "",
    override var channelCmsId: String = ""
) : ContentInfo()
