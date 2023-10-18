package com.truedigital.common.share.datalegacy.domain.other.usecase

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.truedigital.core.utils.SharedPrefsUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AddKeySharedPrefsUseCaseTest {
    private lateinit var addKeySharedPrefsUseCase: AddKeySharedPrefsUseCase
    private val sharedPref: SharedPrefsUtils = mock()

    @BeforeEach
    fun setup() {
        addKeySharedPrefsUseCase = AddKeySharedPrefsUseCaseImpl(sharedPref)
    }

    @Test
    fun testAddKeySharedPrefs_Success() {
        addKeySharedPrefsUseCase.execute("key", "value")
        verify(sharedPref, times(1)).put("key", "value")
    }
}
