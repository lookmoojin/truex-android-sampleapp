package com.truedigital.common.share.componentv3.widget.badge.domain.usecase

import com.truedigital.component.constant.configuration.Constant
import com.truedigital.core.utils.SharedPrefsUtils
import javax.inject.Inject

interface GetCountInboxUseCase {
    fun execute(): Int
}

class GetCountInboxUseCaseImpl @Inject constructor(private val sharedPrefs: SharedPrefsUtils) : GetCountInboxUseCase {

    override fun execute(): Int {
        return sharedPrefs.get(Constant.FEATURE_COUNT_INBOX_MESSAGE, 0)
    }
}
