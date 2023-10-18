package com.truedigital.features.tuned.data.stream.repository

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.tuned.api.steam.StreamApiInterface
import com.truedigital.features.tuned.data.station.model.request.OfflinePlaybackState
import com.truedigital.features.tuned.data.station.model.request.PlaybackState
import io.reactivex.Single
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Date

class StreamRepositoryTest {

    private lateinit var streamRepository: StreamRepository
    private val streamApi: StreamApiInterface = mock()

    @BeforeEach
    fun setup() {
        streamRepository = StreamRepositoryImpl(streamApi)
    }

    @Test
    fun testGet_whenDeviceId_returnSuccess() {
        whenever(streamApi.streamLocation(any(), any(), any())).thenReturn(
            Single.just("123")
        )

        streamRepository.get(123, 1, "1")
            .test()
            .assertNoErrors()
            .assertComplete()

        verify(streamApi, times(1)).streamLocation("1", 123, 1)
    }

    @Test
    fun testGet_whenStakkarId_returnSuccess() {
        whenever(streamApi.stakkarStreamLocation(any())).thenReturn(
            Single.just("123")
        )

        streamRepository.get(123)
            .test()
            .assertNoErrors()
            .assertComplete()

        verify(streamApi, times(1)).stakkarStreamLocation(123)
    }

    @Test
    fun testPutPlaybackState_returnSuccess() {
        whenever(streamApi.logPlay(any(), any())).thenReturn(
            Single.just("Item")
        )

        streamRepository.putPlaybackState(
            123,
            PlaybackState(1, "state", "fileSource", 1, "source", 1, "guid")
        )
            .test()
            .assertNoErrors()
            .assertComplete()

        verify(streamApi, times(1)).logPlay(
            123,
            PlaybackState(1, "state", "fileSource", 1, "source", 1, "guid")
        )
    }

    @Test
    fun testPutOfflinePlaybackState_returnSuccess() {
        whenever(
            streamApi.logOfflinePlay(
                123,
                listOf(
                    OfflinePlaybackState(
                        Date(2000, 12, 1),
                        PlaybackState(1, "state", "fileSource", 1, "source", 1, "guid")
                    )
                )
            )
        ).thenReturn(
            Single.just(Any())
        )

        streamRepository.putOfflinePlaybackState(
            123,
            listOf(
                OfflinePlaybackState(
                    Date(2000, 12, 1),
                    PlaybackState(1, "state", "fileSource", 1, "source", 1, "guid")
                )
            )
        )
            .test()
            .assertNoErrors()
            .assertComplete()

        verify(streamApi, times(1)).logOfflinePlay(
            123,
            listOf(
                OfflinePlaybackState(
                    Date(2000, 12, 1),
                    PlaybackState(1, "state", "fileSource", 1, "source", 1, "guid")
                )
            )
        )
    }
}
