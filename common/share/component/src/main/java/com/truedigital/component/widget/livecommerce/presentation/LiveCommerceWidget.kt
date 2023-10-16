package com.truedigital.component.widget.livecommerce.presentation

import android.content.Context
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.truedigital.component.databinding.LivecommerceShelfviewBinding
import com.truedigital.component.injections.TIDComponent
import com.truedigital.component.widget.livecommerce.presentation.adapter.LiveCommerceWidgetAdapter
import com.truedigital.component.widget.livecommerce.presentation.viewmodel.LiveCommerceWidgetViewModel
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible
import com.truedigital.foundation.presentations.ViewModelFactory
import javax.inject.Inject

class LiveCommerceWidget(
    context: Context,
    shelfItemData: String,
    openDeeplink: ((strDeeplink: String) -> Unit)?,
    trackFirebaseEvent: ((HashMap<String, Any>) -> Unit)?
) : ConstraintLayout(context), LifecycleObserver {

    private val binding: LivecommerceShelfviewBinding by lazy {
        LivecommerceShelfviewBinding.inflate(LayoutInflater.from(context), this, false)
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: LiveCommerceWidgetViewModel by lazy {
        viewModelFactory.create(LiveCommerceWidgetViewModel::class.java)
    }
    private val lifecycleOwner = (context as? LifecycleOwner)
    private var openDeeplink: ((strDeeplink: String) -> Unit)? = null
    private var trackFirebaseEvent: ((HashMap<String, Any>) -> Unit)? = null
    private lateinit var adapter: LiveCommerceWidgetAdapter

    init {
        TIDComponent.getInstance().inject(this)

        addView(binding.root)
        initialLifecycle()
        this.openDeeplink = openDeeplink
        this.trackFirebaseEvent = trackFirebaseEvent
        initialView()
        startLoadWidget(shelfItemData)
    }

    fun startLoadWidget(shelfItemData: String) {
        viewModel.initData(shelfItemData = shelfItemData)
    }

    private fun initialView() {
        binding.seemoreImageView.gone()
        adapter = LiveCommerceWidgetAdapter(
            context = context,
            onLiveCommerceItemClicked = { _, item ->
                viewModel.performLivestreamingDeeplink(item)
            },
            onTrackFirebaseEvent = null
        )
        binding.commerceRecyclerView.adapter = adapter

        lifecycleOwner?.let { _lifeCycleOwner ->
            with(viewModel) {
                titleOfWidget.observe(_lifeCycleOwner) { titleString ->
                    binding.titleTextView.text = titleString
                }
                performDeepLink.observe(_lifeCycleOwner) { url ->
                    openDeeplink?.invoke(url)
                }
                navigateLink.observe(_lifeCycleOwner) { url ->
                    if (url.isNotEmpty()) {
                        binding.seemoreImageView.visible()
                        binding.seemoreImageView.onClick {
                            openDeeplink?.invoke(url)
                        }
                    } else {
                        binding.seemoreImageView.gone()
                    }
                }
                activeStreamDataList.observe(_lifeCycleOwner) { activeStreamDataList ->
                    binding.commerceViewGroupContainer.visible()
                    adapter.setItem(activeStreamDataList)
                }
            }
        }
    }

    private fun initialLifecycle() {
        lifecycleOwner?.lifecycle?.addObserver(this)
    }
}
