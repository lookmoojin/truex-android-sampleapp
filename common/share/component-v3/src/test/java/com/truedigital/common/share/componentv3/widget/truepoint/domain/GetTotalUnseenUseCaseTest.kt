package com.truedigital.common.share.componentv3.widget.truepoint.domain

import com.truedigital.common.share.componentv3.widget.badge.data.model.CountCategoriesInboxResponse
import com.truedigital.common.share.componentv3.widget.badge.data.model.Data
import com.truedigital.common.share.componentv3.widget.badge.data.repository.CountInboxRepository
import com.truedigital.common.share.componentv3.widget.badge.domain.usecase.GetNewCountInboxUseCase
import com.truedigital.common.share.componentv3.widget.badge.domain.usecase.GetTotalUnseenUseCaseImpl
import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.coroutines.TestCoroutinesExtension
import com.truedigital.share.mock.reactivex.RxImmediateSchedulerExtension
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertEquals

@ExtendWith(
    RxImmediateSchedulerExtension::class,
    InstantTaskExecutorExtension::class,
    TestCoroutinesExtension::class
)

class GetTotalUnseenUseCaseTest {

    @MockK
    lateinit var countInboxRepository: CountInboxRepository
    @MockK
    lateinit var userRepository: UserRepository
    @MockK
    lateinit var getNewCountInboxUseCase: GetNewCountInboxUseCase
    // @MockK
    // lateinit var apiNotificationBadge: NotificationBadge
    @MockK
    private lateinit var useCase: GetTotalUnseenUseCaseImpl

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = GetTotalUnseenUseCaseImpl(
            countInboxRepository = countInboxRepository,
            getNewCountInboxUseCase = getNewCountInboxUseCase
        )
    }

    @Test
    fun testExecuteLoadingTrueIsFirstTimeTrue() = runTest {
        // arrange
        val isLoadOnly = true
        useCase.isFirstTime = true

        every {
            userRepository.getSsoId()
        } returns "1"

        every {
            getNewCountInboxUseCase.execute()
        } returns 1

        val mockResponse = CountCategoriesInboxResponse().apply {
            code = 200
            data = Data().apply {
                totalUnseens = 10
            }
        }
        every { countInboxRepository.getCountInbox() } returns flow {
            emit(mockResponse)
        }

        // act
        val flow = useCase.execute(isLoadOnly)
        flow.collect { countInbox ->
            // assert
            assertEquals(countInbox, 10)
        }
    }

    @Test
    fun testExecuteLoadingFalseIsFirstTimeFalse() = runTest {
        // arrange
        val isLoadOnly = false
        useCase.isFirstTime = false

        every {
            userRepository.getSsoId()
        } returns "1"

        every {
            getNewCountInboxUseCase.execute()
        } returns 1

        val mockResponse = CountCategoriesInboxResponse().apply {
            code = 200
            data = Data().apply {
                totalUnseens = 10
            }
        }
        every { countInboxRepository.getCountInbox() } returns flow {
            emit(mockResponse)
        }

        // act
        val flow = useCase.execute(isLoadOnly)
        flow.collect { countInbox ->
            // assert
            assertEquals(countInbox, 10)
        }
    }
    @Test
    fun testExecuteLoadingTrueIsFirstTimeFalse() = runTest {
        // arrange
        val isLoadOnly = true
        useCase.isFirstTime = false

        every {
            userRepository.getSsoId()
        } returns "1"

        every {
            getNewCountInboxUseCase.execute()
        } returns 1

        val mockResponse = CountCategoriesInboxResponse().apply {
            code = 200
            data = Data().apply {
                totalUnseens = 10
            }
        }
        every { countInboxRepository.getCountInbox() } returns flow {
            emit(mockResponse)
        }

        // act
        val flow = useCase.execute(isLoadOnly)
        flow.collect { countInbox ->
            // assert
            assertEquals(countInbox, 10)
        }
    }
    @Test
    fun testExecuteLoadingFalseIsFirstTimeTrue() = runTest {
        // arrange
        val isLoadOnly = false
        useCase.isFirstTime = true

        every {
            userRepository.getSsoId()
        } returns "1"

        every {
            getNewCountInboxUseCase.execute()
        } returns 1

        val mockResponse = CountCategoriesInboxResponse().apply {
            code = 200
            data = Data().apply {
                totalUnseens = 10
            }
        }
        every { countInboxRepository.getCountInbox() } returns flow {
            emit(mockResponse)
        }

        // act
        val flow = useCase.execute(isLoadOnly)
        flow.collect { countInbox ->
            // assert
            assertEquals(countInbox, 1)
        }
    }
}
