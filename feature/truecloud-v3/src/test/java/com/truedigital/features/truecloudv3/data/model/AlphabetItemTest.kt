package com.truedigital.features.truecloudv3.data.model

import com.truedigital.features.truecloudv3.domain.model.AlphabetItemModel
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AlphabetItemTest {

    @Test
    fun testSetAlphabetItem() {
        val alphabetItem = AlphabetItemModel()
        alphabetItem.alphabet = "A"
        alphabetItem.index = 0
        alphabetItem.position = 1
        alphabetItem.size = 2
        alphabetItem.isActive = true

        assertEquals("A", alphabetItem.alphabet)
        assertEquals(0, alphabetItem.index)
        assertEquals(1, alphabetItem.position)
        assertEquals(2, alphabetItem.size)
        assert(alphabetItem.isActive)
    }
}
