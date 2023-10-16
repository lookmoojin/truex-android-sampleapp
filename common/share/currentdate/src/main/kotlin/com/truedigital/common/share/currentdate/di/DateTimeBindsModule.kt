package com.truedigital.common.share.currentdate.di

import com.truedigital.common.share.currentdate.usecase.ConvertTimeToMillisecondsUseCase
import com.truedigital.common.share.currentdate.usecase.ConvertTimeToMillisecondsUseCaseImpl
import com.truedigital.common.share.currentdate.usecase.ConvertToDateFormatUseCase
import com.truedigital.common.share.currentdate.usecase.ConvertToDateFormatUseCaseImpl
import com.truedigital.common.share.currentdate.usecase.GetCurrentDateTimeUseCase
import com.truedigital.common.share.currentdate.usecase.GetCurrentDateTimeUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
interface DateTimeBindsModule {

    @Binds
    fun bindsConvertTimeToMillisecondsUseCase(
        convertTimeToMillisecondsUseCaseImpl: ConvertTimeToMillisecondsUseCaseImpl
    ): ConvertTimeToMillisecondsUseCase

    @Binds
    fun bindsGetCurrentDateTimeUseCase(
        getCurrentDateTimeUseCaseImpl: GetCurrentDateTimeUseCaseImpl
    ): GetCurrentDateTimeUseCase

    @Binds
    fun bindsConvertToDateFormatUseCase(
        convertToDateFormatUseCaseImpl: ConvertToDateFormatUseCaseImpl
    ): ConvertToDateFormatUseCase
}
