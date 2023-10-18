package com.truedigital.common.share.datalegacy.data.repository.avatar

import com.truedigital.core.BuildConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AvatarRepositoryTest {
    private lateinit var repository: AvatarRepository

    @BeforeEach
    fun setUp() {
        repository = AvatarRepositoryImpl()
    }

    @Test
    fun `when ssoId is number should return avatar url`() {
        val avatarUrl = repository.getAvatarUrl("30000000")

        assertEquals("${BuildConfig.BASE_URL_URL_PROFILE}p80x80/0/30000000.png", avatarUrl)
    }

    @Test
    fun `when ssoId is null should return default url`() {
        val avatarUrl = repository.getAvatarUrl(null)

        assertEquals("${BuildConfig.BASE_URL_URL_PROFILE}p80x80/default.png", avatarUrl)
    }

    @Test
    fun `when ssoId is empty should return default url`() {
        val avatarUrl = repository.getAvatarUrl("")

        assertEquals("${BuildConfig.BASE_URL_URL_PROFILE}p80x80/default.png", avatarUrl)
    }

    @Test
    fun `when ssoId is not number should return default url`() {
        val avatarUrl = repository.getAvatarUrl("A")

        assertEquals("${BuildConfig.BASE_URL_URL_PROFILE}p80x80/default.png", avatarUrl)
    }

    @Test
    fun `when ssoId is number and size small should return avatar url small size`() {
        val avatarUrl = repository.getAvatarUrl("30000000", AvatarSize.SMALL)

        assertEquals("${BuildConfig.BASE_URL_URL_PROFILE}p40x40/0/30000000.png", avatarUrl)
    }

    @Test
    fun `when ssoId is number and size large should return avatar url lage size`() {
        val avatarUrl = repository.getAvatarUrl("30000000", AvatarSize.LARGE)

        assertEquals("${BuildConfig.BASE_URL_URL_PROFILE}p320x320/0/30000000.png", avatarUrl)
    }
}
