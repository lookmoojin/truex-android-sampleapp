package com.truedigital.features.tuned.presentation.station.presenter

import android.os.Bundle
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.presentation.bottomsheet.PickerOptions
import com.truedigital.features.tuned.presentation.station.facade.StationFacade
import com.truedigital.features.utils.MockDataModel
import com.truedigital.share.mock.reactivex.RxImmediateSchedulerExtension
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExtendWith(RxImmediateSchedulerExtension::class)
internal class StationPresenterTest {

    private val view: StationPresenter.ViewSurface = mock()
    private val router: StationPresenter.RouterSurface = mock()
    private val stationFacade: StationFacade = mock()
    private val stationSubscription: Disposable = mock()
    private val isFavouritedSubscription: Disposable = mock()
    private val toggleFavouriteSubscription: Disposable = mock()
    private val clearVotesSubscription: Disposable = mock()
    private lateinit var presenter: StationPresenter

    @BeforeEach
    fun setUp() {
        presenter = StationPresenter(stationFacade = stationFacade)
        presenter.onInject(view, router)
    }

    @Test
    fun onStart_argumentIsNotNull_containsKey_STATION_ID_KEY_loadStation() {
        // Given
        val mockBundle = Mockito.mock(Bundle::class.java)
        whenever(mockBundle.containsKey(StationPresenter.STATION_ID_KEY)).thenReturn(true)
        whenever(mockBundle.getInt(StationPresenter.STATION_ID_KEY)).thenReturn(10)
        whenever(stationFacade.loadStation(any())).thenReturn(Single.just(MockDataModel.mockStation))

        // When
        presenter.onStart(mockBundle)

        // Then
        verify(stationFacade, times(1)).loadStation(any())
    }

    @Test
    fun onStart_argumentIsNotNull_containsKey_STATION_ID_KEY_stationIsNotNull_initStationAndShowOnlineStation() {
        // Given
        val mockBundle = Mockito.mock(Bundle::class.java)
        whenever(mockBundle.containsKey(StationPresenter.STATION_KEY)).thenReturn(true)
        whenever(mockBundle.getParcelable<Station>(StationPresenter.STATION_KEY)).thenReturn(
            MockDataModel.mockStation
        )
        whenever(stationFacade.loadFavourited(any())).thenReturn(Single.just(true))

        // When
        presenter.onStart(mockBundle)

        // Then
        verify(view, times(1)).initStation(any())
        verify(view, times(1)).showOnlineStation(any(), anyOrNull(), any())
        verify(stationFacade, times(1)).loadFavourited(any())
    }

    @Test
    fun onStart_argumentIsNotNull_containsKey_STATION_ID_KEY_stationIsNull_doNothing() {
        // Given
        val mockBundle = Mockito.mock(Bundle::class.java)
        whenever(mockBundle.containsKey(StationPresenter.STATION_KEY)).thenReturn(true)
        whenever(mockBundle.getParcelable<Station>(StationPresenter.STATION_KEY)).thenReturn(null)

        // When
        presenter.onStart(mockBundle)

        // Then
        verify(view, times(0)).initStation(any())
        verify(view, times(0)).showOnlineStation(any(), anyOrNull(), any())
        verify(stationFacade, times(0)).loadFavourited(any())
    }

    @Test
    fun onStart_argumentIsNull_doNothing() {
        // When
        presenter.onStart(null)

        // Then
        verify(stationFacade, times(0)).loadStation(any())
    }

