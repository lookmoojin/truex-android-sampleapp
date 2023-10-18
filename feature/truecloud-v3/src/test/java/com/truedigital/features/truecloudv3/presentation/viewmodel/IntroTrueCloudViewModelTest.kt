package com.truedigital.features.truecloudv3.presentation.viewmodel

import com.jraska.livedata.TestObserver
import com.nhaarman.mockitokotlin2.any
import com.truedigital.common.share.analytics.measurement.AnalyticManagerInterface
import com.truedigital.common.share.datalegacy.login.LoginManagerInterface
import com.truedigital.common.share.datalegacy.wrapper.AuthLoginListener
import com.truedigital.core.coroutines.TestCoroutineDispatcherProvider
import com.truedigital.features.truecloudv3.domain.usecase.ConfigIntroImageUseCase
import com.truedigital.features.truecloudv3.navigation.IntroTrueCloudToMain
import com.truedigital.features.truecloudv3.navigation.router.IntroTrueCloudRouterUseCase
import com.truedigital.navigation.domain.usecase.SetRouterToNavControllerUseCase
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.coroutines.TestCoroutinesExtension
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension

internal interface IntroTrueCloudViewModelTestCase {
    fun `test checkAuthenticationState case isLoggedIn and route IntroToMain`()
    fun `test checkAuthenticationState case isLoggedIn false and route IntroToMain`()
    fun `test loginSuccess`()
}

@ExtendWith(InstantTaskExecutorExtension::class)
internal class IntroTrueCloudViewModelTest : IntroTrueCloudViewModelTestCase {

    @ExperimentalCoroutinesApi
    @RegisterExtension
    @JvmField
    val testCoroutine = TestCoroutinesExtension()

    private val router: IntroTrueCloudRouterUseCase = mockk()
    private val loginManagerInterface: LoginManagerInterface = mockk()
    private val configIntroImageUseCase: ConfigIntroImageUseCase = mockk()
    private val analyticManagerInterface: AnalyticManagerInterface = mockk()
    private val setRouterToNavControllerUseCase: SetRouterToNavControllerUseCase = mockk()
    private val coroutineDispatcher = TestCoroutineDispatcherProvider(testCoroutine.dispatcher)

    private lateinit var viewModel: IntroTrueCloudViewModel

    @BeforeEach
    fun setUp() {
        viewModel = IntroTrueCloudViewModel(
            router,
            loginManagerInterface,
            coroutineDispatcher,
            configIntroImageUseCase,
            analyticManagerInterface,
            setRouterToNavControllerUseCase,
        )
    }

    @Test
    override fun `test checkAuthenticationState case isLoggedIn and route IntroToMain`() {
        // arrange
        every { loginManagerInterface.isLoggedIn() } returns true
        every { router.execute(any()) } just runs

        // act
        viewModel.checkAuthenticationState(any())

        // assert
        verify(exactly = 1) { router.execute(IntroTrueCloudToMain) }
    }

    @Test
    override fun `test checkAuthenticationState case isLoggedIn false and route IntroToMain`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onDisplayIntroImage)

        every { loginManagerInterface.isLoggedIn() } returns false
        every { router.execute(any()) } just runs
        every {
            configIntroImageUseCase.execute(any())
        } returns flowOf("")

        // act
        viewModel.checkAuthenticationState(any())

        // assert
        verify(exactly = 1) { configIntroImageUseCase.execute(any()) }
        testObserver.assertHasValue()
    }

    @Test
    override fun `test loginSuccess`() {
        // arrange
        every { router.execute(destination = IntroTrueCloudToMain, bundle = any()) } just runs

        // act
        viewModel.loginSuccess()

        // assert
        verify(exactly = 1) { router.execute(destination = IntroTrueCloudToMain) }
    }

    @Test
    fun `test openAuthenticationPage`() {
        // arrange
        every { router.execute(IntroTrueCloudToMain, bundle = any()) } just runs

        val slotListener = slot<AuthLoginListener>()
        every {
            loginManagerInterface.login(capture(slotListener), any(), any())
        } answers {
            slotListener.captured.onLoginSuccess()
        }
        every { analyticManagerInterface.trackEvent(any()) } just runs
        // act
        viewModel.openAuthenticationPage()

        // assert
        TestObserver.test(viewModel.onOpenMainTrueCloud).assertHistorySize(1)
    }

    @Test
    fun `test onClickCloseButton`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onClosePage)
        every { analyticManagerInterface.trackEvent(any()) } just runs
        // act
        viewModel.onClickCloseButton()

        // assert
        verify(exactly = 1) {
            analyticManagerInterface.trackEvent(any())
        }
        testObserver.assertHasValue()
    }
}
