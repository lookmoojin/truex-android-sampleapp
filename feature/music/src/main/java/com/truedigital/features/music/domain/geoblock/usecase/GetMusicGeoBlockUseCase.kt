package com.truedigital.features.music.domain.geoblock.usecase

import com.truedigital.common.share.datalegacy.data.base.Success
import com.truedigital.core.extensions.collectSafe
import com.truedigital.features.music.data.geoblock.repository.CacheMusicGeoBlockRepository
import com.truedigital.share.data.geoinformation.domain.usecase.GetGeoInformationByClientUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

interface GetMusicGeoBlockUseCase {
    fun execute(): Flow<Boolean>
}

class GetMusicGeoBlockUseCaseImpl @Inject constructor(
    private val cacheMusicGeoBlockRepository: CacheMusicGeoBlockRepository,
    private val getGeoInformationByClientUseCase: GetGeoInformationByClientUseCase
) : GetMusicGeoBlockUseCase {

    override fun execute(): Flow<Boolean> {
        cacheMusicGeoBlockRepository.loadCache()?.let { cacheData ->
            return flowOf(cacheData)
        } ?: run {
            return loadGeoBlock()
        }
    }

    private fun loadGeoBlock() = flow {
        getGeoInformationByClientUseCase.execute()
            .collectSafe { result ->
                when (result) {
                    is Success -> {
                        val isGeoBlock = result.data.countryCode?.equals("th", true) == false
                        cacheMusicGeoBlockRepository.saveCache(isGeoBlock)
                        emit(isGeoBlock)
                    }
                    else -> {
                        cacheMusicGeoBlockRepository.saveCache(false)
                        emit(false)
                    }
                }
            }
    }
}
