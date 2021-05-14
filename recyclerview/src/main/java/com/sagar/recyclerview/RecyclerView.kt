package com.sagar.recyclerview

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun RecyclerView.awaitScrollEnd() = suspendCancellableCoroutine<Unit> { continuation ->
    val listener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == SCROLL_STATE_IDLE) {
                recyclerView.removeOnScrollListener(this)
                continuation.resume(Unit)
            }
        }
    }
    addOnScrollListener(listener)
    continuation.invokeOnCancellation { removeOnScrollListener(listener) }
}

@ExperimentalCoroutinesApi
fun RecyclerView.awaitStateChangeFlow() = callbackFlow<Int> {
    val listener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            offer(newState)
        }
    }
    addOnScrollListener(listener)
    awaitClose { removeOnScrollListener(listener) }
}
