package com.truedigital.common.share.componentv3.widget.truepoint.data

import com.truedigital.common.share.componentv3.widget.truepoint.data.model.TruePointCardStyle
import javax.inject.Inject

interface TruePointCardStyleCacheRepository {
    fun getCardStyle(): TruePointCardStyle
    fun saveCacheCardStyle(style: TruePointCardStyle)
}

class TruePointCardStyleCacheRepositoryImpl @Inject constructor() : TruePointCardStyleCacheRepository {
    private var cardStyle: TruePointCardStyle = TruePointCardStyle()

    override fun getCardStyle(): TruePointCardStyle = cardStyle

    override fun saveCacheCardStyle(style: TruePointCardStyle) {
        cardStyle = style
    }
}
