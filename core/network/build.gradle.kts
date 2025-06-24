plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.hien.le.dkvfinder.core.network"
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

    defaultConfig {
        buildConfigField("String", "API_KEY", "\"eaab6b29-8d8d-4776-88d2-d26c275f50ba\"")
    }
}

dependencies {
    implementation(project(":core:model"))

    implementation(libs.bundles.arrow)
    implementation(libs.bundles.retrofit)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.hilt.core)
}
