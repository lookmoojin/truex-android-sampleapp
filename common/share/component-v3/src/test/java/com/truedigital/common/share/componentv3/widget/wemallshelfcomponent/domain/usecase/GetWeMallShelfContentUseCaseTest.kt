package com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.domain.usecase

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.data.model.WeMallShelfRequestModel
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.data.model.WeMallShelfResponseModel
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.data.repository.WeMallShelfRepository
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class GetWeMallShelfContentUseCaseTest {
    private lateinit var getWeMallShelfContentUseCase: GetWeMallShelfContentUseCase
    private val weMallShelfRepository: WeMallShelfRepository = mock()
    private val token = "authorization"
    private val requestModel = WeMallShelfRequestModel()

    @BeforeEach
    fun setup() {
        getWeMallShelfContentUseCase = GetWeMallShelfContentUseCaseImpl(weMallShelfRepository)
    }

    @Test
    fun `When executed success should return data`() = runTest {
        whenever(
            weMallShelfRepository.getWeMallShelfComponent(
                token,
                requestModel
            )
        ).thenReturn(flow { emit(WeMallShelfResponseModel()) })
        assertNotNull(getWeMallShelfContentUseCase.execute(token, requestModel))
    }
}
