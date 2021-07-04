import kotlinx.validation.ApiValidationExtension
import com.vanniktech.maven.publish.MavenPublishPluginExtension
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version Versions.KOTLIN_BINARY_COMPATIBILITY
}

buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:${Versions.GRADLE_PLUGIN}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.KOTLIN}")
        classpath("org.jlleitschuh.gradle:ktlint-gradle:${Versions.KTLINT_PLUGIN}")
        classpath("com.vanniktech:gradle-maven-publish-plugin:${Versions.MAVEN_PUBLISH_PLUGIN}")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }

    extensions.getByType<MavenPublishPluginExtension>().apply {
        sonatypeHost = SonatypeHost.S01
    }
}

extensions.configure<ApiValidationExtension>() {
    ignoredProjects = mutableSetOf("test")
}

tasks {
    val clean by registering(Delete::class) {
        delete(buildDir)
    }
}