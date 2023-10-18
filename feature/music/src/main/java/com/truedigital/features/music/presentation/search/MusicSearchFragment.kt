package com.truedigital.features.music.presentation.search

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.truedigital.common.share.analytics.measurement.AnalyticManager
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.music.domain.search.model.MusicSearchModel
import com.truedigital.features.music.domain.search.model.MusicType
import com.truedigital.features.music.domain.search.model.ThemeType.Companion.getThemeType
import com.truedigital.features.music.domain.search.model.ThemeType.DARK
import com.truedigital.features.music.domain.search.model.TopMenuModel
import com.truedigital.features.music.injections.MusicComponent
import com.truedigital.features.music.presentation.search.adapter.HorizontalConcatAdapter
import com.truedigital.features.music.presentation.search.adapter.MusicSearchContentAdapter
import com.truedigital.features.music.presentation.search.adapter.MusicSearchTopMenuAdapter
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.common.extensions.bindServiceMusic
import com.truedigital.features.tuned.common.extensions.getMusicServiceIntent
import com.truedigital.features.tuned.databinding.FragmentSearchMusicBinding
import com.truedigital.features.tuned.presentation.album.presenter.AlbumPresenter
import com.truedigital.features.tuned.presentation.album.view.AlbumActivity
import com.truedigital.features.tuned.presentation.artist.presenter.ArtistPresenter
import com.truedigital.features.tuned.presentation.artist.view.ArtistActivity
import com.truedigital.features.tuned.presentation.common.SimpleServiceConnection
import com.truedigital.features.tuned.presentation.playlist.presenter.PlaylistPresenter
import com.truedigital.features.tuned.presentation.playlist.view.PlaylistActivity
import com.truedigital.features.tuned.service.music.MusicPlayerService
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.visible
import com.truedigital.foundation.presentations.ViewModelFactory
import java.util.Locale
import javax.inject.Inject

class MusicSearchFragment : Fragment(R.layout.fragment_search_music) {

    companion object {
        val TAG = MusicSearchFragment::class.java.canonicalName as String
        private const val KEY_EXTRA_KEYWORD = "KEY_EXTRA_KEYWORD"
        private const val KEY_EXTRA_THEME = "KEY_EXTRA_THEME"

        fun newInstance(keyword: String, themeType: String = DARK.name) =
            MusicSearchFragment().apply {
                this.arguments = Bundle().apply {
                    this.putString(KEY_EXTRA_KEYWORD, keyword)
                    this.putString(KEY_EXTRA_THEME, themeType)
                }
            }
    }

    private val binding by viewBinding(FragmentSearchMusicBinding::bind)

    @Inject
    lateinit var analyticManager: AnalyticManager

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val musicSearchViewModel: MusicSearchViewModel by viewModels({ requireActivity() }) { viewModelFactory }
    private val musicTrackViewModel by viewModels<MusicTrackViewModel> { viewModelFactory }

    private val musicSearchTopMenuAdapter by lazy { MusicSearchTopMenuAdapter() }
    private val musicSearchContentAdapter by lazy { MusicSearchContentAdapter() }
    private val horizontalConcatAdapter by lazy { HorizontalConcatAdapter(musicSearchTopMenuAdapter) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        MusicComponent.getInstance().getInstanceComponent().inject(this)
        super.onViewCreated(view, savedInstanceState)

        initView()
        initListener()
        observeViewModel()
        resetDefaultTopMenuPosition()
        musicSearchViewModel.fetchTopMenu()
        musicSearchViewModel.resetLoadData()

        val keyword = arguments?.getString(KEY_EXTRA_KEYWORD).orEmpty()
        musicSearchViewModel.searchWithCurrentTopMenu(keyword)
    }

    private fun resetDefaultTopMenuPosition() {
        musicSearchTopMenuAdapter.updateActivePosition(0)
        musicSearchViewModel.updateCurrentTopMenuType(MusicType.ALL)
    }

