package com.sagar.recyclerview

import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine

sealed class ScrollState {
    object Idle : ScrollState()
}

@ExperimentalCoroutinesApi
/**
 * method that takes care of first time notifying that the recyclerview scroll has ended
 * use case may include the purpose where we do some user interaction after initial scroll
 */
suspend fun RecyclerView.awaitScrollEnd() =
    suspendCancellableCoroutine<ScrollState> { continuation ->
        // create anonymous object for recyclerview
        val listener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // removing the listener so we don't leak the the continuation coroutine
                    recyclerView.removeOnScrollListener(this)
                    // Resume the coroutine once the scroll is idle
                    continuation.resume(ScrollState.Idle) {
                        // do nothing
                    }
                }
            }
        }
        // attach scroll listener reference to recyclerview
        addOnScrollListener(listener)
        // take care of what happens when the coroutine is cancelled
        continuation.invokeOnCancellation {
            // If the coroutine is cancelled, remove the scroll listener
            removeOnScrollListener(listener)
        }
    }

@ExperimentalCoroutinesApi
/**
 * Method that takes care of the use case where we want to be notified of each time the
 * recyclerview has come to a pause after scrolling - ideal for a large list and do some
 * action based on the streaming event
 */
suspend fun RecyclerView.awaitScrollEndFlow() = callbackFlow<ScrollState> {
    // create anonymous object for recyclerview
    val listener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                // emit value every time scroll is idle
                offer(ScrollState.Idle)
            }
        }
    }
    //attach scroll listener reference to recyclerview
    addOnScrollListener(listener)
    //take care of what happens when the coroutine is cancelled
    awaitClose {
        // If the coroutine is cancelled, remove the scroll listener
        removeOnScrollListener(listener)
    }
}