package com.sagar.recyclerview

import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.Adapter
import com.sagar.test.RecyclerViewTestActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.hamcrest.CoreMatchers.`is`

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class RecyclerViewExtensionTest {
    private lateinit var recyclerView: RecyclerView

    @get:Rule
    val activityRule = ActivityScenarioRule(RecyclerViewTestActivity::class.java)

    @Before
    fun before() {
        val thisAdapter = Adapter()
        thisAdapter.list.addAll(1..50)
        activityRule.scenario.onActivity {
            recyclerView = it.demoRecyclerView
            recyclerView.adapter = thisAdapter
        }
        activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun awaitScrollEndTest() {
        runBlocking(Dispatchers.Main.immediate) {
            val deferredAssertions = async {
                assertTrue(recyclerView.awaitScrollEnd())
            }
            recyclerView.layoutManager?.smoothScrollToPosition(recyclerView, null, 20)
            deferredAssertions.await()
        }
    }


    @ExperimentalCoroutinesApi
    @Test
    fun awaitScrollEndFlowTest() {
        runBlocking(Dispatchers.Main.immediate) {
            val deferredAssertions = async {
                assertFalse(recyclerView.awaitScrollEndFlow().first())
            }
            recyclerView.layoutManager?.smoothScrollToPosition(recyclerView, null, 20)
            deferredAssertions.await()

        }
    }
}