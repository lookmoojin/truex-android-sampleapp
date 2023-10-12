package com.truedigital.core.manager.location

import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationRequest
import com.truedigital.foundation.extension.addTo
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider
import javax.inject.Inject

interface LocationManager {
    fun getLatitude(): String
    fun getLongitude(): String
    fun startAttractiveLocation(context: Context)
    fun stopAttractiveLocation()
    fun onLocationChange(): Observable<Location>
}

class LocationManagerImpl @Inject constructor() : LocationManager {

    companion object {
        /**
         * 10 Minute for update device location
         * */
        private const val MINUTE_FOR_UPDATE_DEVICE_LOCATION = (10 * 60 * 1000).toLong()

        val instance: LocationManager by lazy {
            LocationManagerImpl()
        }
    }

    private val locationChange: PublishSubject<Location> = PublishSubject.create()
    private var currentLocation: Location? = null
    private val compositeDisposable = CompositeDisposable()

    override fun getLatitude(): String = currentLocation?.latitude?.toString() ?: ""

    override fun getLongitude(): String = currentLocation?.longitude?.toString() ?: ""

    override fun startAttractiveLocation(context: Context) {
        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        locationRequest.interval = MINUTE_FOR_UPDATE_DEVICE_LOCATION

        val locationProvider = ReactiveLocationProvider(context)
        locationProvider.getUpdatedLocation(locationRequest)
            .subscribe(
                { location ->
                    currentLocation = location

                    locationChange.onNext(location)
                },
                { _ ->
                    /** Nothing to do */
                }
            )
            .addTo(composite = compositeDisposable)
    }

    override fun stopAttractiveLocation() {
        compositeDisposable.clear()
    }

    override fun onLocationChange(): Observable<Location> {
        return locationChange
    }
}
