package com.truedigital.features.music.domain.track.usecase

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.listens.share.constant.MusicConstant
import com.truedigital.features.music.data.track.repository.MusicTrackRepository
import com.truedigital.features.music.data.trending.model.response.playlist.Translation
import com.truedigital.features.utils.MockDataModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetTrackListUseCaseTest {

    private lateinit var getTrackListUseCase: GetTrackListUseCase
    private val musicTrackRepository: MusicTrackRepository = mock()
    private val mockTrackList = listOf(
        MockDataModel.mockTrack.copy(
            id = 1,
            translationsList = listOf(
                Translation(
                    language = com.truedigital.features.listens.share.constant.MusicConstant.Language.LANG_TH,
                    value = "nameTh"
                )
            )
        )
    )

    @BeforeEach
    fun setup() {
        getTrackListUseCase = GetTrackListUseCaseImpl(musicTrackRepository)
    }

    @Test
    fun testGetTrackList_success_returnTrackList() = runTest {
        whenever(musicTrackRepository.getTrackList(any(), any(), any())).thenReturn(
            flowOf(
                mockTrackList
            )
        )

        getTrackListUseCase.execute(1)
            .collect { list ->
                list.size == 1 && list.firstOrNull()?.id == 1 && list.firstOrNull()?.name == "nameTh"
            }
    }

    @Test
    fun testGetTrackList_fail_returnError() = runTest {
        whenever(musicTrackRepository.getTrackList(any(), any(), any())).thenReturn(
            flow {
                error("error")
            }
        )

        getTrackListUseCase.execute(1)
            .catch {}
            .collect()
    }
}
