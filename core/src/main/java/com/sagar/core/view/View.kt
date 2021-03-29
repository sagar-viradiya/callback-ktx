package com.sagar.core.view

import android.os.Build
import android.view.View
import android.view.ViewTreeObserver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine

@ExperimentalCoroutinesApi
suspend fun View.awaitPost() = suspendCancellableCoroutine<Boolean> { cont ->

    val runnable = object : Runnable {
        override fun run() {
            cont.resume(true) {
                removeCallbacks(this)
            }
        }
    }

    cont.invokeOnCancellation { removeCallbacks(runnable) }

    val status = post(runnable)

    if (!status && cont.isActive) {
        cont.resume(false) {
            // Do nothing
        }
    }
}

@ExperimentalCoroutinesApi
suspend fun View.awaitPostDelay(delay: Long) = suspendCancellableCoroutine<Boolean> { cont ->

    val runnable = object : Runnable {
        override fun run() {
            cont.resume(true) {
                removeCallbacks(this)
            }
        }
    }

    cont.invokeOnCancellation { removeCallbacks(runnable) }

    val status = postDelayed(runnable, delay)

    if (!status && cont.isActive) {
        cont.resume(false) {
            // Do nothing
        }
    }
}

@ExperimentalCoroutinesApi
suspend fun View.awaitGlobalLayout() = suspendCancellableCoroutine<Unit> { cont ->

    val listener = object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            cont.resume(Unit) {
                // Do nothing
            }
        }
    }

    viewTreeObserver.addOnGlobalLayoutListener(listener)

    cont.invokeOnCancellation {
        viewTreeObserver.removeOnGlobalLayoutListener(listener)
    }
}

@ExperimentalCoroutinesApi
suspend fun View.awaitDoOnNextLayout() = suspendCancellableCoroutine<View> { cont ->
    val listener = object : View.OnLayoutChangeListener {
        override fun onLayoutChange(
            view: View,
            left: Int,
            top: Int,
            right: Int,
            bottom: Int,
            oldLeft: Int,
            oldTop: Int,
            oldRight: Int,
            oldBottom: Int
        ) {
            view.removeOnLayoutChangeListener(this)
            cont.resume(view) {
                // Do nothing
            }
        }
    }

    addOnLayoutChangeListener(listener)

    cont.invokeOnCancellation {
        removeOnLayoutChangeListener(listener)
    }
}

@ExperimentalCoroutinesApi
suspend fun View.awaitDoOnLayout() = suspendCancellableCoroutine<View> { cont ->
    val isLaidOut = if (Build.VERSION.SDK_INT >= 19) isLaidOut else width > 0 && height > 0

    if (isLaidOut && !isLayoutRequested) {
        cont.resume(this) {
            // Do nothing
        }
    } else {
        val listener = object : View.OnLayoutChangeListener {
            override fun onLayoutChange(
                view: View,
                left: Int,
                top: Int,
                right: Int,
                bottom: Int,
                oldLeft: Int,
                oldTop: Int,
                oldRight: Int,
                oldBottom: Int
            ) {
                view.removeOnLayoutChangeListener(this)
                cont.resume(view) {
                    // Do nothing
                }
            }
        }

        addOnLayoutChangeListener(listener)

        cont.invokeOnCancellation {
            removeOnLayoutChangeListener(listener)
        }
    }
}
