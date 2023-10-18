package com.truedigital.common.share.currentdate.domain

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.truedigital.common.share.currentdate.DateTimeInterface
import com.truedigital.common.share.currentdate.usecase.ConvertTimeToMillisecondsUseCase
import com.truedigital.common.share.currentdate.usecase.ConvertTimeToMillisecondsUseCaseImpl
import com.truedigital.core.data.device.repository.LocalizationRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ConvertTimeToMillisecondsUseCaseTest {
    private val dateTimeUtil: DateTimeInterface = mock()
    private val localizationRepository: LocalizationRepository = mock()
    private lateinit var convertTimeToMillisecondsUseCase: ConvertTimeToMillisecondsUseCase

    @BeforeEach
    fun setup() {
        convertTimeToMillisecondsUseCase =
            ConvertTimeToMillisecondsUseCaseImpl(dateTimeUtil, localizationRepository)
    }

    @Test
    fun getCurrentDateTimeWithTrueIsRequireLocalTime() {
        val dateTime = "2021-10-06 08:00:00"
        convertTimeToMillisecondsUseCase.execute(dateTime)

        verify(dateTimeUtil, times(1)).convertToMilliSeconds(
            dateTime,
            localizationRepository.getAppLocale()
        )
    }
}
