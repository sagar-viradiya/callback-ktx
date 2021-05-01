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
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.callbackFlow

sealed class SensorState {
    data class SensorEvent(val sensorEvent: android.hardware.SensorEvent) : SensorState()
    data class SensorAccuracy(val sensor: Sensor?, val accuracy: Int) : SensorState()
}

@ExperimentalCoroutinesApi
fun SensorManager.sensorEventFlow(
    sensor: Sensor,
    lifecycleOwner: LifecycleOwner,
    accuracy: Int = SensorManager.SENSOR_DELAY_NORMAL
) = callbackFlow<SensorState> {

    val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            offer(SensorState.SensorEvent(event))
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            offer(SensorState.SensorAccuracy(sensor, accuracy))
        }
    }

    lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {

        private var registered = false

        init {
            if(lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                registerListener(sensorListener, sensor, accuracy)
                registered = true
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun registerListener() {
            if(!registered) {
                registerListener(sensorListener, sensor, accuracy)
                registered = true
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun unregisterListener() {
            unregisterListener(sensorListener)
            registered = false
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun clear() {
            close()
        }
    })

    awaitClose {
        unregisterListener(sensorListener)
    }
}