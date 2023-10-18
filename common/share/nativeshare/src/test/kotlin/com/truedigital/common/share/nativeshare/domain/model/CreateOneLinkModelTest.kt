package com.truedigital.common.share.nativeshare.domain.model

import com.truedigital.common.share.nativeshare.domain.model.onelink.CreateOneLinkModel
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class CreateOneLinkModelTest {
    @Test
    fun `set data for CreateOneLinkModel`() {
        val data = CreateOneLinkModel(
            deepLinkUrl = ""
        )
        data.campaign = "redeem"
        data.channel = "https://movie.trueid.net/series/OXzQr5GWJQ1o/{83e6jzLO5gbl}"
        data.contentId = "communicator"
        data.contentType = "movie"
        data.deepLinkUrl = "trueid://home.trueid.net/inapp-browser?website=https://www.trueid.net"
        data.desktopUrl = "https://www.trueid.net"
        data.masterId = "https://movie.trueid.net/series/{OXzQr5GWJQ1o}/83e6jzLO5gbl"
        data.title = "communicator"

        assertTrue {
            data.campaign == "redeem" &&
                data.channel == "https://movie.trueid.net/series/OXzQr5GWJQ1o/{83e6jzLO5gbl}" &&
                data.contentId == "communicator" &&
                data.contentType == "movie" &&
                data.deepLinkUrl == "trueid://home.trueid.net/inapp-browser?website=https://www.trueid.net" &&
                data.desktopUrl == "https://www.trueid.net" &&
                data.masterId == "https://movie.trueid.net/series/{OXzQr5GWJQ1o}/83e6jzLO5gbl" &&
                data.title == "communicator"
        }
    }
}
