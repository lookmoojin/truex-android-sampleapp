package com.truedigital.features.music.presentation.searchlanding

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.truedigital.common.share.analytics.measurement.AnalyticManager
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant
import com.truedigital.core.extensions.ifNotNullOrEmpty
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.music.domain.search.model.ThemeType
import com.truedigital.features.music.injections.MusicComponent
import com.truedigital.features.music.presentation.search.MusicSearchFragment
import com.truedigital.features.music.presentation.search.MusicSearchViewModel
import com.truedigital.features.music.presentation.searchtrending.MusicSearchTrendingFragment
import com.truedigital.features.music.presentation.searchtrending.MusicSearchTrendingViewModel
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.databinding.FragmentMusicSearchLandingBinding
import com.truedigital.features.tuned.presentation.common.TunedFragment
import com.truedigital.foundation.extension.clearKeyboardFocus
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible
import com.truedigital.foundation.presentations.ViewModelFactory
import javax.inject.Inject

class MusicSearchLandingFragment : TunedFragment(R.layout.fragment_music_search_landing) {

    companion object {
        private const val TAKE_NUMBER = 100
    }

    private val binding by viewBinding(FragmentMusicSearchLandingBinding::bind)

    @Inject
    lateinit var analyticManager: AnalyticManager

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val musicSearchViewModel: MusicSearchViewModel by viewModels(
        { requireActivity() }) { viewModelFactory }
    private val musicSearchTrendingViewModel: MusicSearchTrendingViewModel by viewModels(
        { requireActivity() }) { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        MusicComponent.getInstance().inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBackButton()
        initSearchEditText()
        initClearSearch()
        initViewModel()
        renderMusicSearchTrendingFragment()
    }

    override fun onDestroyView() {
        musicSearchTrendingViewModel.clearCache()
        super.onDestroyView()
    }

    private fun initViewModel() = with(musicSearchViewModel) {
        onSelectedTopMenu().observe(viewLifecycleOwner) {
            hideSoftKeyboard()

            if (childFragmentManager.fragments.lastOrNull() is MusicSearchFragment) {
                binding.searchLandingEditText.text.toString().trim().ifNotNullOrEmpty { query ->
                    musicSearchViewModel.searchWithCurrentTopMenu(query)
                }
            }
        }
    }

    private fun initBackButton() = with(binding) {
        searchLandingImageView.onClick {
            hideSoftKeyboard()
            findNavController().popBackStack()
        }
    }

    private fun initSearchEditText() = with(binding) {
        searchLandingEditText.apply {
            this.doOnTextChanged { text, _, _, _ ->
                text?.let { keyWord ->
                    when {
                        keyWord.isNotBlank() -> {
                            clearTextImageView.visible()
                        }
                        else -> {
                            clearTextImageView.gone()
                            renderMusicSearchTrendingFragment()
                        }
                    }
                }
            }

            this.setOnEditorActionListener { textView, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_SEARCH -> {
                        textView?.text.toString().trim().ifNotNullOrEmpty { keyword ->
                            trackEventSearch(keyword)
                            renderMusicSearchFragment(keyword)
                        }
                        hideSoftKeyboard()
                        return@setOnEditorActionListener true
                    }
                    else -> false
                }
            }
        }
    }

    private fun initClearSearch() = with(binding) {
        clearTextImageView.onClick {
            clearTextImageView.gone()
            searchLandingEditText.setText("")
        }
    }

    private fun hideSoftKeyboard() = with(binding) {
        searchLandingEditText.clearKeyboardFocus()
    }

    private fun renderMusicSearchTrendingFragment() {
        if ((childFragmentManager.fragments.lastOrNull() is MusicSearchTrendingFragment).not()) {
            childFragmentManager.findFragmentByTag(MusicSearchTrendingFragment.TAG)
                ?.let { fragment ->
                    childFragmentManager.beginTransaction().remove(fragment).commit()
                }
            childFragmentManager.beginTransaction()
                .replace(
                    R.id.searchLandingContainerFrameLayout,
                    MusicSearchTrendingFragment.newInstance(),
                    MusicSearchTrendingFragment.TAG
                )
                .commit()
        }
    }

    private fun renderMusicSearchFragment(keyword: String) {
        if (childFragmentManager.fragments.lastOrNull() is MusicSearchFragment) {
            musicSearchViewModel.searchWithCurrentTopMenu(keyword)
        } else {
            childFragmentManager.findFragmentByTag(MusicSearchFragment.TAG)?.let { fragment ->
                childFragmentManager.beginTransaction().remove(fragment).commit()
            }
            childFragmentManager.beginTransaction()
                .replace(
                    R.id.searchLandingContainerFrameLayout,
                    MusicSearchFragment.newInstance(keyword, ThemeType.LIGHT.name),
                    MusicSearchFragment.TAG
                )
                .commit()
        }
    }

    private fun trackEventSearch(keyword: String) {
        analyticManager.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_EVENT_NAME to MeasurementConstant.Event.EVENT_SEARCH,
                MeasurementConstant.Key.KEY_SEARCH_TERM to keyword.take(TAKE_NUMBER)
            )
        )
    }
}
