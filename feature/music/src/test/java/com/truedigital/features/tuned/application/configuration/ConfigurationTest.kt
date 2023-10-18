package com.truedigital.features.tuned.application.configuration

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class ConfigurationTest {

    private val result = Configuration(
        thumborUrl = "thumborUrl",
        servicesUrl = "servicesUrl",
        metadataUrl = "metadataUrl",
        authUrl = "authUrl",
        storeId = "storeId",
        applicationId = Configuration.APP_ID_TRUE,
    )

    @Test
    fun testIsTrue_isAppIdTrue_returnTrue() {
        assertEquals(true, result.isTrue)
    }

    @Test
    fun testIsTrue_isNotAppIdTrue_returnFalse() {
        val resultAppId = result.copy(applicationId = 1)
        assertEquals(false, resultAppId.isTrue)
    }

    @Test
    fun testEnablePlaylistEditing_default_returnFalse() {
        assertEquals(false, result.enablePlaylistEditing)
    }

    @Test
    fun testEnableShare_default_returnFalse() {
        assertEquals(false, result.enableShare)
    }

    @Test
    fun testEnableTabUnderline_default_returnFalse() {
        assertEquals(false, result.enableTabUnderline)
    }

    @Test
    fun testEnableArtistCount_default_returnFalse() {
        assertEquals(false, result.enableArtistCount)
    }

    @Test
    fun testEnableShareAndFavIcon_default_returnFalse() {
        assertEquals(false, result.enableShareAndFavIcon)
    }

    @Test
    fun testEnableRadioButton_default_returnFalse() {
        assertEquals(false, result.enableRadioButton)
    }

    @Test
    fun testEnableHintOverlay_default_returnFalse() {
        assertEquals(false, result.enableHintOverlay)
    }
}
