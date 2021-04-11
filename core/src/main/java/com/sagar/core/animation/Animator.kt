package com.sagar.core.animation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun Animator.awaitStart() = suspendCancellableCoroutine<Unit> { cont ->
    val listener = getAnimatorListener(
        onStart = {
            cont.resume(Unit)
        }
    )
    addListener(listener)
    cont.invokeOnCancellation { removeListener(listener) }
}

@RequiresApi(Build.VERSION_CODES.KITKAT)
suspend fun Animator.awaitPause() = suspendCancellableCoroutine<Unit> { cont ->
    val listener = getAnimatorListener(
        onEnd = { isEndedSuccessfully ->
            if (cont.isActive && !isEndedSuccessfully) {
                cont.cancel()
            }
        },
        onPause = {
            cont.resume(Unit)
        }
    )
    addListener(listener)
    addPauseListener(listener)
    cont.invokeOnCancellation { cancel() }
}

@RequiresApi(Build.VERSION_CODES.KITKAT)
suspend fun Animator.awaitResume() = suspendCancellableCoroutine<Unit> { cont ->
    val listener = getAnimatorListener(
        onEnd = { isEndedSuccessfully ->
            if (cont.isActive && !isEndedSuccessfully) {
                cont.cancel()
            }
        },
        onResume = {
            cont.resume(Unit)
        }
    )
    addListener(listener)
    addPauseListener(listener)
    cont.invokeOnCancellation { cancel() }
}

suspend fun Animator.awaitEnd() = suspendCancellableCoroutine<Unit> { cont ->
    addListener(
        getAnimatorListener(
            onEnd = { isEndedSuccessfully ->
                if (cont.isActive) {
                    if (isEndedSuccessfully) {
                        cont.resume(Unit)
                    } else {
                        cont.cancel()
                    }
                }
            }
        )
    )
    cont.invokeOnCancellation { cancel() }
}

private inline fun getAnimatorListener(
    crossinline onEnd: (isEndedSuccessfully: Boolean) -> Unit = {},
    crossinline onStart: () -> Unit = {},
    crossinline onPause: () -> Unit = {},
    crossinline onResume: () -> Unit = {}
): AnimatorListenerAdapter {
    return object : AnimatorListenerAdapter() {
        var endedSuccessfully = true

        override fun onAnimationEnd(animator: Animator) {
            animator.removeListener(this)
            onEnd(endedSuccessfully)
        }

        override fun onAnimationCancel(animator: Animator) {
            endedSuccessfully = false
        }

        override fun onAnimationStart(animator: Animator) = onStart()

        override fun onAnimationPause(animation: Animator) = onPause()

        override fun onAnimationResume(animation: Animator) = onResume()
    }
}
