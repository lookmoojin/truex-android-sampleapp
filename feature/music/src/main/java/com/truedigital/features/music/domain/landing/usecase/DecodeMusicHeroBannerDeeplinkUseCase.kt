package com.truedigital.features.music.domain.landing.usecase

import android.net.Uri
import com.truedigital.common.share.data.coredata.deeplink.constants.DeeplinkConstants
import com.truedigital.common.share.data.coredata.deeplink.usecase.IsInternalDeeplinkUseCase
import com.truedigital.features.listens.share.constant.MusicConstant
import com.truedigital.features.music.domain.landing.model.MusicHeroBannerDeeplinkType
import javax.inject.Inject

interface DecodeMusicHeroBannerDeeplinkUseCase {
    fun execute(url: String): Pair<MusicHeroBannerDeeplinkType, String>
}

class DecodeMusicHeroBannerDeeplinkUseCaseImpl @Inject constructor(
    private val isInternalDeeplinkUseCase: IsInternalDeeplinkUseCase
) : DecodeMusicHeroBannerDeeplinkUseCase {

    override fun execute(url: String): Pair<MusicHeroBannerDeeplinkType, String> {
        return when {
            isMusicHostOrTrueIDListenHost(url) -> {
                val uri = Uri.parse(url)
                return when {
                    isSupportLink(url, com.truedigital.features.listens.share.constant.MusicConstant.Type.ALBUM) -> {
                        Pair(MusicHeroBannerDeeplinkType.ALBUM, uri.lastPathSegment.orEmpty())
                    }
                    isSupportLink(url, com.truedigital.features.listens.share.constant.MusicConstant.Type.ARTIST) -> {
                        Pair(MusicHeroBannerDeeplinkType.ARTIST, uri.lastPathSegment.orEmpty())
                    }
                    isSupportLink(url, com.truedigital.features.listens.share.constant.MusicConstant.Type.PLAYLIST) -> {
                        Pair(MusicHeroBannerDeeplinkType.PLAYLIST, uri.lastPathSegment.orEmpty())
                    }
                    isSupportLink(url, com.truedigital.features.listens.share.constant.MusicConstant.Type.SONG) -> {
                        Pair(MusicHeroBannerDeeplinkType.SONG, uri.lastPathSegment.orEmpty())
                    }
                    isSupportLink(url, MusicConstant.Type.RADIO) -> {
                        val contentId = getContentId(
                            MusicConstant.Type.RADIO,
                            uri.lastPathSegment.orEmpty()
                        )
                        Pair(getRadioContentType(contentId), contentId)
                    }
                    else -> {
                        Pair(MusicHeroBannerDeeplinkType.EXTERNAL_BROWSER, "")
                    }
                }
            }
            isInternalDeeplinkUseCase.execute(url) -> {
                Pair(MusicHeroBannerDeeplinkType.INTERNAL_DEEPLINK, url)
            }
            else -> {
                Pair(MusicHeroBannerDeeplinkType.EXTERNAL_BROWSER, url)
            }
        }
    }

    private fun isMusicHostOrTrueIDListenHost(url: String): Boolean {
        val firstPathSegment = Uri.parse(url).pathSegments.firstOrNull().orEmpty()

        return (url.contains(com.truedigital.features.listens.share.constant.MusicConstant.Deeplink.DEEPLINK_HOST)) ||
            (
                url.contains(DeeplinkConstants.DeeplinkConstants.HOST_TRUE_ID_HTTP) &&
                    firstPathSegment == com.truedigital.features.listens.share.constant.MusicConstant.Slug.LISTEN
                )
    }

    private fun isSupportLink(url: String, path: String): Boolean {
        return url.contains(path, true)
    }

    private fun getRadioContentType(contentId: String): MusicHeroBannerDeeplinkType {
        return if (contentId.isNotEmpty()) {
            MusicHeroBannerDeeplinkType.RADIO
        } else {
            MusicHeroBannerDeeplinkType.SEE_MORE_RADIO
        }
    }

    private fun getContentId(contentType: String, lastPathSegment: String): String {
        return if (lastPathSegment != contentType) lastPathSegment else ""
    }
}
