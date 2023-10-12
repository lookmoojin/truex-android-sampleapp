package com.truedigital.common.share.analytics.measurement.usecase.truemoney

import com.google.gson.Gson
import com.truedigital.authentication.data.model.response.ProfileResponse
import com.truedigital.authentication.domain.model.ProfileModel
import com.truedigital.common.share.analytics.measurement.AnalyticManager
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant
import com.truedigital.common.share.datalegacy.wrapper.AuthManagerWrapper
import com.truedigital.share.mock.utils.FileUtil
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class TrackCustomerHasTrueMoneyUseCaseImplTest {

    private lateinit var trackCustomerHasTrueMoneyUseCase: TrackCustomerHasTrueMoneyUseCase

    private val authManagerWrapper = mockk<AuthManagerWrapper>()
    private val analyticManager = mockk<AnalyticManager>()

    @BeforeEach
    fun setUp() {
        trackCustomerHasTrueMoneyUseCase =
            TrackCustomerHasTrueMoneyUseCaseImpl(authManagerWrapper, analyticManager)
    }

    @Test
    fun execute() {
        lateinit var mockResponse: ProfileResponse

        FileUtil.readFileWithoutNewLineFromResources("GetProfileResponse200.json")
            .let { jsonString ->
                Gson().fromJson(jsonString, ProfileResponse::class.java).let { json ->
                    mockResponse = json
                }
            }

        every {
            authManagerWrapper.getProfileCache()?.ascend
        } returns mockResponse.refs?.ascendData?.let {
            ProfileModel.AscendModel(
                tmnId = it.trueMoneyData?.tmnId,
                status = it.trueMoneyData?.status ?: 0,
                createdAt = it.trueMoneyData?.createdAt,
                updatedAt = it.trueMoneyData?.updatedAt
            )
        }

        every {
            analyticManager.trackUserProperties(
                MeasurementConstant.Key.KEY_CUSTOMER_HAS_TRUE_MONEY,
                "true"
            )
        } just runs

        trackCustomerHasTrueMoneyUseCase.execute()
        verify(exactly = 1) { authManagerWrapper.getProfileCache() }
    }
}
