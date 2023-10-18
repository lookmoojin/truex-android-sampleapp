package com.truedigital.common.share.componentv3.widget.truepoint.data

import com.truedigital.common.share.componentv3.R
import com.truedigital.common.share.componentv3.widget.truepoint.data.model.TruePointCardStyle
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TruePointCardStyleCacheRepositoryTest {

    private val repository = TruePointCardStyleCacheRepositoryImpl()

    @Test
    fun `Test save style and get get style`() {
        val style = TruePointCardStyle(textColor = R.color.white)

        repository.saveCacheCardStyle(style)
        assertEquals(style, repository.getCardStyle())
    }
}
