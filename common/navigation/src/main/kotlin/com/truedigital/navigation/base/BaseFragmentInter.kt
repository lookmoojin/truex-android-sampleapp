package com.truedigital.navigation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.truedigital.common.share.analytics.measurement.AnalyticManager
import com.truedigital.navigation.adapter.ShelfAdapter
import com.truedigital.navigations.share.data.model.TabResponseItem

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseFragmentInter<VB : ViewBinding, ViewModel : BaseInterViewModel>(
    private val inflate: Inflate<VB>
) : Fragment() {

    private var _binding: VB? = null
    val binding get() = _binding!!

    protected abstract val viewModel: ViewModel
    protected abstract val analyticManager: AnalyticManager

    var shelfAdapter: ShelfAdapter? = null
    var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        if (arguments != null) {
            requireArguments().apply {
                getParcelable<TabResponseItem>(SKELETON_CONTENT)?.let {
                    viewModel.setContent(content = it)
                }
                getInt(TAB_POSITION, 0).let {
                    position = it
                }
            }
        }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (_binding == null) {
            _binding = inflate.invoke(inflater, container, false)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
        initViewModel()
        checkLoadShelf()
        super.onViewCreated(view, savedInstanceState)
    }

    abstract fun initView()
    abstract fun initViewModel()

    fun trackFirebaseEvent(event: HashMap<String, Any>) {
        analyticManager.trackEvent(event)
    }

    private fun checkLoadShelf() {
        if (shelfAdapter?.itemCount == 0) {
            viewModel.getShelf()
        }
    }

    companion object {
        const val SKELETON_CONTENT = "SKELETON_CONTENT"
        const val TAB_POSITION = "TAB_POSITION"
    }
}
