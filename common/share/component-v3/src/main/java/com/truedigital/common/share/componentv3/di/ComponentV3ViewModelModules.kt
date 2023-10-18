package com.truedigital.common.share.componentv3.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.truedigital.common.share.componentv3.widget.ads.presentation.AdsWidgetViewModel
import com.truedigital.common.share.componentv3.widget.searchanimation.presentation.SearchAnimationViewModel
import com.truedigital.common.share.componentv3.widget.truepoint.presentation.TruePointWidgetViewModel
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.presentation.WeMallShelfWidgetViewModel
import com.truedigital.foundation.di.scopes.ViewModelKey
import com.truedigital.foundation.presentations.ViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ComponentV3ViewModelModules {

    @Binds
    fun bindsViewModelFactory(
        viewModelFactory: ViewModelFactory
    ): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(AdsWidgetViewModel::class)
    fun bindsAdsWidgetViewModel(
        adsWidgetViewModel: AdsWidgetViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchAnimationViewModel::class)
    fun bindsSearchAnimationViewModel(
        searchAnimationViewModel: SearchAnimationViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TruePointWidgetViewModel::class)
    fun bindsTruePointWidgetViewModel(
        truePointWidgetViewModel: TruePointWidgetViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WeMallShelfWidgetViewModel::class)
    fun bindsWeMallShelfWidgetViewModel(
        weMallShelfWidgetViewModel: WeMallShelfWidgetViewModel
    ): ViewModel
}
