package com.truedigital.features.tuned.presentation.station.presenter

import android.os.Bundle
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.tuned.data.station.model.LikedTrack
import com.truedigital.features.tuned.data.station.model.Rating
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.station.model.Vote
import com.truedigital.features.tuned.presentation.station.facade.TuningFacade
import com.truedigital.features.utils.MockDataModel
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.util.Date

internal class TuningPresenterTest {

    private lateinit var tuningPresenter: TuningPresenter
    private val tuningFacade: TuningFacade = mock()
    private val view: TuningPresenter.ViewSurface = mock()
    private val mockDeleteLikeSubscription: Disposable = mock()
    private val mockVotesSubscription: Disposable = mock()

    @BeforeEach
    fun setup() {
        tuningPresenter = TuningPresenter(tuningFacade)
        tuningPresenter.onInject(view)
    }

    @Test
    fun testOnStart_argumentNull_notVerifyShowProgress() {
        // When
        tuningPresenter.onStart(null)

        // Then
        verify(view, times(0)).showProgress()
        verify(view, times(0)).showStation(any())
    }

    @Test
    fun testOnStart_argumentStationNull_notVerifyShowStation() {
        // Given
        val mockBundle = Mockito.mock(Bundle::class.java)
        whenever(mockBundle.getParcelable<Station>(TuningPresenter.STATION_KEY)).thenReturn(null)
        whenever(tuningFacade.getStationTrackVotes(anyOrNull())).thenReturn(Single.just(listOf()))

        // When
        tuningPresenter.onStart(mockBundle)

        // Then
        verify(view, times(1)).showProgress()
        verify(view, times(0)).showStation(any())
    }

    @Test
    fun testOnStart_argumentStationNotNull_verifyShowStation() {
        // Given
        val mockBundle = Mockito.mock(Bundle::class.java)
        whenever(mockBundle.getParcelable<Station>(TuningPresenter.STATION_KEY)).thenReturn(
            MockDataModel.mockStation
        )
        whenever(tuningFacade.getStationTrackVotes(anyOrNull())).thenReturn(Single.just(listOf()))

        // When
        tuningPresenter.onStart(mockBundle)

        // Then
        verify(view, times(1)).showProgress()
        verify(view, times(1)).showStation(any())
    }

    @Test
    fun onPause_subscriptionIsNotNull_disposeSubscription() {
        // Given
        tuningPresenter.setSubscription(
            deleteLikeSubscription = mockDeleteLikeSubscription,
            votesSubscription = mockVotesSubscription
        )

        // When
        tuningPresenter.onPause()

        // Then
        verify(mockDeleteLikeSubscription, times(1)).dispose()
        verify(mockVotesSubscription, times(1)).dispose()
    }

    @Test
    fun onPause_subscriptionIsNull_doNothing() {
        // Given
        tuningPresenter.setSubscription(
            deleteLikeSubscription = null,
            votesSubscription = null
        )

        // When
        tuningPresenter.onPause()

        // Then
        verify(mockDeleteLikeSubscription, times(0)).dispose()
        verify(mockVotesSubscription, times(0)).dispose()
    }

    @Test
    fun getDeleteLikeSubscription_success_getVotesObservableIsCalled() {
        // Given
        tuningPresenter.setSubscription(deleteLikeSubscription = mockDeleteLikeSubscription)
        tuningPresenter.setObservable(deleteLikeObservable = Single.just(MockDataModel.mockVote))
        whenever(tuningFacade.getStationTrackVotes(anyOrNull())).thenReturn(Single.just(listOf()))

        // When
        tuningPresenter.onResume()

        // Then
        verify(tuningFacade, times(1)).getStationTrackVotes(anyOrNull())
    }

    @Test
    fun getDeleteLikeSubscription_fail_showDeleteError() {
        // Given
        tuningPresenter.setSubscription(deleteLikeSubscription = mockDeleteLikeSubscription)
        tuningPresenter.setObservable(deleteLikeObservable = Single.error(Throwable("error")))

        // When
        tuningPresenter.onResume()

        // Then
        verify(view, times(1)).showDeleteError()
    }

    @Test
    fun getVotesSubscription_success_votesAfterFilterIsEmpty_showLikeEmpty() {
        // Given
        val mockLikeTrackValue = MockDataModel.mockLikeTrack.copy(type = Rating.DISLIKED)
        tuningPresenter.setPrivateData(currentFilter = Rating.LIKED)
        tuningPresenter.setSubscription(votesSubscription = mockVotesSubscription)
        tuningPresenter.setObservable(votesObservable = Single.just(listOf(mockLikeTrackValue)))

        // When
        tuningPresenter.onResume()

        // Then
        verify(view, times(1)).showLikesEmpty()
    }

    @Test
    fun getVotesSubscription_success_votesAfterFilterIsNotEmpty_showVotes() {
        // Given
        val mockLikeTrackValue = MockDataModel.mockLikeTrack.copy(type = Rating.LIKED)
        tuningPresenter.setPrivateData(currentFilter = Rating.LIKED)
        tuningPresenter.setSubscription(votesSubscription = mockVotesSubscription)
        tuningPresenter.setObservable(votesObservable = Single.just(listOf(mockLikeTrackValue)))

        // When
        tuningPresenter.onResume()

        // Then
        verify(view, times(1)).showVotes(any())
    }

