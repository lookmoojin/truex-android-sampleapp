package com.truedigital.features.truecloudv3.data.repository

import android.os.Build
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal interface BuildVersionCheckRepositoryTestCase {
    fun `test isGreaterOrEqualVersionSdk27 case true`()
    fun `test isGreaterOrEqualVersionSdk27 case false`()
}

internal class BuildVersionCheckRepositoryTest : BuildVersionCheckRepositoryTestCase {

    private lateinit var buildVersionCheckRepository: BuildVersionCheckRepository

    @BeforeEach
    fun setUp() {
        buildVersionCheckRepository = BuildVersionCheckRepositoryImpl()
    }
    @Disabled
    @Test
    override fun `test isGreaterOrEqualVersionSdk27 case true`() {
        // arrange
        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 27)

        // act
        val result = buildVersionCheckRepository.isGreaterOrEqualVersionSdk27()

        // assert
        assertTrue(result)
    }

    @Disabled
    @Test
    override fun `test isGreaterOrEqualVersionSdk27 case false`() {
        // arrange
        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 25)

        // act
        val result = buildVersionCheckRepository.isGreaterOrEqualVersionSdk27()

        // assert
        assertFalse(result)
    }

    @Throws(Exception::class)
    private fun setFinalStatic(field: Field, newValue: Any?) {
        field.isAccessible = true
        val modifiersField: Field = Field::class.java.getDeclaredField("modifiers")
        modifiersField.isAccessible = true
        modifiersField.setInt(field, field.modifiers and Modifier.FINAL.inv())
        field.set(null, newValue)
    }
}
