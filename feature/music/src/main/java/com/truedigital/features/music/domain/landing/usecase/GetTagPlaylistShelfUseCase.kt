package com.truedigital.features.music.domain.landing.usecase

import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.features.music.data.landing.repository.MusicLandingRepository
import com.truedigital.features.music.domain.landing.model.MusicForYouItemModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface GetTagPlaylistShelfUseCase {
    fun execute(tag: String, limit: String): Flow<List<MusicForYouItemModel>>
}

class GetTagPlaylistShelfUseCaseImpl @Inject constructor(
    private val musicLandingRepository: MusicLandingRepository,
    private val localizationRepository: LocalizationRepository
) : GetTagPlaylistShelfUseCase {

    companion object {
        private const val COUNT = 10
        private const val OFFSET = 1
        private const val TYPE = "all"
        private const val ERROR_TAG_EMPTY = "tag for you shelf error is empty"
    }

    override fun execute(tag: String, limit: String): Flow<List<MusicForYouItemModel>> {
        return if (tag.isNotEmpty()) {
            val defaultLimit = if (limit.isEmpty()) {
                COUNT
            } else {
                limit.toInt()
            }
            musicLandingRepository.getTagPlaylistShelf(tag, OFFSET, defaultLimit, TYPE)
                .map { response ->
                    response?.results?.map { result ->
                        MusicForYouItemModel.PlaylistShelfItem(
                            playlistId = result.playlistId ?: 0,
                            coverImage = result.coverImage?.find { coverImage ->
                                coverImage.language == localizationRepository.getAppLanguageCodeForEnTh()
                            }?.value.orEmpty(),
                            name = result.name?.find { name ->
                                name.language == localizationRepository.getAppLanguageCodeForEnTh()
                            }?.value ?: result.name?.first()?.value.orEmpty(),
                            nameEn = result.name?.find { name ->
                                name.language == LocalizationRepository.Localization.EN.languageCode
                            }?.value
                        )
                    } ?: emptyList()
                }
        } else {
            flow { error(ERROR_TAG_EMPTY) }
        }
    }
}
