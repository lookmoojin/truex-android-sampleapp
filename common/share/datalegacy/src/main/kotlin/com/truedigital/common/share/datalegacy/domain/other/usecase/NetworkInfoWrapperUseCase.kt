package com.truedigital.common.share.datalegacy.domain.other.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface NetworkInfoWrapperUseCase {
    fun execute(): Flow<List<String>>
}

class NetworkInfoWrapperUseCaseImpl @Inject constructor(
    private val networkInfoUseCase: NetworkInfoUseCase
) : NetworkInfoWrapperUseCase {
    override fun execute(): Flow<List<String>> {
        return flow {
            emit(networkInfoUseCase.getNetworkInfo())
        }
    }
}
