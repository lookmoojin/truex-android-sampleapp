package com.truedigital.common.share.datalegacy.domain.avatar.usecase

import com.truedigital.common.share.datalegacy.data.repository.avatar.AvatarRepository
import com.truedigital.common.share.datalegacy.data.repository.avatar.AvatarSize
import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import java.util.UUID
import javax.inject.Inject

interface GetAvatarUrlUserLastedUseCase {
    fun execute(ssoId: String?, size: AvatarSize = AvatarSize.MEDIUM): String
}

class GetAvatarUrlUserLastedUseCaseImpl @Inject constructor(
    private val avatarRepository: AvatarRepository,
    private val userRepository: UserRepository,
    private val getAvatarUrlUseCase: GetAvatarUrlUseCase
) : GetAvatarUrlUserLastedUseCase {
    override fun execute(ssoId: String?, size: AvatarSize): String {
        return if (userRepository.getSsoId() == ssoId) {
            avatarRepository.getAvatarUrl(ssoId, size).plus("?ts=${UUID.randomUUID()}")
        } else {
            getAvatarUrlUseCase.execute(ssoId, size)
        }
    }
}
