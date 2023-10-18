package com.truedigital.features.tuned.common.extension

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.content.res.Resources.Theme
import android.util.DisplayMetrics
import android.view.View
import android.view.Window
import com.truedigital.features.tuned.common.extensions.actionBarHeight
import com.truedigital.features.tuned.common.extensions.getMusicServiceIntent
import com.truedigital.features.tuned.common.extensions.launchIntent
import com.truedigital.features.tuned.common.extensions.statusBarHeight
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ContextExtensionsTest {
    @RelaxedMockK
    private lateinit var theme: Theme

    @RelaxedMockK
    private lateinit var packageManager: PackageManager

    @RelaxedMockK
    private lateinit var resources: Resources

    @RelaxedMockK
    private lateinit var displayMetrics: DisplayMetrics

    @RelaxedMockK
    private lateinit var window: Window

    @RelaxedMockK
    private lateinit var view: View

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun actionBarHeight_resolveAttributeIsTrue_returnActionBarHeight() {
        // Given
        val mockContext: Context = mockk<Context>(relaxed = true)
        every { mockContext.theme } returns theme
        every { theme.resolveAttribute(any(), any(), any()) } returns true

        // When
        val result = mockContext.actionBarHeight

        // Then
        assertNotNull(result)
    }

    @Test
    fun actionBarHeight_resolveAttributeIsFalse_returnDefaultActionBarHeight() {
        // Given
        val mockContext: Context = mockk<Context>(relaxed = true)
        every { mockContext.theme } returns theme
        every { theme.resolveAttribute(any(), any(), any()) } returns false

        // When
        val result = mockContext.actionBarHeight

        // Then
        assertEquals(0, result)
    }

    @Test
    fun statusBarHeight_resourceIdMoreThanZero_returnStatusBarHeight() {
        // Given
        val mockHeight = 100
        val mockContext: Context = mockk<Context>(relaxed = true)
        every { mockContext.resources } returns resources
        every { resources.getIdentifier(any(), any(), any()) } returns 10
        every { resources.getDimensionPixelSize(any()) } returns mockHeight

        // When
        val result = mockContext.statusBarHeight

        // Then
        assertEquals(mockHeight, result)
    }

    @Test
    fun statusBarHeight_resourceIdEqualsZero_contextIsActivity_returnStatusBarHeight() {
        // Given
        val mockContext: Context = mockk<Activity>(relaxed = true)
        every { mockContext.resources } returns resources
        every { (mockContext as Activity).window } returns window
        every { window.decorView } returns view

        // When
        val result = mockContext.statusBarHeight

        // Then
        assertNotNull(result)
    }

    @Test
    fun statusBarHeight_resourceIdEqualsZero_contextIsNotActivity_returnStatusBarHeight() {
        // Given
        val mockContext: Context = mockk<Context>(relaxed = true)
        every { mockContext.resources } returns resources
        every { resources.getIdentifier(any(), any(), any()) } returns 0
        every { resources.displayMetrics } returns displayMetrics

        // When
        val result = mockContext.statusBarHeight

        // Then
        assertNotNull(result)
    }

    @Test
    fun launchIntent_launchIntentForPackageIsNotNull_returnIntent() {
        // Given
        val mockContext: Context = mockk<Context>(relaxed = true)
        every { mockContext.packageManager } returns packageManager
        every { mockContext.packageName } returns "packageName"
        every { packageManager.getLaunchIntentForPackage(any()) } returns Intent()

        // When
        val result = mockContext.launchIntent()

        // Then
        assertNotNull(result)
    }

    @Test
    fun launchIntent_launchIntentForPackageIsNull_returnNull() {
        // Given
        val mockContext: Context = mockk<Context>(relaxed = true)
        every { mockContext.packageManager } returns packageManager
        every { mockContext.packageName } returns "packageName"
        every { packageManager.getLaunchIntentForPackage(any()) } returns null

        // When
        val result = mockContext.launchIntent()

        // Then
        assertNull(result)
    }

    @Test
    fun getMusicServiceIntent_returnServiceIntent() {
        // Given
        val mockContext: Context = mockk<Context>(relaxed = true)

        // When
        val result = mockContext.getMusicServiceIntent()

        // Then
        assertNotNull(result)
    }
}
