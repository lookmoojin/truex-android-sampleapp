package com.truedigital.common.share.data.usecase

import com.truedigital.common.share.data.coredata.data.api.CmsFnCounterApiInterface
import com.truedigital.common.share.data.coredata.domain.usecase.CountViewUseCaseImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Response

internal class CountViewUseCaseTest {

    private lateinit var api: CmsFnCounterApiInterface
    private lateinit var useCase: CountViewUseCaseImpl

    @BeforeEach
    fun setup() {
        api = mockk(relaxed = true)
        useCase = CountViewUseCaseImpl(api)
    }

    @AfterEach
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun `execute should call api getCountView and emit Unit`() = runTest {
        // given
        val cmsId = "123"
        coEvery { api.getCountView(cmsId) } returns Response.success(Unit)

        // when
        val result = useCase.execute(cmsId).toList()

        // then
        assertEquals(listOf(Unit), result)
        coVerify(exactly = 1) { api.getCountView(cmsId) }
    }
}
