package com.truedigital.core.extensions

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Locale
import kotlin.test.Ignore
import kotlin.test.assertEquals

class ContextExtensionTest {

    private lateinit var context: Context

    @BeforeEach
    fun setUp() {
        context = mockk(relaxed = true)
    }

    @Ignore
    @Test
    fun `test createIntentFromString`() {
        val packageName = "com.example.package"
        val flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val clazz = Any::class.java

        every { Class.forName(packageName) } returns clazz

        val intent = context.createIntentFromString(packageName, flags)

        assertEquals(Intent(context, clazz).apply { this.flags = flags }, intent)
    }

    @Ignore
    @Test
    fun `test getStringByLocale`() {
        val stringRes = 1234
        val locale = Locale.US
        val formatArgs = arrayOf("arg1", 2)

        every { context.createConfigurationContext(any()) } returns mockk(relaxed = true)
        every { context.resources.getString(stringRes, *formatArgs) } returns "test string"

        val result = context.getStringByLocale(stringRes, locale, *formatArgs)

        assertEquals("test string", result)
        verify {
            context.createConfigurationContext(Configuration(context.resources.configuration).apply { setLocale(locale) })
            context.resources.getString(stringRes, *formatArgs)
        }
    }

    @Ignore
    @Test
    fun `test getLifeCycleOwner`() {
        val contextWrapper = mockk<ContextWrapper>()
        val appCompatActivity = mockk<AppCompatActivity>()

        every { contextWrapper.baseContext } returns appCompatActivity
        every { context.getLifeCycleOwner() } returns null
        every { contextWrapper.getLifeCycleOwner() } returns appCompatActivity

        val result = context.getLifeCycleOwner()

        assertEquals(appCompatActivity, result)
        verify { contextWrapper.getLifeCycleOwner() }
        verify(exactly = 0) { appCompatActivity.getLifeCycleOwner() }
    }
}
