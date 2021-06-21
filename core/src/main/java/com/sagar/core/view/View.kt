package com.sagar.core.view

import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.view.View
import android.view.ViewTreeObserver
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Await on runnable post to be executed on view.
 * This extension will take care of removing listener in case coroutine gets cancelled.
 *
 * @return true if the Runnable was successfully placed in to the
 *         message queue. Returns false on failure.
 */
suspend fun View.awaitPost() = suspendCancellableCoroutine<Boolean> { cont ->

    val runnable = Runnable { cont.resume(true) }

    cont.invokeOnCancellation { removeCallbacks(runnable) }

    val status = post(runnable)

    if (!status && cont.isActive) {
        cont.resume(false)
    }
}

/**
 * Await on runnable post to be executed on view after specified [delay].
 * This extension will take care of removing listener in case coroutine gets cancelled.
 *
 * @param delay Delay in milliseconds
 * @return true if the Runnable was successfully placed in to the
 *         message queue. Returns false on failure.
 */
suspend fun View.awaitPostDelay(delay: Long) = suspendCancellableCoroutine<Boolean> { cont ->

    val runnable = Runnable { cont.resume(true) }

    cont.invokeOnCancellation { removeCallbacks(runnable) }

    val status = postDelayed(runnable, delay)

    if (!status && cont.isActive) {
        cont.resume(false)
    }
}

/**
 * Await on global layout state or the visibility of views within the view tree changes.
 * This extension will take care of removing listener in case coroutine gets cancelled.
 */
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

/**
 * Await till view is next laid out.
 * This extension will take care of removing listener in case coroutine gets cancelled.
 *
 * @return [View] on which this function call happened.
 */
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

/**
 * Await till view is laid out. If the view has been laid out and it
 * has not requested a layout, this function would call resume on continuation immediately.
 * This extension will take care of removing listener in case coroutine gets cancelled.
 *
 * @return [View] on which this function call happened.
 */
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

/**
 * Await till view is attached to a window. If the view is already
 * attached to a window, this function would call resume on continuation immediately.
 * This extension will take care of removing listener in case coroutine gets cancelled.
 *
 * @return [View] on which this function call happened.
 */
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

/**
 * Await till view is detach from a window. If the view is not
 * attached to a window, this function would call resume on continuation immediately.
 * This extension will take care of removing listener in case coroutine gets cancelled.
 *
 * @return [View] on which this function call happened.
 */
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

/**
 * Await till view is about to draw.
 * This extension will take care of removing listener in case coroutine gets cancelled.
 */
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
