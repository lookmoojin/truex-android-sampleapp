package com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant
import com.truedigital.common.share.componentv3.R
import com.truedigital.common.share.componentv3.databinding.ShelfMultipleHorizontalBinding
import com.truedigital.common.share.componentv3.injections.ComponentV3Component
import com.truedigital.common.share.componentv3.widget.decoration.MarginItemDecoration
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.domain.model.WeMallParametersModel
import com.truedigital.common.share.componentv3.widget.wemallshelfcomponent.presentation.adapter.WeMallShelfAdapter
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible
import com.truedigital.foundation.presentations.ViewModelFactory
import javax.inject.Inject

@SuppressLint("ViewConstructor")
class WeMallShelfWidget(
    context: Context,
    openDeeplink: (strDeeplink: String?) -> Unit,
    trackFirebaseEvent: (HashMap<String, Any>) -> Unit,
    shelfId: String
) : ConstraintLayout(context), LifecycleObserver {

    companion object {
        private const val CONTENT_DESCRIPTION_VIEWGROUP_PREFIX = "commerce_scroll_"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val weMallShelfLayoutViewModel: WeMallShelfWidgetViewModel by lazy {
        viewModelFactory.create(WeMallShelfWidgetViewModel::class.java)
    }

    private val binding: ShelfMultipleHorizontalBinding by lazy {
        ShelfMultipleHorizontalBinding.inflate(LayoutInflater.from(context), this, false)
    }
    private var divider: MarginItemDecoration? = null
    private lateinit var adapter: WeMallShelfAdapter
    private val lifecycleOwner = (context as? LifecycleOwner)
    private var openDeeplink: ((strDeeplink: String?) -> Unit)? = null
    private var trackFirebaseEvent: ((HashMap<String, Any>) -> Unit)? = null
    private var shelfId: String = ""

    init {
        ComponentV3Component.getInstance().inject(this)

        addView(binding.root)
        initialLifecycle()
        this.openDeeplink = openDeeplink
        this.trackFirebaseEvent = trackFirebaseEvent
        this.shelfId = shelfId
        initialView()
    }

    private fun initialLifecycle() {
        lifecycleOwner?.lifecycle?.addObserver(this)
    }

    private fun initialView() {
        initialViewModel()
        adapter = WeMallShelfAdapter(context, openDeeplink, trackFirebaseEvent, shelfId)
        binding.feedHorizontalRecyclerView.adapter = adapter
    }

    private fun initialViewModel() = with(weMallShelfLayoutViewModel) {
        lifecycleOwner?.let { _lifecycleOwner ->
            weMallShelfLayout.observe(
                _lifecycleOwner
            ) { dataModel ->
                dataModel?.let { _dataModel ->
                    initView(
                        parametersModel = _dataModel,
                        openDeeplink = openDeeplink,
                        trackFirebaseEvent = trackFirebaseEvent
                    )
                }
            }

            weMallItem.observe(
                _lifecycleOwner
            ) { item ->
                item?.let { _item ->
                    adapter.addItem(_item)
                }
            }
            responseItem.observe(
                _lifecycleOwner
            ) { itemList ->
                itemList?.let { _itemList ->
                    weMallShelfLayoutViewModel.transformData(_itemList)
                }
            }
        }
    }

    private fun initView(
        parametersModel: WeMallParametersModel,
        openDeeplink: ((strDeeplink: String?) -> Unit)?,
        trackFirebaseEvent: ((HashMap<String, Any>) -> Unit)?
    ) {
        binding.horizontalHeaderLayout.root.visible()
        binding.horizontalHeaderLayout.titleTextView.text = parametersModel.title
        parametersModel.setting?.let { _setting ->
            if (_setting.seeMore.isNotEmpty()) {
                binding.root.contentDescription = CONTENT_DESCRIPTION_VIEWGROUP_PREFIX +
                    parametersModel.setting?.category
                binding.horizontalHeaderLayout.seeMoreHeaderView.visible()
                binding.horizontalHeaderLayout.seeMoreHeaderView.onClick {
                    trackFirebaseEvent?.let { _trackFirebaseEvent ->
                        _trackFirebaseEvent.invoke(
                            hashMapOf(
                                MeasurementConstant.Key.KEY_EVENT_NAME to
                                    MeasurementConstant.Event.EVENT_CLICK,
                                MeasurementConstant.Key.KEY_LINK_TYPE to
                                    MeasurementConstant.LinkType.LINK_TYPE_SEE_MORE,
                                MeasurementConstant.Key.KEY_LINK_DESC to parametersModel.title.take(
                                    100
                                )
                                    .ifEmpty { " " },
                                MeasurementConstant.Key.KEY_SHELF_NAME to parametersModel.title.take(
                                    100
                                )
                                    .ifEmpty { " " },
                                MeasurementConstant.Key.KEY_SHELF_CODE to parametersModel.id.ifEmpty { " " }
                            )
                        )
                        openDeeplink?.invoke(_setting.seeMore)
                    }
                }
            } else {
                binding.horizontalHeaderLayout.seeMoreHeaderView.gone()
            }
        }

        divider?.let { _divider ->
            binding.feedHorizontalRecyclerView.removeItemDecoration(_divider)
            binding.feedHorizontalRecyclerView.addItemDecoration(_divider)
        } ?: run {
            divider = MarginItemDecoration(
                context.resources.getDimensionPixelSize(
                    R.dimen.spacing_3
                )
            )
            binding.feedHorizontalRecyclerView.addItemDecoration(divider!!)
        }
    }

    fun startLoad(
        component: String
    ) {
        weMallShelfLayoutViewModel.getWeMallShelfLayout(
            component = component
        )
    }

    override fun onViewRemoved(view: View?) {
        super.onViewRemoved(view)
        weMallShelfLayoutViewModel.cancelJob()
    }
}
