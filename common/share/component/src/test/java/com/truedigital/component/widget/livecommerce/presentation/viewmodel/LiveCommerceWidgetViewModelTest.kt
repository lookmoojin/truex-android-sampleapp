package com.truedigital.component.widget.livecommerce.presentation.viewmodel

import com.jraska.livedata.TestObserver
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.component.widget.livecommerce.domain.model.CommerceActiveLiveStreamModel
import com.truedigital.component.widget.livecommerce.domain.usecase.ConvertShelfDataToLiveStreamDataUseCase
import com.truedigital.component.widget.livecommerce.domain.usecase.CreateCommerceLiveDeeplinkUseCase
import com.truedigital.component.widget.livecommerce.domain.usecase.GetActiveLiveStreamUseCase
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.coroutines.TestCoroutinesExtension
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension

@ExtendWith(InstantTaskExecutorExtension::class)
class LiveCommerceWidgetViewModelTest {

    private lateinit var liveCommerceWidgetViewModel: LiveCommerceWidgetViewModel
    private val createCommerceLiveDeeplinkUseCase: CreateCommerceLiveDeeplinkUseCase = mock()
    private val convertShelfDataToLiveStreamDataUseCase: ConvertShelfDataToLiveStreamDataUseCase =
        mock()
    private val getActiveLiveStreamUseCase: GetActiveLiveStreamUseCase = mock()
    private val coroutineDispatcher: CoroutineDispatcherProvider = mock()

    @RegisterExtension
    @JvmField
    val testCoroutine = TestCoroutinesExtension()

    @BeforeEach
    fun setup() {
        liveCommerceWidgetViewModel = LiveCommerceWidgetViewModel(
            convertShelfDataToLiveStreamDataUseCase,
            createCommerceLiveDeeplinkUseCase,
            getActiveLiveStreamUseCase,
            coroutineDispatcher
        )
    }

    @Test
    fun `when shelfitem data has value should call muze api`() = runTest {
        whenever(
            convertShelfDataToLiveStreamDataUseCase
                .execute(shelfItemData = "string")
        )
            .thenReturn(
                Triple(
                    "title",
                    "navigate",
                    "ssoIdList"
                )
            )

        whenever(
            getActiveLiveStreamUseCase.execute(ssoIds = "ssoIdList")
        )
            .thenReturn(
                flowOf()
            )

        val testObserver1 = TestObserver.test(liveCommerceWidgetViewModel.titleOfWidget)
        val testObserver2 = TestObserver.test(liveCommerceWidgetViewModel.navigateLink)

        liveCommerceWidgetViewModel.initData("string")

        testObserver1
            .assertHasValue()
            .assertValue { data ->
                data.equals("title")
            }
        testObserver2
            .assertHasValue()
            .assertValue { data ->
                data.equals("navigate")
            }
    }

    @Test
    fun `when loadActiveLiveStream has value should call observer`() = runTest {
        whenever(
            getActiveLiveStreamUseCase.execute("ssoIdList")
        )
            .thenReturn(
                flowOf(
                    listOf(
                        CommerceActiveLiveStreamModel()
                    )
                )
            )

        liveCommerceWidgetViewModel.loadActiveLiveStream("ssoIdList")

        verify(getActiveLiveStreamUseCase, times(1)).execute("ssoIdList")
    }
}
