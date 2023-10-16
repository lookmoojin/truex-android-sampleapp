package com.truedigital.component.widget.share

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.truedigital.common.share.nativeshare.domain.model.onelink.CreateOneLinkModel
import com.truedigital.common.share.nativeshare.utils.DynamicLinkGenerator
import com.truedigital.common.share.nativeshare.utils.OneLinkCallback
import com.truedigital.common.share.nativeshare.utils.OneLinkGenerator
import com.truedigital.common.share.nativeshare.utils.ShortenUrlCallback
import org.json.JSONObject
import javax.inject.Inject

class ShareWidgetViewModel @Inject constructor(
    private val dynamicLinkGenerator: DynamicLinkGenerator,
    private val oneLinkGenerator: OneLinkGenerator
) : ViewModel() {

    private var shareWidgetModel: ShareWidgetModel? = null

    private val shareSocial = MutableLiveData<String>()
    private val showShareErrorMessage = MutableLiveData<String>()

    fun onShareSocial(): LiveData<String> = shareSocial
    fun onShowShareErrorMessage(): LiveData<String> = showShareErrorMessage

    fun setShareWidgetModel(model: ShareWidgetModel) {
        shareWidgetModel = model
    }

    fun generateOneLink() {
        val deeplinkUrl = if (shareWidgetModel?.deeplinkUrl.isNullOrEmpty()) {
            shareWidgetModel?.shareUrl.orEmpty()
        } else {
            shareWidgetModel?.deeplinkUrl.orEmpty()
        }
        oneLinkGenerator.generateOneLink(
            createOneLinkModel = CreateOneLinkModel(
                deepLinkUrl = deeplinkUrl,
                desktopUrl = shareWidgetModel?.shareUrl.orEmpty()
            ),
            callback = object : OneLinkCallback {
                override fun onSuccess(oneLinkUrl: String) {
                    shareSocial.postValue(oneLinkUrl)
                }
            }
        )
    }

    fun generateDynamicLink(goToPlayStore: Boolean = false) {
        val info = JSONObject()
        info.put("api_url", shareWidgetModel?.apiUrl.orEmpty())
        info.put("content_id", shareWidgetModel?.id.orEmpty())

        dynamicLinkGenerator.generateDynamicLink(
            title = shareWidgetModel?.title.orEmpty(),
            description = shareWidgetModel?.detail.orEmpty(),
            imageUrl = shareWidgetModel?.thumbnail.orEmpty(),
            type = shareWidgetModel?.type.orEmpty(),
            slug = shareWidgetModel?.slug.orEmpty(),
            info = info,
            shareUrl = shareWidgetModel?.shareUrl.orEmpty(),
            goToPlayStore = goToPlayStore,
            callback = object : ShortenUrlCallback {
                override fun onLoading() {
                    // Do nothing
                }

                override fun onSuccess(shortUrl: String) {
                    shareSocial.value = shortUrl
                }

                override fun onFailure(errorMessage: String) {
                    showShareErrorMessage.value = errorMessage
                }
            }
        )
    }
}
