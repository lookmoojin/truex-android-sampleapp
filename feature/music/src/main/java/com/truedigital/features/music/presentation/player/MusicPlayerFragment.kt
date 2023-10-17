package com.truedigital.features.music.presentation.player

import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bumptech.glide.load.MultiTransformation
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.truedigital.common.share.analytics.measurement.AnalyticManager
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant
import com.truedigital.common.share.componentv3.data.SnackBarType
import com.truedigital.common.share.componentv3.extension.showSnackBar
import com.truedigital.common.share.nativeshare.NativeShareManagerImpl
import com.truedigital.common.share.nativeshare.constant.NativeShareConstant
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.music.constant.FavoriteType
import com.truedigital.features.music.injections.MusicComponent
import com.truedigital.features.music.presentation.musicshare.MusicShareViewModel
import com.truedigital.features.music.widget.favorite.MusicFavoriteViewModel
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.common.extensions.albumArtUri
import com.truedigital.features.tuned.common.extensions.artUri
import com.truedigital.features.tuned.common.extensions.artist
import com.truedigital.features.tuned.common.extensions.duration
import com.truedigital.features.tuned.common.extensions.mediaType
import com.truedigital.features.tuned.common.extensions.title
import com.truedigital.features.tuned.common.extensions.trackId
import com.truedigital.features.tuned.common.extensions.windowHeight
import com.truedigital.features.tuned.common.extensions.windowWidth
import com.truedigital.features.tuned.data.download.ImageManager
import com.truedigital.features.tuned.data.player.model.MediaType
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.databinding.FragmentMusicPlayerBinding
import com.truedigital.features.tuned.presentation.bottomsheet.ProductPickerType
import com.truedigital.features.tuned.presentation.bottomsheet.view.BottomSheetProductPicker
import com.truedigital.features.tuned.presentation.productlist.view.ProductListActivity
import com.truedigital.foundation.extension.loadCustomSizeCrossFadeWithCallback
import com.truedigital.foundation.extension.loadMultipleTransformation
import com.truedigital.foundation.presentations.ViewModelFactory
import com.truedigital.share.data.prasarn.constant.PrasarnConstant
import com.truedigital.share.data.prasarn.manager.PrasarnManager
import jp.wasabeef.glide.transformations.BlurTransformation
import timber.log.Timber
import javax.inject.Inject

class MusicPlayerFragment : Fragment(R.layout.fragment_music_player) {

    companion object {
        private const val BLUR_RADIUS = 25
        private const val BLUR_SAMPLING = 3
        private const val THUMB_SIZE = 600
        private const val TAKE_NUMBER = 100
    }

    @Inject
    lateinit var analyticManager: AnalyticManager

    @Inject
    lateinit var mediaSession: MediaSessionCompat

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var imageManager: ImageManager

    @Inject
    lateinit var prasarnManager: PrasarnManager

    private val musicPlayerNavigationViewModel: MusicPlayerNavigationViewModel by viewModels { viewModelFactory }

