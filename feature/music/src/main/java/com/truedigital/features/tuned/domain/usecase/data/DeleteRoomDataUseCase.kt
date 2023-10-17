package com.truedigital.features.tuned.domain.usecase.data

import com.truedigital.features.tuned.data.database.repository.MusicRoomRepository
import javax.inject.Inject

interface DeleteRoomDataUseCase {
    suspend fun execute()
}

class DeleteRoomDataUseCaseImpl @Inject constructor(
    private val musicRoomRepository: MusicRoomRepository
) : DeleteRoomDataUseCase {

    override suspend fun execute() {
        musicRoomRepository.deleteData()
    }
}
