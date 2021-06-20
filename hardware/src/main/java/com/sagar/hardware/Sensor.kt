package com.sagar.hardware

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

/**
 * Sealed class wrapping [Sensor]'s data changes and accuracy changes
 */
sealed class SensorState {
    /**
     * A data class wrapping [Sensor]'s data changes
     *
     * @property sensorEvent [SensorState] instance wrapping sensor data
     */
    data class SensorData(val sensorEvent: SensorEvent) : SensorState()

    /**
     * A data class wrapping [Sensor]'s accuracy changes
     *
     * @property sensor [Sensor] for which accuracy changed.
     * @property accuracy The new accuracy of this sensor.
     */
    data class SensorAccuracy(val sensor: Sensor?, val accuracy: Int) : SensorState()
}

/**
 * Observe [Sensor]'s data and accuracy change through flow. This extension will take care of
 * registering and unregistering sensor event listener based on lifecycle state of [lifecycleOwner].
 * Also it will take care of unregistering sensor event listener if coroutine gets cancelled.
 *
 * @param sensor [Sensor] to be observed
 * @param lifecycleOwner [LifecycleOwner] to unregister sensor listener on pause
 * @param samplingRate Sampling rate at which changes will be delivered
 *
 * @return Flow of [SensorState]
 */
@ExperimentalCoroutinesApi
fun SensorManager.sensorStateFlow(
    sensor: Sensor,
    lifecycleOwner: LifecycleOwner,
    samplingRate: Int = SensorManager.SENSOR_DELAY_NORMAL
) = callbackFlow<SensorState> {

    val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            if (!isClosedForSend) {
                trySend(SensorState.SensorData(event))
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            if (!isClosedForSend) {
                trySend(SensorState.SensorAccuracy(sensor, accuracy))
            }
        }
    }

    lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {

        private var registered = false

        init {
            if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                registerListener(sensorListener, sensor, samplingRate)
                registered = true
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun registerListener() {
            if (!registered) {
                registerListener(sensorListener, sensor, samplingRate)
                registered = true
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun unregisterListener() {
            unregisterListener(sensorListener)
            registered = false
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun closeFlow() {
            close()
        }
    })

    awaitClose {
        unregisterListener(sensorListener)
    }
}
