package com.truedigital.core.extensions

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StringExtensionTest {

    @Test
    fun `test String Extension hexToDec empty string return 0`() {
        assertEquals("".hexToDec(), 0)
    }

    @Test
    fun `test String Extension hexToDec hex string return dex`() {
        assertEquals("af".hexToDec(), 175)
    }

    @Test
    fun `test String Extension hexToDec non hex string return 0`() {
        assertEquals("z".hexToDec(), 0)
    }

    @Test
    fun `test String Extension isNumeric empty string return false`() {
        assertEquals("".isNumeric(), false)
    }

    @Test
    fun `test String Extension isNumeric string text return false`() {
        assertEquals("ABC".isNumeric(), false)
    }

    @Test
    fun `test String Extension isNumeric string missing format return false`() {
        assertEquals("12.34.56".isNumeric(), false)
    }

    @Test
    fun `test String Extension isNumeric string int return true`() {
        assertEquals("12".isNumeric(), true)
    }

    @Test
    fun `test String Extension isNumeric string float return true`() {
        assertEquals("12.3".isNumeric(), true)
    }

    @Test
    fun `test String Extension isNumeric string int negative return true`() {
        assertEquals("-12.3".isNumeric(), true)
    }

    @Test
    fun `test String Extension isNumeric string float negative return true`() {
        assertEquals("-12.3".isNumeric(), true)
    }

    @Test
    fun `test String Extension compareVersionTo missing format both return equal`() {
        assertEquals("-1.2.3".compareVersionTo("ABC"), CompareVersion.EQUAL)
    }

    @Test
    fun `test String Extension compareVersionTo missing format return equal`() {
        assertEquals("1.2.3".compareVersionTo("ABC"), CompareVersion.EQUAL)
    }

    @Test
    fun `test String Extension compareVersionTo equal version return equal`() {
        assertEquals("1.2.3".compareVersionTo("1.2.3"), CompareVersion.EQUAL)
    }

    @Test
    fun `test String Extension compareVersionTo less version return less`() {
        assertEquals("1.2".compareVersionTo("1.2.3"), CompareVersion.LESS_THAN)
    }

    @Test
    fun `test String Extension compareVersionTo greater version return greater`() {
        assertEquals("1.2.3".compareVersionTo("1.2"), CompareVersion.GREATER_THAN)
    }

    @Test
    fun `test String Extension ifIsNullOrEmpty null string return defaultValue`() {
        val mockString: String? = null
        assertEquals(mockString.ifIsNullOrEmpty { "defaultValue" }, "defaultValue")
    }

    @Test
    fun `test String Extension ifIsNullOrEmpty empty string return defaultValue`() {
        assertEquals("".ifIsNullOrEmpty { "defaultValue" }, "defaultValue")
    }

    @Test
    fun `test String Extension ifIsNullOrEmpty empty string return self`() {
        assertEquals("text".ifIsNullOrEmpty { "defaultValue" }, "text")
    }

    @Test
    fun `test String Extension removeStartTagHtml data start don't have tag return self `() {
        val mockData = "TrueID+ special for you to 0 days"
        assertEquals(mockData.removeStartTagHtml("p"), mockData)
    }

    @Test
    fun `test String Extension removeStartTagHtml data only have tag close return self`() {
        val mockData = "TrueID+ special for you to 0 days</p>"
        assertEquals(mockData.removeStartTagHtml("p"), mockData)
    }

    @Test
    fun `test String Extension removeStartTagHtml data start tag return string don't have tag`() {
        val mockData = "<p>TrueID+ special for you to 0 days</p>"
        assertEquals(mockData.removeStartTagHtml("p"), "TrueID+ special for you to 0 days")
    }

    @Test
    fun `test String Extension removeStartTagHtml data start tag with class return string don't have tag`() {
        val mockData = "<p class = asdasfasdg>TrueID+ special for you to 0 days</p>"
        assertEquals(mockData.removeStartTagHtml("p"), "TrueID+ special for you to 0 days")
    }

    @Test
    fun `test String Extension removeStartTagHtml data wrong tag start return string self`() {
        val mockData = "<h1>TrueID+ special for you to 0 days</p>"
        assertEquals(mockData.removeStartTagHtml("p"), mockData)
    }

    @Test
    fun `test String Extension removeStartTagHtml data wrong tag end return data remove only tag start`() {
        val mockData = "<p>TrueID+ special for you to 0 days</h>"
        assertEquals(mockData.removeStartTagHtml("p"), "TrueID+ special for you to 0 days</h>")
    }

    @Test
    fun `test String Extension ifNotNullOrEmpty data empty return data and don't do in lambda`() {
        val mockData = ""
        var result = "result"
        var doInLambda = false
        val returnData = mockData.ifNotNullOrEmpty { _mockData ->
            result = _mockData
            doInLambda = true
        }
        assertEquals(false, doInLambda)
        assertEquals("result", result)
        assertEquals("", returnData)
    }

    @Test
    fun `test String Extension ifNotNullOrEmpty data one character return data and do in lambda`() {
        val mockData = "a"
        var result = ""
        var doInLambda = false
        val returnData = mockData.ifNotNullOrEmpty { _mockData ->
            result = _mockData
            doInLambda = true
        }
        assertEquals(true, doInLambda)
        assertEquals("a", result)
        assertEquals("a", returnData)
    }

    @Test
    fun `test String Extension ifNotNullOrEmpty data is space return data and do in lambda`() {
        val mockData = " "
        var result = ""
        var doInLambda = false
        val returnData = mockData.ifNotNullOrEmpty { _mockData ->
            result = _mockData
            doInLambda = true
        }
        assertEquals(true, doInLambda)
        assertEquals(" ", result)
        assertEquals(" ", returnData)
    }

    @Test
    fun `test String Extension ifNotNullOrEmpty data normal return data and do in lambda`() {
        val mockData = "TrueID+ special"
        var result = ""
        var doInLambda = false
        val returnData = mockData.ifNotNullOrEmpty { _mockData ->
            result = _mockData
            doInLambda = true
        }
        assertEquals(true, doInLambda)
        assertEquals("TrueID+ special", result)
        assertEquals("TrueID+ special", returnData)
    }

    @Test
    fun `test String Extension ifIsNullOrEmpty data null do and return data in lambda`() {
        val mockData = null
        val returnData = mockData.ifIsNullOrEmpty {
            "default"
        }
        assertEquals("default", returnData)
    }

    @Test
    fun `test String Extension ifIsNullOrEmpty data empty do and return data in lambda`() {
        val mockData = ""
        val returnData = mockData.ifIsNullOrEmpty {
            "default"
        }
        assertEquals("default", returnData)
    }

    @Test
    fun `test String Extension ifIsNullOrEmpty data space don't do in lambda and return old data`() {
        val mockData = " "
        val returnData = mockData.ifIsNullOrEmpty {
            "default"
        }
        assertEquals(" ", returnData)
    }

    @Test
    fun `test String Extension ifIsNullOrEmpty data normal do in lambda and return old data`() {
        val mockData = "TrueID+ special"
        val returnData = mockData.ifIsNullOrEmpty {
            "default"
        }
        assertEquals("TrueID+ special", returnData)
    }

    @Test
    fun `test String Extension doOnNullOrEmpty data null do in lambda`() {
        val mockData = null
        var returnData = ""
        mockData.doOnNullOrEmpty {
            returnData = "default"
        }
        assertEquals("default", returnData)
    }

    @Test
    fun `test String Extension doOnNullOrEmpty data empty null do in lambda`() {
        val mockData = ""
        var returnData = ""
        mockData.doOnNullOrEmpty {
            returnData = "default"
        }
        assertEquals("default", returnData)
    }

    @Test
    fun `test String Extension doOnNullOrEmpty space data null don't do in lambda`() {
        val mockData = " "
        var returnData = ""
        mockData.doOnNullOrEmpty {
            returnData = "default"
        }
        assertEquals("", returnData)
    }

    @Test
    fun `test String Extension doOnNullOrEmpty data normal null don't do in lambda`() {
        val mockData = "TrueID+ special"
        var returnData = ""
        mockData.doOnNullOrEmpty {
            returnData = "default"
        }
        assertEquals("", returnData)
    }

    @Test
    fun `test String Extension environmentCase data empty do in lambda prod`() {
        val mockData = ""
        val returnData = mockData.environmentCase({
            "staging"
        }, {
            "preprod"
        }, {
            "prod"
        })
        assertEquals("prod", returnData)
    }

    @Test
    fun `test String Extension environmentCase data space do in lambda prod`() {
        val mockData = " "
        val returnData = mockData.environmentCase({
            "staging"
        }, {
            "preprod"
        }, {
            "prod"
        })
        assertEquals("prod", returnData)
    }

    @Test
    fun `test String Extension environmentCase data string other do in lambda prod`() {
        val mockData = "TrueID"
        val returnData = mockData.environmentCase({
            "staging"
        }, {
            "preprod"
        }, {
            "prod"
        })
        assertEquals("prod", returnData)
    }

    @Test
    fun `test String Extension environmentCase data staging do in lambda staging`() {
        val mockData = "staging"
        val returnData = mockData.environmentCase({
            "staging"
        }, {
            "preprod"
        }, {
            "prod"
        })
        assertEquals("staging", returnData)
    }

    @Test
    fun `test String Extension environmentCase data preprod do in lambda preprod`() {
        val mockData = "preprod"
        val returnData = mockData.environmentCase({
            "staging"
        }, {
            "preprod"
        }, {
            "prod"
        })
        assertEquals("preprod", returnData)
    }

    @Test
    fun `test String Extension environmentCase data prod do in lambda prod`() {
        val mockData = "prod"
        val returnData = mockData.environmentCase({
            "staging"
        }, {
            "preprod"
        }, {
            "prod"
        })
        assertEquals("prod", returnData)
    }

    @Test
    fun `test String Extension environmentCase data staging do in lambda staging return type integer`() {
        val mockData = "staging"
        val returnData = mockData.environmentCase({
            0
        }, {
            1
        }, {
            2
        })
        assertEquals(0, returnData)
    }

    @Test
    fun `test String Extension environmentCase data preprod do in lambda preprod return type integer`() {
        val mockData = "preprod"
        val returnData = mockData.environmentCase({
            0
        }, {
            1
        }, {
            2
        })
        assertEquals(1, returnData)
    }

    @Test
    fun `test String Extension environmentCase data prod do in lambda prod return type integer`() {
        val mockData = "prod"
        val returnData = mockData.environmentCase({
            0
        }, {
            1
        }, {
            2
        })
        assertEquals(2, returnData)
    }

    @Test
    fun `test String Extension fillUrlPattern data empty return only start with http`() {
        val mockData = ""
        val returnData = mockData.fillUrlPattern()
        assertEquals("http://", returnData)
    }

    @Test
    fun `test String Extension fillUrlPattern data space return only start with http`() {
        val mockData = " "
        val returnData = mockData.fillUrlPattern()
        assertEquals("http://", returnData)
    }

    @Test
    fun `test String Extension fillUrlPattern data start with space return http string`() {
        val mockData = " trueid"
        val returnData = mockData.fillUrlPattern()
        assertEquals("http://trueid", returnData)
    }

    @Test
    fun `test String Extension fillUrlPattern data middle have space return http string`() {
        val mockData = "trueid plus"
        val returnData = mockData.fillUrlPattern()
        assertEquals("http://trueidplus", returnData)
    }

    @Test
    fun `test String Extension fillUrlPattern data end with space return http string`() {
        val mockData = "trueid "
        val returnData = mockData.fillUrlPattern()
        assertEquals("http://trueid", returnData)
    }

    @Test
    fun `test String Extension fillUrlPattern data normal return http string`() {
        val mockData = "trueid"
        val returnData = mockData.fillUrlPattern()
        assertEquals("http://trueid", returnData)
    }

    @Test
    fun `test String Extension fillUrlPattern data start with http return start with http`() {
        val mockData = "http://trueid"
        val returnData = mockData.fillUrlPattern()
        assertEquals("http://trueid", returnData)
    }

    @Test
    fun `test String Extension fillUrlPattern data start with space http return start with http`() {
        val mockData = " http://trueid"
        val returnData = mockData.fillUrlPattern()
        assertEquals("http://trueid", returnData)
    }

    @Test
    fun `test String Extension fillUrlPattern data end with space http return start with http`() {
        val mockData = "http://trueid "
        val returnData = mockData.fillUrlPattern()
        assertEquals("http://trueid", returnData)
    }

    @Test
    fun `test String Extension fillUrlPattern data start and end with space http return start with http`() {
        val mockData = " http://trueid "
        val returnData = mockData.fillUrlPattern()
        assertEquals("http://trueid", returnData)
    }

    @Test
    fun `test String Extension fillUrlPattern data start with http and have space return start with http`() {
        val mockData = "http: //trueid"
        val returnData = mockData.fillUrlPattern()
        assertEquals("http://trueid", returnData)
    }

    @Test
    fun `test String Extension fillUrlPattern data start with https return start with http`() {
        val mockData = "https://trueid"
        val returnData = mockData.fillUrlPattern()
        assertEquals("https://trueid", returnData)
    }

    @Test
    fun `test String Extension fillUrlPattern data start wrong http return start with http`() {
        val mockData = "htttps: //trueid"
        val returnData = mockData.fillUrlPattern()
        assertEquals("http://htttps://trueid", returnData)
    }

    @Test
    fun `test String Extension insertPeriodically data normal insert space and 4 period return data has space every 4 text`() {
        val mockData = "1111222233334444"
        val returnData = mockData.insertPeriodically(insert = " ", period = 4)
        assertEquals("1111 2222 3333 4444", returnData)
    }

    @Test
    fun `test String Extension insertPeriodically data empty insert space and one period return empty string`() {
        val mockData = ""
        val returnData = mockData.insertPeriodically(insert = " ", period = 1)
        assertEquals("", returnData)
    }

    @Test
    fun `test String Extension insertPeriodically data double text insert space 1 period return data has space every 1 text`() {
        val mockData = "11"
        val returnData = mockData.insertPeriodically(insert = " ", period = 1)
        assertEquals("1 1", returnData)
    }

    @Test
    fun `test String Extension insertPeriodically data 4 text insert space 0 period return old data `() {
        val mockData = "1111"
        val returnData = mockData.insertPeriodically(insert = " ", period = 0)
        assertEquals("1111", returnData)
    }

    @Test
    fun `test String Extension insertPeriodically data 4 text insert space negative numbers period return old data `() {
        val mockData = "1111"
        val returnData = mockData.insertPeriodically(insert = " ", period = -1)
        assertEquals("1111", returnData)
    }

    @Test
    fun `test String Extension insertPeriodically data empty insert empty and one period return old data`() {
        val mockData = ""
        val returnData = mockData.insertPeriodically(insert = "", period = 1)
        assertEquals("", returnData)
    }

    @Test
    fun `test String Extension insertPeriodically data double text insert empty 1 period return old data`() {
        val mockData = "11"
        val returnData = mockData.insertPeriodically(insert = "", period = 1)
        assertEquals("11", returnData)
    }

    @Test
    fun `test String Extension insertPeriodically data 4 text insert empty 0 period return old data `() {
        val mockData = "1111"
        val returnData = mockData.insertPeriodically(insert = "", period = 0)
        assertEquals("1111", returnData)
    }

    @Test
    fun `test String Extension insertPeriodically data 4 text insert empty negative numbers period return old data `() {
        val mockData = "1111"
        val returnData = mockData.insertPeriodically(insert = "", period = -1)
        assertEquals("1111", returnData)
    }

    @Test
    fun `test String Extension removeSpace data no space return old data `() {
        val mockData = "1111"
        val returnData = mockData.removeSpace()
        assertEquals("1111", returnData)
    }

    @Test
    fun `test String Extension removeSpace data start with space return data no space`() {
        val mockData = " 1111"
        val returnData = mockData.removeSpace()
        assertEquals("1111", returnData)
    }

    @Test
    fun `test String Extension removeSpace data end with space return data no space`() {
        val mockData = "1111 "
        val returnData = mockData.removeSpace()
        assertEquals("1111", returnData)
    }

    @Test
    fun `test String Extension removeSpace data space center return data no space`() {
        val mockData = "11 11"
        val returnData = mockData.removeSpace()
        assertEquals("1111", returnData)
    }

    @Test
    fun `test String Extension removeSpace data many space return data no space`() {
        val mockData = "T r u e I D + s p e c i a l"
        val returnData = mockData.removeSpace()
        assertEquals("TrueID+special", returnData)
    }

    @Test
    fun `test String Extension removeSpace data double space return data no space`() {
        val mockData = "TrueID+  special"
        val returnData = mockData.removeSpace()
        assertEquals("TrueID+special", returnData)
    }

    @Test
    fun `test String Extension removeSpaceWithLowerCase data no space return old data `() {
        val mockData = "1111"
        val returnData = mockData.removeSpaceWithLowerCase()
        assertEquals("1111", returnData)
    }

    @Test
    fun `test String Extension removeSpaceWithLowerCase data start with space return data no space`() {
        val mockData = " 1111"
        val returnData = mockData.removeSpaceWithLowerCase()
        assertEquals("1111", returnData)
    }

    @Test
    fun `test String Extension removeSpaceWithLowerCase data end with space return data no space`() {
        val mockData = "1111 "
        val returnData = mockData.removeSpaceWithLowerCase()
        assertEquals("1111", returnData)
    }

    @Test
    fun `test String Extension removeSpaceWithLowerCase data space center return data no space`() {
        val mockData = "11 11"
        val returnData = mockData.removeSpaceWithLowerCase()
        assertEquals("1111", returnData)
    }

    @Test
    fun `test String Extension removeSpaceWithLowerCase data many space return data no space`() {
        val mockData = "T r u e I D + s p e c i a l"
        val returnData = mockData.removeSpaceWithLowerCase()
        assertEquals("trueid+special", returnData)
    }

    @Test
    fun `test String Extension removeSpaceWithLowerCase data double space return data no space`() {
        val mockData = "TrueID+  special"
        val returnData = mockData.removeSpaceWithLowerCase()
        assertEquals("trueid+special", returnData)
    }

    @Test
    fun `test String urlEncode data normal return data encode`() {
        val mockData = "TrueID+special"
        val returnData = mockData.urlEncode()
        assertEquals("TrueID%2Bspecial", returnData)
    }

    @Test
    fun `test String urlEncode data start space return data encode`() {
        val mockData = " TrueID+special"
        val returnData = mockData.urlEncode()
        assertEquals("%20TrueID%2Bspecial", returnData)
    }

    @Test
    fun `test String urlEncode data end space return data encode`() {
        val mockData = "TrueID+special "
        val returnData = mockData.urlEncode()
        assertEquals("TrueID%2Bspecial%20", returnData)
    }

    @Test
    fun `test String urlEncode empty data normal return data empty`() {
        val mockData = ""
        val returnData = mockData.urlEncode()
        assertEquals("", returnData)
    }

    @Test
    fun orEmptyFA_stringIsNotNullOrEmpty_returnValue() {
        val mockValue = "test"
        val response = mockValue.orEmptyFA()
        assertEquals(mockValue, response)
    }

    @Test
    fun orEmptyFA_stringIsEmpty_returnDefaultValue() {
        val mockValue = ""
        val response = mockValue.orEmptyFA()
        assertEquals(" ", response)
    }

    @Test
    fun orEmptyFA_stringIsNull_returnDefaultValue() {
        val mockValue = null
        val response = mockValue.orEmptyFA()
        assertEquals(" ", response)
    }

    @Test
    fun `test string is color and not empty then return true`() {
        val colorMock = "#DFFAFF"
        assertEquals(true, colorMock.checkIsColorAndNotEmpty())
    }

    @Test
    fun `test string is empty then return false`() {
        val colorMock = ""
        assertEquals(false, colorMock.checkIsColorAndNotEmpty())
    }

    @Test
    fun `test string is not color then return false`() {
        val colorMock = "#ZADA"
        assertEquals(false, colorMock.checkIsColorAndNotEmpty())
    }

    @Test
    fun `test string is start with # then remove it`() {
        val mockString = "#ABC"
        assertEquals("ABC", mockString.checkIsStartWithTagAndRemove())
    }

    @Test
    fun `test string is not start with # then not remove it`() {
        val mockString = "A#BC"
        assertEquals("A#BC", mockString.checkIsStartWithTagAndRemove())
    }

    @Test
    fun `test string is not have with # then return default`() {
        val mockString = "ABC"
        assertEquals("ABC", mockString.checkIsStartWithTagAndRemove())
    }

    @Test
    fun `test string is start with # then return true`() {
        val mockString = "#ABC"
        assertTrue { mockString.checkIsStartWithTag() }
    }

    @Test
    fun `test string is have space and start with # then return true`() {
        val mockString = "   #ABC"
        assertTrue { mockString.checkIsStartWithTag() }
    }

    @Test
    fun `test string is not start with # then return false`() {
        val mockString = "   ABC"
        assertFalse { mockString.checkIsStartWithTag() }
    }
    @Test
    fun `test string is have space and not start with # then return false`() {
        val mockString = "ABC"
        assertFalse { mockString.checkIsStartWithTag() }
    }
    @Test
    fun `test string is have %23 return decode`() {
        val mockString = "%23ABC"
        assertEquals("#ABC", mockString.decodeHashTag())
    }

    @Test
    fun `test string is not have %23 return default`() {
        val mockString = "#CBX"
        assertEquals("#CBX", mockString.decodeHashTag())
    }
}
