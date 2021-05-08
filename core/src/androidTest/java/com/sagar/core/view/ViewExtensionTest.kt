package com.sagar.core.view

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.rules.activityScenarioRule
import com.sagar.test.TestActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ViewExtensionTest {

    private lateinit var activity: TestActivity

    @get:Rule
    val activityRule = activityScenarioRule<TestActivity>()

    @Before
    fun before() {
        activityRule.scenario.onActivity {
            activity = it
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

    @Test
    fun awaitGlobalLayoutTest() = runBlocking(Dispatchers.Main.immediate) {
        val deferredAssertion = async {
            activity.demoView.awaitGlobalLayout()
            Assert.assertTrue(true)
        }
        // Change view's visibility to trigger global layout change callback
        activity.demoView.visibility = View.GONE
        deferredAssertion.await()
    }

    @Test
    fun awaitDoOnNextLayoutTest() = runBlocking(Dispatchers.Main.immediate) {
        val deferredAssertions = async {
            val view = activity.demoView.awaitDoOnNextLayout()
            Assert.assertEquals(activity.demoView, view)
            Assert.assertEquals(100, view.width)
        }
        // Change view's width to trigger layout change
        val layoutParams = activity.demoView.layoutParams
        layoutParams.width = 100
        activity.demoView.layoutParams = layoutParams
        deferredAssertions.await()
    }

    @Test
    fun awaitDoOnLayoutTest() = runBlocking(Dispatchers.Main.immediate) {
        val view = activity.demoView.awaitDoOnLayout()
        Assert.assertEquals(activity.demoView, view)
    }

    @Test
    fun awaitOnAttachTest() = runBlocking(Dispatchers.Default) {
        activityRule.scenario.moveToState(Lifecycle.State.CREATED)
        val deferredAssertion = async(Dispatchers.Main.immediate) {
            val view = activity.demoView.awaitOnAttach()
            Assert.assertTrue(activity.demoView == view)
        }
        activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
        deferredAssertion.await()
    }

    @Test
    fun awaitOnDetachTest() = runBlocking(Dispatchers.Default) {
        val deferredAssertion = async(Dispatchers.Main.immediate) {
            val view = activity.demoView.awaitOnDetach()
            Assert.assertTrue(activity.demoView == view)
        }
        activityRule.scenario.moveToState(Lifecycle.State.DESTROYED)
        deferredAssertion.await()
    }

    @Test
    fun awaitOnPreDrawTest() = runBlocking(Dispatchers.Main.immediate) {
        var count = 0
        val deferredAssertion = async {
            activity.demoView.awaitPreDraw()
            count++
        }
        activity.demoView.viewTreeObserver.dispatchOnPreDraw()
        deferredAssertion.await()
        Assert.assertTrue(count == 1)
    }
}
