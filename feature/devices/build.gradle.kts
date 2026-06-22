plugins {
    id("biomech.kmp.compose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:ui"))
            implementation(project(":core:common"))
            implementation(project(":core:ble"))
            implementation(project(":domain"))
        }
    }
}

android {
    namespace = "com.biomech.feature.devices"
}
