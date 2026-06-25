plugins {
    id("com.android.kotlin.multiplatform.library")
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

kotlin {
    android {
        namespace = "com.biomech.feature.settings.ui"
        compileSdk = 36
        minSdk = 26
    }

    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":feature:auth:auth-domain"))
            implementation(project(":core:base"))
            implementation(libs.kotlinx.coroutines.core)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
        }
    }
}
