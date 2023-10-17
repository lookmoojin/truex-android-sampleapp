package com.truedigital.features.music.presentation.landing.viewholder

import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.common.share.resource.ui.theme.TrueIdTheme
import com.truedigital.features.music.domain.landing.model.MusicForYouItemModel
import com.truedigital.features.music.presentation.compose.ItemSquareTitleView

class MusicForYouItemSquareTitleViewHolder(
    private val composeView: ComposeView
) : RecyclerView.ViewHolder(composeView) {

    companion object {
        fun from(parent: ViewGroup): MusicForYouItemSquareTitleViewHolder {
            return MusicForYouItemSquareTitleViewHolder(ComposeView(parent.context))
        }
    }

    fun bind(
        musicForYouItemModel: MusicForYouItemModel,
        onItemClicked: (MusicForYouItemModel) -> Unit
    ) {
        when (musicForYouItemModel) {
            is MusicForYouItemModel.RadioShelfItem -> {
                composeView.setContent {
                    TrueIdTheme {
                        ItemSquareTitleView(
                            musicForYouItemModel.thumbnail,
                            musicForYouItemModel.title
                        ) {
                            onItemClicked.invoke(musicForYouItemModel)
                        }
                    }
                }
            }
            else -> Unit
        }
    }
}