    @Test
    fun getVotesSubscription_fail_errorIsHttpException_errorCodeIsResourceNotFound_showDislikesError() {
        // Given
        tuningPresenter.setPrivateData(currentFilter = Rating.DISLIKED)
        tuningPresenter.setSubscription(votesSubscription = mockVotesSubscription)
        tuningPresenter.setObservable(votesObservable = Single.error(MockDataModel.mockHttpExceptionCodeResourceNotFound))

        // When
        tuningPresenter.onResume()

        // Then
        verify(view, times(1)).showDislikesEmpty()
    }

    @Test
    fun getVotesSubscription_fail_errorIsHttpException_errorCodeIsNotResourceNotFound_showDislikesError() {
        // Given
        tuningPresenter.setPrivateData(currentFilter = Rating.DISLIKED)
        tuningPresenter.setSubscription(votesSubscription = mockVotesSubscription)
        tuningPresenter.setObservable(votesObservable = Single.error(MockDataModel.mockHttpExceptionCodeUnauthorised))

        // When
        tuningPresenter.onResume()

        // Then
        verify(view, times(1)).showDislikesError()
    }

    @Test
    fun getVotesSubscription_fail_errorIsNotHttpException_showLikesError() {
        // Given
        tuningPresenter.setPrivateData(currentFilter = Rating.LIKED)
        tuningPresenter.setSubscription(votesSubscription = mockVotesSubscription)
        tuningPresenter.setObservable(votesObservable = Single.error(Throwable("error")))

        // When
        tuningPresenter.onResume()

        // Then
        verify(view, times(1)).showLikesError()
    }

    @Test
    fun testOnUpdateRating_verifyGetStationTrackVotes() {
        // Given
        whenever(tuningFacade.getStationTrackVotes(anyOrNull())).thenReturn(Single.just(listOf()))

        // When
        tuningPresenter.onUpdateRating()

        // Then
        verify(tuningFacade, times(1)).getStationTrackVotes(anyOrNull())
    }

    @Test
    fun testOnRemoveVote_loadApi_verifyDeleteVote() {
        // Given
        whenever(tuningFacade.getStationTrackVotes(anyOrNull())).thenReturn(Single.just(listOf()))
        whenever(tuningFacade.deleteVote(anyOrNull(), anyOrNull())).thenReturn(
            Single.just(
                Vote(1, "vote", "type", Date())
            )
        )

        // When
        tuningPresenter.onRemoveVote(
            LikedTrack(type = Rating.LIKED, track = null, artists = listOf())
        )

        // Then
        verify(tuningFacade, times(1)).deleteVote(anyOrNull(), anyOrNull())
    }

    @Test
    fun testOnRemoveVote_notLoadApi_verifyShowDeleteInProgressError() {
        // Given
        whenever(tuningFacade.getStationTrackVotes(anyOrNull())).thenReturn(Single.just(listOf()))
        whenever(tuningFacade.deleteVote(anyOrNull(), anyOrNull())).thenReturn(
            Single.just(Vote(1, "vote", "type", Date()))
        )
        tuningPresenter.setObservable(
            deleteLikeObservable = Single.just(Vote(1, "vote", "type", Date()))
        )

        // When
        tuningPresenter.onRemoveVote(
            LikedTrack(type = Rating.LIKED, track = null, artists = listOf())
        )

        // Then
        verify(view, times(1)).showDeleteInProgressError()
    }

    @Test
    fun testOnVotesSelected_notVerifyGetStationTrackVotes() {
        // When
        tuningPresenter.onVotesSelected(Rating.DISLIKED)

        // Then
        verify(tuningFacade, times(0)).getStationTrackVotes(anyOrNull())
    }

    @Test
    fun testShowLikeOrDislikeEmpty_ratingLiked_verifyShowLikesEmpty() {
        // Given
        tuningPresenter.onVotesSelected(Rating.LIKED)

        // When
        tuningPresenter.showLikeOrDislikeEmpty()

        // Then
        verify(view, times(1)).showLikesEmpty()
    }

    @Test
    fun testShowLikeOrDislikeEmpty_ratingDisliked_verifyShowDislikesEmpty() {
        // Given
        tuningPresenter.onVotesSelected(Rating.DISLIKED)

        // When
        tuningPresenter.showLikeOrDislikeEmpty()

        // Then
        verify(view, times(1)).showDislikesEmpty()
    }

    @Test
    fun testShowLikeOrDislikeError_ratingLiked_verifyShowLikesError() {
        // Given
        tuningPresenter.onVotesSelected(Rating.LIKED)

        // When
        tuningPresenter.showLikeOrDislikeError()

        // Then
        verify(view, times(1)).showLikesError()
    }

    @Test
    fun testShowLikeOrDislikeError_ratingDisliked_verifyShowDislikesError() {
        // Given
        tuningPresenter.onVotesSelected(Rating.DISLIKED)

        // When
        tuningPresenter.showLikeOrDislikeError()

        // Then
        verify(view, times(1)).showDislikesError()
    }
}
