package com.sagar.core.view

import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.rules.activityScenarioRule
import com.sagar.test.TestActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ViewExtensionTest {

    @get:Rule
    val activityRule = activityScenarioRule<TestActivity>()

    private lateinit var activity: TestActivity

    @Before
    fun before() {
        activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
        runBlocking(Dispatchers.Main.immediate) {
            activityRule.scenario.onActivity {
                activity = it
            }
        }
    }

    @Test
    fun postTest() = runBlocking(Dispatchers.Default) {
        Assert.assertTrue(activity.demoView.awaitPost())
    }

    @Test
    fun postDelayTest() = runBlocking(Dispatchers.Default) {
        Assert.assertTrue(activity.demoView.awaitPostDelay(2000))
    }
}
