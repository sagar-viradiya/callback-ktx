package com.sagar.core.view

import android.os.Build
import android.view.View
import android.view.ViewTreeObserver
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun View.awaitPost() = suspendCancellableCoroutine<Boolean> { cont ->

    val runnable = Runnable { cont.resume(true) }

    cont.invokeOnCancellation { removeCallbacks(runnable) }

    val status = post(runnable)

    if (!status && cont.isActive) {
        cont.resume(false)
    }
}

suspend fun View.awaitPostDelay(delay: Long) = suspendCancellableCoroutine<Boolean> { cont ->

    val runnable = Runnable { cont.resume(true) }

    cont.invokeOnCancellation { removeCallbacks(runnable) }

    val status = postDelayed(runnable, delay)

    if (!status && cont.isActive) {
        cont.resume(false)
    }
}

suspend fun View.awaitGlobalLayout() = suspendCancellableCoroutine<Unit> { cont ->

    val listener = object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            cont.resume(Unit)
        }
    }

    viewTreeObserver.addOnGlobalLayoutListener(listener)

    cont.invokeOnCancellation {
        viewTreeObserver.removeOnGlobalLayoutListener(listener)
    }
}

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
            cont.resume(view)
        }
    }
    addOnLayoutChangeListener(listener)
    cont.invokeOnCancellation {
        removeOnLayoutChangeListener(listener)
    }
}

suspend fun View.awaitDoOnLayout(): View {
    val isLaidOut = if (Build.VERSION.SDK_INT >= 19) isLaidOut else width > 0 && height > 0
    return if (isLaidOut && !isLayoutRequested) {
        suspendCancellableCoroutine<View> { cont ->
            cont.resume(this)
        }
    } else {
        awaitDoOnNextLayout()
    }
}
