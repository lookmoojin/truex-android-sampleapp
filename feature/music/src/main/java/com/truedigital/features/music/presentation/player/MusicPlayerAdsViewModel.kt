package com.truedigital.features.music.presentation.player

import android.support.v4.media.MediaMetadataCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.truedigital.core.base.ScopedViewModel
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import com.truedigital.core.extensions.collectSafe
import com.truedigital.core.extensions.launchSafe
import com.truedigital.features.music.domain.ads.usecase.ActionPreviousNextUseCase
import com.truedigital.features.music.domain.ads.usecase.ClearCacheMusicPlayerAdsUseCase
import com.truedigital.features.music.domain.ads.usecase.GetMusicPlayerAdsUrlUseCase
import com.truedigital.features.music.domain.ads.usecase.IsShowMusicPlayerAdsUseCase
import com.truedigital.foundation.extension.SingleLiveEvent
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MusicPlayerAdsViewModel @Inject constructor(
    private val coroutineDispatcher: CoroutineDispatcherProvider,
    private val actionPreviousNextUseCase: ActionPreviousNextUseCase,
    private val clearCacheMusicPlayerAdsUseCase: ClearCacheMusicPlayerAdsUseCase,
    private val getMusicPlayerAdsUrlUseCase: GetMusicPlayerAdsUrlUseCase,
    private val isShowMusicPlayerAdsUseCase: IsShowMusicPlayerAdsUseCase
) : ScopedViewModel() {

    private val showAds = SingleLiveEvent<String>()
    fun onShowAds(): LiveData<String> = showAds

    fun actionPrevious(isFirstTrack: Boolean, position: Long?) {
        if (!isFirstTrack) {
            actionPreviousNextUseCase.execute(true, position)
        }
    }

    fun actionNext(isLastTrack: Boolean) {
        if (!isLastTrack) {
            actionPreviousNextUseCase.execute(false)
        }
    }

    fun validateAds(metadata: MediaMetadataCompat?) {
        if (metadata == null) {
            clearCacheMusicPlayerAdsUseCase.execute()
        } else {
            if (isShowMusicPlayerAdsUseCase.execute()) {
                getMusicPlayerAdsUrl()
            }
        }
    }

    private fun getMusicPlayerAdsUrl() = viewModelScope.launchSafe {
        getMusicPlayerAdsUrlUseCase.execute()
            .flowOn(coroutineDispatcher.io())
            .filter {
                it.isNotBlank()
            }
            .collectSafe {
                showAds.value = it
            }
    }
}
