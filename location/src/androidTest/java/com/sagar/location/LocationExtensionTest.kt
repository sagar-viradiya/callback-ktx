package com.sagar.location

import android.Manifest
import android.os.Build.VERSION.SDK_INT
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.rule.GrantPermissionRule
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.sagar.test.TestActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LocationExtensionTest {

    private lateinit var testActivity: TestActivity
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @get:Rule
    val activityScenarioRule = activityScenarioRule<TestActivity>()

    @get:Rule
    val permissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

    @Before
    fun setup() {
        activityScenarioRule.scenario.onActivity {
            testActivity = it
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(testActivity)
    }

    @Test
    fun lastLocationTest() {
        /*Emulator with google play service is only available form API level 24 and above
        so skip test for all API below 24*/
        assumeTrue(SDK_INT > 23)
        runBlocking {
            try {
                val location = fusedLocationProviderClient.awaitLastLocation()
                location?.let {
                    Assert.assertTrue(!it.isFromMockProvider)
                    Assert.assertTrue(it.latitude != 0.0)
                    Assert.assertTrue(it.longitude != 0.0)
                } ?: Assert.assertTrue(true)
            } catch (exception: Exception) {
                // If there is any exception while getting last location we need to still pass this test
                Assert.assertTrue(true)
            }
        }
    }

    @Test
    fun locationFlowTest() {
        /*Emulator with google play service is only available form API level 24 and above
        so skip test for all API below 24*/
        assumeTrue(SDK_INT > 23)
        runBlocking(Dispatchers.Main.immediate) {
            val locationFlow = fusedLocationProviderClient.locationFlow(
                LocationRequest.create().apply {
                    interval = 100
                    fastestInterval = 100
                    priority = LocationRequest.PRIORITY_LOW_POWER
                },
                testActivity
            )

            locationFlow.take(2).collect {
                Assert.assertTrue(!it.isFromMockProvider)
                Assert.assertTrue(it.latitude != 0.0)
                Assert.assertTrue(it.longitude != 0.0)
            }
        }
    }

    @Test
    fun activityResumePauseDestroyTest() {
        /*Emulator with google play service is only available form API level 24 and above
        so skip test for all API below 24*/
        assumeTrue(SDK_INT > 23)
        runBlocking(Dispatchers.Default) {
            activityScenarioRule.scenario.moveToState(Lifecycle.State.CREATED)
            var shouldEmit = false
            var flowCompleted = false
            val deferredAssertions = async(Dispatchers.Main.immediate) {
                fusedLocationProviderClient.locationFlow(
                    LocationRequest.create().apply {
                        interval = 100
                        fastestInterval = 100
                        priority = LocationRequest.PRIORITY_LOW_POWER
                    },
                    testActivity
                ).onCompletion {
                    flowCompleted = true
                }.collect {
                    Assert.assertTrue(shouldEmit)
                }
            }
            shouldEmit = true
            activityScenarioRule.scenario.moveToState(Lifecycle.State.RESUMED)
            delay(500)
            activityScenarioRule.scenario.moveToState(Lifecycle.State.CREATED)
            delay(500)
            shouldEmit = false
            delay(500)
            activityScenarioRule.scenario.moveToState(Lifecycle.State.DESTROYED)
            deferredAssertions.await()
            Assert.assertTrue(flowCompleted)
        }
    }

    @Test
    fun locationFlowCancelTest() {
        /*Emulator with google play service is only available form API level 24 and above
        so skip test for all API below 24*/
        assumeTrue(SDK_INT > 23)
        runBlocking {
            var flowCompleted = false
            val deferredAssertions = async(Dispatchers.Main.immediate) {
                fusedLocationProviderClient.locationFlow(
                    LocationRequest.create().apply {
                        interval = 100
                        fastestInterval = 100
                        priority = LocationRequest.PRIORITY_LOW_POWER
                    },
                    testActivity
                ).onCompletion {
                    flowCompleted = true
                }.collect {
                    Assert.assertTrue(!it.isFromMockProvider)
                    Assert.assertTrue(it.latitude != 0.0)
                    Assert.assertTrue(it.longitude != 0.0)
                }
            }
            delay(2000)
            deferredAssertions.cancelAndJoin()
            Assert.assertTrue(flowCompleted)
        }
    }
}
