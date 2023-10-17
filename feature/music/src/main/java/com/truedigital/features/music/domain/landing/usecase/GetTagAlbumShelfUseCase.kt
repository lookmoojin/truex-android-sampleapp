package com.truedigital.features.music.domain.landing.usecase

import com.truedigital.features.music.data.landing.repository.MusicLandingRepository
import com.truedigital.features.music.domain.landing.model.MusicForYouItemModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface GetTagAlbumShelfUseCase {
    fun execute(tag: String, limit: String): Flow<List<MusicForYouItemModel>>
}

class GetTagAlbumShelfUseCaseImpl @Inject constructor(
    private val musicLandingRepository: MusicLandingRepository
) : GetTagAlbumShelfUseCase {

    companion object {
        private const val OFFSET = 1
        private const val COUNT = 10
        const val ERROR_TAG_EMPTY = "Tag is empty."
    }

    override fun execute(tag: String, limit: String): Flow<List<MusicForYouItemModel>> {
        return if (tag.isNotEmpty()) {
            val count = limit.ifEmpty { COUNT }.toString()

            musicLandingRepository.getTagAlbumShelf(tag, OFFSET, count.toInt()).map { response ->
                response?.results?.map { result ->
                    MusicForYouItemModel.AlbumShelfItem(
                        albumId = result.albumId ?: 0,
                        releaseId = result.primaryRelease?.releaseId ?: 0,
                        coverImage = result.primaryRelease?.image.orEmpty(),
                        albumName = result.name,
                        artistName = result.artists?.firstOrNull()?.name.orEmpty(),
                    )
                } ?: emptyList()
            }
        } else {
            flow { error(ERROR_TAG_EMPTY) }
        }
    }
}
