package com.truedigital.features.tuned.data.profile.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ProfileTest {

    private val mockId = 1
    private val mockUserName = "userName"
    private val mockName = "name"
    private val mockImage = "image"
    private val mockBackgroundImage = "backgroundImage"
    private val mockIsVerified = true
    private val mockContentKey = "contentKey"
    private val mockFollowerCount = 100

    private val mockProfile = Profile(
        id = mockId,
        username = mockUserName,
        name = mockName,
        image = mockImage,
        backgroundImage = mockBackgroundImage,
        isVerified = mockIsVerified,
        contentKey = mockContentKey,
        followerCount = mockFollowerCount
    )

    @Test
    fun testGetValue() {
        val profile = mockProfile.copy()

        assertEquals(profile.id, mockId)
        assertEquals(profile.username, mockUserName)
        assertEquals(profile.name, mockName)
        assertEquals(profile.image, mockImage)
        assertEquals(profile.backgroundImage, mockBackgroundImage)
        assertEquals(profile.isVerified, mockIsVerified)
        assertEquals(profile.contentKey, mockContentKey)
        assertEquals(profile.followerCount, mockFollowerCount)
    }

    @Test
    fun testSetValue() {
        val id = 200
        val username = "mockUserName"
        val name = "mockName"
        val image = "mockImage"
        val backgroundImage = "mockBackgroundImage"
        val isVerified = true
        val contentKey = "mockContentKey"
        val followerCount = 500

        val profile = mockProfile.copy()

        profile.id = id
        profile.username = username
        profile.name = name
        profile.image = image
        profile.backgroundImage = backgroundImage
        profile.isVerified = isVerified
        profile.contentKey = contentKey
        profile.followerCount = followerCount

        assertEquals(profile.id, id)
        assertEquals(profile.username, username)
        assertEquals(profile.name, name)
        assertEquals(profile.image, image)
        assertEquals(profile.backgroundImage, backgroundImage)
        assertEquals(profile.isVerified, isVerified)
        assertEquals(profile.contentKey, contentKey)
        assertEquals(profile.followerCount, followerCount)
    }

    @Test
    fun getFollowerCountString_followerCountMoreThan1000_returnCountString() {
        val mockProfileValue = mockProfile.copy(followerCount = 12000)
        val value = mockProfileValue.getFollowerCountString()

        assertEquals(value, "1000+")
    }

    @Test
    fun getFollowerCountString_followerCountEquals1000_returnCountString() {
        val mockProfileValue = mockProfile.copy(followerCount = 1000)
        val value = mockProfileValue.getFollowerCountString()

        assertEquals(value, "1000+")
    }

    @Test
    fun getFollowerCountString_followerCountMoreThan500AndLessThan1000_returnCountString() {
        val mockProfileValue = mockProfile.copy(followerCount = 700)
        val value = mockProfileValue.getFollowerCountString()

        assertEquals(value, "500+")
    }

    @Test
    fun getFollowerCountString_followerCountEquals500_returnCountString() {
        val mockProfileValue = mockProfile.copy(followerCount = 500)
        val value = mockProfileValue.getFollowerCountString()

        assertEquals(value, "500+")
    }

    @Test
    fun getFollowerCountString_followerCountLessThan100_returnCountString() {
        val mockProfileValue = mockProfile.copy(followerCount = 99)
        val value = mockProfileValue.getFollowerCountString()

        assertEquals(value, "<100")
    }

    @Test
    fun getFollowerCountString_followerCountEquals100_returnCountString() {
        val mockProfileValue = mockProfile.copy(followerCount = 100)
        val value = mockProfileValue.getFollowerCountString()

        assertEquals(value, "100+")
    }

    @Test
    fun getFollowerCountString_followerCountMoreThan99AndLessThan500_returnCountString() {
        val mockProfileValue = mockProfile.copy(followerCount = 220)
        val value = mockProfileValue.getFollowerCountString()

        assertEquals(value, "200+")
    }
}
