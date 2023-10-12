package com.truedigital.common.share.datalegacy.domain.other.usecase

import com.truedigital.core.utils.SharedPrefsUtils
import javax.inject.Inject

interface RemoveKeySharedPrefsUseCase {
    fun execute(removeKey: String)
}

class RemoveKeySharedPrefsUseCaseImpl @Inject constructor(private val sharedPrefs: SharedPrefsUtils) : RemoveKeySharedPrefsUseCase {
    override fun execute(removeKey: String) {
        sharedPrefs.remove(removeKey)
    }
}
