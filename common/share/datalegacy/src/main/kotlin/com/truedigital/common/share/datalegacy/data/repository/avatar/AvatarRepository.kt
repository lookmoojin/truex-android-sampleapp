package com.truedigital.common.share.datalegacy.data.repository.avatar

import com.truedigital.core.BuildConfig
import javax.inject.Inject

interface AvatarRepository {
    fun getAvatarUrl(ssoId: String?, size: AvatarSize = AvatarSize.MEDIUM): String
}

class AvatarRepositoryImpl @Inject constructor() : AvatarRepository {
    companion object {
        private const val AVATAR_HASH_MODULO = 2000
    }

    override fun getAvatarUrl(ssoId: String?, size: AvatarSize): String {
        return ssoId?.toIntOrNull()?.let { id ->
            generateBaseUrlAvatar(size).plus("${calculateAvatarHash(id)}/$id.png")
        } ?: run {
            generateBaseUrlAvatar(size).plus("default.png")
        }
    }

    private fun calculateAvatarHash(ssoId: Int): Int {
        return ssoId % AVATAR_HASH_MODULO
    }

    private fun generateBaseUrlAvatar(size: AvatarSize): String {
        return "${BuildConfig.BASE_URL_URL_PROFILE}${size.value}/"
    }
}
