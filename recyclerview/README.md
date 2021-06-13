# RecyclerView extensions

Await on RecyclerView scroll to end

```kotlin
viewLifecycleOwner.lifecycleScope.launch {
  recyclerView.awaitScrollEnd()
  // Your code goes here after recyclerview scroll ends
}
```

Observe RecyclerView scroll state changes with kotlin flow

```kotlin
awaitStateChangeFlow().collect { scrollState ->
    when(scrollState) {
        SCROLL_STATE_IDLE -> {
        // Do something while recyclerView is idle
        }
        SCROLL_STATE_DRAGGING -> {
        // Do something while recyclerView is dragging                          
        }
        SCROLL_STATE_SETTLING -> {
        // Do something while recyclerView is settling                         
        }
    }
}
```