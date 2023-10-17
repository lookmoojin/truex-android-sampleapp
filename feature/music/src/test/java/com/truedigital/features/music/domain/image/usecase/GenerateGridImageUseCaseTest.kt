package com.truedigital.features.music.domain.image.usecase

import com.truedigital.features.music.domain.image.usecase.GenerateGridImageUseCaseImpl.Companion.URL_SEPARATOR
import com.truedigital.features.utils.MockDataModel
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class GenerateGridImageUseCaseTest {

    private lateinit var generateGridImageUseCase: GenerateGridImageUseCase

    @BeforeEach
    fun setup() {
        generateGridImageUseCase = GenerateGridImageUseCaseImpl()
    }

    @Test
    fun generateGridImage_trackListEmpty_returnEmpty() {
        val result = generateGridImageUseCase.execute(listOf())

        assertEquals("", result.first)
        assertEquals(0, result.second.size)
    }

    @Test
    fun generateGridImage_trackListOneSize_returnDefault() {
        val mockTrackList = listOf(MockDataModel.mockTrack.copy(image = "url"))

        val result = generateGridImageUseCase.execute(mockTrackList)

        assertEquals("url", result.first)
        assertEquals(0, result.second.size)
    }

    @Test
    fun generateGridImage_trackListImageEmptyAll_returnDefault() {
        val mockTrackList = listOf(
            MockDataModel.mockTrack.copy(image = ""),
            MockDataModel.mockTrack.copy(image = ""),
            MockDataModel.mockTrack.copy(image = ""),
            MockDataModel.mockTrack.copy(image = "")
        )

        val result = generateGridImageUseCase.execute(mockTrackList)

        assertEquals("", result.first)
        assertEquals(0, result.second.size)
    }

    @Test
    fun generateGridImage_trackListImage_removePrefix_returnFilterString() {
        val mockTrackList = listOf(
            MockDataModel.mockTrack.copy(image = "https://url1"),
            MockDataModel.mockTrack.copy(image = "https://url2"),
            MockDataModel.mockTrack.copy(image = "https://url3"),
            MockDataModel.mockTrack.copy(image = "https://url4")
        )

        val result = generateGridImageUseCase.execute(mockTrackList)

        assertEquals("https://url1", result.first)
        assertEquals(
            "distributed_collage(grid,url1${URL_SEPARATOR}url2${URL_SEPARATOR}url3${URL_SEPARATOR}url4)",
            result.second.first()
        )
    }
}
