package com.truedigital.common.share.data.coredata.deeplink.constants

sealed class DeepLinkResult

object DeepLinkContentNotFound : DeepLinkResult()
object DeepLinkFeatureOff : DeepLinkResult()
object DeepLinkDefaultError : DeepLinkResult()
object DeepLinkUnknown : DeepLinkResult()
object DeepLinkCustomDialog : DeepLinkResult()
class DeepLinkSuccess(val url: String, val tab: String) : DeepLinkResult()
class DeepLinkCommunity(val communityId: String, val postId: String) : DeepLinkResult()
