package com.truedigital.features.tuned.common.extension

import com.truedigital.features.tuned.common.extensions.toDurationString
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class StringExtensionTest {

    @Test
    fun testToDurationString_returnString() {
        val result = 214000L.toDurationString()
        assertEquals("3:34", result)
    }
}
