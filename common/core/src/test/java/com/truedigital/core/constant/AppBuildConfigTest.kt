package com.truedigital.core.constant

import com.truedigital.core.BuildConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class AppBuildConfigTest {

    @Test
    fun environment() {
        assertEquals(BuildConfig.ENVIRONMENT, AppBuildConfig.environment())
    }
}
