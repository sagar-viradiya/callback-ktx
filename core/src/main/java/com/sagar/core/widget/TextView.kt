package com.sagar.core.widget

import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

sealed class ChangeDetails {
    data class BeforeChangeDetails(
        val s: CharSequence?,
        val start: Int,
        val count: Int,
        val after: Int
    ) : ChangeDetails()
    data class OnChangeDetails(
        val s: CharSequence?,
        val start: Int,
        val count: Int,
        val before: Int
    ) : ChangeDetails()
    data class AfterChangeDetails(val editable: Editable?) : ChangeDetails()
}

@ExperimentalCoroutinesApi
suspend fun TextView.afterTextChangeFlow() = callbackFlow<ChangeDetails.AfterChangeDetails?> {
    val textWatcher = getTextWatcher {
        offer(ChangeDetails.AfterChangeDetails(it))
    }
    addTextChangedListener(textWatcher)
    awaitClose {
        removeTextChangedListener(textWatcher)
    }
}

@ExperimentalCoroutinesApi
suspend fun TextView.beforeTextChangeFlow() = callbackFlow<ChangeDetails.BeforeChangeDetails> {
    val textWatcher = getTextWatcher(
        beforeTextChanged = { s, start, count, after ->
            offer(ChangeDetails.BeforeChangeDetails(s, start, count, after))
        }
    )
    addTextChangedListener(textWatcher)
    awaitClose {
        removeTextChangedListener(textWatcher)
    }
}

@ExperimentalCoroutinesApi
suspend fun TextView.onTextChangeFlow() = callbackFlow<ChangeDetails.OnChangeDetails> {
    val textWatcher = getTextWatcher(
        onTextChange = { s, start, before, count ->
            offer(ChangeDetails.OnChangeDetails(s, start, count, before))
        }
    )
    addTextChangedListener(textWatcher)
    awaitClose {
        removeTextChangedListener(textWatcher)
    }
}

@ExperimentalCoroutinesApi
suspend fun TextView.textChangeFlow() = callbackFlow<ChangeDetails> {
    val textWatcher = getTextWatcher(
        onTextChange = { s, start, before, count ->
            offer(ChangeDetails.OnChangeDetails(s, start, count, before))
        },
        beforeTextChanged = { s, start, count, after ->
            offer(ChangeDetails.BeforeChangeDetails(s, start, count, after))
        },
        afterTextChanged = {
            offer(ChangeDetails.AfterChangeDetails(it))
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
) = object : TextWatcher {
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
