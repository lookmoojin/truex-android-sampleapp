package com.truedigital.features.music.domain.image.usecase

import com.truedigital.features.tuned.data.track.model.Track
import javax.inject.Inject

interface GenerateGridImageUseCase {
    fun execute(trackList: List<Track>): Pair<String, List<String>>
}

class GenerateGridImageUseCaseImpl @Inject constructor() : GenerateGridImageUseCase {

    companion object {
        const val NUMBER_IMAGES = 4
        const val URL_SEPARATOR = "%7C"
        private const val URL_PREFIX = "https://"
    }

    override fun execute(trackList: List<Track>): Pair<String, List<String>> {
        if (trackList.isNotEmpty()) {
            var filterString = "distributed_collage(grid,"
            val filterStringSuffix = ")"
            val imageUrlList = trackList.map { it.image }.take(NUMBER_IMAGES)
            val defaultUrl = imageUrlList.first()
            imageUrlList.filter { it.isNotEmpty() }.takeIf { it.size == NUMBER_IMAGES }
                ?.also { list ->
                    list.forEachIndexed { index, imageUrl ->
                        if (index != 0) {
                            filterString += URL_SEPARATOR
                        }

                        filterString += imageUrl.removePrefix(URL_PREFIX)
                    }
                    filterString += filterStringSuffix

                    return Pair(defaultUrl, listOf(filterString))
                } ?: run {
                return Pair(defaultUrl, listOf())
            }
            return Pair(defaultUrl, listOf())
        } else {
            return Pair("", listOf())
        }
    }
}
