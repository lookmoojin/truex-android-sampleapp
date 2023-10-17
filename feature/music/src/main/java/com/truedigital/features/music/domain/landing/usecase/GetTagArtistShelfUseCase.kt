package com.truedigital.features.music.domain.landing.usecase

import com.truedigital.features.music.data.landing.repository.MusicLandingRepository
import com.truedigital.features.music.domain.landing.model.MusicForYouItemModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface GetTagArtistShelfUseCase {
    fun execute(tag: String, limit: String): Flow<List<MusicForYouItemModel>>
}

class GetTagArtistShelfUseCaseImpl @Inject constructor(
    private val musicLandingRepository: MusicLandingRepository
) : GetTagArtistShelfUseCase {
    companion object {
        private const val offset = 1
        private const val count = 10
        private const val ERROR_TAG_EMPTY = "tag for you shelf error is empty"
    }

    override fun execute(tag: String, limit: String): Flow<List<MusicForYouItemModel>> {
        return if (tag.isNotEmpty()) {
            val defaultLimit = if (limit.isEmpty()) {
                count
            } else {
                limit.toInt()
            }
            musicLandingRepository.getTagArtist(tag, offset, defaultLimit).map { response ->
                response?.results?.map { result ->
                    MusicForYouItemModel.ArtistShelfItem(
                        artistId = result?.artistId ?: 0,
                        coverImage = result?.image.orEmpty(),
                        name = result?.name.orEmpty()
                    )
                } ?: emptyList()
            }
        } else {
            flow { error(ERROR_TAG_EMPTY) }
        }
    }
}
