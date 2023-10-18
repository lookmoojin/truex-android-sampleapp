package com.truedigital.navigation.di.multi.article

import com.truedigital.common.share.articledetails.share.domain.usecase.deeplink.ArticleDetailDecodeDeeplinkUseCaseImpl
import com.truedigital.common.share.data.coredata.deeplink.usecase.DecodeDeeplinkUseCase
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet

@Module
object NavigationArticleDeepLinksModule {

    @Provides
    @IntoSet
    fun bindsDecodeDeeplinkUseCase(): DecodeDeeplinkUseCase {
        return ArticleDetailDecodeDeeplinkUseCaseImpl()
    }
}
