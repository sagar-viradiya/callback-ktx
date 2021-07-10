# callback-ktx
![CI](https://github.com/sagar-viradiya/callback-ktx/actions/workflows/ci.yml/badge.svg)

Extension functions over Android's callback-based APIs which allows writing them in a sequential way within coroutines or observe multiple callbacks through Kotlin flow.

Currently covers following APIs

- Animation
- Location
- RecyclerView
- Sensor
- View
- Widget(TextView)

## Including in your project

Callback extensions are divided across different modules based on the category they fall under. For example, all framework APIs would fall under the core module. Anything not related to the framework is in its separate module. So depending on your requirement you can depend on a specific module available on `mavenCentral()`

To include core extension add the following in your `build.gradle`

```groovy
implementation("io.github.sagar-viradiya:callback-core-ktx:1.0.0")
```

Similarly, you can check individual module's README to know how to include those dependencies.

## Examples

Below are a few examples of the extensions and it's usage in coroutine.

Await on animation start (Core extension)

```kotlin
viewLifecycleOwner.lifecycleScope.launch {
  animator.awaitStart()
  // Your code goes here after animation start
}
```

Await view's layout. If a view is already laid out it will resume coroutine immediately otherwise suspends till the next view layout. The extension takes care of removing the listener internally.

```kotlin
viewLifecycleOwner.lifecycleScope.launch {
  view.awaitDoOnLayout()
  // Do things after view laid out
}
```

Await on the last location.

```kotlin
viewLifecycleOwner.lifecycleScope.launch {
  val location = fusedLocationProviderClient.awaitLastLocation()    // Suspend coroutine
  // Use last location
}
```

Observe location changes. The extension takes care of registering and unregistering location update callback based on the state of the lifecycle owner internally.

```kotlin
viewLifecycleOwner.lifecycleScope.launch {
  fusedLocationProviderClient.locationFlow(locationRequest, lifecycleOwner).collect { location ->
    // Consume location
  }
}
```

Please check the individual module's README for more details.

## Contributing

Found APIs that are not covered and want to contribute new extensions? Found an issue or have any suggestions for enhancements? Head over to [Contribution guidelines](CONTRIBUTING.md) to know more about contributing to this library.

# License

```
Copyright 2021 callback-ktx contributors

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
