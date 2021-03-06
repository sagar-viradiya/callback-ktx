# Core extensions

Core contains extensions on the following framework APIs

- Animator
- TextView
- View

## Including in your project

Core extensions are available on `mavenCentral()`

```groovy
implementation("io.github.sagar-viradiya:callback-core-ktx:1.0.0")
```

## Animator

Await on animation start

```kotlin
viewLifecycleOwner.lifecycleScope.launch {
  animator.awaitStart()
  // Your code goes here after animation start
}
```

Await on animation pause

```kotlin
viewLifecycleOwner.lifecycleScope.launch {
  animator.awaitPause()
  // Your code goes here after animation pause
}
```

Await on animation resume

```kotlin
viewLifecycleOwner.lifecycleScope.launch {
  animator.awaitResume()
  // Your code goes here after animation resume
}
```


Await on animation end

```kotlin
viewLifecycleOwner.lifecycleScope.launch {
  animator.awaitEnd()
  // Your code goes here after animation end
}
```

Orchestrating your animations

```kotlin
viewLifecycleOwner.lifecycleScope.launch {
  ObjectAnimator.ofFloat(imageView, View.ALPHA, 0f, 1f).run {
    start()
    awaitEnd()
  }

  ObjectAnimator.ofFloat(imageView, View.TRANSLATION_Y, 0f, 100f).run {
    start()
    awaitEnd()
  }

  ObjectAnimator.ofFloat(imageView, View.TRANSLATION_X, -100f, 0f).run {
    start()
    awaitEnd()
  }
}
```

## TextView

Observe text changes through kotlin flow

```kotlin

// Observe after text change
textView.afterTextChangeFlow.collect { afterChangeDetails ->
  // afterChangeDetails.editable
}

// Observe before text change
textView.beforeTextChangeFlow.collect { beforeChangeDetails ->
  // beforeChangeDetails.s -> Text before change
  // beforeChangeDetails.start -> Start index from where characters are going to replaced by new characters
  // beforeChangeDetails.count -> Lenght of current text
  // beforeChangeDetails.after -> Lenght of new text
}

// Observe before text change
textView.onTextChangeFlow.collect { onChangeDetails ->
  // onChangeDetails.s -> Text after change
  // onChangeDetails.start -> Start index from where characters gotreplaced by new characters
  // onChangeDetails.count -> Lenght of new text
  // onChangeDetails.before -> Lenght of new text
}

// Observe all changes
textView.textChangeFlow.collect { changeDetails ->
  when(changeDetails) {
    BeforeChangeDetails -> {
      // Do something before text change
    }
    OnChangeDetails -> {
      // Do something on text change
    }
    AfterChangeDetails -> {
      // Do something after text change
    } 
  }
}
```

## View

Await runnable on view while running on a worker thread

```kotlin
launch(Dispatchers.Default) {
  .
  .
  .
  view.awaitPost()
  // Execute things on main thread
}
```

Await delayed runnable on view while running on a worker thread

```kotlin
launch(Dispatchers.Default) {
  .
  .
  .
  view.awaitPost(delay = 1000)
  // Execute things on main thread after specified delay
}
```

Await on the global layout state or the visibility of views within the view tree changes. The extension takes care of removing the listener internally.

```kotlin
viewLifecycleOwner.lifecycleScope.launch {
  view.awaitGlobalLayout()
  // Do things after global layout state or the visibility of views within the view tree changes
}
```

Await till view is next laid out. The extension takes care of removing the listener internally.

```kotlin
viewLifecycleOwner.lifecycleScope.launch {
  view.awaitDoOnNextLayout()
  // Do things after view laid out
}
```

Await view's layout. If a view is already laid out it will resume coroutine immediately otherwise suspends till the next view layout. The extension takes care of removing the listener internally.

```kotlin
viewLifecycleOwner.lifecycleScope.launch {
  view.awaitDoOnLayout()
  // Do things after view laid out
}
```

Await till view attaches to the window. The extension takes care of removing the listener internally.

```kotlin
viewLifecycleOwner.lifecycleScope.launch {
  view.awaitOnAttach()
  // Do things once view is attached to window
}
```

Await till the view detaches from the window. The extension takes care of removing the listener internally.

```kotlin
viewLifecycleOwner.lifecycleScope.launch {
  view.awaitOnDetach()
  // Do things once view is detach from window
}
```

Await till the view tree is about to be drawn. The extension takes care of removing the listener internally.

```kotlin
viewLifecycleOwner.lifecycleScope.launch {
  view.awaitPreDraw()
  // Do things before view tree draw
}
```
