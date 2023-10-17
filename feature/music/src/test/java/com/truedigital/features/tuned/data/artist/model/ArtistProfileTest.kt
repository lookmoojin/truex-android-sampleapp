package com.truedigital.features.tuned.data.artist.model

import com.truedigital.features.tuned.data.artist.model.response.ArtistProfile
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ArtistProfileTest {

    @Test
    fun testArtistProfileData_initialValueData() {
        val mockArtistProfile = ArtistProfile(
            identity = ArtistProfile.Identity(
                id = 1,
                name = "name"
            ),
            image = "image"
        )
        assertEquals(1, mockArtistProfile.identity.id)
        assertEquals("name", mockArtistProfile.identity.name)
        assertEquals("image", mockArtistProfile.image)
    }

    @Test
    fun testArtistProfileData_initialDefaultData() {
        val mockArtistProfile = ArtistProfile(
            identity = ArtistProfile.Identity(
                id = 1,
                name = "name"
            ),
            image = "image"
        )
        assertEquals(1, mockArtistProfile.identity.id)
        assertEquals("name", mockArtistProfile.identity.name)
        assertEquals("image", mockArtistProfile.image)
    }

    @Test
    fun testArtistProfileData_setData() {
        val mockArtistProfile = ArtistProfile(
            identity = ArtistProfile.Identity(
                id = 1,
                name = "name"
            ),
            image = "image"
        )
        mockArtistProfile.identity = ArtistProfile.Identity(
            id = 2,
            name = "name2"
        )
        mockArtistProfile.image = "image2"

        assertEquals(2, mockArtistProfile.identity.id)
        assertEquals("name2", mockArtistProfile.identity.name)
        assertEquals("image2", mockArtistProfile.image)
    }
}
