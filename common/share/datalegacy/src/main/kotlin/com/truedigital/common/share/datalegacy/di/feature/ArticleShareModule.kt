package com.truedigital.common.share.datalegacy.di.feature

import com.truedigital.common.share.datalegacy.utils.ArticleDetailStateUtil
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object ArticleShareModule {

    @Singleton
    @Provides
    fun provideArticleDetailStateUtil(dispatcher: CoroutineDispatcherProvider): ArticleDetailStateUtil {
        return ArticleDetailStateUtil(dispatcher)
    }
}
