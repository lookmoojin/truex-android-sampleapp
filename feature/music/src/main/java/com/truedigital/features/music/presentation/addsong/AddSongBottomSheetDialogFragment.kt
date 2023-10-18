package com.truedigital.features.music.presentation.addsong

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.truedigital.common.share.componentv3.extension.setSavedStateHandle
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.listens.share.constant.MusicConstant.Key.ADD_SONG_SUCCESS
import com.truedigital.features.music.domain.warning.model.MusicWarningModel
import com.truedigital.features.music.domain.warning.model.MusicWarningType
import com.truedigital.features.music.extensions.BaseMusicBottomSheetDialogFragment
import com.truedigital.features.music.injections.MusicComponent
import com.truedigital.features.music.presentation.addsong.adapter.AddSongAdapter
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.databinding.DialogAddSongBinding
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.visible
import com.truedigital.foundation.extension.visibleOrGone
import com.truedigital.foundation.presentations.ViewModelFactory
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddSongBottomSheetDialogFragment : BaseMusicBottomSheetDialogFragment() {

    companion object {
        const val PLAYLIST_ID_SLUG = "playlistId"
    }

    @Inject
    lateinit var addSongAdapterFactory: AddSongAdapter.AddSongAdapterFactory

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val addSongAdapter: AddSongAdapter by lazy {
        addSongAdapterFactory.create(onAddSongClicked)
    }
    private val addSongViewModel: AddSongViewModel by viewModels { viewModelFactory }
    private val binding by viewBinding(DialogAddSongBinding::bind)

    private val onAddSongClicked: (Int?) -> Unit = { trackId ->
        addSongViewModel.onSelectedSong(trackId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        MusicComponent.getInstance().inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_add_song, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            isCancelable = false
            setOnShowListener {
                val parentLayout =
                    findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
                parentLayout.let { bottomSheet ->
                    val behaviour = BottomSheetBehavior.from(bottomSheet)
                    val layoutParams = bottomSheet.layoutParams
                    layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
                    bottomSheet.layoutParams = layoutParams
                    behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                    behaviour.isDraggable = false
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showGreetingView()
        initOnClick()
        initRecyclerView()
        observeViewModel()
        initSearch()

        dialog?.apply {
            setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_BACK
                ) {
                    onShowCancel()
                    return@setOnKeyListener true
                } else {
                    return@setOnKeyListener false
                }
            }
        }
    }

    private fun initOnClick() = with(binding) {
        collapseImageView.setOnClickListener {
            onShowCancel()
        }
        clearSearchImageView.setOnClickListener {
            clearSearchImageView.gone()
            searchEditText.setText("")
        }
        doneTextView.setOnClickListener {
            val playlistId = arguments?.getInt(PLAYLIST_ID_SLUG) ?: 0
            addSongViewModel.addSong(playlistId)
        }
    }

    private fun initRecyclerView() {
        binding.searchSongRecyclerView.adapter = addSongAdapter

        addSongAdapter.addLoadStateListener { loadState ->
            binding.searchLoading.visibleOrGone(loadState.source.refresh is LoadState.Loading)
            addSongViewModel.handlerShowEmptyResult(
                loadState.refresh,
                addSongAdapter.itemCount == 0
            )
        }

        lifecycleScope.launch {
            addSongAdapter.loadStateFlow
                .distinctUntilChangedBy { it.refresh }
                .filter { it.refresh is LoadState.NotLoading }
                .collect {
                    binding.searchSongRecyclerView.scrollToPosition(0)
                }
        }
    }

    private fun observeViewModel() = with(addSongViewModel) {
        onSearchSongResult().observe(viewLifecycleOwner) { pagingData ->
            showSearchData()
            addSongAdapter.submitData(lifecycle, pagingData)
        }
        onShowEmptyResult().observe(viewLifecycleOwner) {
            showSearchEmptyView()
        }
        onHideEmptyResult().observe(viewLifecycleOwner) {
            showSearchData()
        }
        onErrorAddSong().observe(viewLifecycleOwner) {
            onShowError()
        }
        onSuccessAddSong().observe(viewLifecycleOwner) {
            findNavController().setSavedStateHandle(ADD_SONG_SUCCESS, true)
            dismiss()
        }
        onShowProgressAddSong().observe(viewLifecycleOwner) {
            showProgress()
        }
        onHideProgressAddSong().observe(viewLifecycleOwner) {
            hideProgress()
        }
        onSongSelected().observe(viewLifecycleOwner) {
            binding.doneTextView.isVisible = it.isNotEmpty()
        }
    }

    private fun initSearch() = with(binding) {
        searchEditText.apply {
            this.doOnTextChanged { query, _, _, _ ->
                if (query.isNullOrEmpty()) {
                    clearSearchImageView.gone()
                } else {
                    clearSearchImageView.visible()
                }
            }

            this.setOnEditorActionListener { editText, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_SEARCH -> {
                        addSongViewModel.searchSong(editText?.text.toString())
                        hideSoftKeyboard(this)
                        return@setOnEditorActionListener true
                    }
                    else -> false
                }
            }
        }
    }

    private fun showSearchData() = with(binding) {
        searchSongRecyclerView.visible()
        addSongErrorView.gone()
    }

    private fun hideSearchData() = with(binding) {
        searchSongRecyclerView.gone()
        addSongErrorView.visible()
    }

    private fun showGreetingView() = with(binding) {
        addSongErrorView.setupView(
            drawableImage = R.drawable.music_ic_headphone,
            title = resources.getString(R.string.add_songs_search_greeting),
            titleStyle = R.style.TrueID_Body_Black
        )
        hideSearchData()
    }

    private fun showSearchEmptyView() = with(binding) {
        addSongErrorView.setupView(
            drawableImage = R.drawable.music_ic_search_empty,
            title = resources.getString(R.string.add_songs_empty_search_title),
            detail = resources.getString(R.string.add_songs_empty_search_description),
            titleStyle = R.style.TrueID_Body_Black,
            detailStyle = R.style.TrueID_Body_Black
        )
        hideSearchData()
    }

    private fun onShowError() {
        val musicWarningModel = MusicWarningModel(
            title = R.string.add_song_error_title_dialog,
            description = R.string.add_song_error_description_dialog,
            confirmText = R.string.ok,
            type = MusicWarningType.FORCE_ANSWER
        )
        showMusicWarningDialog(musicWarningModel)
    }

    private fun onShowCancel() {
        if (!binding.doneTextView.isVisible) {
            dismiss()
        } else {
            val musicWarningModel = MusicWarningModel(
                title = R.string.add_song_cancel_title_dialog,
                description = R.string.add_song_cancel_message_dialog,
                confirmText = R.string.confirm,
                cancelText = R.string.cancel,
                type = MusicWarningType.CHOICE_ANSWER
            )
            showMusicWarningDialog(musicWarningModel, onConfirmClicked = {
                dismiss()
            })
        }
    }
}
