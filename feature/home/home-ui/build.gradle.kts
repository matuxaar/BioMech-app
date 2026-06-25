plugins {
    id("com.android.kotlin.multiplatform.library")
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

kotlin {
    android {
        namespace = "com.biomech.feature.home"
        compileSdk = 36
        minSdk = 26
    }

    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.material3)
            implementation(compose.foundation)
            implementation(project(":core:mvi"))
            implementation(project(":core:common"))
            implementation(project(":core:navigation"))
            implementation(project(":feature:devices:devices-domain"))
        }
    }
}
