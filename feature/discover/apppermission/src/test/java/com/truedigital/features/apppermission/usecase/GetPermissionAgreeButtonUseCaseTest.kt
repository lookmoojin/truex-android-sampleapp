package com.truedigital.features.apppermission.usecase

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.apppermission.data.repository.AppPermissionGetConfigRepository
import com.truedigital.features.apppermission.domain.model.AppPermissionConfigDataModel
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GetPermissionAgreeButtonUseCaseTest {
    private val appPermissionGetConfigRepository: AppPermissionGetConfigRepository = mock()
    private lateinit var getPermissionAgreeButtonUseCase: GetPermissionAgreeButtonUseCase

    @BeforeEach
    fun setUp() {
        getPermissionAgreeButtonUseCase = GetPermissionAgreeButtonUseCaseImpl(
            appPermissionGetConfigRepository
        )
    }

    @Test
    fun whenGetDataSuccess_shouldReturnText() {
        whenever(appPermissionGetConfigRepository.getAppPermissionData()).thenReturn(
            fakeResponseData()
        )

        val getPermissionData = getPermissionAgreeButtonUseCase.execute()
        assert(getPermissionData.isNotEmpty())
        assertEquals(getPermissionData, "button")
    }

    @Test
    fun whenGetDataEmpty_shoulReturnEmptyText() {
        whenever(appPermissionGetConfigRepository.getAppPermissionData()).thenReturn(
            AppPermissionConfigDataModel()
        )

        val getPermissionData = getPermissionAgreeButtonUseCase.execute()
        assertEquals(getPermissionData, "")
    }

    private fun fakeResponseData(): AppPermissionConfigDataModel {
        return AppPermissionConfigDataModel().apply {
            imageLocation = "url_location"
            imageStorage = "url_storage"
            button = "button"
        }
    }
}
