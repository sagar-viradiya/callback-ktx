# RecyclerView extensions

Recyclerview extensions allows you to either await on scroll to end or observe scroll states through flow.

## Including in your project

RecyclerView extensions are available on `mavenCentral()`

```groovy
implementation("io.github.sagar-viradiya:callback-recyclerview-ktx:1.0.0")
```

## Extensions

Await on RecyclerView scroll to end

```kotlin
viewLifecycleOwner.lifecycleScope.launch {
  recyclerView.awaitScrollEnd()
  // Your code goes here after recyclerview scroll ends
}
```

Observe RecyclerView scroll state changes with kotlin flow

```kotlin
viewLifecycleOwner.lifecycleScope.launch {
    recyclerView.awaitStateChangeFlow().collect { scrollState ->
        when (scrollState) {
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
}
```