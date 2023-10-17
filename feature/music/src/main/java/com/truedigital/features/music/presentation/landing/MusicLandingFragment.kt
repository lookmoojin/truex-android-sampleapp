package com.truedigital.features.music.presentation.landing

import android.content.ComponentName
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.truedigital.common.share.analytics.measurement.AnalyticManager
import com.truedigital.common.share.analytics.measurement.base.platform.PlatformAnalyticModel
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant
import com.truedigital.common.share.componentv3.data.SnackBarType
import com.truedigital.common.share.componentv3.extension.showSnackBar
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.music.domain.landing.model.MusicForYouItemModel
import com.truedigital.features.music.domain.landing.model.MusicForYouShelfModel
import com.truedigital.features.music.domain.landing.model.MusicLandingFASelectContentModel
import com.truedigital.features.music.injections.MusicComponent
import com.truedigital.features.music.presentation.landing.adapter.MusicForYouShelfAdapter
import com.truedigital.features.music.presentation.player.MusicPlayerViewModel
import com.truedigital.features.music.presentation.search.MusicTrackViewModel
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.common.extensions.bindServiceMusic
import com.truedigital.features.tuned.common.extensions.getMusicServiceIntent
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.databinding.FragmentMusicLandingBinding
import com.truedigital.features.tuned.presentation.common.SimpleServiceConnection
import com.truedigital.features.tuned.service.music.MusicPlayerService
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.visible
import com.truedigital.foundation.presentations.ViewModelFactory
import javax.inject.Inject

class MusicLandingFragment : Fragment(R.layout.fragment_music_landing) {

    private val binding by viewBinding(FragmentMusicLandingBinding::bind)

    @Inject
    lateinit var analyticManager: AnalyticManager

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val musicTrackViewModel: MusicTrackViewModel by viewModels { viewModelFactory }
    private val musicPlayerViewModel: MusicPlayerViewModel by viewModels({ requireActivity() }) { viewModelFactory }
    private val musicLandingActionViewModel: MusicLandingActionViewModel by viewModels({ requireActivity() }) { viewModelFactory }
    private val musicLandingViewModel: MusicLandingViewModel by viewModels { viewModelFactory }
    private val musicLandingTrackFAViewModel: MusicLandingTrackFAViewModel by viewModels { viewModelFactory }

    @Inject
    lateinit var musicForYouShelfAdapterFactory: MusicForYouShelfAdapter.MusicForYouShelfAdapterFactory

    private val musicForYouShelfAdapter: MusicForYouShelfAdapter by lazy {
        musicForYouShelfAdapterFactory.create(onItemClicked, onSeeAllClicked, onScrollShelves)
    }

    private val onItemClicked: (MusicForYouItemModel, MusicLandingFASelectContentModel) -> Unit =
        { itemModel, selectContentModel ->
            musicLandingViewModel.performClickItem(itemModel)
            musicLandingTrackFAViewModel.trackFASelectContent(itemModel, selectContentModel)
        }
    private val onSeeAllClicked: (MusicForYouShelfModel) -> Unit = { model ->
        musicLandingViewModel.performClickSeeAll(model)
    }
    private val onScrollShelves: (MusicForYouShelfModel) -> Unit = { shelfModel ->
        musicLandingTrackFAViewModel.onScrollShelves(shelfId.orEmpty(), shelfModel)
    }

    private var parentLayout: View? = null
    private var shelfId: String? = null
    private var shelfSlug: String? = null

    companion object {
        private const val CACHE_SIZE = 20
        const val KEY_CONTENT_ID = "contentId"
        const val KEY_CONTENT_TYPE = "contentType"
        const val KEY_SHELF_ID = "shelfId"
        const val KEY_SHELF_SLUG = "shelfSlug"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        MusicComponent.getInstance().inject(this)
        super.onCreate(savedInstanceState)

        shelfId = arguments?.getString(KEY_SHELF_ID, "")
        shelfSlug = arguments?.getString(KEY_SHELF_SLUG, "")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentLayout = activity?.findViewById(android.R.id.content)
        observeViewModel()
        navigateWithBundle()
        initRecyclerView()

        musicLandingViewModel.loadData(shelfId, shelfSlug)
    }

    override fun onResume() {
        super.onResume()
        analyticManager.trackScreen(
            PlatformAnalyticModel().apply {
                screenClass = MusicLandingFragment::class.java.simpleName
                screenName = MeasurementConstant.Music.ScreenName.SCREEN_NAME_LISTEN_SEE_MORE
            },
        )
    }

    override fun onDestroyView() {
        arguments?.clear()
        super.onDestroyView()
    }

    private fun initRecyclerView() = with(binding) {
        shelfRecyclerView.adapter = musicForYouShelfAdapter
        shelfRecyclerView.setItemViewCacheSize(CACHE_SIZE)
    }

