package com.sagar.core.view

import androidx.test.rule.ActivityTestRule
import com.sagar.test.TestActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class ViewExtensionTest {

    // TODO : Replace this with ActivityScenarioRule
    @get:Rule
    val activityRule = ActivityTestRule(TestActivity::class.java)

    @Test
    fun postTest() = runBlocking(Dispatchers.Default) {
        Assert.assertTrue(activityRule.activity.demoView.awaitPost())
    }

    @Test
    fun postDelayTest() = runBlocking(Dispatchers.Default) {
        Assert.assertTrue(activityRule.activity.demoView.awaitPostDelay(2000))
    }
}
