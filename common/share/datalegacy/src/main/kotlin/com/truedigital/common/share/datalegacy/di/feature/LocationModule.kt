package com.truedigital.common.share.datalegacy.di.feature

import com.truedigital.common.share.datalegacy.domain.other.usecase.EncryptLocationUseCase
import com.truedigital.common.share.datalegacy.domain.other.usecase.EncryptLocationUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
interface LocationModule {

    @Binds
    fun bindsEncryptLocationUseCase(useCase: EncryptLocationUseCaseImpl): EncryptLocationUseCase
}
