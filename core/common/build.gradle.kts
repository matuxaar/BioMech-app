plugins {
    id("biomech.kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines)
        }
    }
}

android {
    namespace = "com.biomech.core.common"
}
