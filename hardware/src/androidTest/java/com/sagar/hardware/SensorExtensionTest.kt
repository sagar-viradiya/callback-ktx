package com.sagar.hardware

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.activityScenarioRule
import com.sagar.test.TestActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SensorExtensionTest {

    @get:Rule
    val activityRule = activityScenarioRule<TestActivity>()

    private lateinit var sensorManager: SensorManager
    private lateinit var sensor: Sensor
    private lateinit var testActivity: TestActivity

    @Before
    fun before() {
        sensorManager = (ApplicationProvider.getApplicationContext() as Context)
            .getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        activityRule.scenario.onActivity {
            testActivity = it
        }
    }

    @Test
    fun sensorEventFlowTest() = runBlocking(Dispatchers.Main.immediate) {
        val sensorState: SensorState = sensorManager
            .sensorStateFlow(sensor, testActivity)
            .filter { it is SensorState.SensorData }
            .first()
        Assert.assertTrue((sensorState as SensorState.SensorData).sensorEvent.sensor == sensor)
    }

    @Test
    fun sensorEventFlowRegisterUnRegisterTest() = runBlocking(Dispatchers.Default) {
        activityRule.scenario.moveToState(Lifecycle.State.CREATED)
        var shouldFire = false
        var flowCompleted = false
        val deferredAssertion = async(Dispatchers.Main.immediate) {
            sensorManager.sensorStateFlow(sensor, testActivity)
                .filter { it is SensorState.SensorData }
                .onCompletion {
                    flowCompleted = true
                }.collect {
                    Assert.assertTrue(shouldFire)
                    Assert.assertTrue((it as SensorState.SensorData).sensorEvent.sensor == sensor)
                }
        }
        delay(500)
        shouldFire = true
        activityRule.scenario.moveToState(Lifecycle.State.RESUMED) // After this, flow should start firing sensor events
        delay(500)
        activityRule.scenario.moveToState(Lifecycle.State.CREATED) // After this, flow should stop firing sensor events
        delay(500)
        shouldFire = false
        delay(500)
        deferredAssertion.cancelAndJoin()
        Assert.assertTrue(flowCompleted)
    }

    @Test
    fun sensorEventFlowCancelTest() = runBlocking(Dispatchers.Default) {
        var flowCompleted = false
        val deferredAssertion = async(Dispatchers.Main.immediate) {
            sensorManager.sensorStateFlow(sensor, testActivity)
                .filter { it is SensorState.SensorData }
                .onCompletion {
                    flowCompleted = true
                }.collect {
                    Assert.assertTrue((it as SensorState.SensorData).sensorEvent.sensor == sensor)
                }
        }
        delay(1000)
        activityRule.scenario.moveToState(Lifecycle.State.DESTROYED)
        deferredAssertion.await()
        Assert.assertTrue(flowCompleted)
    }

    @Test
    fun sensorAccuracyFlowTest() = runBlocking(Dispatchers.Main.immediate) {
        val sensorAccuracy = sensorManager
            .sensorStateFlow(sensor, testActivity)
            .filter { it is SensorState.SensorAccuracy }
            .first()
        Assert.assertTrue((sensorAccuracy as SensorState.SensorAccuracy).sensor == sensor)
    }
}
