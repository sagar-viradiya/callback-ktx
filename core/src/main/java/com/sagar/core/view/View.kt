package com.sagar.core.view

import android.os.Build
import android.os.Build.VERSION.SDK_INT
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

suspend fun View.awaitOnAttach() = suspendCancellableCoroutine<View> { cont ->
    var listener: View.OnAttachStateChangeListener? = null
    val isAttachToWindow = if (SDK_INT >= 19) isAttachedToWindow else windowToken != null
    if (isAttachToWindow) {
        cont.resume(this)
    } else {
        listener = object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(view: View) {
                removeOnAttachStateChangeListener(this)
                cont.resume(view)
            }

            override fun onViewDetachedFromWindow(view: View) {
                // Do nothing
            }
        }
        addOnAttachStateChangeListener(listener)
    }

    cont.invokeOnCancellation {
        listener?.let { removeOnAttachStateChangeListener(it) }
    }
}

suspend fun View.awaitOnDetach() = suspendCancellableCoroutine<View> { cont ->
    var listener: View.OnAttachStateChangeListener? = null
    val isAttachToWindow = if (SDK_INT >= 19) isAttachedToWindow else windowToken != null
    if (!isAttachToWindow) {
        cont.resume(this)
    } else {
        listener = object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(view: View) {
                // Do nothing
            }

            override fun onViewDetachedFromWindow(view: View) {
                removeOnAttachStateChangeListener(this)
                cont.resume(view)
            }
        }
        addOnAttachStateChangeListener(listener)
    }

    cont.invokeOnCancellation {
        listener?.let { removeOnAttachStateChangeListener(it) }
    }
}

suspend fun View.awaitPreDraw() = suspendCancellableCoroutine<Unit> { cont ->
    var viewTreeObserver: ViewTreeObserver = viewTreeObserver

    val listener = object : ViewTreeObserver.OnPreDrawListener, View.OnAttachStateChangeListener {
        override fun onPreDraw(): Boolean {
            removeListener(this@awaitPreDraw, viewTreeObserver, this)
            cont.resume(Unit)
            return true
        }

        override fun onViewAttachedToWindow(view: View) {
            viewTreeObserver = view.viewTreeObserver
        }

        override fun onViewDetachedFromWindow(view: View) {
            cont.cancel()
        }
    }

    viewTreeObserver.addOnPreDrawListener(listener)
    addOnAttachStateChangeListener(listener)

    cont.invokeOnCancellation {
        removeListener(this, viewTreeObserver, listener)
    }
}

private fun removeListener(
    view: View,
    viewTreeObserver: ViewTreeObserver,
    listener: Any
) {
    if (viewTreeObserver.isAlive) {
        viewTreeObserver.removeOnPreDrawListener(listener as ViewTreeObserver.OnPreDrawListener?)
    } else {
        view.viewTreeObserver.removeOnPreDrawListener(
            listener as ViewTreeObserver.OnPreDrawListener?
        )
    }
    view.removeOnAttachStateChangeListener(listener as View.OnAttachStateChangeListener?)
}
