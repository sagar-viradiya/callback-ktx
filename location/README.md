# Location extensions

Location extensions covers [FusedLocationProviderClient](https://developers.google.com/android/reference/com/google/android/gms/location/FusedLocationProviderClient) from google play services to suspend on the last location or observe location changes through kotlin flow.

## Including in your project

Location extensions are available on `mavenCentral()`

```groovy
implementation("io.github.sagar-viradiya:callback-location-ktx:1.0.0")
```

## Extensions

Suspend on the last location.

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
