plugins {
    id("com.android.kotlin.multiplatform.library")
    kotlin("multiplatform")
}

kotlin {
    android {
        namespace = "com.biomech.core.notification"
        compileSdk = 36
        minSdk = 26
    }

    iosArm64()
    iosSimulatorArm64()

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:common"))
        }
    }
}