    private fun observeViewModel() {
        with(musicSearchViewModel) {
            onMusicSearchTopMenuList().observe(viewLifecycleOwner) { topMenuList ->
                renderMusicTopMenu(topMenuList)
            }
            onShowError().observe(viewLifecycleOwner) {
                showErrorView()
            }
            onHideError().observe(viewLifecycleOwner) {
                hideErrorView()
            }
            onShowLoading().observe(viewLifecycleOwner) {
                binding.musicSearchProgressView.visible()
            }
            onHideLoading().observe(viewLifecycleOwner) {
                binding.musicSearchProgressView.gone()
            }
            onRenderMusicList().observe(viewLifecycleOwner) { musicList ->
                if (musicSearchViewModel.isLoadData()) {
                    musicSearchContentAdapter.submitList(musicList)
                }
            }
        }

        with(musicTrackViewModel) {
            onPlayTrack().observe(viewLifecycleOwner) { track ->
                context?.let { _context ->
                    val musicServiceConnection = object : SimpleServiceConnection {
                        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
                            if (binder is MusicPlayerService.PlayerBinder) {
                                binder.service.apply {
                                    startMusicForegroundService(_context.getMusicServiceIntent())
                                    playTracks(listOf(track), forceSequential = true)
                                }
                            }
                            _context.unbindService(this)
                        }
                    }
                    _context.bindServiceMusic(musicServiceConnection)
                }
            }

            onErrorPlayTrack().observe(viewLifecycleOwner) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.error_music_get_track_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun renderMusicTopMenu(topMenuList: List<TopMenuModel>?) {
        musicSearchTopMenuAdapter.submitList(topMenuList)
    }

    private fun initListener() {
        musicSearchContentAdapter.onMusicClicked = { musicModel ->
            when (musicModel) {
                is MusicSearchModel.MusicItemModel -> {
                    onClickMusicItem(musicModel)
                }

                is MusicSearchModel.MusicHeaderModel -> {
                    onClickMusicSeeMore(musicModel)
                }
            }
        }

        musicSearchTopMenuAdapter.onSearchTopMenuClicked =
            { topMenuModel: TopMenuModel, position: Int ->

                analyticManager.trackEvent(
                    HashMap<String, Any>().apply {
                        put(
                            MeasurementConstant.Key.KEY_EVENT_NAME,
                            MeasurementConstant.Event.EVENT_CLICK
                        )
                        put(
                            MeasurementConstant.Key.KEY_LINK_TYPE,
                            MeasurementConstant.Music.Event.EVENT_SEARCH_CATEGORY
                        )
                        put(
                            MeasurementConstant.Key.KEY_LINK_DESC,
                            topMenuModel.nameEn.orEmpty().lowercase(Locale.ENGLISH)
                        )
                    }
                )

                musicSearchTopMenuAdapter.updateActivePosition(position) {
                    musicSearchViewModel.onClickedTopMenu(topMenuModel.type)
                }
            }
    }

    private fun onClickMusicSeeMore(musicModel: MusicSearchModel.MusicHeaderModel) {
        musicSearchViewModel.onClickedTopMenu(musicModel.type)
        musicSearchTopMenuAdapter.updateActivePosition(musicSearchViewModel.getCurrentTopMenuPosition())
        horizontalConcatAdapter.updateActivePosition(musicSearchViewModel.getCurrentTopMenuPosition())
    }

    private fun onClickMusicItem(musicModel: MusicSearchModel.MusicItemModel) {
        when (musicModel.type) {
            MusicType.SONG -> {
                musicModel.id?.toIntOrNull()?.let { id ->
                    musicTrackViewModel.getTrack(id)
                }
            }

            MusicType.ARTIST -> {
                musicModel.id?.toIntOrNull()?.let { _id ->
                    activity?.startActivity(
                        Intent(activity, ArtistActivity::class.java).apply {
                            putExtra(ArtistPresenter.ARTIST_ID_KEY, _id)
                        }
                    )
                }
            }

            MusicType.ALBUM -> {
                musicModel.id?.toIntOrNull()?.let { _id ->
                    activity?.startActivity(
                        Intent(activity, AlbumActivity::class.java).apply {
                            putExtra(AlbumPresenter.ALBUM_ID_KEY, _id)
                        }
                    )
                }
            }

            MusicType.PLAYLIST -> {
                musicModel.id?.toIntOrNull()?.let { _id ->
                    activity?.startActivity(
                        Intent(activity, PlaylistActivity::class.java).apply {
                            putExtra(PlaylistPresenter.PLAYLIST_ID_KEY, _id)
                        }
                    )
                }
            }

            else -> Unit
        }
    }

    private fun initView() = with(binding) {
        arguments?.getString(KEY_EXTRA_THEME)?.let { themeName ->
            musicSearchViewModel.setTheme(getThemeType(themeName))
            val backgroundColor = when (themeName) {
                DARK.name -> R.color.bg_search_result
                else -> R.color.white
            }
            musicSearchParentLayout.setBackgroundColor(
                ContextCompat.getColor(requireContext(), backgroundColor)
            )
        }

        val concatAdapter = ConcatAdapter(horizontalConcatAdapter, musicSearchContentAdapter)

        musicSearchItemRecyclerView.apply {
            this.adapter = concatAdapter
            this.layoutManager = LinearLayoutManager(context)
        }
    }

    private fun showErrorView() = with(binding) {
        musicSearchErrorView.visible()
        musicSearchErrorView.setupView(
            drawableImage = R.drawable.ic_error_dialog,
            title = getString(R.string.error_music_search_title),
            detail = getString(R.string.error_music_search_description),
            titleStyle = R.style.TrueID_Header2_Black,
            detailStyle = R.style.TrueID_Small_Black
        )
    }

    private fun hideErrorView() = with(binding) {
        musicSearchErrorView.gone()
    }
}
