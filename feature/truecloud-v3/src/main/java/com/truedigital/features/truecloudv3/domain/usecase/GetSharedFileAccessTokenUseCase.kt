package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.data.model.GetSharedObjectAccessTokenRequestModel
import com.truedigital.features.truecloudv3.data.repository.GetSharedFileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface GetSharedFileAccessTokenUseCase {
    fun execute(encryptedSharedObjectId: String, password: String): Flow<String?>
}

class GetSharedFileAccessTokenUseCaseImpl @Inject constructor(
    private val getSharedFileRepository: GetSharedFileRepository
) : GetSharedFileAccessTokenUseCase {
    override fun execute(
        encryptedSharedObjectId: String,
        password: String
    ): Flow<String?> {
        val requestModel = GetSharedObjectAccessTokenRequestModel(
            password = password,
            encryptedSharedObjectId = encryptedSharedObjectId
        )
        return getSharedFileRepository.getSharedObjectAccessToken(requestModel).map { response ->
            response?.accessToken?.sharedObjectAccessToken
        }
    }
}
