plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    compileSdkVersion(Versions.COMPILE_SDK_VERSION)
    buildToolsVersion = "30.0.3"

    defaultConfig {
        minSdkVersion(Versions.MIN_SDK_VERSION)
        targetSdkVersion(Versions.TARGET_SDK_VERSION)
    }
}

dependencies {
    implementation(Libs.LIFECYCLE_RUNTIME_KTX)
    implementation(Libs.RECYCLER_VIEW)
}
