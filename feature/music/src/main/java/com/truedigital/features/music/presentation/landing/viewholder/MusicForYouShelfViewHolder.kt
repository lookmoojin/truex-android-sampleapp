package com.truedigital.features.music.presentation.landing.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.listens.share.constant.MusicConstant
import com.truedigital.features.music.constant.MusicShelfType
import com.truedigital.features.music.domain.landing.model.MusicForYouItemModel
import com.truedigital.features.music.domain.landing.model.MusicForYouShelfModel
import com.truedigital.features.music.domain.landing.model.MusicLandingFASelectContentModel
import com.truedigital.features.music.injections.MusicComponent
import com.truedigital.features.music.presentation.landing.adapter.MusicForYouItemAdapter
import com.truedigital.features.tuned.data.productlist.model.ProductListType
import com.truedigital.features.tuned.databinding.ViewShelfForYouBinding
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visibleOrGone
import javax.inject.Inject

class MusicForYouShelfViewHolder(
    val binding: ViewShelfForYouBinding,
    private val onItemClicked: (MusicForYouItemModel, MusicLandingFASelectContentModel) -> Unit,
    private val onSeeAllClicked: (MusicForYouShelfModel) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private var shelfModel: MusicForYouShelfModel? = null

    @Inject
    lateinit var musicForYouItemAdapterFactory: MusicForYouItemAdapter.MusicForYouItemAdapterFactory

    private val musicForYouItemAdapter: MusicForYouItemAdapter by lazy {
        musicForYouItemAdapterFactory.create(onShelfItemClicked)
    }
    private val recyclerViewPool: RecyclerView.RecycledViewPool = RecyclerView.RecycledViewPool()
    private val onShelfItemClicked: (MusicForYouItemModel, Int) -> Unit = { itemModel, itemIndex ->
        onItemClicked.invoke(
            itemModel,
            MusicLandingFASelectContentModel(
                shelfName = shelfModel?.titleFA.orEmpty(),
                shelfIndex = shelfModel?.shelfIndex ?: 0,
                itemIndex = itemIndex
            )
        )
    }

    companion object {
        private const val VERTICAL_TYPE = 5
        private const val OTHER_TYPE = 3

        fun from(
            parent: ViewGroup,
            onItemClicked: (MusicForYouItemModel, MusicLandingFASelectContentModel) -> Unit,
            onSeeAllClicked: (MusicForYouShelfModel) -> Unit
        ): MusicForYouShelfViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = ViewShelfForYouBinding.inflate(layoutInflater, parent, false)
            return MusicForYouShelfViewHolder(view, onItemClicked, onSeeAllClicked)
        }
    }

    init {
        MusicComponent.getInstance().inject(this)
    }

    fun bind(musicForYouShelfModel: MusicForYouShelfModel) = with(binding) {
        shelfModel = musicForYouShelfModel
        titleTextView.text = musicForYouShelfModel.title
        shelfItemRecyclerView.apply {
            setHasFixedSize(true)
            setRecycledViewPool(recyclerViewPool)
            layoutManager = getLayoutManager(musicForYouShelfModel)
            adapter = musicForYouItemAdapter
        }
        musicForYouItemAdapter.submitList(musicForYouShelfModel.itemList)
        seeMoreImageView.visibleOrGone(musicForYouShelfModel.productListType != ProductListType.CONTENT)
        seeMoreImageView.onClick {
            onSeeAllClicked.invoke(musicForYouShelfModel)
        }
    }

    private fun getLayoutManager(data: MusicForYouShelfModel): RecyclerView.LayoutManager {
        return when (data.productListType) {
            ProductListType.TRACKS_PLAYLIST -> {
                val mSpanCount = when (data.options?.displayType) {
                    MusicConstant.Display.VERTICAL_LIST -> VERTICAL_TYPE
                    else -> OTHER_TYPE
                }
                getGridLayoutManager(
                    spanCount = mSpanCount,
                    orientation = GridLayoutManager.HORIZONTAL,
                    canScrollVertically = false
                )
            }
            ProductListType.CONTENT -> {
                when (data.shelfType) {
                    MusicShelfType.GRID_2 -> getGridLayoutManager(
                        spanCount = data.shelfType.columns,
                        orientation = GridLayoutManager.VERTICAL,
                        canScrollVertically = false
                    )
                    MusicShelfType.VERTICAL -> getLinearLayoutManager(LinearLayoutManager.VERTICAL)
                    MusicShelfType.HORIZONTAL -> getLinearLayoutManager(LinearLayoutManager.HORIZONTAL)
                }
            }
            else -> getLinearLayoutManager(LinearLayoutManager.HORIZONTAL)
        }
    }

    private fun getLinearLayoutManager(orientation: Int): LinearLayoutManager {
        return LinearLayoutManager(
            binding.root.context,
            orientation,
            false
        )
    }

    private fun getGridLayoutManager(
        spanCount: Int,
        orientation: Int,
        canScrollVertically: Boolean = true,
        canScrollHorizontally: Boolean = true
    ): GridLayoutManager {
        return object : GridLayoutManager(
            binding.root.context,
            spanCount,
            orientation,
            false
        ) {
            override fun canScrollVertically(): Boolean {
                return canScrollVertically
            }

            override fun canScrollHorizontally(): Boolean {
                return canScrollHorizontally
            }
        }
    }
}
