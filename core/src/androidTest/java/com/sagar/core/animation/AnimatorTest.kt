package com.sagar.core.animation

import android.animation.ValueAnimator
import android.os.Build.VERSION.SDK_INT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test

class AnimatorTest {

    @Test
    fun awaitStartTest() = runBlocking(Dispatchers.Main.immediate) {
        val valueAnimation: ValueAnimator = ValueAnimator.ofInt(0, 100)
        val deferredAssertion = async {
            valueAnimation.awaitStart()
            assertTrue(valueAnimation.isRunning)
            valueAnimation.cancel()
        }
        valueAnimation.start()
        deferredAssertion.await()
    }

    @Test
    fun awaitResumeTest() {
        /*
            Pause and Resume listener support is only on API level 19 and above
            so skip this test if API level is < 19
        */
        assumeTrue(SDK_INT > 18)
        runBlocking(Dispatchers.Main.immediate) {
            val valueAnimation: ValueAnimator = ValueAnimator.ofInt(0, 100)
            val deferredAssertion = async {
                valueAnimation.awaitResume()
                assertTrue(valueAnimation.isRunning)
                valueAnimation.cancel()
            }
            valueAnimation.start()
            valueAnimation.pause()
            valueAnimation.resume()
            deferredAssertion.await()
        }
    }

    @Test
    fun awaitPauseTest() {
        /*
            Pause and Resume listener support is only on API level 19 and above
            so skip this test if API level is < 19
        */
        assumeTrue(SDK_INT > 18)
        runBlocking(Dispatchers.Main.immediate) {
            val valueAnimation: ValueAnimator = ValueAnimator.ofInt(0, 100)
            val deferredAssertion = async {
                valueAnimation.awaitPause()
                assertTrue(valueAnimation.isPaused)
                valueAnimation.cancel()
            }
            valueAnimation.start()
            valueAnimation.pause()
            deferredAssertion.await()
        }
    }

    @Test
    fun awaitEndTest() = runBlocking(Dispatchers.Main.immediate) {
        val valueAnimation: ValueAnimator = ValueAnimator.ofInt(0, 100)
        val deferredAssertion = async {
            valueAnimation.awaitEnd()
            // Give sometime to Animator to set running flag to false
            delay(500)
            assertTrue(!valueAnimation.isRunning)
        }
        valueAnimation.start()
        deferredAssertion.await()
    }

    @Test
    fun cancelAnimationTest() = runBlocking(Dispatchers.Main.immediate) {
        val valueAnimation: ValueAnimator = ValueAnimator.ofInt(0, 100)
        val deferred = async {
            valueAnimation.awaitEnd()
        }
        valueAnimation.start()
        valueAnimation.cancel()
        assertTrue(deferred.isCancelled)
        assertTrue(!valueAnimation.isRunning)
    }

    @Test
    fun cancelCoroutineWhileWaitingForStartTest() = runBlocking(Dispatchers.Main.immediate) {
        val valueAnimation: ValueAnimator = ValueAnimator.ofInt(0, 100).apply {
            duration = 1000
        }
        val deferred = async {
            valueAnimation.awaitStart()
        }
        deferred.cancel()
        assertTrue(!valueAnimation.isStarted)
        assertTrue(valueAnimation.listeners == null)
    }

    @Test
    fun cancelCoroutineWhileWaitingForEndTest() = runBlocking(Dispatchers.Main.immediate) {
        val valueAnimation: ValueAnimator = ValueAnimator.ofInt(0, 100).apply {
            duration = 1000
        }
        val deferred = async {
            valueAnimation.awaitEnd()
        }
        valueAnimation.start()
        assertTrue(valueAnimation.isRunning)
        deferred.cancel()
        assertTrue(valueAnimation.listeners == null)
        assertTrue(!valueAnimation.isRunning)
    }

    @Test
    fun cancelCoroutineWhileWaitingForPauseTest() {
        /*
            Pause and Resume listener support is only on API level 19 and above
            so skip this test if API level is < 19
        */
        assumeTrue(SDK_INT > 18)
        runBlocking(Dispatchers.Main.immediate) {
            val valueAnimation: ValueAnimator = ValueAnimator.ofInt(0, 100).apply {
                duration = 1000
            }
            val deferred = async {
                valueAnimation.awaitPause()
            }
            valueAnimation.start()
            assertTrue(valueAnimation.isRunning)
            deferred.cancel()
            assertTrue(valueAnimation.listeners == null)
            assertTrue(!valueAnimation.isRunning)
        }
    }

    @Test
    fun cancelCoroutineWhileWaitingForResumeTest() {
        /*
            Pause and Resume listener support is only on API level 19 and above
            so skip this test if API level is < 19
        */
        assumeTrue(SDK_INT > 18)
        runBlocking(Dispatchers.Main.immediate) {
            val valueAnimation: ValueAnimator = ValueAnimator.ofInt(0, 100).apply {
                duration = 1000
            }
            val deferred = async {
                valueAnimation.awaitResume()
            }
            valueAnimation.start()
            assertTrue(valueAnimation.isRunning)
            deferred.cancel()
            assertTrue(valueAnimation.listeners == null)
            assertTrue(!valueAnimation.isRunning)
        }
    }
}
