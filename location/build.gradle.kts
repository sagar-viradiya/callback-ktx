plugins {
    id("com.android.library")
    id("kotlin-android")
    id("org.jlleitschuh.gradle.ktlint")
    id("com.vanniktech.maven.publish")
}

android {
    compileSdkVersion(Versions.COMPILE_SDK_VERSION)
    buildToolsVersion = "30.0.3"

    libraryVariants.all {
        generateBuildConfigProvider?.configure { enabled = false }
    }

    defaultConfig {
        minSdkVersion(Versions.MIN_SDK_VERSION)
        targetSdkVersion(Versions.TARGET_SDK_VERSION)

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    packagingOptions {
        exclude("**/attach_hotspot_windows.dll")
        exclude("META-INF/licenses/**")
        exclude("META-INF/AL2.0")
        exclude("META-INF/LGPL2.1")
    }

    extensions.getByType<com.vanniktech.maven.publish.MavenPublishPluginExtension>().apply {
        sonatypeHost = com.vanniktech.maven.publish.SonatypeHost.S01
    }
}

dependencies {
    implementation(Libs.COROUTINES)
    implementation(Libs.LIFECYCLE_RUNTIME_KTX) {
        exclude("org.jetbrains.kotlinx", "kotlinx-coroutines-android")
    }
    implementation(Libs.ANNOTATION)
    implementation(Libs.PLAY_SERVICES_LOCATION)
    androidTestImplementation(Libs.COROUTINES_ANDROID)
    androidTestImplementation(Libs.TEST_CORE)
    androidTestImplementation(Libs.TEST_RUNNER)
    androidTestImplementation(Libs.TEST_RULE)
    androidTestImplementation(Libs.TEST_EXT_JUNIT_KTX)
    androidTestImplementation(project(":test"))
}
