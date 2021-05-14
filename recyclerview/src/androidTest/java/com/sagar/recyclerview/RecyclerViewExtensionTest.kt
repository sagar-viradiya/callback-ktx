package com.sagar.recyclerview

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import androidx.test.ext.junit.rules.activityScenarioRule
import com.example.myapplication.TestAdapter
import com.sagar.test.RecyclerViewTestActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

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

    @Test
    fun awaitScrollEndTest() {
        runBlocking(Dispatchers.Main.immediate) {
            val deferredAssertions = async {
                recyclerView.awaitScrollEnd()
                assertThat(recyclerView.scrollState, `is`(SCROLL_STATE_IDLE))
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
                recyclerView.awaitStateChangeFlow()
                    .take(2)
                    .collect {
                        assertThat(it, `is`(recyclerView.scrollState))
                    }
            }
            recyclerView.layoutManager?.smoothScrollToPosition(
                recyclerView,
                null,
                20
            )
            deferredAssertions.await()
        }
    }
}
