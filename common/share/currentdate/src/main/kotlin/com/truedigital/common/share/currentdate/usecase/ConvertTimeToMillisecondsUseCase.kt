package com.truedigital.common.share.currentdate.usecase

import com.truedigital.common.share.currentdate.DateTimeInterface
import com.truedigital.core.data.device.repository.LocalizationRepository
import javax.inject.Inject

interface ConvertTimeToMillisecondsUseCase {
    fun execute(dateTime: String): Long
}

class ConvertTimeToMillisecondsUseCaseImpl @Inject constructor(
    private val dateTimeUtil: DateTimeInterface,
    private val localizationRepository: LocalizationRepository
) : ConvertTimeToMillisecondsUseCase {

    override fun execute(dateTime: String): Long {
        return dateTimeUtil.convertToMilliSeconds(dateTime, localizationRepository.getAppLocale())
    }
}
