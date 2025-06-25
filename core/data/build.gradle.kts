plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.hien.le.dkvfinder.core.data"
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
    implementation(project(":core:model"))
    implementation(project(":core:network"))
    implementation(project(":core:common"))

    implementation(libs.bundles.arrow)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
}
