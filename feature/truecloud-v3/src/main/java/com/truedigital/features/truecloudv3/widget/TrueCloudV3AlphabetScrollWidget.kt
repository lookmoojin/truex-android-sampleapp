package com.truedigital.features.truecloudv3.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tdg.truecloud.databinding.TrueCloudv3ViewAlphabetScrollBinding
import com.truedigital.features.truecloudv3.domain.model.AlphabetItemModel
import com.truedigital.features.truecloudv3.domain.model.ContactTrueCloudModel
import com.truedigital.features.truecloudv3.presentation.adapter.AlphabetScrollAdapter
import com.truedigital.features.truecloudv3.presentation.adapter.ContactAdapter
import com.truedigital.features.truecloudv3.util.TrueCloudV3GroupAlphabetUtils

class TrueCloudV3AlphabetScrollWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    companion object {
        private const val DEFAULT_NO_POSITION = -1
        private const val DEFAULT_OFFSET = 0
        private const val DEFAULT_WIDGET_HEIGHT = 0
    }

    private val binding: TrueCloudv3ViewAlphabetScrollBinding by lazy {
        TrueCloudv3ViewAlphabetScrollBinding.inflate(
            LayoutInflater.from(context), this
        )
    }

    private var attachedRecyclerView: RecyclerView? = null
    private var alphabetAdapter = AlphabetScrollAdapter()
    private var alphabets = listOf<AlphabetItemModel>()
    private var widgetHeight: Int = DEFAULT_WIDGET_HEIGHT

    var onScrollListener: (() -> Unit?)? = null

    init {
        setupView()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        widgetHeight = h
    }

    fun setAttachedRecyclerView(recyclerView: RecyclerView) {
        attachedRecyclerView = recyclerView.apply {
            addOnScrollListener(onAttachedRecyclerScrollListener)
        }
    }

    fun submitList(alphabets: List<AlphabetItemModel>) {
        this.alphabets = alphabets
        alphabetAdapter.setAlphabetList(alphabets)
    }

    private fun setupView() {
        alphabetAdapter.apply {
            onItemClicked = {
                performSelectedAlphabetItem(position = it.index)
                performScrollAttachedRecyclerView(position = it.position)
            }
        }

        binding.trueCloudAlphabetRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = alphabetAdapter
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun performSelectedAlphabetItem(position: Int) {
        alphabets.forEach { item ->
            item.isActive = false
        }
        alphabets.getOrNull(position)?.isActive = true
        binding.trueCloudAlphabetRecyclerView.post {
            alphabetAdapter.notifyDataSetChanged()
        }
    }

    private fun performScrollAttachedRecyclerView(position: Int) {
        val itemCount = attachedRecyclerView?.adapter?.itemCount ?: DEFAULT_NO_POSITION

        if (position <= DEFAULT_NO_POSITION || position > itemCount) {
            return
        }

        (attachedRecyclerView?.layoutManager as LinearLayoutManager)
            .scrollToPositionWithOffset(position, DEFAULT_OFFSET)
        onScrollListener?.invoke()
    }

    private val onAttachedRecyclerScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val position = (recyclerView.layoutManager as? LinearLayoutManager)
                ?.findFirstVisibleItemPosition() ?: DEFAULT_NO_POSITION

            (recyclerView.adapter as? ContactAdapter)?.apply {
                val itemModel = this.getItemAtPosition(position) as? ContactTrueCloudModel
                itemModel?.let { _itemModel ->
                    val header =
                        TrueCloudV3GroupAlphabetUtils.getFirstAlphabet(_itemModel.firstName)
                    alphabets.firstOrNull { it.alphabet == header }?.let {
                        performSelectedAlphabetItem(it.index)
                    }
                }
            }
        }
    }
}