    private val binding: FragmentMusicPlayerBinding by viewBinding(FragmentMusicPlayerBinding::bind)
    private val musicServiceConnectionViewModel: MusicServiceConnectionViewModel by viewModels { viewModelFactory }
    private val musicPlayerAdsViewModel: MusicPlayerAdsViewModel by viewModels { viewModelFactory }
    private val musicShareViewModel: MusicShareViewModel by viewModels { viewModelFactory }
    private val musicFavoriteViewModel: MusicFavoriteViewModel by viewModels { viewModelFactory }
    private val musicPlayerViewModel: MusicPlayerViewModel by activityViewModels { viewModelFactory }
    private val musicPlayerStateViewModel: MusicPlayerStateViewModel by activityViewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        MusicComponent.getInstance().getInstanceComponent().inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBackPress()
        observeViewModel()
        initOnClick()
        initListener()
    }

    override fun onStart() {
        super.onStart()
        musicServiceConnectionViewModel.register()
    }

    override fun onResume() {
        super.onResume()
        updateState()
        musicServiceConnectionViewModel.onResumePlayer()
        view?.apply {
            isFocusableInTouchMode = true
            requestFocus()
        }
    }

    override fun onStop() {
        super.onStop()
        musicServiceConnectionViewModel.unRegister()
    }

    private fun updateState() = with(binding) {
        if (musicServiceConnectionViewModel.isPlayerInActiveState().not()) {
            playPauseButton.updateState()
            repeatButton.updateState()
            shuffleButton.updateState()
        }
    }

    private fun setupBackPress() {
        view?.apply {
            setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_BACK &&
                    binding.root.isExpand()
                ) {
                    collapsedPlayer()
                    return@setOnKeyListener true
                } else {
                    return@setOnKeyListener false
                }
            }
        }
    }

    private fun initOnClick() = with(binding) {
        previousButton.setOnClickListener {
            musicPlayerAdsViewModel.actionPrevious(
                musicServiceConnectionViewModel.isFirstTrack(),
                musicServiceConnectionViewModel.getPlayPosition()
            )
            musicPlayerViewModel.actionPrevious()
        }
        nextButton.setOnClickListener {
            musicPlayerAdsViewModel.actionNext(
                musicServiceConnectionViewModel.isLastTrack()
            )
            musicPlayerViewModel.actionNext()
        }
        closeButton.setOnClickListener {
            binding.root.closePlayerView()
            musicPlayerViewModel.actionClose()
            musicServiceConnectionViewModel.clearLastThumbUrl()
        }
        collapseButton.setOnClickListener {
            collapsedPlayer()
        }
        queueButton.setOnClickListener {
            musicPlayerNavigationViewModel.navigateToPlayerQueue(
                musicServiceConnectionViewModel.getRepeatMode(),
                musicServiceConnectionViewModel.getShuffleMode(),
                requireActivity()
            )
        }
        shareButton.setOnClickListener {
            musicServiceConnectionViewModel.navigateToShare()
        }
        moreButton.setOnClickListener {
            showMore(musicServiceConnectionViewModel.getTrackId())
        }
    }

    private fun initListener() = with(binding) {
        musicSeekBarWidget.unableReplayStateListener = {
            binding.playPauseButton.setNotReplayState()
        }
        root.onPlayerStateChange = { isExpand ->
            musicPlayerStateViewModel.onPlayerStateChange(isExpand)
            musicPlayerViewModel.handlerBottomMarginMiniPlayer(!isExpand)
        }
    }

    private fun observeViewModel() {
        with(musicPlayerViewModel) {
            onAddMarginBottom().observe(viewLifecycleOwner) {
                if (binding.root.isExpand().not()) {
                    updateBottomMarginMiniPlayer(true)
                }
            }
            onHideMusicPlayer().observe(viewLifecycleOwner) {
                if (binding.root.isExpand().not()) {
                    hidePlayer()
                }
            }
            onListenScope().observe(viewLifecycleOwner) {
                if (binding.root.isExpand().not()) {
                    updateBottomMarginMiniPlayer(false)
                }
            }
            onCollapsePlayer().observe(viewLifecycleOwner) {
                if (binding.root.isExpand()) {
                    collapsedPlayer()
                }
            }
            onPausePlayer().observe(viewLifecycleOwner) {
                musicServiceConnectionViewModel.handlerPausePlayer()
            }
            onLoadTDGBackgroundImage().observe(viewLifecycleOwner) {
                loadTDGBackgroundImage(it)
            }
            onLoadTunedBackgroundImage().observe(viewLifecycleOwner) {
                loadTunedBackgroundImage(it)
            }
            onFavoriteAddSuccess().observe(viewLifecycleOwner) {
                binding.root.showSnackBar(R.string.added_to_favorite, SnackBarType.SUCCESS)
            }
            onFavoriteRemoveSuccess().observe(viewLifecycleOwner) {
                binding.root.showSnackBar(R.string.removed_to_favorite, SnackBarType.SUCCESS)
            }
            onFavoriteAddError().observe(viewLifecycleOwner) {
                binding.root.showSnackBar(R.string.error_added_to_favorite, SnackBarType.ERROR)
            }
            onFavoriteRemoveError().observe(viewLifecycleOwner) {
                binding.root.showSnackBar(R.string.error_removed_to_favorite, SnackBarType.ERROR)
            }
        }
        with(musicServiceConnectionViewModel) {
            onUpdateView().observe(viewLifecycleOwner) { mediaMetadataCompat ->
                musicPlayerAdsViewModel.validateAds(mediaMetadataCompat)
                updateView(mediaMetadataCompat)
            }
            onHidePlayer().observe(viewLifecycleOwner) {
                hidePlayer()
            }
            onUpdateSeekBar().observe(viewLifecycleOwner) { mediaControllerCompat ->
                binding.musicSeekBarWidget.update(mediaControllerCompat)
            }
            onPlayingState().observe(viewLifecycleOwner) {
                binding.playPauseButton.setPlayState()
            }
            onPauseState().observe(viewLifecycleOwner) {
                binding.playPauseButton.setPauseState()
            }
            onReplayState().observe(viewLifecycleOwner) { duration ->
                if (musicServiceConnectionViewModel.isPlayerErrorState()) {
                    binding.playPauseButton.setReplayState()
                    binding.musicSeekBarWidget.replay(duration)
                }
            }
            onShareSong().observe(viewLifecycleOwner) { trackId ->
                musicShareViewModel.shareSong(trackId)
            }
            onTrackFAShareSong().observe(viewLifecycleOwner) { metaData ->
                firebaseAnalyticSelectShare(metaData)
            }
            onCanPausePlayer().observe(viewLifecycleOwner) { isPlayerActiveState ->
                musicPlayerViewModel.actionPause(isPlayerActiveState)
            }
        }
        with(musicShareViewModel) {
            onOpenNativeShare().observe(viewLifecycleOwner) { url ->
                context?.let { _context ->
                    NativeShareManagerImpl(_context).onSharePress(
                        shortUrl = url,
                        title = "",
                        imageUrl = "",
                        NativeShareConstant.DYNAMIC_LINK
                    )
                }
            }
        }
        with(musicPlayerAdsViewModel) {
            onShowAds().observe(viewLifecycleOwner) {
                loadInterstitialAds(it)
            }
        }
        with(musicFavoriteViewModel) {
            onFavSong().observe(viewLifecycleOwner) {
                binding.favoriteButton.updateFavoriteState(it)
            }
            onAddFavToast().observe(viewLifecycleOwner) {
                binding.root.showSnackBar(R.string.added_to_favorite, SnackBarType.SUCCESS)
            }
            onRemoveFavToast().observe(viewLifecycleOwner) {
                binding.root.showSnackBar(R.string.removed_to_favorite, SnackBarType.SUCCESS)
            }
            onFavAddErrorToast().observe(viewLifecycleOwner) {
                binding.root.showSnackBar(R.string.error_added_to_favorite, SnackBarType.ERROR)
            }
            onFavRemoveErrorToast().observe(viewLifecycleOwner) {
                binding.root.showSnackBar(R.string.error_removed_to_favorite, SnackBarType.ERROR)
            }
        }
    }

    private fun updateBottomMarginMiniPlayer(isAdd: Boolean) = with(binding) {
        val activeState = musicServiceConnectionViewModel.isPlayerInActiveState().not()
        root.apply {
            musicPlayerViewModel.setVisibilityPlayerStatus(activeState)
            setMiniPlayerVisibility(activeState, onVisibilityViewChange = { isVisible ->
                musicPlayerStateViewModel.setVisibilityPlayerStatus(isVisible)
            })
            addBottomMarginMiniPlayer(isAdd)
            transitionToStart()
            refreshView()
        }
    }

    private fun updateView(metadata: MediaMetadataCompat?) = with(binding) {
        metadata?.let { _metadata ->
            if (musicPlayerViewModel.getMiniPlayerVisible()) {
                musicPlayerViewModel.setVisibilityPlayerStatus(true)
                root.setMiniPlayerVisibility(true, onVisibilityViewChange = { isVisible ->
                    musicPlayerStateViewModel.setVisibilityPlayerStatus(isVisible)
                })
            }

            root.setFullPlayerStyle(_metadata.mediaType != MediaType.RADIO)

            loadImage(_metadata)
            miniSongNameTextView.text = _metadata.title
            fullSongNameTextView.text = _metadata.title
            miniArtistNameTextView.text = _metadata.artist
            fullArtistNameTextView.text = _metadata.artist

            musicSeekBarWidget.start(_metadata.duration)
            repeatButton.updateState()
            shuffleButton.updateState()
            favoriteButton.updateState(_metadata.trackId ?: 0, FavoriteType.TRACK)
        } ?: run {
            hidePlayer()
        }
    }

    private fun loadImage(metadata: MediaMetadataCompat) = with(binding) {
        val imageUri = when (metadata.mediaType) {
            MediaType.PRESET -> metadata.artUri
            else -> metadata.albumArtUri
        }

        musicPlayerViewModel.handlerLoadBackgroundImage(imageUri, metadata.mediaType)
        thumbnailImageView.loadCustomSizeCrossFadeWithCallback(
            context = root.context,
            currentUrl = imageUri,
            lastUrl = musicServiceConnectionViewModel.getLastThumbUrl(),
            placeholder = R.drawable.placeholder_new_trueid_white_square,
            scaleType = ImageView.ScaleType.CENTER_CROP,
            width = THUMB_SIZE,
            height = THUMB_SIZE,
            onError = {
                thumbnailImageView.setImageResource(R.drawable.placeholder_new_trueid_white_square)
            }
        )
        musicServiceConnectionViewModel.saveLastThumbUrl(imageUri.orEmpty())
    }

    private fun loadTunedBackgroundImage(imageUri: String?) = with(binding) {
        context?.let { _context ->
            imageManager.init(_context)
                .load(imageUri.orEmpty())
                .options(
                    width = _context.windowWidth / 2,
                    height = _context.windowHeight / 2,
                    filters = arrayOf("blur(50, 10)", "saturation(1.2)")
                )
                .intoBitmap { bitmap ->
                    val startDrawable = backgroundImageView.drawable
                        ?: ColorDrawable(Color.TRANSPARENT)
                    val transitionDrawable = TransitionDrawable(
                        arrayOf(
                            startDrawable,
                            BitmapDrawable(resources, bitmap)
                        )
                    )
                    transitionDrawable.isCrossFadeEnabled = true
                    backgroundImageView.setImageDrawable(transitionDrawable)
                    transitionDrawable.startTransition(0)
                }
        }
    }

    private fun loadTDGBackgroundImage(imageUri: String) = with(binding) {
        backgroundImageView.loadMultipleTransformation(
            context = requireContext(),
            url = imageUri,
            transformations = MultiTransformation(BlurTransformation(BLUR_RADIUS, BLUR_SAMPLING))
        )
    }

    private fun collapsedPlayer() {
        binding.root.transitionToStart()
    }

    private fun hidePlayer() = with(binding) {
        root.apply {
            musicPlayerViewModel.setVisibilityPlayerStatus(false)
            transitionToStart()
            setMiniPlayerVisibility(false, onVisibilityViewChange = { isVisible ->
                musicPlayerStateViewModel.setVisibilityPlayerStatus(isVisible)
            })
            refreshView()
        }
    }

    private fun firebaseAnalyticSelectShare(metaData: MediaMetadataCompat) {
        val firebaseAnalyticsHashMap = HashMap<String, Any>().apply {
            put(
                MeasurementConstant.Key.KEY_EVENT_NAME,
                MeasurementConstant.Event.EVENT_SOCIAL_SHARE
            )
            put(MeasurementConstant.Key.KEY_CMS_ID, metaData.trackId.toString())
            put(MeasurementConstant.Key.KEY_TITLE, metaData.title.toString().take(TAKE_NUMBER))
            put(MeasurementConstant.Key.KEY_CONTENT_TYPE, "music")
        }
        analyticManager.trackEvent(firebaseAnalyticsHashMap)
    }

    private fun showMore(trackId: Int) {
        BottomSheetProductPicker(requireContext()) {
            itemType = ProductPickerType.SONG_PLAYER
            itemId = trackId
            fragmentManager = activity?.supportFragmentManager
            onInCollectionStatusChanged = { isInCollection, product ->
                if (context is ProductListActivity) {
                    (context as ProductListActivity).updateFavouriteItem(
                        isInCollection,
                        product as Track
                    )
                }
            }
            onFavoriteItemClick = { isFavourited, isSuccess ->
                musicPlayerViewModel.onFavouriteSelect(isFavourited, isSuccess)
            }
        }.show()
    }

    private fun loadInterstitialAds(adsId: String) {
        val ppId = prasarnManager.getPPID()
        AdManagerInterstitialAd.load(
            requireActivity(),
            adsId,
            AdManagerAdRequest
                .Builder()
                .setPublisherProvidedId(ppId)
                .addCustomTargeting(PrasarnConstant.PRASARN_ID_KEY, ppId)
                .build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Timber.e("error music player interstitial ads : ${adError.message}")
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    interstitialAd.show(requireActivity())
                }
            }
        )
    }
}
