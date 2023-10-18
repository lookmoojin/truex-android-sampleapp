package com.truedigital.common.share.componentv3.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.truedigital.common.share.communityshare.presentation.viewmodel.CommunityShareViewModel
import com.truedigital.foundation.di.scopes.ViewModelKey
import com.truedigital.foundation.presentations.ViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface CommunityShareBindsModule {

    @Binds
    fun bindsViewModelFactory(
        viewModelFactory: ViewModelFactory
    ): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(CommunityShareViewModel::class)
    fun bindsCommunityShareViewModel(
        communityShareViewModel: CommunityShareViewModel
    ): ViewModel
}
