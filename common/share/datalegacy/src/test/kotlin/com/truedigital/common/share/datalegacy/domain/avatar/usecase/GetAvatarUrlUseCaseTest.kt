package com.truedigital.common.share.datalegacy.domain.avatar.usecase

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.common.share.datalegacy.data.repository.avatar.AvatarRepository
import com.truedigital.core.BuildConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetAvatarUrlUseCaseTest {

    private lateinit var getAvatarUrlUseCase: GetAvatarUrlUseCase
    private val avatarRepository: AvatarRepository = mock()

    @BeforeEach
    fun setUp() {
        getAvatarUrlUseCase = GetAvatarUrlUseCaseImpl(avatarRepository)
    }

    @Test
    fun `when ssoId is number should return avatar url`() {
        val mockedUrl = "${BuildConfig.BASE_URL_URL_PROFILE}p80x80/345/12345.png"
        doReturn(mockedUrl).whenever(avatarRepository).getAvatarUrl(any(), any())

        val avatarUrl = getAvatarUrlUseCase.execute("12345")

        verify(avatarRepository, times(1)).getAvatarUrl(any(), any())
        assertEquals(mockedUrl, avatarUrl)
    }

    @Test
    fun `when ssoId is null should return default avatar url`() {
        val mockedUrl = "${BuildConfig.BASE_URL_URL_PROFILE}p80x80/0/default.png"
        doReturn(mockedUrl).whenever(avatarRepository).getAvatarUrl(anyOrNull(), any())

        val avatarUrl = getAvatarUrlUseCase.execute(null)

        verify(avatarRepository, times(1)).getAvatarUrl(anyOrNull(), any())
        assertEquals(mockedUrl, avatarUrl)
    }

    @Test
    fun `when ssoId is empty should return default avatar url`() {
        val mockedUrl = "${BuildConfig.BASE_URL_URL_PROFILE}p80x80/0/default.png"
        doReturn(mockedUrl).whenever(avatarRepository).getAvatarUrl(any(), any())

        val avatarUrl = getAvatarUrlUseCase.execute("")

        verify(avatarRepository, times(1)).getAvatarUrl(any(), any())
        assertEquals(mockedUrl, avatarUrl)
    }

    @Test
    fun `when ssoId is not number should return default avatar url`() {
        val mockedUrl = "${BuildConfig.BASE_URL_URL_PROFILE}p80x80/0/default.png"
        doReturn(mockedUrl).whenever(avatarRepository).getAvatarUrl(any(), any())

        val avatarUrl = getAvatarUrlUseCase.execute("ABC")

        verify(avatarRepository, times(1)).getAvatarUrl(any(), any())
        assertEquals(mockedUrl, avatarUrl)
    }
}
