package com.truedigital.features.tuned.common.extension

import android.os.Bundle
import android.os.Parcelable
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.tuned.common.extensions.put
import kotlinx.parcelize.Parcelize
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import java.io.Serializable
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class BundleExtensionsTest {

    private val bundle = mock(Bundle::class.java)

    @Test
    fun testPutBundle_matchType_returnBundle() {
        val mockString = "nameString"
        val mockCharSequence: CharSequence = "nameCharSequence"
        val mockShort: Short = 12345
        val mockParcelable = MockBundleParcelable()
        val mockSerializable = MockBundleSerializable()
        val mockBundle = Bundle()

        doReturn(1).whenever(bundle).getInt("intKey")
        doReturn(2L).whenever(bundle).getLong("longKey")
        doReturn(mockCharSequence).whenever(bundle).getCharSequence("charSequenceKey")
        doReturn(mockString).whenever(bundle).getString("stringKey")
        doReturn(3F).whenever(bundle).getFloat("floatKey")
        doReturn(4.0).whenever(bundle).getDouble("doubleKey")
        doReturn('a').whenever(bundle).getChar("charKey")
        doReturn(mockShort).whenever(bundle).getShort("shortKey")
        doReturn(true).whenever(bundle).getBoolean("booleanKey")
        doReturn(mockParcelable).whenever(bundle)
            .getParcelable<MockBundleParcelable>("parcelableKey")
        doReturn(mockSerializable).whenever(bundle).getSerializable("serializableKey")
        doReturn(mockBundle).whenever(bundle).getBundle("bundleKey")

        bundle.put(
            "nullKey" to null,
            "intKey" to 1,
            "longKey" to 2L,
            "charSequenceKey" to mockCharSequence,
            "stringKey" to mockString,
            "floatKey" to 3F,
            "doubleKey" to 4.0,
            "charKey" to 'a',
            "shortKey" to mockShort,
            "booleanKey" to true,
            "parcelableKey" to mockParcelable,
            "serializableKey" to mockSerializable,
            "bundleKey" to mockBundle
        )

        assertNotNull(bundle)
        assertEquals(false, bundle.containsKey("nullKey"))
        assertEquals(1, bundle.getInt("intKey"))
        assertEquals(2L, bundle.getLong("longKey"))
        assertEquals(mockCharSequence, bundle.getCharSequence("charSequenceKey"))
        assertEquals(mockString, bundle.getString("stringKey"))
        assertEquals(3F, bundle.getFloat("floatKey"))
        assertEquals(4.0, bundle.getDouble("doubleKey"))
        assertEquals('a', bundle.getChar("charKey"))
        assertEquals(mockShort, bundle.getShort("shortKey"))
        assertEquals(true, bundle.getBoolean("booleanKey"))
        assertEquals(mockParcelable, bundle.getParcelable("parcelableKey"))
        assertEquals(mockSerializable, bundle.getSerializable("serializableKey"))
        assertEquals(mockBundle, bundle.getBundle("bundleKey"))
    }

    @Test
    fun testPutBundle_matchTypeArray_returnBundle() {
        val mockCharSequence: CharSequence = "nameCharSequence"
        val mockArrayCharSequence: Array<CharSequence> = arrayOf(mockCharSequence, mockCharSequence)
        val mockArrayString: Array<String> = arrayOf("a", "b")
        val mockArrayParcelable: Array<Parcelable> =
            arrayOf(MockBundleParcelable(), MockBundleParcelable())
        val mockIntArray: IntArray = intArrayOf(1, 2, 3)
        val mockLongArray = longArrayOf(1L, 2L, 3L)
        val mockFloatArray = floatArrayOf(1F, 2F, 3F)
        val mockDoubleArray = doubleArrayOf(1.0, 2.0, 3.0)
        val mockCharArray = charArrayOf('a', 'b', 'c')
        val mockShortArray = shortArrayOf(11111, 22222)
        val mockBooleanArray = booleanArrayOf(true, false)

        doReturn(mockArrayCharSequence).whenever(bundle)
            .getCharSequenceArray("arrayCharSequenceKey")
        doReturn(mockArrayString).whenever(bundle).getStringArray("arrayStringKey")
        doReturn(mockArrayParcelable).whenever(bundle).getParcelableArray("arrayParcelableKey")
        doReturn(mockIntArray).whenever(bundle).getIntArray("intArrayKey")
        doReturn(mockLongArray).whenever(bundle).getLongArray("longArrayKey")
        doReturn(mockFloatArray).whenever(bundle).getFloatArray("floatArrayKey")
        doReturn(mockDoubleArray).whenever(bundle).getDoubleArray("doubleArrayKey")
        doReturn(mockCharArray).whenever(bundle).getCharArray("charArrayKey")
        doReturn(mockShortArray).whenever(bundle).getShortArray("shortArrayKey")
        doReturn(mockBooleanArray).whenever(bundle).getBooleanArray("booleanArrayKey")

        bundle.put(
            "arrayCharSequenceKey" to mockArrayCharSequence,
            "arrayStringKey" to mockArrayString,
            "arrayParcelableKey" to mockArrayParcelable,
            "intArrayKey" to mockIntArray,
            "longArrayKey" to mockLongArray,
            "floatArrayKey" to mockFloatArray,
            "doubleArrayKey" to mockDoubleArray,
            "charArrayKey" to mockCharArray,
            "shortArrayKey" to mockShortArray,
            "booleanArrayKey" to mockBooleanArray
        )

        assertEquals(mockArrayCharSequence, bundle.getCharSequenceArray("arrayCharSequenceKey"))
        assertEquals(mockArrayString, bundle.getStringArray("arrayStringKey"))
        assertEquals(mockArrayParcelable, bundle.getParcelableArray("arrayParcelableKey"))
        assertEquals(mockIntArray, bundle.getIntArray("intArrayKey"))
        assertEquals(mockLongArray, bundle.getLongArray("longArrayKey"))
        assertEquals(mockFloatArray, bundle.getFloatArray("floatArrayKey"))
        assertEquals(mockDoubleArray, bundle.getDoubleArray("doubleArrayKey"))
        assertEquals(mockCharArray, bundle.getCharArray("charArrayKey"))
        assertEquals(mockShortArray, bundle.getShortArray("shortArrayKey"))
        assertEquals(mockBooleanArray, bundle.getBooleanArray("booleanArrayKey"))
    }

    @Test
    fun testPutBundle_notMatchType_returnException() {
        val mockArrayInt = arrayListOf(1, 2)
        doReturn(mockArrayInt).whenever(bundle).get("arrayIntKey")

        bundle.put(
            "arrayIntKey" to mockArrayInt,
        )

        try {
            bundle.get("arrayIntKey")
        } catch (e: RuntimeException) {
            assertEquals(
                "Intent extra arrayIntKey has wrong type ${mockArrayInt.javaClass.name}",
                e.message
            )
        }
    }
}

@Parcelize
private class MockBundleParcelable : Parcelable

private class MockBundleSerializable : Serializable
