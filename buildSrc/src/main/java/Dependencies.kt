object Versions {

    const val GRADLE_PLUGIN = "4.2.0"

    // region Android
    const val LIFECYCLE = "2.3.1"
    const val ANNOTATION = "1.2.0"
    const val PLAY_SERVICES_LOCATION = "18.0.0"
    const val RECYCLER_VIEW = "1.2.0"
    // endregion

    // region Kotlin
    const val KOTLIN = "1.5.0"
    const val COROUTINES = "1.5.0"
    const val COROUTINES_ANDROID = "1.5.0"
    // endregion

    // region Test
    const val TEST_CORE = "1.3.0"
    const val TEST_RUNNER = "1.3.0"
    const val TEST_RULES = "1.3.0"
    const val TEST_EXT_JUNIT = "1.1.2"
    // endregion
}

object Libs {

    // region Android
    const val ANNOTATION = "androidx.annotation:annotation:${Versions.ANNOTATION}"
    const val LIFECYCLE_RUNTIME_KTX = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.LIFECYCLE}"
    const val PLAY_SERVICES_LOCATION = "com.google.android.gms:play-services-location:${Versions.PLAY_SERVICES_LOCATION}"
    const val RECYCLER_VIEW = "androidx.recyclerview:recyclerview:${Versions.RECYCLER_VIEW}"
    // endregion

    // region Kotlin
    const val KOTLIN_STD = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.KOTLIN}"
    const val COROUTINES = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.COROUTINES}"
    const val COROUTINES_ANDROID =
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.COROUTINES_ANDROID}"
    // endregion

    // region Test
    const val TEST_CORE = "androidx.test:core:${Versions.TEST_CORE}"
    const val TEST_RUNNER = "androidx.test:runner:${Versions.TEST_RUNNER}"
    const val TEST_RULE = "androidx.test:rules:${Versions.TEST_RULES}"
    const val TEST_EXT_JUNIT_KTX = "androidx.test.ext:junit-ktx:${Versions.TEST_EXT_JUNIT}"
    // endregion
}