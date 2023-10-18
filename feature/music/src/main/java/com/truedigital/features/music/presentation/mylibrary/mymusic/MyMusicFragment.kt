package com.truedigital.features.music.presentation.mylibrary.mymusic

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.music.domain.myplaylist.model.MusicMyPlaylistModel
import com.truedigital.features.music.injections.MusicComponent
import com.truedigital.features.music.presentation.mylibrary.MusicMyLibraryNavigationViewModel
import com.truedigital.features.music.presentation.mylibrary.mymusic.adapter.MusicFavoriteShelfAdapter
import com.truedigital.features.music.presentation.mylibrary.mymusic.adapter.MyPlaylistAdapter
import com.truedigital.features.music.presentation.mylibrary.mymusic.adapter.MyPlaylistWrapperAdapter
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.databinding.FragmentMyMusicBinding
import com.truedigital.foundation.presentations.ViewModelFactory
import javax.inject.Inject

class MyMusicFragment : Fragment(R.layout.fragment_my_music) {

    private val binding by viewBinding(FragmentMyMusicBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val musicMyLibraryNavigationViewModel: MusicMyLibraryNavigationViewModel by viewModels(
        { requireActivity() }) { viewModelFactory }
    private val musicFavoriteShelfAdapter by lazy { MusicFavoriteShelfAdapter() }
    private val myPlaylistAdapter: MyPlaylistAdapter by lazy {
        MyPlaylistAdapter(
            { onCreatePlaylist() },
            { onItemClicked(it) }
        )
    }

    private val myPlaylistWrapperAdapter: MyPlaylistWrapperAdapter by lazy {
        MyPlaylistWrapperAdapter(myPlaylistAdapter)
    }

    private val concatAdapter: ConcatAdapter by lazy {
        ConcatAdapter(myPlaylistWrapperAdapter, musicFavoriteShelfAdapter)
    }

    private val myMusicViewModel: MyMusicViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        MusicComponent.getInstance().inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPullToRefresh()
        initRecyclerView()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        musicFavoriteShelfAdapter.onResumeMyFavorite()
        myMusicViewModel.fetchMyPlaylist()
    }

    override fun onPause() {
        super.onPause()
        musicFavoriteShelfAdapter.onPauseMyFavorite()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        myPlaylistWrapperAdapter.onSaveState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let { myPlaylistWrapperAdapter.onRestoreState(it) }
    }

    private fun initPullToRefresh() = with(binding) {
        rootMyMusicFragment.setProgressBackgroundColorSchemeResource(R.color.primary)
        rootMyMusicFragment.setColorSchemeResources(android.R.color.white)
        rootMyMusicFragment.setOnRefreshListener { refresh() }
    }

    private fun observeViewModel() {
        with(myMusicViewModel) {
            onGetMyPlaylist().observe(viewLifecycleOwner) {
                myPlaylistAdapter.update(it)
            }

            onPullRefresh().observe(viewLifecycleOwner) {
                binding.rootMyMusicFragment.isRefreshing = it
            }
        }
    }

    private fun refresh() {
        musicFavoriteShelfAdapter.onRefreshMyFavorite()
        myMusicViewModel.fetchMyPlaylist()
    }

    private fun initRecyclerView() = with(binding) {
        myPlaylistAdapter.update()
        myMusicRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = concatAdapter
        }
    }

    private fun onItemClicked(myPlaylistModel: MusicMyPlaylistModel.MyPlaylistModel) {
        musicMyLibraryNavigationViewModel.showMyPlaylist(myPlaylistModel.playlistId)
        myMusicViewModel.trackFASelectContent(myPlaylistModel)
    }

    private fun onCreatePlaylist() {
        musicMyLibraryNavigationViewModel.createNewPlaylist()
        myMusicViewModel.trackFASelectContent(MusicMyPlaylistModel.CreateMyPlaylistModel())
    }
}
