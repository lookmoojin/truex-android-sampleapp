package com.truedigital.features.tuned.common.extension

import com.nhaarman.mockitokotlin2.mock
import com.truedigital.features.tuned.common.extensions.localisedStringForLanguage
import com.truedigital.features.tuned.common.extensions.localisedStringForSystemLanguage
import com.truedigital.features.tuned.common.extensions.valueForSystemLanguage
import com.truedigital.features.tuned.data.util.LocalisedString
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class TranslationExtensionsTest {

    // valueForSystemLanguage //////////////////////////////////////////////////////////////////////
    @Test
    fun testValueForSystemLanguage_emptyList_returnNull() {
        val result = listOf<LocalisedString>().valueForSystemLanguage(mock())
        assertEquals(null, result)
    }

    // localisedStringForSystemLanguage ////////////////////////////////////////////////////////////
    @Test
    fun testLocalisedStringForSystemLanguage_emptyList_returnNull() {
        val result = listOf<LocalisedString>().localisedStringForSystemLanguage(mock())
        assertEquals(null, result)
    }

    // localisedStringForLanguage //////////////////////////////////////////////////////////////////
    @Test
    fun testLocalisedStringForLanguage_emptyList_returnNull() {
        val result = listOf<LocalisedString>().localisedStringForLanguage("en")
        assertEquals(null, result)
    }

    @Test
    fun testLocalisedStringForLanguageEn_containsLanguageEn_returnLocalisedStringEn() {
        val result = listOf(
            LocalisedString(language = "en", value = "nameEn"),
            LocalisedString(language = "th", value = "nameTh")
        ).localisedStringForLanguage("en")

        assertEquals("en", result?.language)
        assertEquals("nameEn", result?.value)
    }

    @Test
    fun testLocalisedStringForLanguageEn_valueEnEmpty_returnLocalisedStringTh() {
        val result = listOf(
            LocalisedString(language = "en", value = ""),
            LocalisedString(language = "th", value = "nameTh")
        ).localisedStringForLanguage("en")

        assertEquals("th", result?.language)
        assertEquals("nameTh", result?.value)
    }

    @Test
    fun testLocalisedStringForLanguageEn_valueEnNull_returnLocalisedStringTh() {
        val result = listOf(
            LocalisedString(language = "en", value = null),
            LocalisedString(language = "th", value = "nameTh")
        ).localisedStringForLanguage("en")

        assertEquals("th", result?.language)
        assertEquals("nameTh", result?.value)
    }

    @Test
    fun testLocalisedStringForLanguageTh_containsLanguageTh_returnLocalisedStringTh() {
        val result = listOf(
            LocalisedString(language = "en", value = "nameEn"),
            LocalisedString(language = "th", value = "nameTh")
        ).localisedStringForLanguage("th")

        assertEquals("th", result?.language)
        assertEquals("nameTh", result?.value)
    }

    @Test
    fun testLocalisedStringForLanguageTh_valueThEmpty_returnLocalisedStringEn() {
        val result = listOf(
            LocalisedString(language = "en", value = "nameEn"),
            LocalisedString(language = "th", value = "")
        ).localisedStringForLanguage("th")

        assertEquals("en", result?.language)
        assertEquals("nameEn", result?.value)
    }

    @Test
    fun testLocalisedStringForLanguageTh_valueThNull_returnLocalisedStringEn() {
        val result = listOf(
            LocalisedString(language = "en", value = "nameEn"),
            LocalisedString(language = "th", value = null)
        ).localisedStringForLanguage("th")

        assertEquals("en", result?.language)
        assertEquals("nameEn", result?.value)
    }

    @Test
    fun testLocalisedStringForLanguageTh_valueThEnNull_returnNull() {
        val result = listOf(
            LocalisedString(language = "en", value = null),
            LocalisedString(language = "th", value = null)
        ).localisedStringForLanguage("th")

        assertEquals(null, result)
    }

    @Test
    fun testLocalisedStringForLanguageEn_notContainsLanguageEn_returnLocalisedStringTh() {
        val result = listOf(
            LocalisedString(language = "th", value = "nameTh")
        ).localisedStringForLanguage("en")

        assertEquals("th", result?.language)
        assertEquals("nameTh", result?.value)
    }

    @Test
    fun testLocalisedStringForLanguageEn_splitLanguageEn_returnLocalisedStringEn() {
        val result = listOf(
            LocalisedString(language = "EN", value = "nameEn"),
            LocalisedString(language = "TH", value = "nameTh")
        ).localisedStringForLanguage("en_th")

        assertEquals("EN", result?.language)
        assertEquals("nameEn", result?.value)
    }

    @Test
    fun testLocalisedStringForLanguageEn_splitLanguageEnValueEmpty_returnLocalisedStringTh() {
        val result = listOf(
            LocalisedString(language = "EN", value = ""),
            LocalisedString(language = "TH", value = "nameTh")
        ).localisedStringForLanguage("en_th")

        assertEquals("TH", result?.language)
        assertEquals("nameTh", result?.value)
    }

    @Test
    fun testLocalisedStringForLanguageEn_splitLanguageEnValueNull_returnLocalisedStringTh() {
        val result = listOf(
            LocalisedString(language = "EN", value = null),
            LocalisedString(language = "TH", value = "nameTh")
        ).localisedStringForLanguage("en_th")

        assertEquals("TH", result?.language)
        assertEquals("nameTh", result?.value)
    }

    @Test
    fun testLocalisedStringForLanguageEn_splitLanguageEnValueNull_returnLocalisedStringNull() {
        val result = listOf(
            LocalisedString(language = "EN", value = null)
        ).localisedStringForLanguage("en_th")

        assertEquals(null, result)
    }

    @Test
    fun testLocalisedStringForLanguageEn_splitLanguageThValueNull_returnLocalisedStringNull() {
        val result = listOf(
            LocalisedString(language = "EN", value = null)
        ).localisedStringForLanguage("th_en")

        assertEquals(null, result)
    }
}
