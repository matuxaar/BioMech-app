plugins {
    id("com.android.kotlin.multiplatform.library")
    kotlin("multiplatform")
    id("com.google.devtools.ksp")
}

kotlin {
    android {
        namespace = "com.biomech.core.database"
        compileSdk = 36
        minSdk = 26
    }

    listOf(iosArm64(), iosSimulatorArm64()).forEach { target ->
        target.binaries.framework {
            baseName = "Database"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:common"))
            implementation(project(":domain"))
            api(libs.room.common)
            implementation(libs.kotlinx.coroutines.core)
        }
        androidMain.dependencies {
            implementation(libs.room.runtime)
            implementation(libs.room.ktx)
        }
        iosMain.dependencies {
        }
    }
}

dependencies {
    add("kspAndroid", libs.room.compiler)
}
