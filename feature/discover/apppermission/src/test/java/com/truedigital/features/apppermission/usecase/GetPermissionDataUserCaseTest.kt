package com.truedigital.features.apppermission.usecase

import android.Manifest
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.apppermission.data.repository.AppPermissionGetConfigRepository
import com.truedigital.features.apppermission.domain.model.AppPermissionConfigDataModel
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GetPermissionDataUserCaseTest {
    private val appPermissionGetConfigRepository: AppPermissionGetConfigRepository = mock()
    private lateinit var getPermissionDataUseCase: GetPermissionDataUseCase

    @BeforeEach
    fun setUp() {
        getPermissionDataUseCase = GetPermissionDataUseCaseImpl(
            appPermissionGetConfigRepository
        )
    }

    @Test
    fun whenGetDataSuccess_shouldReturnUrlStorage() {
        // arrange
        val permissions = arrayListOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        whenever(appPermissionGetConfigRepository.getAppPermissionData()).thenReturn(
            fakeResponseData()
        )

        // action
        val getPermissions = getPermissionDataUseCase.execute(permissions)
        // assert
        assert(getPermissions.isNotEmpty())
        assertEquals(getPermissions[0].thum, "url_storage")
    }

    @Test
    fun whenGetDataSuccess_shouldReturnUrlLocation() {
        val permissions = arrayListOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        whenever(appPermissionGetConfigRepository.getAppPermissionData()).thenReturn(
            fakeResponseData()
        )
        // action
        val getPermissions = getPermissionDataUseCase.execute(permissions)
        // assert
        assert(getPermissions.isNotEmpty())
        assertEquals(getPermissions[0].thum, "url_location")
    }

    @Test
    fun whenGetDataEmpty_shouldReturnEmpty() {
        val permissions = arrayListOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        whenever(appPermissionGetConfigRepository.getAppPermissionData()).thenReturn(
            AppPermissionConfigDataModel()
        )
        // action
        val getPermissions = getPermissionDataUseCase.execute(permissions)
        // assert
        assert(getPermissions.size == 0)
    }

    @Test
    fun whenGetDataSuccess_WrongLocation_shouldReturnEmpty() {
        val permissions = arrayListOf(
            Manifest.permission.WRITE_VOICEMAIL
        )
        whenever(appPermissionGetConfigRepository.getAppPermissionData()).thenReturn(
            fakeResponseData()
        )
        // action
        val getPermissions = getPermissionDataUseCase.execute(permissions)
        // assert
        assert(getPermissions.size == 0)
    }

    private fun fakeResponseData(): AppPermissionConfigDataModel {
        return AppPermissionConfigDataModel().apply {
            imageLocation = "url_location"
            imageStorage = "url_storage"
            button = "button"
        }
    }
}
