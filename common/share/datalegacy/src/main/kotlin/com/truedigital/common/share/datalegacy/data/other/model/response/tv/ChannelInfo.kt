package com.truedigital.common.share.datalegacy.data.other.model.response.tv

import com.google.gson.annotations.SerializedName

/***************************************************************************************************
 * Note: This class will replace by
 * [com.truedigital.features.multimedia.data.streamer.model.response.StreamerChannelInfo]
 **************************************************************************************************/

class ChannelInfo {

    @SerializedName("channel_name_eng")
    var channelNameEn: String? = null

    @SerializedName("channel_name_th")
    var channelNameTh: String? = null
}
