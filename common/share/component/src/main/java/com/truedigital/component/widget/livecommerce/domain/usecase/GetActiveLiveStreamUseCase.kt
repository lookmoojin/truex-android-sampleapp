package com.truedigital.component.widget.livecommerce.domain.usecase

import androidx.annotation.VisibleForTesting
import com.truedigital.component.widget.livecommerce.data.repository.GetActiveLiveStreamRepository
import com.truedigital.component.widget.livecommerce.domain.model.CommerceActiveLiveStreamModel
import com.truedigital.core.BuildConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface GetActiveLiveStreamUseCase {
    fun execute(ssoIds: String): Flow<List<CommerceActiveLiveStreamModel>>
}

class GetActiveLiveStreamUseCaseImpl @Inject constructor(
    private val getActiveLiveStreamRepository: GetActiveLiveStreamRepository
) : GetActiveLiveStreamUseCase {
    private val prefixThumbnailPath = "https://api.sg.amity.co/api/v3/files/"
    private val suffixThumbnailPath = "/download?size=medium"
    private val liveStatus = "live"
    override fun execute(ssoIds: String): Flow<List<CommerceActiveLiveStreamModel>> {
        return getActiveLiveStreamRepository.execute(userIds = ssoIds)
            .map { liveStreamList ->
                // map to presentation model
                liveStreamList
                    .filter { data ->
                        data.commerceVideoStreamings.firstOrNull()?.isLive == true &&
                            data.commerceVideoStreamings?.firstOrNull()?.status == liveStatus
                    }
                    .map { liveStreamData ->
                        CommerceActiveLiveStreamModel(
                            streamIds = liveStreamData.commerceVideoStreamings.firstOrNull()?.streamId.orEmpty(),
                            postId = liveStreamData.posts.firstOrNull()?.postId.orEmpty(),
                            thumbnailField =
                            prefixThumbnailPath +
                                (
                                    liveStreamData.commerceVideoStreamings.firstOrNull()?.thumbnailFileId
                                        ?: ""
                                    ) +
                                suffixThumbnailPath,
                            displayName = liveStreamData.users.firstOrNull()?.displayName ?: "",
                            title = liveStreamData.commerceVideoStreamings.firstOrNull()?.title
                                ?: "",
                            description = liveStreamData.commerceVideoStreamings.firstOrNull()?.description
                                ?: "",
                            profileImageUrl = liveStreamData.commerceVideoStreamings.firstOrNull()?.userId?.let {
                                generateProfileImageUrl(
                                    it
                                )
                            } ?: run {
                                ""
                            }
                        )
                    }
            }
            .catch { error ->
                throw(error)
            }
    }

    @VisibleForTesting
    fun generateProfileImageUrl(userId: String): String {
        return "https://" + getEnvironment() +
            ".dmpcdn.com/" + "p40x40" + "/${getHashOfUserId(userId)}/" +
            userId + ".png"
    }

    @VisibleForTesting
    fun getEnvironment(): String {
        return if (BuildConfig.ENVIRONMENT_IS_DEV) {
            "stg-avatar"
        } else {
            "avatar"
        }
    }

    @VisibleForTesting
    fun getHashOfUserId(userId: String): String {
        return try {
            (userId.toInt() % 2000).toString()
        } catch (e: Exception) {
            ""
        }
    }
}
