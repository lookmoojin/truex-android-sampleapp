package com.truedigital.common.share.componentv3.widget.badge.presentation

import com.truedigital.common.share.componentv3.widget.badge.domain.usecase.CountInboxUseCase
import com.truedigital.common.share.componentv3.widget.badge.domain.usecase.GetInboxEnableUseCase
import com.truedigital.common.share.componentv3.widget.badge.domain.usecase.GetNewCountInboxUseCase
import com.truedigital.common.share.componentv3.widget.badge.domain.usecase.GetTotalUnseenUseCase
import com.truedigital.common.share.componentv3.widget.badge.domain.usecase.SaveNewCountInboxUseCase
import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.core.coroutines.TestCoroutineDispatcherProvider
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.coroutines.TestCoroutinesExtension
import com.truedigital.share.mock.livedata.getOrAwaitValue
import com.truedigital.share.mock.reactivex.RxImmediateSchedulerExtension
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import io.reactivex.Observable
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import kotlin.test.assertEquals

@ExtendWith(
    RxImmediateSchedulerExtension::class,
    InstantTaskExecutorExtension::class,
    TestCoroutinesExtension::class,
)
class CountInboxViewModelTest {
    @RegisterExtension
    @JvmField
    val testCoroutine = TestCoroutinesExtension()

    private val coroutineDispatcher = TestCoroutineDispatcherProvider(testCoroutine.dispatcher)

    @MockK
    lateinit var saveNewCountInboxUseCase: SaveNewCountInboxUseCase

    @MockK
    lateinit var getNewCountInboxUseCase: GetNewCountInboxUseCase

    @MockK
    lateinit var getInboxEnableUseCase: GetInboxEnableUseCase

    @MockK
    lateinit var getTotalUnseenUseCase: GetTotalUnseenUseCase

    @MockK
    lateinit var userRepository: UserRepository

    @MockK(relaxed = true)
    lateinit var countInboxUseCase: CountInboxUseCase

