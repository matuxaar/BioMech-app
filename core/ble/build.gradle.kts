plugins {
    id("biomech.kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:common"))
            implementation(libs.kotlinx.coroutines)
        }
    }
}

android {
    namespace = "com.biomech.core.ble"
}
