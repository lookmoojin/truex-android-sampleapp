package com.truedigital.features.tuned.injection.module.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.truedigital.features.music.presentation.login.LoginTunedViewModel
import com.truedigital.features.music.presentation.myplaylist.MyPlaylistTrackViewModel
import com.truedigital.features.music.presentation.player.MusicServiceConnectionViewModel
import com.truedigital.features.music.presentation.search.MusicTrackViewModel
import com.truedigital.features.music.widget.favorite.MusicFavoriteViewModel
import com.truedigital.foundation.di.scopes.ViewModelKey
import com.truedigital.foundation.presentations.ViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {

    @Binds
    fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MusicTrackViewModel::class)
    fun bindMusicTrackViewModel(viewModel: MusicTrackViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LoginTunedViewModel::class)
    fun bindLoginTunedViewModel(viewModel: LoginTunedViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MusicServiceConnectionViewModel::class)
    fun bindMusicServiceConnectionViewModel(viewModel: MusicServiceConnectionViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MusicFavoriteViewModel::class)
    fun bindMusicFavoriteViewModel(viewModel: MusicFavoriteViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MyPlaylistTrackViewModel::class)
    fun bindMyPlaylistTrackViewModel(viewModel: MyPlaylistTrackViewModel): ViewModel
}
