package com.truedigital.features.truecloudv3.common

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class HeaderTitleTest {
    @Test
    fun headerTitleTest() {
        val headerTitle = HeaderTitle(
            title = "testTile",
            resTitle = 123
        )
        headerTitle.resTitle = 456
        assertEquals("testTile", headerTitle.title)
        assertEquals(456, headerTitle.resTitle)
    }
}
