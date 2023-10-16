package com.truedigital.component.widget.livecommerce.domain.usecase

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CreateCommerceLiveDeeplinkUseCaseTest {

    private lateinit var useCase: CreateCommerceLiveDeeplinkUseCase

    @BeforeEach
    fun setUp() {
        useCase = CreateCommerceLiveDeeplinkUseCaseImpl()
    }

    @Test
    fun `create deep-link, with given postId and streamId`() {
        val givenPostId = "pstid"
        val givenStreamId = "strmid"
        val expected = "https://shopping.trueid.net/commercelive/$givenPostId/$givenStreamId"

        val actual = useCase.execute(givenPostId, givenStreamId)

        assertEquals(
            expected = expected,
            actual = actual
        )
    }

    @Test
    fun `create deep-link, with empty both postId and streamId`() {
        val givenPostId = ""
        val givenStreamId = ""
        val expected = "https://shopping.trueid.net/commercelive"

        val actual = useCase.execute(givenPostId, givenStreamId)

        assertEquals(
            expected = expected,
            actual = actual
        )
    }

    @Test
    fun `create deep-link, with null both postId and streamId`() {
        val givenPostId = null
        val givenStreamId = null
        val expected = "https://shopping.trueid.net/commercelive"

        val actual = useCase.execute(givenPostId, givenStreamId)

        assertEquals(
            expected = expected,
            actual = actual
        )
    }
}