    private fun observeViewModel() {
        with(musicLandingViewModel) {
            onShowLoading().observe(viewLifecycleOwner) {
                binding.musicLandingLoadingGroup.visible()
            }
            onShowError().observe(viewLifecycleOwner) {
                showErrorView()
            }
            onShowRadioError().observe(viewLifecycleOwner) {
                showRadioErrorToast()
            }
            onPausePlayer().observe(viewLifecycleOwner) {
                musicPlayerViewModel.pausePlayer()
            }
            onShowShelf().observe(viewLifecycleOwner) { shelfList ->
                showShelf(shelfList)
            }
            onGetTrack().observe(viewLifecycleOwner) { trackId ->
                musicTrackViewModel.getTrack(trackId)
            }
            onPlayTrackList().observe(viewLifecycleOwner) { trackList ->
                playTrackList(trackList, musicLandingViewModel.onTrackPosition().value)
            }
            onUpdateTrackList().observe(viewLifecycleOwner) { trackList ->
                updateTrackQueue(trackList)
            }
            onScrollToTopPage().observe(viewLifecycleOwner) {
                binding.shelfRecyclerView.smoothScrollToPosition(0)
            }
            onPlayRadio().observe(viewLifecycleOwner) { radio ->
                playRadio(radio)
            }
            onRenderContentBySlug().observe(viewLifecycleOwner) {
                musicLandingActionViewModel.setActiveTopNav(it)
            }

            setRouterSecondaryToNavController(findNavController())
        }

        with(musicTrackViewModel) {
            onPlayTrack().observe(viewLifecycleOwner) { track ->
                playSong(track)
            }
            onErrorPlayTrack().observe(viewLifecycleOwner) {
                showTrackErrorToast()
            }
        }
    }

    private fun navigateWithBundle() {
        arguments?.let { bundle ->
            val contentId = bundle.getString(KEY_CONTENT_ID, "")
            val contentType = bundle.getString(KEY_CONTENT_TYPE, "")
            musicPlayerViewModel.setCollapsePlayer()
            musicLandingViewModel.handlerNavigateWithBundle(contentId, contentType)
        }
    }

    private fun playSong(track: Track) {
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

    private fun playRadio(radio: MusicForYouItemModel.RadioShelfItem) {
        context?.let { _context ->
            val musicServiceConnection = object : SimpleServiceConnection {
                override fun onServiceConnected(name: ComponentName, binder: IBinder) {
                    if (binder is MusicPlayerService.PlayerBinder) {
                        binder.service.apply {
                            startMusicForegroundService(_context.getMusicServiceIntent())
                            playRadio(radio)
                        }
                    }
                    _context.unbindService(this)
                }
            }
            _context.bindServiceMusic(musicServiceConnection)
        }
    }

    private fun playTrackList(trackList: List<Track>, startIndex: Int? = null) {
        context?.let { _context ->
            val musicServiceConnection = object : SimpleServiceConnection {
                override fun onServiceConnected(name: ComponentName, binder: IBinder) {
                    if (binder is MusicPlayerService.PlayerBinder) {
                        binder.service.apply {
                            startMusicForegroundService(_context.getMusicServiceIntent())
                            playTracks(
                                trackList,
                                startIndex = startIndex,
                                forceSequential = true,
                            )
                        }
                    }
                    _context.unbindService(this)
                }
            }
            _context.bindServiceMusic(musicServiceConnection)
        }
    }

    private fun updateTrackQueue(trackList: List<Track>) {
        val musicServiceConnection = object : SimpleServiceConnection {
            override fun onServiceConnected(name: ComponentName, binder: IBinder) {
                val musicPlayerService = (binder as? MusicPlayerService.PlayerBinder)?.service
                musicPlayerService?.updateQueue(trackList)
                context?.unbindService(this)
            }
        }
        context?.bindServiceMusic(musicServiceConnection)
    }

    private fun showErrorView() = with(binding) {
        musicLandingErrorView.setupView(
            drawableImage = R.drawable.music_ic_error,
            title = resources.getString(R.string.error_music_title),
            detail = resources.getString(R.string.error_music_landing_description),
            titleStyle = R.style.TrueID_Title_Bold_Black,
            detailStyle = R.style.TrueID_Header3_Black,
        )
        musicLandingErrorView.visible()
        musicLandingLoadingGroup.gone()
        shelfRecyclerView.gone()
    }

    private fun showShelf(musicForYouShelfList: MutableList<MusicForYouShelfModel>) =
        with(binding) {
            musicLandingErrorView.gone()
            musicLandingLoadingGroup.gone()
            shelfRecyclerView.visible()
            musicForYouShelfAdapter.addItems(musicForYouShelfList)
        }

    private fun showTrackErrorToast() {
        Toast.makeText(
            requireContext(),
            getString(R.string.error_music_get_track_error),
            Toast.LENGTH_SHORT,
        ).show()
    }

    private fun showRadioErrorToast() {
        parentLayout?.showSnackBar(R.string.play_radio_error, SnackBarType.ERROR)
    }
}
