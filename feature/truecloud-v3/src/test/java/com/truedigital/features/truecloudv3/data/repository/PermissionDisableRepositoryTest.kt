package com.truedigital.features.truecloudv3.data.repository

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.core.constant.SharedPrefsKeyConstant
import com.truedigital.core.utils.SharedPrefsInterface
import com.truedigital.core.utils.get
import com.truedigital.core.utils.put
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

internal interface PermissionDisableRepositoryTestCase {
    fun `test isDisableExternalStorage get`()
    fun `test isDisableExternalStorage set`()
    fun `test isDisableReadContact get`()
    fun `test isDisableReadContact set`()
}

internal class PermissionDisableRepositoryTest : PermissionDisableRepositoryTestCase {

    private lateinit var permissionDisableRepository: PermissionDisableRepository

    private val sharedPrefsInterface: SharedPrefsInterface = mock()

    @BeforeEach
    fun setUp() {
        permissionDisableRepository = PermissionDisableRepositoryImpl(
            sharedPrefsInterface
        )
    }

    @Test
    override fun `test isDisableExternalStorage get`() {
        // arrange
        val key = SharedPrefsKeyConstant.PERMISSION_EXTERNAL_STORAGE_DISABLE
        whenever(
            sharedPrefsInterface.get(key = key, defaultValue = false)
        ).thenReturn(true)

        // act
        val action = permissionDisableRepository.isDisableExternalStorage

        // assert
        verify(sharedPrefsInterface, times(1)).get(key = key, defaultValue = false)
        assertTrue(action)
    }

    @Test
    override fun `test isDisableExternalStorage set`() {
        // arrange
        val key = SharedPrefsKeyConstant.PERMISSION_EXTERNAL_STORAGE_DISABLE

        // act
        permissionDisableRepository.isDisableExternalStorage = true

        // assert
        verify(sharedPrefsInterface, times(1)).put(key = key, value = true)
    }

    @Test
    override fun `test isDisableReadContact get`() {
        // arrange
        val key = SharedPrefsKeyConstant.PERMISSION_READ_CONTACTS_DISABLE
        whenever(
            sharedPrefsInterface.get(key = key, defaultValue = false)
        ).thenReturn(true)

        // act
        val action = permissionDisableRepository.isDisableReadContact

        // assert
        verify(sharedPrefsInterface, times(1)).get(key = key, defaultValue = false)
        assertTrue(action)
    }

    @Test
    override fun `test isDisableReadContact set`() {
        // arrange
        val key = SharedPrefsKeyConstant.PERMISSION_READ_CONTACTS_DISABLE

        // act
        permissionDisableRepository.isDisableReadContact = true

        // assert
        verify(sharedPrefsInterface, times(1)).put(key = key, value = true)
    }
}
