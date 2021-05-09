package com.sagar.recyclerview

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import androidx.test.ext.junit.rules.activityScenarioRule
import com.example.myapplication.TestAdapter
import com.sagar.test.RecyclerViewTestActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class RecyclerViewExtensionTest {

    private lateinit var recyclerView: RecyclerView

    @get:Rule
    val activityRule = activityScenarioRule<RecyclerViewTestActivity>()

    @Before
    fun before() {
        val thisAdapter = TestAdapter()
        thisAdapter.list.addAll(1..50)
        activityRule.scenario.onActivity {
            recyclerView = it.demoRecyclerView
            recyclerView.adapter = thisAdapter
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun awaitScrollEndTest() {
        runBlocking(Dispatchers.Main.immediate) {
            val deferredAssertions = async {
                assertThat(
                    recyclerView.awaitScrollEnd(),
                    `is`(SCROLL_STATE_IDLE)
                )
            }
            recyclerView.layoutManager?.smoothScrollToPosition(
                recyclerView,
                null,
                20
            )
            deferredAssertions.await()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun awaitScrollEndFlowTest() {
        runBlocking(Dispatchers.Main.immediate) {
            val deferredAssertions = async {
                recyclerView.awaitScrollEndFlow()
                    .take(2)
                    .collect {
                        assertThat(it, `is`(SCROLL_STATE_IDLE))
                    }
            }
            recyclerView.layoutManager?.smoothScrollToPosition(
                recyclerView,
                null,
                20
            )
            delay(500)
            recyclerView.layoutManager?.smoothScrollToPosition(
                recyclerView,
                null,
                30
            )
            delay(500)
            deferredAssertions.await()
        }
    }
}