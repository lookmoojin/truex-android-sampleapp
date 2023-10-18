package com.truedigital.features.music.presentation.searchtrending

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.music.injections.MusicComponent
import com.truedigital.features.music.presentation.searchtrending.adapter.MusicSearchTrendingAdapter
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.databinding.FragmentMusicSearchTrendingBinding
import com.truedigital.features.tuned.presentation.album.presenter.AlbumPresenter
import com.truedigital.features.tuned.presentation.album.view.AlbumActivity
import com.truedigital.features.tuned.presentation.artist.presenter.ArtistPresenter
import com.truedigital.features.tuned.presentation.artist.view.ArtistActivity
import com.truedigital.features.tuned.presentation.playlist.presenter.PlaylistPresenter
import com.truedigital.features.tuned.presentation.playlist.view.PlaylistActivity
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.invisible
import com.truedigital.foundation.extension.visible
import com.truedigital.foundation.presentations.ViewModelFactory
import javax.inject.Inject

class MusicSearchTrendingFragment : Fragment(R.layout.fragment_music_search_trending) {

    companion object {
        val TAG = MusicSearchTrendingFragment::class.java.canonicalName as String
        fun newInstance() = MusicSearchTrendingFragment()
    }

    private val binding: FragmentMusicSearchTrendingBinding by viewBinding(
        FragmentMusicSearchTrendingBinding::bind
    )

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val musicSearchTrendingViewModel: MusicSearchTrendingViewModel by viewModels(
        { requireActivity() }) { viewModelFactory }

    @Inject
    lateinit var adapterFactory: MusicSearchTrendingAdapter.MusicSearchTrendingAdapterFactory

    private val musicSearchTrendingAdapter: MusicSearchTrendingAdapter by lazy {
        adapterFactory.create(onTrendingArtistClicked, onTrendingPlaylistClicked, onTrendingAlbumClicked)
    }
    private val onTrendingArtistClicked: (id: Int?) -> Unit = { id ->
        id?.let { openArtist(it) }
    }
    private val onTrendingPlaylistClicked: (id: Int?) -> Unit = { id ->
        id?.let { openPlaylist(it) }
    }
    private val onTrendingAlbumClicked: (id: Int?) -> Unit = { id ->
        id?.let { openAlbum(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        MusicComponent.getInstance().inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        initRecyclerView()
    }

    override fun onDestroyView() {
        musicSearchTrendingViewModel.clearTrendingData()
        super.onDestroyView()
    }

    private fun initViewModel() = with(musicSearchTrendingViewModel) {
        onTrendingDataItems().observe(viewLifecycleOwner) { dataItem ->
            dataItem.let {
                musicSearchTrendingAdapter.submitList(it)
            }
        }

        onShowLoading().observe(viewLifecycleOwner) {
            showGhostLoading()
        }

        onHideLoading().observe(viewLifecycleOwner) {
            hideGhostLoading()
        }

        onShowError().observe(viewLifecycleOwner) {
            showErrorView()
        }

        onHideError().observe(viewLifecycleOwner) {
            hideErrorView()
        }
    }

    private fun initRecyclerView() = with(binding) {
        musicTrendingRecyclerView.adapter = musicSearchTrendingAdapter
        musicSearchTrendingViewModel.searchTrending()
    }

    private fun openArtist(id: Int) {
        activity?.startActivity(
            Intent(activity, ArtistActivity::class.java).apply {
                putExtra(ArtistPresenter.ARTIST_ID_KEY, id)
            }
        )
    }

    private fun openPlaylist(id: Int) {
        activity?.startActivity(
            Intent(activity, PlaylistActivity::class.java).apply {
                putExtra(PlaylistPresenter.PLAYLIST_ID_KEY, id)
            }
        )
    }

    private fun openAlbum(id: Int) {
        activity?.startActivity(
            Intent(activity, AlbumActivity::class.java).apply {
                putExtra(AlbumPresenter.ALBUM_ID_KEY, id)
            }
        )
    }

    private fun showGhostLoading() = with(binding) {
        trendingGhostLoadingView.visible()
        trendingGhostLoadingView.start()
    }

    private fun hideGhostLoading() = with(binding) {
        trendingGhostLoadingView.stop()
        trendingGhostLoadingView.invisible()
    }

    private fun showErrorView() = with(binding) {
        trendingErrorView.visible()
        trendingErrorView.setupView(
            drawableImage = R.drawable.ic_searching_not_found,
            title = getString(R.string.message_insert_keyword_search),
            titleStyle = R.style.TrueID_Header2_Black,
            detailStyle = R.style.TrueID_Small_Black
        )
    }

    private fun hideErrorView() = with(binding) {
        trendingErrorView.gone()
    }
}