    @Test
    fun onResume_getStationSubscriptionSuccess_getFavouriteSubscriptionSuccess_showOnlineStationAndFavourite() {
        // Given
        val favouriteResponse = Single.just(true)
        val mockStationSubscription: Disposable = mock()
        val mockIsFavouriteSubscription: Disposable = mock()
        presenter.setSubscription(
            stationSubscription = mockStationSubscription,
            isFavouritedSubscription = mockIsFavouriteSubscription
        )
        presenter.setObservable(
            stationObservable = Single.just(MockDataModel.mockStation),
            isFavouritedObservable = favouriteResponse
        )
        whenever(stationFacade.loadFavourited(any())).thenReturn(favouriteResponse)

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).initStation(any())
        verify(view, times(1)).showOnlineStation(any(), anyOrNull(), any())
        verify(view, times(1)).showFavourited(any())
        verify(stationFacade, times(1)).loadFavourited(any())
    }

    @Test
    fun onResume_getStationSubscriptionSuccess_getFavouriteSubscriptionFail_showOnlineStationAndFavourite() {
        // Given
        val favouriteResponse = Single.error<Boolean>(Throwable("Unknown favorite type"))
        val mockStationSubscription: Disposable = mock()
        val mockIsFavouriteSubscription: Disposable = mock()
        presenter.setSubscription(
            stationSubscription = mockStationSubscription,
            isFavouritedSubscription = mockIsFavouriteSubscription
        )
        presenter.setObservable(
            stationObservable = Single.just(MockDataModel.mockStation),
            isFavouritedObservable = favouriteResponse
        )
        whenever(stationFacade.loadFavourited(any())).thenReturn(favouriteResponse)

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).initStation(any())
        verify(view, times(1)).showOnlineStation(any(), anyOrNull(), any())
        verify(view, times(1)).showFavourited(any())
        verify(stationFacade, times(1)).loadFavourited(any())
    }

    @Test
    fun onResume_getStationSubscriptionFail_showLoadStationError() {
        // Given
        val mockStationSubscription: Disposable = mock()
        presenter.setSubscription(stationSubscription = mockStationSubscription)
        presenter.setObservable(stationObservable = Single.error(Throwable("error")))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showLoadStationError()
    }

    @Test
    fun onResume_getIsFavouriteSubscriptionSuccess_showFavourite() {
        // Given
        val mockIsFavouriteSubscription: Disposable = mock()
        presenter.setSubscription(isFavouritedSubscription = mockIsFavouriteSubscription)
        presenter.setObservable(isFavouritedObservable = Single.just(true))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showFavourited(any())
    }

    @Test
    fun onResume_getIsFavouriteSubscriptionFail_showFavourite() {
        // Given
        val mockIsFavouriteSubscription: Disposable = mock()
        presenter.setSubscription(isFavouritedSubscription = mockIsFavouriteSubscription)
        presenter.setObservable(isFavouritedObservable = Single.error(Throwable("error")))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showFavourited(any())
    }

    @Test
    fun onResume_getToggleFavouriteSubscriptionSuccess_favouriteIsTrue_showFavouriteToast() {
        // Given
        val mockToggleFavouriteSubscription: Disposable = mock()
        presenter.setPrivateData(isFavourited = true)
        presenter.setSubscription(toggleFavouriteSubscription = mockToggleFavouriteSubscription)
        presenter.setObservable(toggleFavouriteObservable = Single.just(Any()))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showFavouritedToast()
    }

    @Test
    fun onResume_getToggleFavouriteSubscriptionSuccess_favouriteIsFalse_doNothing() {
        // Given
        val mockToggleFavouriteSubscription: Disposable = mock()
        presenter.setPrivateData(isFavourited = false)
        presenter.setSubscription(toggleFavouriteSubscription = mockToggleFavouriteSubscription)
        presenter.setObservable(toggleFavouriteObservable = Single.just(Any()))

        // When
        presenter.onResume()

        // Then
        verify(view, times(0)).showFavouritedToast()
    }

    @Test
    fun onResume_getToggleFavouriteSubscriptionFail_showFavouriteError() {
        // Given
        val mockToggleFavouriteSubscription: Disposable = mock()
        presenter.setSubscription(toggleFavouriteSubscription = mockToggleFavouriteSubscription)
        presenter.setObservable(toggleFavouriteObservable = Single.error(Throwable("errror")))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showFavourited(any())
        verify(view, times(1)).showFavouritedError()
    }

    @Test
    fun onResume_getClearVotesSubscriptionSuccess_showVotesCleared() {
        // Given
        val mockClearVotesSubscription: Disposable = mock()
        presenter.setSubscription(clearVotesSubscription = mockClearVotesSubscription)
        presenter.setObservable(clearVotesObservable = Single.just(Any()))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showVotesCleared()
    }

    @Test
    fun onResume_getClearVotesSubscriptionFail_showVotesCleared() {
        // Given
        val mockClearVotesSubscription: Disposable = mock()
        presenter.setSubscription(clearVotesSubscription = mockClearVotesSubscription)
        presenter.setObservable(clearVotesObservable = Single.error(Throwable("error")))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showClearVotesError()
    }

    @Test
    fun onPause_subscriptionIsNotNull_disposeSubscription() {
        // Given
        presenter.setSubscription(
            stationSubscription = stationSubscription,
            isFavouritedSubscription = isFavouritedSubscription,
            toggleFavouriteSubscription = toggleFavouriteSubscription,
            clearVotesSubscription = clearVotesSubscription
        )

        // When
        presenter.onPause()

        // Then
        verify(stationSubscription, times(1)).dispose()
        verify(isFavouritedSubscription, times(1)).dispose()
        verify(toggleFavouriteSubscription, times(1)).dispose()
        verify(clearVotesSubscription, times(1)).dispose()
    }

    @Test
    fun onPause_subscriptionIsNull_doNothing() {
        // Given
        presenter.setSubscription(
            stationSubscription = null,
            isFavouritedSubscription = null,
            toggleFavouriteSubscription = null,
            clearVotesSubscription = null
        )

        // When
        presenter.onPause()

        // Then
        verify(stationSubscription, times(0)).dispose()
        verify(isFavouritedSubscription, times(0)).dispose()
        verify(toggleFavouriteSubscription, times(0)).dispose()
        verify(clearVotesSubscription, times(0)).dispose()
    }

    @Test
    fun onMoreOptionSelected_selectionIsAddToCollection_stationIsNotNull_observableIsNull_returnTrueAndShowFavourited() {
        // Given
        presenter.setPrivateData(station = MockDataModel.mockStation)
        presenter.setObservable(toggleFavouriteObservable = null)
        whenever(stationFacade.toggleFavourite(any())).thenReturn(Single.just(Any()))

        // When
        val result = presenter.onMoreOptionSelected(PickerOptions.ADD_TO_COLLECTION)

        // Then
        assertTrue(result)
        verify(stationFacade, times(1)).toggleFavourite(any())
        verify(view, times(1)).showFavourited(any())
    }

    @Test
    fun onMoreOptionSelected_selectionIsAddToCollection_stationIsNotNull_observableIsNotNull_returnTrue() {
        // Given
        presenter.setPrivateData(station = MockDataModel.mockStation)
        presenter.setObservable(toggleFavouriteObservable = Single.just(Any()))

        // When
        val result = presenter.onMoreOptionSelected(PickerOptions.ADD_TO_COLLECTION)

        // Then
        assertTrue(result)
        verify(stationFacade, times(0)).toggleFavourite(any())
        verify(view, times(0)).showFavourited(any())
    }

    @Test
    fun onMoreOptionSelected_selectionIsAddToCollection_stationIsNull_returnTrue() {
        // Given
        presenter.setPrivateData(station = null)

        // When
        val result = presenter.onMoreOptionSelected(PickerOptions.ADD_TO_COLLECTION)

        // Then
        assertTrue(result)
        verify(stationFacade, times(0)).toggleFavourite(any())
        verify(view, times(0)).showFavourited(any())
    }

    @Test
    fun onMoreOptionSelected_selectionIsRemoveFromCollection_stationIsNotNull_observableIsNull_returnTrueAndShowFavourited() {
        // Given
        presenter.setPrivateData(station = MockDataModel.mockStation)
        presenter.setObservable(toggleFavouriteObservable = null)
        whenever(stationFacade.toggleFavourite(any())).thenReturn(Single.just(Any()))

        // When
        val result = presenter.onMoreOptionSelected(PickerOptions.REMOVE_FROM_COLLECTION)

        // Then
        assertTrue(result)
        verify(stationFacade, times(1)).toggleFavourite(any())
        verify(view, times(1)).showFavourited(any())
    }

    @Test
    fun onMoreOptionSelected_selectionIsRemoveFromCollection_stationIsNotNull_observableIsNotNull_returnTrue() {
        // Given
        presenter.setPrivateData(station = MockDataModel.mockStation)
        presenter.setObservable(toggleFavouriteObservable = Single.just(Any()))

        // When
        val result = presenter.onMoreOptionSelected(PickerOptions.REMOVE_FROM_COLLECTION)

        // Then
        assertTrue(result)
        verify(stationFacade, times(0)).toggleFavourite(any())
        verify(view, times(0)).showFavourited(any())
    }

    @Test
    fun onMoreOptionSelected_selectionIsRemoveFromCollection_stationIsNull_returnTrue() {
        // Given
        presenter.setPrivateData(station = null)

        // When
        val result = presenter.onMoreOptionSelected(PickerOptions.REMOVE_FROM_COLLECTION)

        // Then
        assertTrue(result)
        verify(stationFacade, times(0)).toggleFavourite(any())
        verify(view, times(0)).showFavourited(any())
    }

    @Test
    fun onMoreOptionSelected_selectionIsClearVote_stationIsNotNull_observableIsNull_returnTrueAndClearVote() {
        // Given
        presenter.setPrivateData(station = MockDataModel.mockStation)
        presenter.setObservable(clearVotesObservable = null)
        whenever(stationFacade.clearVotes(any())).thenReturn(Single.just(Any()))

        // When
        val result = presenter.onMoreOptionSelected(PickerOptions.CLEAR_VOTE)

        // Then
        assertTrue(result)
        verify(stationFacade, times(1)).clearVotes(any())
    }

    @Test
    fun onMoreOptionSelected_selectionIsClearVote_stationIsNotNull_observableIsNotNull_returnTrue() {
        // Given
        presenter.setPrivateData(station = MockDataModel.mockStation)
        presenter.setObservable(clearVotesObservable = Single.just(Any()))

        // When
        val result = presenter.onMoreOptionSelected(PickerOptions.CLEAR_VOTE)

        // Then
        assertTrue(result)
        verify(stationFacade, times(0)).clearVotes(any())
    }

    @Test
    fun onMoreOptionSelected_selectionIsClearVote_stationIsNull_returnTrue() {
        // Given
        presenter.setPrivateData(
            station = null
        )

        // When
        val result = presenter.onMoreOptionSelected(PickerOptions.CLEAR_VOTE)

        // Then
        assertTrue(result)
        verify(stationFacade, times(0)).clearVotes(any())
    }

    @Test
    fun onMoreOptionSelected_selectionIsElse_returnFalse() {
        // When
        val result = presenter.onMoreOptionSelected(PickerOptions.PLAY_VIDEO)

        // Then
        assertFalse(result)
    }

    @Test
    fun onImageSelected_showEnlargedImage() {
        // When
        presenter.onImageSelected()

        // Then
        verify(view, times(0)).showEnlargedImage(any())
    }

    @Test
    fun onShowMoreOptions_stationIsNotNull_showMoreOptions() {
        // Given
        presenter.setPrivateData(station = MockDataModel.mockStation)

        // When
        presenter.onShowMoreOptions()

        // Then
        verify(view, times(1)).showMoreOptions(any())
    }

    @Test
    fun onShowMoreOptions_stationIsNull_doNothing() {
        // Given
        presenter.setPrivateData(station = null)

        // When
        presenter.onShowMoreOptions()

        // Then
        verify(view, times(0)).showMoreOptions(any())
    }
}
