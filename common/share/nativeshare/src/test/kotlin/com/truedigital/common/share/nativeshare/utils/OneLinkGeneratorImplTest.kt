package com.truedigital.common.share.nativeshare.utils

import android.content.Context
import com.appsflyer.share.ShareInviteHelper
import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.common.share.nativeshare.domain.model.onelink.CreateOneLinkModel
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.runs
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.URL

class OneLinkGeneratorImplTest {
    private val context: Context = mockk(relaxed = true)
    private val userRepository = mockk<UserRepository>()
    private lateinit var oneLinkGeneratorImpl: OneLinkGeneratorImpl

    @BeforeEach
    fun setUp() {
        oneLinkGeneratorImpl = OneLinkGeneratorImpl(
            context = context,
            userRepository = userRepository
        )
    }

    @Test
    fun `generateOneLink is success then data not null`() {
        val callback = mockk<OneLinkCallback>()
        val createOneLinkModel = CreateOneLinkModel(
            campaign = "campaign",
            channel = "channel",
            contentId = "contentId",
            contentType = "contentType",
            deepLinkUrl = "http://host.com",
            desktopUrl = "desktopUrl",
            masterId = "masterId",
            title = "title"
        )
        mockkObject(ShareInviteHelper.generateInviteUrl(context))
        mockkObject(URL(createOneLinkModel.deepLinkUrl))
        every {
            oneLinkGeneratorImpl.generateOneLink(
                createOneLinkModel,
                callback
            )
        }
    }

    @Test
    fun `generateOneLink catch then data is null`() {
        val callback = mockk<OneLinkCallback> {
            every { onFailure(any()) } just runs
        }
        val createOneLinkModel = CreateOneLinkModel()
        mockkObject(ShareInviteHelper.generateInviteUrl(context))
        oneLinkGeneratorImpl.generateOneLink(
            createOneLinkModel,
            callback
        )
    }

    @Test
    fun `generateOneLink success then data is null`() {
        val callback = mockk<OneLinkCallback> {
            every { onFailure(any()) } just runs
        }
        val createOneLinkModel = CreateOneLinkModel(deepLinkUrl = "http://hosts.com")
        mockkObject(ShareInviteHelper.generateInviteUrl(context))
        mockkObject(URL(createOneLinkModel.deepLinkUrl))
        oneLinkGeneratorImpl.generateOneLink(
            createOneLinkModel,
            callback
        )
    }
}
