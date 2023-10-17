package com.truedigital.features.music.presentation.myplaylist

import android.content.ComponentName
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import com.truedigital.common.share.componentv3.data.SnackBarType
import com.truedigital.common.share.componentv3.extension.getSavedStateHandle
import com.truedigital.common.share.componentv3.extension.showSnackBar
import com.truedigital.component.dialog.TrueIDProgressDialog
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.listens.share.constant.MusicConstant.Key.ADD_SONG_SUCCESS
import com.truedigital.features.music.domain.warning.model.MusicWarningModel
import com.truedigital.features.music.domain.warning.model.MusicWarningType
import com.truedigital.features.music.injections.MusicComponent
import com.truedigital.features.music.presentation.myplaylist.adapter.MyPlaylistHeaderAdapter
import com.truedigital.features.music.presentation.myplaylist.adapter.MyPlaylistTrackAdapter
import com.truedigital.features.music.presentation.warning.MusicWarningBottomSheetDialog
import com.truedigital.features.music.presentation.warning.MusicWarningBottomSheetDialog.Companion.IS_CONFIRM_BUTTON_CLICKED
import com.truedigital.features.music.presentation.warning.MusicWarningBottomSheetDialog.Companion.MUSIC_WARNING_DIALOG_REQUEST_CODE
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.common.extensions.bindServiceMusic
import com.truedigital.features.tuned.common.extensions.getMusicServiceIntent
import com.truedigital.features.tuned.data.download.ImageManager
import com.truedigital.features.tuned.data.product.model.Product
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.databinding.FragmentMyPlaylistBinding
import com.truedigital.features.tuned.presentation.bottomsheet.PickerOptions
import com.truedigital.features.tuned.presentation.bottomsheet.ProductPickerType
import com.truedigital.features.tuned.presentation.bottomsheet.view.BottomSheetProductPicker
import com.truedigital.features.tuned.presentation.common.SimpleServiceConnection
import com.truedigital.features.tuned.service.music.MusicPlayerService
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.visible
import com.truedigital.foundation.presentations.ViewModelFactory
import javax.inject.Inject

class MyPlaylistFragment : Fragment(R.layout.fragment_my_playlist) {

