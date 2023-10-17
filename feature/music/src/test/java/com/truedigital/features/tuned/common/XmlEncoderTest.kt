package com.truedigital.features.tuned.common

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class XmlEncoderTest {

    @Test
    fun testEncode_XmlEncoderInstance_assertNotNull() {
        assertNotNull("".xmlEncode())
    }

    @Test
    fun testEncode_replacePercent_returnPercent25() {
        assertEquals("%25", "%".xmlEncode())
    }

    @Test
    fun testEncode_replaceLessThan_returnPercent3C() {
        assertEquals("%3C", "<".xmlEncode())
    }

    @Test
    fun testEncode_replaceGraterThan_returnPercent3E() {
        assertEquals("%3E", ">".xmlEncode())
    }

    @Test
    fun testEncode_replaceBackSlash_returnPercent22() {
        assertEquals("%22", "\"".xmlEncode())
    }

    @Test
    fun testEncode_replaceEmpty_returnPercent20() {
        assertEquals("%20", " ".xmlEncode())
    }

    @Test
    fun testEncode_replaceSharp_returnPercent23() {
        assertEquals("%23", "#".xmlEncode())
    }
}
