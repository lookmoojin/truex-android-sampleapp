package com.truedigital.features.music.domain.image.usecase

import android.content.Context
import com.truedigital.common.share.nativeshare.utils.getImageFileFromUrl
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import com.truedigital.features.listens.share.constant.MusicConstant
import com.truedigital.features.music.data.playlist.repository.MusicPlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.Locale
import javax.inject.Inject

interface UploadCoverImageUseCase {
    suspend fun execute(playlistId: Int, imageUrl: String): Flow<Any>
}

class UploadCoverImageUseCaseImpl @Inject constructor(
    private val coroutineDispatcher: CoroutineDispatcherProvider,
    private val context: Context,
    private val musicPlaylistRepository: MusicPlaylistRepository
) : UploadCoverImageUseCase {

    companion object {
        private val mediaContentType = "image/*".toMediaTypeOrNull()
    }

    override suspend fun execute(playlistId: Int, imageUrl: String): Flow<Any> {
        var cacheFile: File? = null
        return withContext(coroutineDispatcher.io()) {
            flowOf(getImageFileFromUrl(context, imageUrl))
                .filterNotNull()
                .flatMapConcat { imageFile ->
                    cacheFile = imageFile
                    val fileName = imageFile.path
                    val requestBody = imageFile.asRequestBody(mediaContentType)
                    val multiPart =
                        MultipartBody.Part.createFormData("upload", fileName, requestBody)
                    musicPlaylistRepository.uploadCoverImage(
                        playlistId,
                        MusicConstant.Language.LANG_EN.lowercase(Locale.ENGLISH),
                        multiPart
                    )
                }
                .onCompletion {
                    deleteCacheFile(cacheFile)
                }
        }
    }

    private fun deleteCacheFile(cacheFile: File?): Boolean {
        return cacheFile?.delete() == true
    }
}
