package com.truedigital.common.share.nativeshare

import android.content.Context
import com.truedigital.common.share.nativeshare.constant.NativeShareConstant
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.Ignore

class NativeShareManagerImplTest {
    private val context: Context = mockk(relaxed = true)
    private lateinit var nativeShareManagerImpl: NativeShareManagerImpl

    @BeforeEach
    fun setUp() {
        nativeShareManagerImpl = NativeShareManagerImpl(
            context = context
        )
    }

    @Ignore
    @Test
    fun `onSharePres is shareType to image`() {
        nativeShareManagerImpl.onSharePress(
            shortUrl = "",
            title = "",
            imageUrl = "",
            shareType = NativeShareConstant.IMAGE
        ) {}
    }

    @Test
    fun `onSharePress is empty value`() {
        nativeShareManagerImpl.onSharePress(
            shortUrl = "",
            title = "",
            imageUrl = "",
            shareType = ""
        )
    }
}
