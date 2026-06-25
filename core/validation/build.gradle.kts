plugins {
    id("com.android.kotlin.multiplatform.library")
    kotlin("multiplatform")
}

kotlin {
    android {
        namespace = "com.biomech.core.validation"
        compileSdk = 36
        minSdk = 26
    }

    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:common"))
        }
    }
}
