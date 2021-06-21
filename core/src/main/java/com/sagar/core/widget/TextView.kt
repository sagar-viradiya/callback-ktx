package com.sagar.core.widget

import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

/**
 * Sealed class to wrap [TextView]'s before, on and after changes.
 */
sealed class ChangeDetails {
    /**
     * Data class to wrap [TextView]'s details before change.
     *
     * @property s [CharSequence] before change
     * @property start Start index from where change will happen
     * @property count Number of character to be replaced
     * @property after New length after change
     */
    data class BeforeChangeDetails(
        val s: CharSequence?,
        val start: Int,
        val count: Int,
        val after: Int
    ) : ChangeDetails()

    /**
     * Data class to wrap [TextView]'s details on change.
     *
     * @property s [CharSequence] before change
     * @property start Start index from where change happened
     * @property count New length after change
     * @property before Old length before change
     */
    data class OnChangeDetails(
        val s: CharSequence?,
        val start: Int,
        val count: Int,
        val before: Int
    ) : ChangeDetails()

    /**
     * Data class to wrap [TextView]'s details after change.
     *
     * @property editable [Editable] instance after text change
     */
    data class AfterChangeDetails(val editable: Editable?) : ChangeDetails()
}

/**
 * Observe [TextView] changes after it happens through flow.
 * This extension will take care of removing text watcher if coroutine gets cancelled.
 *
 * @return Flow of [ChangeDetails.AfterChangeDetails]
 */
@ExperimentalCoroutinesApi
suspend fun TextView.afterTextChangeFlow() = callbackFlow<ChangeDetails.AfterChangeDetails> {
    val textWatcher = getTextWatcher {
        trySend(ChangeDetails.AfterChangeDetails(it))
    }
    addTextChangedListener(textWatcher)
    awaitClose {
        removeTextChangedListener(textWatcher)
    }
}

/**
 * Observe [TextView] changes before it happens through flow.
 * This extension will take care of removing text watcher if coroutine gets cancelled.
 *
 * @return Flow of [ChangeDetails.BeforeChangeDetails]
 */
@ExperimentalCoroutinesApi
suspend fun TextView.beforeTextChangeFlow() = callbackFlow<ChangeDetails.BeforeChangeDetails> {
    val textWatcher = getTextWatcher(
        beforeTextChanged = { s, start, count, after ->
            trySend(ChangeDetails.BeforeChangeDetails(s, start, count, after))
        }
    )

    addTextChangedListener(textWatcher)
    awaitClose {
        removeTextChangedListener(textWatcher)
    }
}

/**
 * Observe [TextView] changes as it happens through flow.
 * This extension will take care of removing text watcher if coroutine gets cancelled.
 *
 * @return Flow of [ChangeDetails.OnChangeDetails]
 */
@ExperimentalCoroutinesApi
suspend fun TextView.onTextChangeFlow() = callbackFlow<ChangeDetails.OnChangeDetails> {
    val textWatcher = getTextWatcher(
        onTextChange = { s, start, before, count ->
            trySend(ChangeDetails.OnChangeDetails(s, start, count, before))
        }
    )
    addTextChangedListener(textWatcher)
    awaitClose {
        removeTextChangedListener(textWatcher)
    }
}

/**
 * Observe [TextView] before, after and on changes through flow.
 * This extension will take care of removing text watcher if coroutine gets cancelled.
 *
 * @return Flow of [ChangeDetails]
 */
@ExperimentalCoroutinesApi
suspend fun TextView.textChangeFlow() = callbackFlow<ChangeDetails> {
    val textWatcher = getTextWatcher(
        onTextChange = { s, start, before, count ->
            trySend(ChangeDetails.OnChangeDetails(s, start, count, before))
        },
        beforeTextChanged = { s, start, count, after ->
            trySend(ChangeDetails.BeforeChangeDetails(s, start, count, after))
        },
        afterTextChanged = {
            trySend(ChangeDetails.AfterChangeDetails(it))
        }
    )
    addTextChangedListener(textWatcher)
    awaitClose {
        removeTextChangedListener(textWatcher)
    }
}

private inline fun getTextWatcher(
    crossinline beforeTextChanged: (
        text: CharSequence?,
        start: Int,
        count: Int,
        after: Int
    ) -> Unit = { _, _, _, _ -> },
    crossinline onTextChange: (
        text: CharSequence?,
        start: Int,
        before: Int,
        count: Int
    ) -> Unit = { _, _, _, _ -> },
    crossinline afterTextChanged: (text: Editable?) -> Unit = {}
): TextWatcher {
    return object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            beforeTextChanged.invoke(s, start, count, after)
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChange.invoke(s, start, before, count)
        }

        override fun afterTextChanged(s: Editable?) {
            afterTextChanged.invoke(s)
        }
    }
}
