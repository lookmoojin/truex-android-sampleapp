package com.truedigital.common.share.componentv3.widget.searchanimation.usecase

import com.truedigital.common.share.componentv3.widget.searchanimation.model.SearchAnimationTime
import com.truedigital.core.extensions.toDate
import java.text.ParseException
import java.util.Calendar
import javax.inject.Inject

interface CheckDateSearchAnimationUseCase {
    fun execute(searchAnimationTime: SearchAnimationTime): Boolean
}

class CheckDateSearchAnimationUseCaseImpl @Inject constructor() : CheckDateSearchAnimationUseCase {
    override fun execute(searchAnimationTime: SearchAnimationTime): Boolean {
        return if (searchAnimationTime.startDate.isNotEmpty() &&
            searchAnimationTime.endDate.isNotEmpty()
        ) {
            try {
                val currentTime = Calendar.getInstance().time
                val startDate = searchAnimationTime.startDate.toDate()
                val endDate = searchAnimationTime.endDate.toDate()
                currentTime.time > startDate.time && currentTime.time < endDate.time
            } catch (e: ParseException) {
                false
            }
        } else {
            false
        }
    }
}
