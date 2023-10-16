package com.truedigital.common.share.componentv3.widget.badge.domain.usecase

import com.truedigital.component.constant.configuration.Constant
import com.truedigital.core.utils.SharedPrefsUtils
import javax.inject.Inject

interface SaveCountInboxUseCase {
    fun execute(count: Int)
}

class SaveCountInboxUseCaseImpl @Inject constructor(private val sharedPrefs: SharedPrefsUtils) : SaveCountInboxUseCase {

    override fun execute(count: Int) {
        sharedPrefs.put(Constant.FEATURE_COUNT_INBOX_MESSAGE, count)
    }
}
