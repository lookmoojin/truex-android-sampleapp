package com.truedigital.common.share.datalegacy.di.feature

import com.truedigital.common.share.datalegacy.domain.other.usecase.AddKeySharedPrefsUseCase
import com.truedigital.common.share.datalegacy.domain.other.usecase.AddKeySharedPrefsUseCaseImpl
import com.truedigital.common.share.datalegacy.domain.other.usecase.RemoveKeySharedPrefsUseCase
import com.truedigital.common.share.datalegacy.domain.other.usecase.RemoveKeySharedPrefsUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
interface OtherModule {

    @Binds
    fun bindsRemoveKeySharedPrefsUseCase(
        removeKeySharedPrefsUseCaseImpl: RemoveKeySharedPrefsUseCaseImpl
    ): RemoveKeySharedPrefsUseCase

    @Binds
    fun bindsAddKeySharedPrefsUseCase(
        addKeySharedPrefsUseCaseImpl: AddKeySharedPrefsUseCaseImpl
    ): AddKeySharedPrefsUseCase
}
