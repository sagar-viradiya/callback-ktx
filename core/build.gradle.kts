plugins {
    id("com.android.library")
    id("kotlin-android")
    id("org.jlleitschuh.gradle.ktlint")
}

android {
    compileSdkVersion(30)
    buildToolsVersion = "30.0.3"

    defaultConfig {
        minSdkVersion(16)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    packagingOptions {
        exclude("**/attach_hotspot_windows.dll")
        exclude("META-INF/licenses/**")
        exclude("META-INF/AL2.0")
        exclude("META-INF/LGPL2.1")
        exclude("META-INF/DEPENDENCIES")
    }
}

dependencies {
    implementation(Libs.KOTLIN_STD)
    implementation(Libs.COROUTINES)
    implementation(Libs.ANNOTATION)
    androidTestImplementation(Libs.COROUTINES_ANDROID)
    androidTestImplementation(Libs.TEST_CORE)
    androidTestImplementation(Libs.TEST_RUNNER)
    androidTestImplementation(Libs.TEST_RULE)
    androidTestImplementation(Libs.TEST_EXT_JUNIT_KTX)
    androidTestImplementation(project(":test"))
}
