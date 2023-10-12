package com.truedigital.common.share.datalegacy.di.feature

import com.truedigital.common.share.datalegacy.js.TrueIDJavaScriptHandler
import dagger.Module
import dagger.Provides

@Module
class JsModule {

    @Provides
    fun providesTrueIDJavaScriptHandler(): TrueIDJavaScriptHandler {
        return TrueIDJavaScriptHandler()
    }
}
