package com.truedigital.features.music.presentation.addtomyplaylist

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.truedigital.common.share.componentv3.data.SnackBarType
import com.truedigital.common.share.componentv3.extension.showSnackBar
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.music.extensions.BaseMusicBottomSheetDialogFragment
import com.truedigital.features.music.injections.MusicComponent
import com.truedigital.features.music.presentation.addtomyplaylist.adapter.AddToMyPlaylistAdapter
import com.truedigital.features.music.presentation.createnewplaylist.CreateNewPlaylistBottomSheetDialogFragment
import com.truedigital.features.music.presentation.createnewplaylist.CreateNewPlaylistBottomSheetDialogFragment.Companion.CREATE_MY_PLAYLIST_REQUEST_CODE
import com.truedigital.features.music.presentation.createnewplaylist.CreateNewPlaylistBottomSheetDialogFragment.Companion.IS_MY_PLAYLIST_WAS_CREATED
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.databinding.FragmentAddToMyPlaylistDialogBinding
import com.truedigital.foundation.presentations.ViewModelFactory
import javax.inject.Inject

class AddToMyPlaylistDialogFragment : BaseMusicBottomSheetDialogFragment() {

    companion object {
        val TAG = AddToMyPlaylistDialogFragment::class.simpleName
        private const val SONG_ID = "songId"

        fun newInstance(songId: Int): AddToMyPlaylistDialogFragment {
            return AddToMyPlaylistDialogFragment().apply {
                this.arguments = Bundle().apply {
                    this.putInt(SONG_ID, songId)
                }
            }
        }
    }

    private val binding by viewBinding(FragmentAddToMyPlaylistDialogBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var addToMyPlaylistAdapterFactory: AddToMyPlaylistAdapter.AddToMyPlaylistAdapterFactory

    private val addToMyPlaylistViewModel: AddToMyPlaylistViewModel by viewModels { viewModelFactory }
    private val addToMyPlaylistAdapter: AddToMyPlaylistAdapter by lazy {
        addToMyPlaylistAdapterFactory.create(onCreateMyPlaylistClicked, onAddToMyPlaylistClicked)
    }

    private val onCreateMyPlaylistClicked: () -> Unit = {
        showCreateNewPlaylist()
    }
    private val onAddToMyPlaylistClicked: (String?) -> Unit = { playlistId ->
        addToMyPlaylistViewModel.addSongToMyPlaylist(playlistId, songId)
    }

    private var songId: Int? = null
    private var parentLayout: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        MusicComponent.getInstance().inject(this)
        super.onCreate(savedInstanceState)
        songId = arguments?.getInt(SONG_ID)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_to_my_playlist_dialog, container, false)
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
        parentLayout = activity?.findViewById(android.R.id.content)

        initView()
        observeViewModel()
        setupBackPress()

        addToMyPlaylistViewModel.loadMyPlaylist()
    }

    private fun observeViewModel() = with(addToMyPlaylistViewModel) {
        onShowMyPlaylist().observe(viewLifecycleOwner) { myPlaylistList ->
            addToMyPlaylistAdapter.submitList(myPlaylistList)
        }
        onShowLoading().observe(viewLifecycleOwner) {
            showProgress()
        }
        onHideLoading().observe(viewLifecycleOwner) {
            hideProgress()
        }
        onShowAddSongSuccessMessage().observe(viewLifecycleOwner) {
            showAddSongSuccessMessage()
        }
        onShowAddSongFailMessage().observe(viewLifecycleOwner) {
            showAddSongFailMessage()
        }
    }

    private fun initView() = with(binding) {
        myPlaylistRecyclerView.adapter = addToMyPlaylistAdapter
        backImageView.setOnClickListener {
            dismiss()
        }
    }

    private fun setupBackPress() {
        dialog?.apply {
            setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_BACK
                ) {
                    this@AddToMyPlaylistDialogFragment.dismiss()
                    return@setOnKeyListener true
                } else {
                    return@setOnKeyListener false
                }
            }
        }
    }

    private fun showAddSongSuccessMessage() {
        parentLayout?.showSnackBar(R.string.add_to_my_playlist_success, SnackBarType.SUCCESS)
        dismiss()
    }

    private fun showAddSongFailMessage() {
        parentLayout?.showSnackBar(R.string.add_to_my_playlist_fail, SnackBarType.ERROR)
        dismiss()
    }

    private fun showCreateNewPlaylist() {
        CreateNewPlaylistBottomSheetDialogFragment.newInstance().let { dialog ->
            dialog.show(
                requireActivity().supportFragmentManager,
                CreateNewPlaylistBottomSheetDialogFragment.TAG
            )
            dialog.setFragmentResultListener(CREATE_MY_PLAYLIST_REQUEST_CODE) { _, result ->
                result.getBoolean(IS_MY_PLAYLIST_WAS_CREATED).let { isMyPlaylistWasCreated ->
                    addToMyPlaylistViewModel.handlerReloadMyPlaylist(isMyPlaylistWasCreated)
                }
            }
        }
    }
}
