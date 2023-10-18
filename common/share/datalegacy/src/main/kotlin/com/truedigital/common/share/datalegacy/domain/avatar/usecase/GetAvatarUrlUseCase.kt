package com.truedigital.common.share.datalegacy.domain.avatar.usecase

import com.truedigital.common.share.datalegacy.data.repository.avatar.AvatarRepository
import com.truedigital.common.share.datalegacy.data.repository.avatar.AvatarSize
import javax.inject.Inject

interface GetAvatarUrlUseCase {
    fun execute(ssoId: String?, size: AvatarSize = AvatarSize.MEDIUM): String
}

class GetAvatarUrlUseCaseImpl @Inject constructor(
    private val avatarRepository: AvatarRepository
) : GetAvatarUrlUseCase {
    override fun execute(ssoId: String?, size: AvatarSize): String {
        return avatarRepository.getAvatarUrl(ssoId, size)
    }
}
