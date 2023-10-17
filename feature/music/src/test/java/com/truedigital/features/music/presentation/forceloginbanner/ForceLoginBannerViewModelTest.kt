package com.truedigital.features.music.presentation.forceloginbanner

import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.common.share.datalegacy.login.LoginManagerInterface
import com.truedigital.common.share.datalegacy.wrapper.AuthLoginListener
import com.truedigital.core.coroutines.TestCoroutineDispatcherProvider
import com.truedigital.features.music.domain.forceloginbanner.usecase.GetLoginBannerUseCase
import com.truedigital.features.music.domain.geoblock.usecase.GetMusicGeoBlockUseCase
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.coroutines.TestCoroutinesExtension
import com.truedigital.share.mock.livedata.getOrAwaitValue
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExtendWith(InstantTaskExecutorExtension::class)
class ForceLoginBannerViewModelTest {

    @RegisterExtension
    @JvmField
    val testCoroutine = TestCoroutinesExtension()

    @MockK
    lateinit var getLoginBannerUseCase: GetLoginBannerUseCase

    @MockK
    lateinit var userRepository: UserRepository

    @MockK
    lateinit var loginManagerInterface: LoginManagerInterface

    @MockK
    lateinit var getMusicGeoBlockUseCase: GetMusicGeoBlockUseCase

    private lateinit var viewModel: ForceLoginBannerViewModel

    private val coroutineDispatcher = TestCoroutineDispatcherProvider(testCoroutine.dispatcher)

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel = ForceLoginBannerViewModel(
            getLoginBannerUseCase,
            userRepository,
            loginManagerInterface,
            getMusicGeoBlockUseCase,
            coroutineDispatcher
        )
    }

    @Test
    fun testNavigateToMusicLanding_returnNavigateToMusicLanding() {
        // When
        viewModel.navigateToMusicLanding()

        // Then
        Assertions.assertEquals(viewModel.onNavigateToMusic().getOrAwaitValue(), Unit)
    }

    @Test
    fun testNavigateToLogin_whenSuccess() {
        // Given
        val slotListener = slot<AuthLoginListener>()
        every { loginManagerInterface.login(capture(slotListener), any(), any()) } just runs

        // When
        viewModel.navigateToLogin()
        slotListener.captured.onLoginSuccess()

        // Then
        verify(exactly = 1) { loginManagerInterface.login(any(), any(), any()) }
    }

    @Test
    fun testNavigateToLogin_whenError() {
        // Given
        val slotListener = slot<AuthLoginListener>()
        every { loginManagerInterface.login(capture(slotListener), any(), any()) } just runs

        // When
        viewModel.navigateToLogin()
        slotListener.captured.onLoginError()

        // Then
        assertEquals(viewModel.onShowLoginErrorToast().value, Unit)
        verify(exactly = 1) { loginManagerInterface.login(any(), any(), any()) }
    }

    @Test
    fun testAuthentication_isNotLogIn_getLoginBannerSuccess_returnLoadForceLoginThumb() = runTest {
        // Given
        val imagePathMock = "image_path"
        every { loginManagerInterface.isLoggedIn() } returns false
        every { getLoginBannerUseCase.execute() } returns flowOf(imagePathMock)

        // When
        viewModel.authentication()

        // Then
        assertEquals(viewModel.onLoadForceLoginThumb().getOrAwaitValue(), imagePathMock)
    }

    @Test
    fun testAuthentication_isNotLogIn_getLoginBannerFail_returnEmptyData() = runTest {
        // Given
        every { loginManagerInterface.isLoggedIn() } returns false
        every { getLoginBannerUseCase.execute() } returns flow { error("error") }

        // When
        viewModel.authentication()

        // Then
        assertTrue(viewModel.onLoadForceLoginThumb().getOrAwaitValue().isEmpty())
    }

    @Test
    fun testAuthentication_isLogInAndGeoBlockIsTrue_returnDisplayGeoBlock() {
        // Given
        every { loginManagerInterface.isLoggedIn() } returns true
        every { getMusicGeoBlockUseCase.execute() } returns flowOf(true)

        // When
        viewModel.authentication()

        // Then
        Assertions.assertEquals(viewModel.onDisplayGeoBlock().getOrAwaitValue(), Unit)
    }

    @Test
    fun testAuthentication_isLogInAndGeoBlockIsFalse_returnShowLoggingInToast() {
        // Given
        every { loginManagerInterface.isLoggedIn() } returns true
        every { getMusicGeoBlockUseCase.execute() } returns flowOf(false)

        // When
        viewModel.authentication()

        // Then
        assertEquals(viewModel.onShowLoading().getOrAwaitValue(), Unit)
    }

    @Test
    fun getAccessToken_success_loginTuned() {
        // Given
        val mockAccessToken = "accessToken"
        val mockUserId = "110"
        every { loginManagerInterface.isLoggedIn() } returns true
        every { getMusicGeoBlockUseCase.execute() } returns flowOf(false)
        every { userRepository.getSsoId() } returns mockUserId
        every { userRepository.getAccessToken() } returns mockAccessToken

        // When
        viewModel.authentication()

        // Then
        assertEquals(viewModel.onShowLoading().getOrAwaitValue(), Unit)
        assertEquals(
            viewModel.onLoginTunedMusic().getOrAwaitValue(),
            Pair(mockUserId.toInt(), mockAccessToken)
        )
    }

    @Test
    fun getAccessToken_fail_showLoginErrorToast() {
        // Given
        every { loginManagerInterface.isLoggedIn() } returns true
        every { getMusicGeoBlockUseCase.execute() } returns flowOf(false)
        every { userRepository.getAccessToken() } returns ""

        // When
        viewModel.authentication()

        // Then
        assertEquals(viewModel.onShowLoading().getOrAwaitValue(), Unit)
        assertEquals(viewModel.onShowLoginErrorToast().getOrAwaitValue(), Unit)
        assertEquals(viewModel.onDisplayForceLogin().getOrAwaitValue(), Unit)
    }

    @Test
    fun handlerAuthentication_alreadyLoggedInIsTrue_navigateToMusicLanding() {
        // When
        viewModel.handlerAuthentication(true)

        // Then
        assertEquals(viewModel.onNavigateToMusic().getOrAwaitValue(), Unit)
    }

    @Test
    fun handlerAuthentication_alreadyLoggedInIsFalse_authentication() {
        // Given
        every { loginManagerInterface.isLoggedIn() } returns true

        // When
        viewModel.handlerAuthentication(false)

        // Then
        verify(exactly = 1) { loginManagerInterface.isLoggedIn() }
    }
}
