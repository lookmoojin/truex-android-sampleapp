package com.truedigital.common.share.datalegacy.domain.avatar.usecase

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.common.share.datalegacy.data.repository.avatar.AvatarRepository
import com.truedigital.common.share.datalegacy.data.repository.avatar.AvatarSize
import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.core.BuildConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class GetAvatarUrlUserLastedUseCaseTest {

    private lateinit var getAvatarUrlFreshUseCase: GetAvatarUrlUserLastedUseCase
    private val getAvatarUrlUseCase: GetAvatarUrlUseCase = mock()
    private val avatarRepository: AvatarRepository = mock()
    private val userRepository: UserRepository = mock()

    @BeforeEach
    fun setUp() {
        getAvatarUrlFreshUseCase =
            GetAvatarUrlUserLastedUseCaseImpl(avatarRepository, userRepository, getAvatarUrlUseCase)
    }

    @Test
    fun `when ssoId is number and belongs to user should return ts avatar url`() {
        val ssoid = "30000000"
        val mockedUrl = "${BuildConfig.BASE_URL_URL_PROFILE}p80x80/0/30000000.png?ts="
        doReturn(mockedUrl).whenever(avatarRepository).getAvatarUrl(any(), any())
        doReturn(ssoid).whenever(userRepository).getSsoId()
        doReturn(mockedUrl).whenever(getAvatarUrlUseCase).execute(any(), any())

        val avatarUrl = getAvatarUrlFreshUseCase.execute("30000000")

        verify(avatarRepository, times(1)).getAvatarUrl(any(), any())
        assertTrue(avatarUrl.contains(mockedUrl))
    }

    @Test
    fun `when ssoId is number and belongs to others should return normal avatar url`() {
        val ssoid = "1234"
        val mockedUrl = "${BuildConfig.BASE_URL_URL_PROFILE}p80x80/0/30000000.png"
        doReturn(mockedUrl).whenever(avatarRepository).getAvatarUrl(any(), any())
        doReturn(ssoid).whenever(userRepository).getSsoId()
        doReturn(mockedUrl).whenever(getAvatarUrlUseCase).execute(any(), any())

        val avatarUrl = getAvatarUrlFreshUseCase.execute("30000000")

        verify(getAvatarUrlUseCase, times(1)).execute(any(), any())
        assertEquals(mockedUrl, avatarUrl)
    }

    @Test
    fun `when ssoId is null and belongs to others should return default avatar url`() {
        val ssoid = "1234"
        val mockedUrl = "${BuildConfig.BASE_URL_URL_PROFILE}p80x80/0/default.png"
        doReturn(mockedUrl).whenever(avatarRepository).getAvatarUrl(anyOrNull(), any())
        doReturn(ssoid).whenever(userRepository).getSsoId()
        doReturn(mockedUrl).whenever(getAvatarUrlUseCase).execute(anyOrNull(), any())

        val avatarUrl = getAvatarUrlFreshUseCase.execute(null)

        verify(getAvatarUrlUseCase, times(1)).execute(anyOrNull(), any())
        assertEquals(mockedUrl, avatarUrl)
    }

    @Test
    fun `when ssoId is empty and belongs to others should return default avatar url`() {
        val ssoid = "1234"
        val mockedUrl = "${BuildConfig.BASE_URL_URL_PROFILE}p80x80/0/default.png"
        doReturn(mockedUrl).whenever(avatarRepository).getAvatarUrl(any(), any())
        doReturn(ssoid).whenever(userRepository).getSsoId()
        doReturn(mockedUrl).whenever(getAvatarUrlUseCase).execute(any(), any())

        val avatarUrl = getAvatarUrlFreshUseCase.execute("")

        verify(getAvatarUrlUseCase, times(1)).execute(any(), any())
        assertEquals(mockedUrl, avatarUrl)
    }

    @Test
    fun `when ssoId is not number should return default avatar url`() {
        val ssoid = "1234"
        val mockedUrl = "${BuildConfig.BASE_URL_URL_PROFILE}p80x80/0/default.png"
        doReturn(mockedUrl).whenever(avatarRepository).getAvatarUrl(any(), any())
        doReturn(ssoid).whenever(userRepository).getSsoId()
        doReturn(mockedUrl).whenever(getAvatarUrlUseCase).execute(any(), any())

        val avatarUrl = getAvatarUrlFreshUseCase.execute("ABC")

        verify(getAvatarUrlUseCase, times(1)).execute(any(), any())
        assertEquals(mockedUrl, avatarUrl)
    }

    @Test
    fun `when ssoId is and size small should return avatar url small size`() {
        val ssoid = "1234"
        val mockedUrl = "${BuildConfig.BASE_URL_URL_PROFILE}p40x40/0/30000000.png"
        doReturn(mockedUrl).whenever(avatarRepository).getAvatarUrl(any(), any())
        doReturn(ssoid).whenever(userRepository).getSsoId()
        doReturn(mockedUrl).whenever(getAvatarUrlUseCase).execute(any(), any())

        val avatarUrl = getAvatarUrlFreshUseCase.execute("30000000", AvatarSize.SMALL)

        verify(getAvatarUrlUseCase, times(1)).execute(any(), any())
        assertEquals(mockedUrl, avatarUrl)
    }

    @Test
    fun `when ssoId is and size large should return avatar url large size`() {
        val ssoid = "1234"
        val mockedUrl = "${BuildConfig.BASE_URL_URL_PROFILE}p320x320/0/30000000.png"
        doReturn(mockedUrl).whenever(avatarRepository).getAvatarUrl(any(), any())
        doReturn(ssoid).whenever(userRepository).getSsoId()
        doReturn(mockedUrl).whenever(getAvatarUrlUseCase).execute(any(), any())

        val avatarUrl = getAvatarUrlFreshUseCase.execute("30000000", AvatarSize.LARGE)

        verify(getAvatarUrlUseCase, times(1)).execute(any(), any())
        assertEquals(mockedUrl, avatarUrl)
    }
}
