package com.truedigital.features.music.presentation.myplaylist

import androidx.lifecycle.LiveData
import com.truedigital.core.base.ScopedViewModel
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import com.truedigital.core.extensions.launchSafe
import com.truedigital.features.music.domain.image.usecase.GenerateGridImageUseCase
import com.truedigital.features.music.domain.image.usecase.UploadCoverImageUseCase
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.foundation.extension.SingleLiveEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.retry
import javax.inject.Inject

class MyPlaylistImageViewModel @Inject constructor(
    private val coroutineDispatcher: CoroutineDispatcherProvider,
    private val generateGridImageUseCase: GenerateGridImageUseCase,
    private val uploadCoverImageUseCase: UploadCoverImageUseCase
) : ScopedViewModel() {

    companion object {
        private const val MAX_RETRIES = 3L
        private const val DELAY_TIMES = 1000L
    }

    private val displayCoverImage = SingleLiveEvent<Pair<String, List<String>>>()
    private val saveCompleted = SingleLiveEvent<Unit>()

    fun onDisplayCoverImage(): LiveData<Pair<String, List<String>>> = displayCoverImage
    fun onSaveCompleted(): LiveData<Unit> = saveCompleted

    fun generateGridImage(trackList: List<Track>) {
        displayCoverImage.value = generateGridImageUseCase.execute(trackList)
    }

    fun saveCoverImage(playlistId: Int, imageUrl: String) = launchSafe {
        uploadCoverImageUseCase.execute(playlistId, imageUrl)
            .flowOn(coroutineDispatcher.io())
            .retry(retries = MAX_RETRIES) { error ->
                delay(DELAY_TIMES)
                error is Exception
            }
            .onCompletion {
                saveCompleted.value = Unit
            }
            .collect()
    }
}
