package com.truedigital.features.music.presentation.createnewplaylist

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.listens.share.constant.MusicConstant
import com.truedigital.features.music.domain.warning.model.MusicWarningModel
import com.truedigital.features.music.domain.warning.model.MusicWarningType
import com.truedigital.features.music.extensions.BaseMusicBottomSheetDialogFragment
import com.truedigital.features.music.injections.MusicComponent
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.databinding.DialogBottomSheetCreateNewPlaylistBinding
import com.truedigital.foundation.extension.clickAsFlow
import com.truedigital.foundation.presentations.ViewModelFactory
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class CreateNewPlaylistBottomSheetDialogFragment : BaseMusicBottomSheetDialogFragment() {

    companion object {
        val TAG = CreateNewPlaylistBottomSheetDialogFragment::class.simpleName
        const val CREATE_MY_PLAYLIST_REQUEST_CODE = "CREATE_MY_PLAYLIST_REQUEST_CODE"
        const val IS_MY_PLAYLIST_WAS_CREATED = "IS_MY_PLAYLIST_WAS_CREATED"
        const val IS_NAVIGATE_BY_NAVIGATION = "IS_NAVIGATE_BY_NAVIGATION"

        fun newInstance(): CreateNewPlaylistBottomSheetDialogFragment {
            return CreateNewPlaylistBottomSheetDialogFragment().apply {
                this.arguments = Bundle().apply {
                    this.putBoolean(IS_NAVIGATE_BY_NAVIGATION, false)
                }
            }
        }
    }

    private val mBinding by viewBinding(DialogBottomSheetCreateNewPlaylistBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val mCreateNewPlaylistViewModel: CreateNewPlaylistViewModel by viewModels { viewModelFactory }

    private var isNavigateByNavigation: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        MusicComponent.getInstance().inject(this)
        super.onCreate(savedInstanceState)

        isNavigateByNavigation = arguments?.getBoolean(IS_NAVIGATE_BY_NAVIGATION) ?: true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_bottom_sheet_create_new_playlist, container, false)
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
        initInstance()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        setupBackPress()
    }

    private fun observeViewModel() {
        with(mCreateNewPlaylistViewModel) {
            onShowLoading().observe(viewLifecycleOwner) {
                showProgress()
            }
            onHideLoading().observe(viewLifecycleOwner) {
                hideProgress()
            }
            onShowErrorDialog().observe(viewLifecycleOwner) {
                onShowError()
            }
            onCreateNewPlayListSuccess().observe(viewLifecycleOwner) {
                if (isNavigateByNavigation) {
                    mCreateNewPlaylistViewModel.navigateToPlaylist(it)
                } else {
                    setFragmentResult(
                        CREATE_MY_PLAYLIST_REQUEST_CODE,
                        bundleOf(IS_MY_PLAYLIST_WAS_CREATED to true)
                    )
                }
                dismiss()
            }
        }
    }

    private fun initInstance() = with(mBinding) {
        createPlaylistNameInputText.doAfterTextChanged {
            it?.let { input ->
                createPlaylistButton.isEnabled = input.isNotEmpty()
            }
        }

        collapseImageView.clickAsFlow()
            .debounce(MusicConstant.DelayTime.DELAY_500)
            .onEach {
                onShowCancel()
            }.launchIn(lifecycleScope)

        createPlaylistButton.clickAsFlow()
            .debounce(MusicConstant.DelayTime.DELAY_500)
            .onEach {
                val playlistName = createPlaylistNameInputText.text.toString()
                mCreateNewPlaylistViewModel.createNewPlaylist(playlistName)
            }.launchIn(lifecycleScope)

        cancelButton.clickAsFlow()
            .debounce(MusicConstant.DelayTime.DELAY_500)
            .onEach {
                onShowCancel()
            }.launchIn(lifecycleScope)
    }

    private fun onShowCancel() {
        if (mBinding.createPlaylistNameInputText.text.isNullOrEmpty()) {
            dismiss()
        } else {
            val musicWarningModel = MusicWarningModel(
                title = R.string.create_new_playlist_cancel_title_dialog,
                description = R.string.create_new_playlist_cancel_message_dialog,
                confirmText = R.string.confirm,
                cancelText = R.string.cancel,
                type = MusicWarningType.CHOICE_ANSWER
            )
            showMusicWarningDialog(musicWarningModel, onConfirmClicked = {
                dismiss()
            })
        }
    }

    private fun onShowError() {
        val musicWarningModel = MusicWarningModel(
            title = R.string.create_new_playlist_error_title_dialog,
            description = R.string.create_new_playlist_error_message_dialog,
            confirmText = R.string.ok,
            type = MusicWarningType.FORCE_ANSWER
        )
        showMusicWarningDialog(musicWarningModel)
    }

    private fun setupBackPress() {
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
}
