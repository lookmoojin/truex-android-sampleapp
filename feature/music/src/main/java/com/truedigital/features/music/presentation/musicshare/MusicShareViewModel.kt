package com.truedigital.features.music.presentation.musicshare

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.truedigital.common.share.data.coredata.deeplink.constants.DeeplinkConstants
import com.truedigital.common.share.data.coredata.deeplink.usecase.GenerateDeeplinkFormatUseCase
import com.truedigital.common.share.nativeshare.domain.model.onelink.CreateOneLinkModel
import com.truedigital.common.share.nativeshare.utils.OneLinkCallback
import com.truedigital.common.share.nativeshare.utils.OneLinkGenerator
import com.truedigital.foundation.extension.SingleLiveEvent
import javax.inject.Inject

class MusicShareViewModel @Inject constructor(
    private val generateDeeplinkFormatUseCase: GenerateDeeplinkFormatUseCase,
    private val oneLinkGenerator: OneLinkGenerator
) : ViewModel() {

    private val openNativeShare = SingleLiveEvent<String>()
    fun onOpenNativeShare(): LiveData<String> = openNativeShare

    fun shareAlbum(albumId: String) {
        generateDeeplink(albumId, DeeplinkConstants.DeeplinkContentType.MUSIC_ALBUM)
    }

    fun sharePlaylist(playlistId: String) {
        generateDeeplink(playlistId, DeeplinkConstants.DeeplinkContentType.MUSIC_PLAYLIST)
    }

    fun shareSong(trackId: String) {
        generateDeeplink(trackId, DeeplinkConstants.DeeplinkContentType.MUSIC_SONG)
    }

    private fun generateDeeplink(
        id: String,
        deeplinkType: DeeplinkConstants.DeeplinkContentType
    ) {
        val map = mapOf(
            DeeplinkConstants.DeeplinkKey.CMS_ID to id
        )
        generateOneLink(generateDeeplinkFormatUseCase.execute(deeplinkType, map))
    }

    private fun generateOneLink(shareUrl: String) {
        oneLinkGenerator.generateOneLink(
            createOneLinkModel = CreateOneLinkModel(
                deepLinkUrl = shareUrl,
                desktopUrl = shareUrl
            ),
            callback = object : OneLinkCallback {
                override fun onSuccess(oneLinkUrl: String) {
                    nativeShare(oneLinkUrl)
                }
            }
        )
    }

    private fun nativeShare(url: String) {
        openNativeShare.value = url
    }
}
