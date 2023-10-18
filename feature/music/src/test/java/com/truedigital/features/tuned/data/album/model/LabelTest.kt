package com.truedigital.features.tuned.data.album.model

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LabelTest {

    @Test
    fun testLabelData_mapData() {
        val mockLabel = Label(
            id = 1,
            name = "name"
        )
        assertEquals(1, mockLabel.id)
        assertEquals("name", mockLabel.name)
    }
}