    private lateinit var viewModel: CountInboxViewModel

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        viewModel = CountInboxViewModel(
            coroutineDispatcher = coroutineDispatcher,
            saveNewCountInboxUseCase = saveNewCountInboxUseCase,
            getNewCountInboxUseCase = getNewCountInboxUseCase,
            getInboxEnableUseCase = getInboxEnableUseCase,
            getTotalUnseenUseCase = getTotalUnseenUseCase,
            userRepository = userRepository,
            countInboxUseCase = countInboxUseCase,
        )
    }

    @Test
    fun testCheckShowInboxMessage() = runTest {
        // arrange
        val countInbox = 10

        every {
            userRepository.getSsoId()
        } returns "1"

        every {
            getInboxEnableUseCase.execute()
        } returns Observable.just(true)

        every {
            getTotalUnseenUseCase.execute(false)
        } returns flowOf(countInbox)

        every {
            saveNewCountInboxUseCase.execute(countInbox)
        } returns Unit

        // act
        viewModel.checkShowInboxMessage()

        // assert
        verify(exactly = 1) { saveNewCountInboxUseCase.execute(countInbox) }
        assertEquals(countInbox.toString(), viewModel.showInboxMessageNumber().getOrAwaitValue())
        assertEquals(true, viewModel.showInboxMessage().getOrAwaitValue())
    }

    @Test
    fun testCheckShowInboxMessageMoreThanMaximunCount() = runTest {
        // arrange
        val countInbox = 100

        every {
            userRepository.getSsoId()
        } returns "1"

        every {
            getInboxEnableUseCase.execute()
        } returns Observable.just(true)

        every {
            getTotalUnseenUseCase.execute(false)
        } returns flowOf(countInbox)

        every {
            saveNewCountInboxUseCase.execute(countInbox)
        } returns Unit

        // act
        viewModel.checkShowInboxMessage()

        // assert
        verify(exactly = 1) { saveNewCountInboxUseCase.execute(countInbox) }
        assertEquals("99+", viewModel.showInboxMessageNumber().getOrAwaitValue())
        assertEquals(true, viewModel.showInboxMessage().getOrAwaitValue())
    }

    @Test
    fun testCheckShowInboxMessageCaseZero() = runTest {
        // arrange
        val countInbox = 0

        every {
            userRepository.getSsoId()
        } returns "1"

        every {
            getInboxEnableUseCase.execute()
        } returns Observable.just(true)

        every {
            getTotalUnseenUseCase.execute(false)
        } returns flowOf(countInbox)

        every {
            saveNewCountInboxUseCase.execute(countInbox)
        } returns Unit

        // act
        viewModel.checkShowInboxMessage()

        // assert
        verify(exactly = 1) { saveNewCountInboxUseCase.execute(countInbox) }
        assertEquals("0", viewModel.showInboxMessageNumber().getOrAwaitValue())
        assertEquals(false, viewModel.showInboxMessage().getOrAwaitValue())
    }

    @Test
    fun testCheckShowInboxMessageCaseSSOIDEmpty() = runTest {
        // arrange
        val countInbox = 0

        every {
            userRepository.getSsoId()
        } returns ""

        // act
        viewModel.checkShowInboxMessage()

        // assert
        verify(exactly = 0) { getInboxEnableUseCase.execute() }
    }

    @Test
    fun testCheckShowInboxMessageCaseDisableInbox() = runTest {
        // arrange
        val countInbox = 0

        every {
            userRepository.getSsoId()
        } returns "1"

        every {
            getInboxEnableUseCase.execute()
        } returns Observable.just(false)

        // act
        viewModel.checkShowInboxMessage()

        // assert
        assertEquals(false, viewModel.showInboxMessage().getOrAwaitValue())
    }

    @Test
    fun testCheckShowInboxMessageNotCallService() = runTest {
        // arrange
        every {
            userRepository.getSsoId()
        } returns "1"

        every {
            getInboxEnableUseCase.execute()
        } returns Observable.just(true)

        every {
            getNewCountInboxUseCase.execute()
        } returns 10

        // act
        viewModel.checkShowInboxMessageNotCallService()

        // assert
        assertEquals(true, viewModel.showInboxMessage().getOrAwaitValue())
        assertEquals("10", viewModel.showInboxMessageNumber().getOrAwaitValue())
    }

    @Test
    fun testCheckShowInboxMessageNotCallServiceCaseNOSSOID() = runTest {
        // arrange
        every {
            userRepository.getSsoId()
        } returns ""

        every {
            getInboxEnableUseCase.execute()
        } returns Observable.just(true)

        every {
            getNewCountInboxUseCase.execute()
        } returns 10

        // act
        viewModel.checkShowInboxMessageNotCallService()

        // assert
        assertEquals(false, viewModel.showInboxMessage().getOrAwaitValue())
        assertEquals("10", viewModel.showInboxMessageNumber().getOrAwaitValue())
    }

    @Test
    fun testCheckShowInboxMessageNotCallServiceCaseGetInboxEnableUseCaseFalse() = runTest {
        // arrange
        every {
            userRepository.getSsoId()
        } returns "1"

        every {
            getInboxEnableUseCase.execute()
        } returns Observable.just(false)

        every {
            getNewCountInboxUseCase.execute()
        } returns 10

        // act
        viewModel.checkShowInboxMessageNotCallService()

        // assert
        assertEquals(false, viewModel.showInboxMessage().getOrAwaitValue())
    }

    @Test
    fun testCheckShowInboxMessageNotCallServiceCaseMoreThanLimit() = runTest {
        // arrange
        every {
            userRepository.getSsoId()
        } returns "1"

        every {
            getInboxEnableUseCase.execute()
        } returns Observable.just(true)

        every {
            getNewCountInboxUseCase.execute()
        } returns 100

        // act
        viewModel.checkShowInboxMessageNotCallService()

        // assert
        assertEquals(true, viewModel.showInboxMessage().getOrAwaitValue())
        assertEquals("99+", viewModel.showInboxMessageNumber().getOrAwaitValue())
    }

    @Test
    fun testloadCountInbox() = runTest {
        // arrange
        val countInbox = 10

        every {
            getTotalUnseenUseCase.execute(true)
        } returns flowOf(countInbox)

        // act
        viewModel.loadCountInbox()

        // assert
        verify(exactly = 1) { saveNewCountInboxUseCase.execute(countInbox) }
    }
}
