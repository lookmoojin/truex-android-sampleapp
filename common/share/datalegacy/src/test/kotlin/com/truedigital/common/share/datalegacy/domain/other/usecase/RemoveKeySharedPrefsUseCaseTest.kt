package com.truedigital.common.share.datalegacy.domain.other.usecase

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.truedigital.core.utils.SharedPrefsUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RemoveKeySharedPrefsUseCaseTest {
    private lateinit var removeKeySharedPrefsUseCase: RemoveKeySharedPrefsUseCase
    private val sharedPref: SharedPrefsUtils = mock()

    @BeforeEach
    fun setup() {
        removeKeySharedPrefsUseCase = RemoveKeySharedPrefsUseCaseImpl(sharedPref)
    }

    @Test
    fun testRemoveKeySharedPrefs_Success() {
        removeKeySharedPrefsUseCase.execute("string")
        verify(sharedPref, times(1)).remove("string")
    }
}
