package com.sagar.location

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.location.Location
import android.os.Looper
import androidx.annotation.RequiresPermission
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
suspend fun FusedLocationProviderClient.awaitLastLocation() =
    suspendCancellableCoroutine<Location?> { cont ->
        lastLocation.addOnSuccessListener { location ->
            if (cont.isActive) {
                cont.resume(location)
            }
        }.addOnCanceledListener {
            if (cont.isActive) {
                cont.cancel()
            }
        }.addOnFailureListener { exception ->
            if (cont.isActive) {
                cont.resumeWithException(exception)
            }
        }
    }

@ExperimentalCoroutinesApi
@RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
fun FusedLocationProviderClient.locationFlow(
    locationRequest: LocationRequest,
    lifecycleOwner: LifecycleOwner
) = callbackFlow<Location> {
    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.locations.forEach { location ->
                if (!isClosedForSend) {
                    offer(location)
                }
            }
        }
    }

    lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
        var registered = false

        init {
            if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                registerListener()
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
        fun registerListener() {
            if (!registered) {
                requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
                registered = true
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun unregisterListener() {
            removeLocationUpdates(locationCallback)
            registered = false
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun closeFlow() {
            close()
        }
    })

    awaitClose {
        removeLocationUpdates(locationCallback)
    }
}
