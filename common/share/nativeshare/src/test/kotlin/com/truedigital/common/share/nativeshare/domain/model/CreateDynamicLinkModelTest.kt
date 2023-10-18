package com.truedigital.common.share.nativeshare.domain.model

import com.truedigital.common.share.nativeshare.domain.model.dynamiclink.CreateDynamicLinkModel
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class CreateDynamicLinkModelTest {
    @Test
    fun `set data for CreateDynamicLinkModel`() {
        val data = CreateDynamicLinkModel(
            longUrl = "longUrl",
            url = "url",
            title = "title",
            description = "description",
            imageUrl = "imageUrl",
            errorMegDisplay = 0
        )
        assertTrue {
            data.longUrl == "longUrl" &&
                data.url == "url" &&
                data.title == "title" &&
                data.description == "description" &&
                data.imageUrl == "imageUrl" &&
                data.errorMegDisplay == 0
        }
    }
}
