package com.truedigital.component.widget.livecommerce.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.truedigital.component.widget.livecommerce.domain.model.CommerceActiveLiveStreamModel
import com.truedigital.component.widget.livecommerce.domain.usecase.ConvertShelfDataToLiveStreamDataUseCase
import com.truedigital.component.widget.livecommerce.domain.usecase.CreateCommerceLiveDeeplinkUseCase
import com.truedigital.component.widget.livecommerce.domain.usecase.GetActiveLiveStreamUseCase
import com.truedigital.core.base.ScopedViewModel
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import com.truedigital.core.extensions.collectSafe
import com.truedigital.core.extensions.launchSafe
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import org.jetbrains.annotations.VisibleForTesting
import javax.inject.Inject

class LiveCommerceWidgetViewModel @Inject constructor(
    private val convertShelfDataToLiveStreamDataUseCase: ConvertShelfDataToLiveStreamDataUseCase,
    private val createCommerceLiveDeeplinkUseCase: CreateCommerceLiveDeeplinkUseCase,
    private val getActiveLiveStreamUseCase: GetActiveLiveStreamUseCase,
    private val coroutineDispatcher: CoroutineDispatcherProvider
) : ScopedViewModel() {
    val titleOfWidget =
        MutableLiveData<String>()

    val navigateLink =
        MutableLiveData<String>()

    private val _performDeepLink = MutableLiveData<String>()
    val performDeepLink: LiveData<String>
        get() = _performDeepLink

    val activeStreamDataList =
        MutableLiveData<List<CommerceActiveLiveStreamModel>>()

    fun initData(shelfItemData: String) {
        val pairOfTitleAndSsoIdsString =
            convertShelfDataToLiveStreamDataUseCase
                .execute(shelfItemData = shelfItemData)

        titleOfWidget.value = pairOfTitleAndSsoIdsString.first.orEmpty()
        navigateLink.value = pairOfTitleAndSsoIdsString.second.orEmpty()
        pairOfTitleAndSsoIdsString.third?.let { ssoIds ->
            loadActiveLiveStream(ssoIds = ssoIds)
        }
    }

    @VisibleForTesting
    fun loadActiveLiveStream(ssoIds: String) {
        launchSafe {
            getActiveLiveStreamUseCase.execute(ssoIds = ssoIds)
                .flowOn(coroutineDispatcher.io())
                .catch {
                    // Do Nothing
                }
                .collectSafe {
                    if (it.isNotEmpty())
                        activeStreamDataList.value = it
                }
        }
    }

    fun performLivestreamingDeeplink(model: CommerceActiveLiveStreamModel?) {
        model?.run {
            val deepLink = createCommerceLiveDeeplinkUseCase.execute(
                postId = postId,
                streamId = streamIds
            )
            _performDeepLink.value = deepLink
        }
    }
}
