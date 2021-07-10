# Hardware Extensions

Hardware extension covers sensor API to observe sensor's data and accuracy changes through kotlin flow. The extension takes care of registering and unregistering sensor event listener based on the lifecycle of activity/fragment internally.

## Including in your project

Hardware extensions are available on `mavenCentral()`

```groovy
implementation("io.github.sagar-viradiya:callback-hardware-ktx:1.0.0")
```

## Extension

```kotlin
viewLifecycleOwner.lifecycleScope.launch {
  sensorManager.sensorStateFlow(
    sensor, lifecycleOwner = activity, accuracy = SensorManager.SENSOR_DELAY_NORMAL
  ).collect { sensorState -> 
    when(sensorState) {
      is SensorState.SensorData -> {
        // sensorState.sensorEvent contains sensor's data change
      }
      is SensorState.SensorAccuracy -> {
        // sensorState.accuracy contains sensor's accuracy change
      }
    }
  }
}
```
