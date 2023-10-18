package com.truedigital.common.share.nativeshare.domain.model.onelink
/**
* A example CreateOneLinkModel parameters for OneLinkGenerator parameters
* @param campaign  Campaign Name. (Ex. redeemTrueIDCalling)
* @param contentId ID of content or cmsId. (Ex. https://movie.trueid.net/series/{master_id}/{id} , https://movie.trueid.net/series/OXzQr5GWJQ1o/{83e6jzLO5gbl})
* @param title Title of content in English. (Ex. communicator, Games Center etc.)
* @param contentType Content Type. (Ex. For movie contentType = movie, series contentType = series)
* @param masterId Id of master content. (Ex. For movie https://movie.trueid.net/series/{master_id}/{id} , https://movie.trueid.net/series/{OXzQr5GWJQ1o}/83e6jzLO5gbl)
* @param deepLinkUrl Deeplink for generate onelink. (Ex. trueid://home.trueid.net/inapp-browser?website=https://www.trueid.net , not null
* @param desktopUrl Desktop URL for generate onelink, When user open onelink on desktop. (Ex. https://www.trueid.net , not null
*/
data class CreateOneLinkModel(
    var campaign: String? = null,
    var channel: String? = null,
    var contentId: String? = null,
    var contentType: String? = null,
    var deepLinkUrl: String = "",
    var desktopUrl: String? = null,
    var masterId: String? = null,
    var title: String? = null
)
