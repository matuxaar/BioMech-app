plugins {
    id("biomech.kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:common"))
        }
    }
}

android {
    namespace = "com.biomech.domain"
}