    companion object {
        const val PLAYLIST_ID_SLUG = "playlistId"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var imageManager: ImageManager

    private val myPlaylistViewModel: MyPlaylistViewModel by viewModels { viewModelFactory }
    private val myPlaylistImageViewModel: MyPlaylistImageViewModel by viewModels { viewModelFactory }
    private val myPlaylistTrackViewModel by viewModels<MyPlaylistTrackViewModel> { viewModelFactory }

    private val binding by viewBinding(FragmentMyPlaylistBinding::bind)

    private var progressDialog: TrueIDProgressDialog? = null
    private var bottomSheetProductPicker: BottomSheetProductPicker? = null

    @Inject
    lateinit var myPlaylistHeaderAdapterFactory: MyPlaylistHeaderAdapter.MyPlaylistHeaderAdapterFactory

    private val myPlaylistHeaderAdapter: MyPlaylistHeaderAdapter by lazy {
        myPlaylistHeaderAdapterFactory.create(onAddSongClicked, onShuffleClicked)
    }

    @Inject
    lateinit var myPlaylistTrackAdapterFactory: MyPlaylistTrackAdapter.MyPlaylistTrackAdapterFactory

    private val myPlaylistTrackAdapter: MyPlaylistTrackAdapter by lazy {
        myPlaylistTrackAdapterFactory.create(onItemClicked, onSeeMoreTrackClicked)
    }

    private val onItemClicked: (Int) -> Unit = { position ->
        myPlaylistViewModel.playTracks(position = position, isShuffle = false)
    }

    private val onShuffleClicked: () -> Unit = {
        myPlaylistViewModel.playTracks(isShuffle = true)
    }

    private val onAddSongClicked: () -> Unit = {
        playlistId?.let { playlistId ->
            myPlaylistViewModel.navigateToAddSong(playlistId)
        }
    }

    private val onSeeMoreTrackClicked: (Track) -> Unit = { track ->
        openMoreDialog(type = ProductPickerType.MY_PLAYLIST_SONG, product = track)
    }

    private val playlistId: Int?
        get() = arguments?.getInt(PLAYLIST_ID_SLUG)

    override fun onCreate(savedInstanceState: Bundle?) {
        MusicComponent.getInstance().inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MusicComponent.getInstance().getInstanceComponent().inject(this)
        setupToolbar()
        initBackPressed()
        initRecyclerView()
        observeMyPlaylistViewModel()
        observeTrackViewModel()
        observeImageViewModel()
        observeCallback()

        playlistId?.let {
            myPlaylistViewModel.loadMyPlaylist(it)
        } ?: run {
            showPlaylistErrorDialog()
        }
    }

    override fun onStart() {
        super.onStart()
        myPlaylistTrackViewModel.register()
    }

    override fun onResume() {
        super.onResume()
        if (!myPlaylistTrackAdapter.currentList.isNullOrEmpty()) {
            myPlaylistTrackViewModel.updateCurrentPlayingTrack()
        }
    }

    override fun onStop() {
        super.onStop()
        myPlaylistTrackViewModel.unRegister()
    }

    private fun setupToolbar() = with(binding) {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_gray)
        toolbar.navigationContentDescription = getString(R.string.myplaylist_button_back)
        toolbar.navigationIcon?.colorFilter = PorterDuffColorFilter(
            Color.BLACK,
            PorterDuff.Mode.SRC_ATOP
        )
        toolbar.inflateMenu(R.menu.menu_more)
        toolbar.setNavigationOnClickListener {
            if (myPlaylistViewModel.getIsLoadingState().not()) {
                findNavController().popBackStack()
            }
        }
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_more -> {
                    openMoreDialog(type = ProductPickerType.PLAYLIST_OWNER, id = playlistId)
                    true
                }
                else -> false
            }
        }
    }

    private fun initRecyclerView() {
        val concatAdapter = ConcatAdapter(myPlaylistHeaderAdapter, myPlaylistTrackAdapter)
        binding.myPlaylistRecyclerView.adapter = concatAdapter
    }

    private fun observeMyPlaylistViewModel() {
        with(myPlaylistViewModel) {
            onRenderMyPlaylist().observe(viewLifecycleOwner) {
                myPlaylistHeaderAdapter.submitList(it)
            }
            onRenderMyPlaylistTrack().observe(viewLifecycleOwner) { trackList ->
                myPlaylistTrackAdapter.updateCurrentPlayingTrack(
                    trackList,
                    myPlaylistTrackViewModel.getCurrentPlayingTrackId()
                )
            }
            onShowPlaylistErrorDialog().observe(viewLifecycleOwner) {
                showPlaylistErrorDialog()
            }
            onShowConfirmDialog().observe(viewLifecycleOwner) {
                showConfirmDialog(it)
            }
            onShowErrorDialog().observe(viewLifecycleOwner) {
                showErrorDialog(it)
            }
            onHideLoading().observe(viewLifecycleOwner) {
                hideLoading()
            }
            onShowLoading().observe(viewLifecycleOwner) {
                showLoading()
            }
            onShowLoadingDialog().observe(viewLifecycleOwner) {
                context?.let { mContext ->
                    if (progressDialog?.isShowing == true) {
                        progressDialog?.dismiss()
                    }
                    progressDialog = TrueIDProgressDialog(mContext).apply {
                        setCancelable(false)
                        show()
                    }
                }
            }
            onHideLoadingDialog().observe(viewLifecycleOwner) {
                if (progressDialog?.isShowing == true) {
                    progressDialog?.dismiss()
                }
            }
            onPlayTracks().observe(viewLifecycleOwner) { (trackList, index, isShuffle) ->
                playTrackList(trackList, index, isShuffle)
            }
            onAddToQueue().observe(viewLifecycleOwner) { trackList ->
                addToQueue(trackList)
            }
            onShowAddToQueueFailToast().observe(viewLifecycleOwner) {
                binding.root.showSnackBar(R.string.add_to_queue_track_empty, SnackBarType.ERROR)
            }
            onShowAddToQueueSuccessToast().observe(viewLifecycleOwner) {
                binding.root.showSnackBar(R.string.add_to_queue_success, SnackBarType.SUCCESS)
            }
            onCloseMyPlaylist().observe(viewLifecycleOwner) {
                binding.root.showSnackBar(R.string.delete_my_playlist_success, SnackBarType.SUCCESS)
                findNavController().popBackStack()
            }
            onDismissMoreDialog().observe(viewLifecycleOwner) {
                bottomSheetProductPicker?.dismiss()
            }
            onGenerateGridImage().observe(viewLifecycleOwner) {
                myPlaylistImageViewModel.generateGridImage(it)
            }
            onNewTracks().observe(viewLifecycleOwner) {
                addTracksToQueue(it)
            }
            onShowRemoveTrackSuccess().observe(viewLifecycleOwner) {
                binding.root.showSnackBar(
                    R.string.my_playlist_remove_song_success,
                    SnackBarType.SUCCESS
                )
            }
            onShowRemoveTrackFail().observe(viewLifecycleOwner) {
                binding.root.showSnackBar(R.string.my_playlist_remove_song_fail, SnackBarType.ERROR)
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
    }

    private fun observeImageViewModel() {
        with(myPlaylistImageViewModel) {
            onDisplayCoverImage().observe(viewLifecycleOwner) { (imageUrl, filterList) ->
                saveImage(imageUrl, filterList)
            }
            onSaveCompleted().observe(viewLifecycleOwner) {
                myPlaylistViewModel.enableLoading(false)
            }
        }
    }

    private fun observeTrackViewModel() {
        with(myPlaylistTrackViewModel) {
            onTrackChange().observe(viewLifecycleOwner) { trackId ->
                myPlaylistTrackAdapter.updateCurrentPlayingTrack(
                    myPlaylistTrackAdapter.currentList,
                    trackId
                )
            }
        }
    }

    private fun observeCallback() {
        findNavController().getSavedStateHandle<Boolean>(ADD_SONG_SUCCESS)
            ?.observe(viewLifecycleOwner) { isSuccess ->
                if (isSuccess) {
                    playlistId?.let { playlistId ->
                        myPlaylistViewModel.reloadMyPlaylistTrack(playlistId)
                        binding.root.showSnackBar(R.string.add_song_toast, SnackBarType.SUCCESS)
                    } ?: run {
                        showPlaylistErrorDialog()
                    }
                }
            }
    }

    private fun hideLoading() = with(binding) {
        myPlaylistLoadingProgress.gone()
        myPlaylistRecyclerView.visible()
    }

    private fun showLoading() = with(binding) {
        myPlaylistLoadingProgress.visible()
        myPlaylistRecyclerView.gone()
    }

    private fun showPlaylistErrorDialog() {
        val musicWarningModel = MusicWarningModel(
            title = R.string.my_playlist_error_title_dialog,
            description = R.string.my_playlist_error_message_dialog,
            confirmText = R.string.ok,
            type = MusicWarningType.FORCE_ANSWER
        )
        showErrorDialog(musicWarningModel)
    }

    private fun showConfirmDialog(musicWarningModel: MusicWarningModel) {
        showMusicWarningDialog(musicWarningModel, onConfirmClicked = {
            if (bottomSheetProductPicker?.itemType == ProductPickerType.PLAYLIST_OWNER) {
                myPlaylistViewModel.deletePlaylist(playlistId)
                bottomSheetProductPicker?.dismiss()
            } else {
                findNavController().popBackStack()
            }
        })
    }

    private fun showErrorDialog(musicWarningModel: MusicWarningModel) {
        showMusicWarningDialog(musicWarningModel)
    }

    private fun showMusicWarningDialog(
        musicWarningModel: MusicWarningModel,
        onConfirmClicked: (() -> Unit)? = null
    ) {
        MusicWarningBottomSheetDialog.newInstance(musicWarningModel).let { dialog ->
            dialog.show(requireActivity().supportFragmentManager, MusicWarningBottomSheetDialog.TAG)
            dialog.setFragmentResultListener(MUSIC_WARNING_DIALOG_REQUEST_CODE) { _, result ->
                result.getBoolean(IS_CONFIRM_BUTTON_CLICKED).let { isConfirmButtonClicked ->
                    if (isConfirmButtonClicked) {
                        onConfirmClicked?.invoke()
                    }
                }
            }
        }
    }

    private fun playTrackList(trackList: List<Track>, startIndex: Int? = null, isShuffle: Boolean) {
        context?.let { _context ->
            val musicServiceConnection = object : SimpleServiceConnection {
                override fun onServiceConnected(name: ComponentName, binder: IBinder) {
                    if (binder is MusicPlayerService.PlayerBinder) {
                        binder.service.apply {
                            startMusicForegroundService(_context.getMusicServiceIntent())
                            playTracks(
                                tracks = trackList,
                                playerSource = myPlaylistViewModel.getPlaylistPlayerSource(),
                                startIndex = startIndex,
                                forceSequential = !isShuffle,
                                forceShuffle = isShuffle
                            )
                        }
                    }
                    _context.unbindService(this)
                }
            }
            _context.bindServiceMusic(musicServiceConnection)
        }
    }

    private fun saveImage(
        defaultUrl: String,
        filterList: List<String>
    ) {
        val imageSize = resources.getDimensionPixelSize(R.dimen.header_art_size)
        var imageUrl = ""
        imageManager.init(requireContext())
            .load(defaultUrl)
            .options(imageSize, filters = filterList.toTypedArray())
            .extractUrl { imageUrl = it }
            .preloadToDisk()

        myPlaylistViewModel.renderMyPlaylist(imageUrl)
        playlistId?.let { _playlistId ->
            myPlaylistImageViewModel.saveCoverImage(_playlistId, imageUrl)
        }
    }

    private fun initBackPressed() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (myPlaylistViewModel.getIsLoadingState().not()) {
                        findNavController().popBackStack()
                    }
                }
            }
        )
    }

    private fun openMoreDialog(type: ProductPickerType, id: Int? = null, product: Product? = null) {
        bottomSheetProductPicker = BottomSheetProductPicker(requireContext()) {
            this.itemType = type
            this.product = product
            this.itemId = id
            this.fragmentManager = activity?.supportFragmentManager
            this.onItemClicked =
                { option: PickerOptions, type: ProductPickerType?, product: Product? ->
                    val track = product as? Track
                    myPlaylistViewModel.selectOptionMore(option, type, track, playlistId)
                }
            this.onFavoriteItemClick = { isFavourited, isSuccess ->
                myPlaylistViewModel.onFavouriteSelect(isFavourited, isSuccess)
            }
        }
        bottomSheetProductPicker?.show()
    }

    private fun addToQueue(trackList: List<Track>) {
        context?.let { _context ->
            val musicServiceConnection = object : SimpleServiceConnection {
                override fun onServiceConnected(name: ComponentName, binder: IBinder) {
                    val musicPlayerService = (binder as MusicPlayerService.PlayerBinder).service
                    val trackQueueInfo = musicPlayerService.getCurrentQueueInfo()
                    when (trackQueueInfo?.queueInDisplayOrder?.get(trackQueueInfo.indexInDisplayOrder)) {
                        null -> playTrackList(trackList = trackList, isShuffle = false)
                        else -> musicPlayerService.addTracks(trackList)
                    }
                    _context.unbindService(this)
                }
            }
            _context.bindServiceMusic(musicServiceConnection)
        }
    }

    private fun addTracksToQueue(list: List<Track>) {
        val musicServiceConnection = object : SimpleServiceConnection {
            override fun onServiceConnected(name: ComponentName, binder: IBinder) {
                val musicPlayerService = (binder as? MusicPlayerService.PlayerBinder)?.service
                musicPlayerService?.addTracks(list)
                context?.unbindService(this)
            }
        }
        context?.bindServiceMusic(musicServiceConnection)
    }
}
