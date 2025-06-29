// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.androidx.navigation.safeargs) apply false

    // Add Ktlint Gradle plugin
    id("org.jlleitschuh.gradle.ktlint") version "12.3.0" apply false
}

// Apply Ktlint to all subprojects that have the Kotlin plugin
subprojects {
    plugins.withId("org.jetbrains.kotlin.android") { // For Android Kotlin modules
        apply(plugin = "org.jlleitschuh.gradle.ktlint")
    }
    plugins.withId("org.jetbrains.kotlin.jvm") { // For pure Kotlin/JVM modules
        apply(plugin = "org.jlleitschuh.gradle.ktlint")
    }
}