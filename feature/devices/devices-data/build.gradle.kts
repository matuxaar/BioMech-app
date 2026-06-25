plugins {
    id("com.android.kotlin.multiplatform.library")
    kotlin("multiplatform")
}

kotlin {
    android {
        namespace = "com.biomech.feature.devices.data"
        compileSdk = 36
        minSdk = 26
    }

    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            api(project(":feature:devices:devices-api"))
            api(project(":feature:devices:devices-domain"))
            implementation(project(":core:network"))
            implementation(project(":core:common"))
            implementation(project(":core:database"))
        }
    }
}
