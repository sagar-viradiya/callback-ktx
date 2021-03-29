package com.sagar.core.view

import android.view.View
import androidx.test.rule.ActivityTestRule
import com.sagar.test.TestActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
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

    @Test
    fun awaitGlobalLayoutTest() = runBlocking(Dispatchers.Main.immediate) {
        val deferredAssertion = async {
            activityRule.activity.demoView.awaitGlobalLayout()
            Assert.assertTrue(true)
        }
        // Change view's visibility to trigger global layout change callback
        activityRule.activity.demoView.visibility = View.GONE
        deferredAssertion.await()
    }

    @Test
    fun awaitDoOnNextLayout() = runBlocking(Dispatchers.Main.immediate) {
        val deferredAssertions = async {
            val view = activityRule.activity.demoView.awaitDoOnNextLayout()
            Assert.assertEquals(activityRule.activity.demoView, view)
            Assert.assertEquals(100, view.width)
        }
        // Change view's width to trigger layout change
        val layoutParams = activityRule.activity.demoView.layoutParams
        layoutParams.width = 100
        activityRule.activity.demoView.layoutParams = layoutParams
        deferredAssertions.await()
    }

    @Test
    fun awaitDoOnLayout() = runBlocking(Dispatchers.Main.immediate) {
        val view = activityRule.activity.demoView.awaitDoOnLayout()
        Assert.assertEquals(activityRule.activity.demoView, view)
    }
}
