package com.truedigital.features.truecloudv3.extension

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.truedigital.share.mock.utils.BuildUtils
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TrueCloudV3FragmentExtensionTest {

    @Test
    fun `test checkStoragePermissionAlready returns correct value`() {
        BuildUtils.setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 30)
        val fragment = mockk<Fragment>()
        val context = mockk<Context>()
        every { fragment.context } returns context
        mockkStatic("androidx.core.content.ContextCompat")
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        val permission2 = Manifest.permission.WRITE_EXTERNAL_STORAGE
        val permission3 = Manifest.permission.READ_MEDIA_IMAGES
        val permission4 = Manifest.permission.READ_MEDIA_AUDIO
        val permission5 = Manifest.permission.READ_MEDIA_VIDEO
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission
            )
        } returns PackageManager.PERMISSION_GRANTED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission2
            )
        } returns PackageManager.PERMISSION_GRANTED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission3
            )
        } returns PackageManager.PERMISSION_GRANTED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission4
            )
        } returns PackageManager.PERMISSION_GRANTED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission5
            )
        } returns PackageManager.PERMISSION_GRANTED

        every { fragment.context } returns mockk()

        val result = fragment.checkStoragePermissionAlready()

        assertTrue(result)
    }

    @Test
    fun `test checkStoragePermissionAlready READ_EXTERNAL_STORAGE false returns false value`() {
        BuildUtils.setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 30)
        val fragment = mockk<Fragment>()
        val context = mockk<Context>()
        every { fragment.context } returns context
        mockkStatic("androidx.core.content.ContextCompat")
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        val permission2 = Manifest.permission.WRITE_EXTERNAL_STORAGE
        val permission3 = Manifest.permission.READ_MEDIA_IMAGES
        val permission4 = Manifest.permission.READ_MEDIA_AUDIO
        val permission5 = Manifest.permission.READ_MEDIA_VIDEO
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission
            )
        } returns PackageManager.PERMISSION_DENIED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission2
            )
        } returns PackageManager.PERMISSION_GRANTED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission3
            )
        } returns PackageManager.PERMISSION_GRANTED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission4
            )
        } returns PackageManager.PERMISSION_GRANTED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission5
            )
        } returns PackageManager.PERMISSION_GRANTED

        every { fragment.context } returns mockk()

        val result = fragment.checkStoragePermissionAlready()

        assertEquals(false, result)
    }

    @Test
    fun `test checkStoragePermissionAlready WRITE_EXTERNAL_STORAGE false returns false value`() {
        BuildUtils.setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 30)
        val fragment = mockk<Fragment>()
        val context = mockk<Context>()
        every { fragment.context } returns context
        mockkStatic("androidx.core.content.ContextCompat")
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        val permission2 = Manifest.permission.WRITE_EXTERNAL_STORAGE
        val permission3 = Manifest.permission.READ_MEDIA_IMAGES
        val permission4 = Manifest.permission.READ_MEDIA_AUDIO
        val permission5 = Manifest.permission.READ_MEDIA_VIDEO
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission
            )
        } returns PackageManager.PERMISSION_GRANTED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission2
            )
        } returns PackageManager.PERMISSION_DENIED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission3
            )
        } returns PackageManager.PERMISSION_GRANTED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission4
            )
        } returns PackageManager.PERMISSION_GRANTED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission5
            )
        } returns PackageManager.PERMISSION_GRANTED

        every { fragment.context } returns mockk()

        val result = fragment.checkStoragePermissionAlready()

        assertEquals(false, result)
    }

    @Test
    fun `test checkStoragePermissionAlready READ_EXTERNAL_STORAGE and WRITE_EXTERNAL_STORAGE false returns false value`() {
        BuildUtils.setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 30)
        val fragment = mockk<Fragment>()
        val context = mockk<Context>()
        every { fragment.context } returns context
        mockkStatic("androidx.core.content.ContextCompat")
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        val permission2 = Manifest.permission.WRITE_EXTERNAL_STORAGE
        val permission3 = Manifest.permission.READ_MEDIA_IMAGES
        val permission4 = Manifest.permission.READ_MEDIA_AUDIO
        val permission5 = Manifest.permission.READ_MEDIA_VIDEO
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission
            )
        } returns PackageManager.PERMISSION_DENIED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission2
            )
        } returns PackageManager.PERMISSION_DENIED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission3
            )
        } returns PackageManager.PERMISSION_GRANTED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission4
            )
        } returns PackageManager.PERMISSION_GRANTED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission5
            )
        } returns PackageManager.PERMISSION_GRANTED

        every { fragment.context } returns mockk()

        val result = fragment.checkStoragePermissionAlready()

        assertEquals(false, result)
    }

    @Test
    fun `test checkStoragePermissionAlready android 13 returns correct value`() {
        BuildUtils.setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 33)
        val fragment = mockk<Fragment>()
        val context = mockk<Context>()
        every { fragment.context } returns context
        mockkStatic("androidx.core.content.ContextCompat")
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        val permission2 = Manifest.permission.WRITE_EXTERNAL_STORAGE
        val permission3 = Manifest.permission.READ_MEDIA_IMAGES
        val permission4 = Manifest.permission.READ_MEDIA_AUDIO
        val permission5 = Manifest.permission.READ_MEDIA_VIDEO
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission
            )
        } returns PackageManager.PERMISSION_GRANTED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission2
            )
        } returns PackageManager.PERMISSION_GRANTED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission3
            )
        } returns PackageManager.PERMISSION_GRANTED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission4
            )
        } returns PackageManager.PERMISSION_GRANTED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission5
            )
        } returns PackageManager.PERMISSION_GRANTED

        every { fragment.context } returns mockk()

        val result = fragment.checkStoragePermissionAlready()

        assertTrue(result)
    }

    @Test
    fun `test checkStoragePermissionAlready android 13 READ_MEDIA_IMAGES false returns false value`() {
        BuildUtils.setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 33)
        val fragment = mockk<Fragment>()
        val context = mockk<Context>()
        every { fragment.context } returns context
        mockkStatic("androidx.core.content.ContextCompat")
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        val permission2 = Manifest.permission.WRITE_EXTERNAL_STORAGE
        val permission3 = Manifest.permission.READ_MEDIA_IMAGES
        val permission4 = Manifest.permission.READ_MEDIA_AUDIO
        val permission5 = Manifest.permission.READ_MEDIA_VIDEO
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission
            )
        } returns PackageManager.PERMISSION_GRANTED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission2
            )
        } returns PackageManager.PERMISSION_GRANTED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission3
            )
        } returns PackageManager.PERMISSION_DENIED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission4
            )
        } returns PackageManager.PERMISSION_GRANTED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission5
            )
        } returns PackageManager.PERMISSION_GRANTED

        every { fragment.context } returns mockk()

        val result = fragment.checkStoragePermissionAlready()

        assertEquals(false, result)
    }

    @Test
    fun `test checkStoragePermissionAlready android 13 READ_MEDIA_AUDIO false returns false value`() {
        BuildUtils.setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 33)
        val fragment = mockk<Fragment>()
        val context = mockk<Context>()
        every { fragment.context } returns context
        mockkStatic("androidx.core.content.ContextCompat")
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        val permission2 = Manifest.permission.WRITE_EXTERNAL_STORAGE
        val permission3 = Manifest.permission.READ_MEDIA_IMAGES
        val permission4 = Manifest.permission.READ_MEDIA_AUDIO
        val permission5 = Manifest.permission.READ_MEDIA_VIDEO
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission
            )
        } returns PackageManager.PERMISSION_GRANTED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission2
            )
        } returns PackageManager.PERMISSION_GRANTED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission3
            )
        } returns PackageManager.PERMISSION_GRANTED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission4
            )
        } returns PackageManager.PERMISSION_DENIED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission5
            )
        } returns PackageManager.PERMISSION_GRANTED

        every { fragment.context } returns mockk()

        val result = fragment.checkStoragePermissionAlready()

        assertEquals(false, result)
    }

    @Test
    fun `test checkStoragePermissionAlready android 13 READ_MEDIA_VIDEO false returns false value`() {
        BuildUtils.setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 33)
        val fragment = mockk<Fragment>()
        val context = mockk<Context>()
        every { fragment.context } returns context
        mockkStatic("androidx.core.content.ContextCompat")
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        val permission2 = Manifest.permission.WRITE_EXTERNAL_STORAGE
        val permission3 = Manifest.permission.READ_MEDIA_IMAGES
        val permission4 = Manifest.permission.READ_MEDIA_AUDIO
        val permission5 = Manifest.permission.READ_MEDIA_VIDEO
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission
            )
        } returns PackageManager.PERMISSION_GRANTED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission2
            )
        } returns PackageManager.PERMISSION_GRANTED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission3
            )
        } returns PackageManager.PERMISSION_GRANTED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission4
            )
        } returns PackageManager.PERMISSION_GRANTED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission5
            )
        } returns PackageManager.PERMISSION_DENIED

        every { fragment.context } returns mockk()

        val result = fragment.checkStoragePermissionAlready()

        assertEquals(false, result)
    }

    @Test
    fun `test checkContactStoragePermissionAlready android 13 returns correct value`() {
        BuildUtils.setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 33)
        val fragment = mockk<Fragment>()
        val context = mockk<Context>()
        every { fragment.context } returns context
        mockkStatic("androidx.core.content.ContextCompat")
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        val permission2 = Manifest.permission.WRITE_EXTERNAL_STORAGE
        val permission3 = Manifest.permission.READ_MEDIA_IMAGES
        val permission4 = Manifest.permission.READ_MEDIA_AUDIO
        val permission5 = Manifest.permission.READ_MEDIA_VIDEO
        val permission6 = Manifest.permission.READ_CONTACTS

        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission
            )
        } returns PackageManager.PERMISSION_GRANTED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission2
            )
        } returns PackageManager.PERMISSION_GRANTED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission3
            )
        } returns PackageManager.PERMISSION_GRANTED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission4
            )
        } returns PackageManager.PERMISSION_GRANTED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission5
            )
        } returns PackageManager.PERMISSION_GRANTED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission6
            )
        } returns PackageManager.PERMISSION_GRANTED

        every { fragment.context } returns mockk()

        val result = fragment.checkContactStoragePermissionAlready()

        assertTrue(result)
    }

    @Test
    fun `test checkContactStoragePermissionAlready android 13 READ_MEDIA_IMAGES false returns false value`() {
        BuildUtils.setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 33)
        val fragment = mockk<Fragment>()
        val context = mockk<Context>()
        every { fragment.context } returns context
        mockkStatic("androidx.core.content.ContextCompat")
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        val permission2 = Manifest.permission.WRITE_EXTERNAL_STORAGE
        val permission3 = Manifest.permission.READ_MEDIA_IMAGES
        val permission4 = Manifest.permission.READ_MEDIA_AUDIO
        val permission5 = Manifest.permission.READ_MEDIA_VIDEO
        val permission6 = Manifest.permission.READ_CONTACTS

        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission
            )
        } returns PackageManager.PERMISSION_GRANTED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission2
            )
        } returns PackageManager.PERMISSION_GRANTED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission3
            )
        } returns PackageManager.PERMISSION_DENIED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission4
            )
        } returns PackageManager.PERMISSION_GRANTED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission5
            )
        } returns PackageManager.PERMISSION_GRANTED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission6
            )
        } returns PackageManager.PERMISSION_GRANTED

        every { fragment.context } returns mockk()

        val result = fragment.checkContactStoragePermissionAlready()

        assertEquals(false, result)
    }

    @Test
    fun `test checkContactStoragePermissionAlready android 13 READ_CONTACTS false returns false value`() {
        BuildUtils.setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 33)
        val fragment = mockk<Fragment>()
        val context = mockk<Context>()
        every { fragment.context } returns context
        mockkStatic("androidx.core.content.ContextCompat")
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        val permission2 = Manifest.permission.WRITE_EXTERNAL_STORAGE
        val permission3 = Manifest.permission.READ_MEDIA_IMAGES
        val permission4 = Manifest.permission.READ_MEDIA_AUDIO
        val permission5 = Manifest.permission.READ_MEDIA_VIDEO
        val permission6 = Manifest.permission.READ_CONTACTS

        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission
            )
        } returns PackageManager.PERMISSION_GRANTED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission2
            )
        } returns PackageManager.PERMISSION_GRANTED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission3
            )
        } returns PackageManager.PERMISSION_GRANTED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission4
            )
        } returns PackageManager.PERMISSION_GRANTED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission5
            )
        } returns PackageManager.PERMISSION_GRANTED
        every {
            ContextCompat.checkSelfPermission(
                any(),
                permission6
            )
        } returns PackageManager.PERMISSION_DENIED

        every { fragment.context } returns mockk()

        val result = fragment.checkContactStoragePermissionAlready()

        assertEquals(false, result)
    }

    @Test
    fun `test getStoragePermissions android 13 returns correct value array size is 3`() {
        BuildUtils.setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 33)
        val fragment = mockk<Fragment>()

        val result = fragment.getStoragePermissions()

        assertEquals(3, result.size)
    }

    @Test
    fun `test getStoragePermissions returns correct value array size is 2`() {
        BuildUtils.setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 30)
        val fragment = mockk<Fragment>()

        val result = fragment.getStoragePermissions()

        assertEquals(2, result.size)
    }

    @Test
    fun `test getNotificationPermissions android 13 returns correct value array size is 4`() {
        BuildUtils.setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 33)
        val fragment = mockk<Fragment>()

        val result = fragment.getNotificationPermission()

        assertEquals(4, result.size)
    }

    @Test
    fun `test getNotificationPermissions returns correct value array size is 2`() {
        BuildUtils.setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 30)
        val fragment = mockk<Fragment>()

        val result = fragment.getNotificationPermission()

        assertEquals(2, result.size)
    }
}
