package com.truedigital.common.share.nativeshare.di

import android.content.Context
import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.common.share.nativeshare.domain.usecase.dynamiclink.DynamicLinkGeneratorUseCaseImpl
import com.truedigital.common.share.nativeshare.utils.DynamicLinkGenerator
import com.truedigital.common.share.nativeshare.utils.DynamicLinkGeneratorImpl
import com.truedigital.common.share.nativeshare.utils.OneLinkGenerator
import com.truedigital.common.share.nativeshare.utils.OneLinkGeneratorImpl
import com.truedigital.core.provider.ContextDataProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object LinkGeneratorModule {

    @Provides
    @Singleton
    fun provideDynamicLinkGenerator(contextDataProvider: ContextDataProvider): DynamicLinkGenerator {
        return DynamicLinkGeneratorImpl(contextDataProvider)
    }

    @Provides
    fun provideOneLinkGenerator(
        context: Context,
        userRepository: UserRepository
    ): OneLinkGenerator {
        return OneLinkGeneratorImpl(context, userRepository)
    }

    @Provides
    @Singleton
    fun getDynamicLinkGeneratorUseCase(contextDataProvider: ContextDataProvider): DynamicLinkGeneratorUseCaseImpl {
        return DynamicLinkGeneratorUseCaseImpl(contextDataProvider)
    }
}
