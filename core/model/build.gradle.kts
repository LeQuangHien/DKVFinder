plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.hien.le.dkvfinder.core.model"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    buildFeatures {
        buildConfig = true
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(project(":core:database"))
    implementation(libs.gson)
}
