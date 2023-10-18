package com.truedigital.features.music.presentation.mylibrary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.music.injections.MusicComponent
import com.truedigital.features.music.presentation.mylibrary.mymusic.MyMusicFragment
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.databinding.FragmentMusicMyLibraryBinding
import com.truedigital.features.tuned.presentation.common.TunedFragment
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.presentations.ViewModelFactory
import javax.inject.Inject

class MusicMyLibraryFragment : TunedFragment(R.layout.fragment_music_my_library) {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val musicMyLibraryNavigationViewModel: MusicMyLibraryNavigationViewModel by viewModels(
        { requireActivity() },
    ) { viewModelFactory }
    private val musicMyLibraryViewModel: MusicMyLibraryViewModel by viewModels { viewModelFactory }

    private val binding by viewBinding(FragmentMusicMyLibraryBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        MusicComponent.getInstance().inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBack()
        observeViewModel()
        showContent()

        musicMyLibraryViewModel.setRouterSecondaryToNavController(findNavController())
    }

    private fun initBack() {
        binding.musicMyLibraryBackImageView.onClick {
            findNavController().popBackStack()
        }
    }

    private fun observeViewModel() {
        musicMyLibraryNavigationViewModel.onShowMyPlaylist()
            .observe(viewLifecycleOwner) { playlistId ->
                musicMyLibraryViewModel.navigateToMyPlaylist(playlistId)
            }

        musicMyLibraryNavigationViewModel.onCreateNewPlaylist()
            .observe(viewLifecycleOwner) {
                musicMyLibraryViewModel.navigateToCreateNewPlaylist()
            }
    }

    private fun showContent() {
        childFragmentManager.beginTransaction()
            .replace(R.id.musicMyLibraryContainer, MyMusicFragment())
            .commit()
    }
}
