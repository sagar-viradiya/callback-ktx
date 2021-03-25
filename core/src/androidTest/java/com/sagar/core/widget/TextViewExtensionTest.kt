package com.sagar.core.widget

import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class TextViewExtensionTest {

    private val context = ApplicationProvider.getApplicationContext() as android.content.Context
    private val view = TextView(context)

    @Test
    fun doBeforeTextChange() = runBlocking(Dispatchers.Main.immediate) {
        view.text = "before"
        val beforeTextChangeFlow = view.beforeTextChangeFlow()
        val deferredAssertions = async {
            val firstItem = beforeTextChangeFlow.first()
            assertEquals("before", firstItem.s.toString())
            assertEquals(0, firstItem.start)
            assertEquals(6, firstItem.count)
            assertEquals(4, firstItem.after)
        }
        view.text = "text"
        deferredAssertions.await()
    }

    @Test
    fun onTextChangeTest() = runBlocking(Dispatchers.Main.immediate) {
        view.text = "before"
        val onTextChangeFlow = view.onTextChangeFlow()
        val deferredAssertions = async {
            val firstItem = onTextChangeFlow.first()
            assertEquals("text", firstItem.s.toString())
            assertEquals(0, firstItem.start)
            assertEquals(6, firstItem.before)
            assertEquals(4, firstItem.count)
        }
        view.text = "text"
        deferredAssertions.await()
    }

    @Test
    fun afterTextChangeTest() = runBlocking(Dispatchers.Main.immediate) {
        val afterTextChangeFlow = view.afterTextChangeFlow()
        val deferredAssertions = async {
            val firstItem = afterTextChangeFlow.first()
            assertEquals("text", firstItem.editable.toString())
        }
        view.text = "text"
        deferredAssertions.await()
    }
}
