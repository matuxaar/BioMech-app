plugins {
    id("biomech.kmp.library")
    alias(libs.plugins.sqldelight)
}

sqldelight {
    databases {
        create("BioMechDatabase") {
            packageName.set("com.biomech.core.database")
        }
    }
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:common"))
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines)
        }

        androidMain.dependencies {
            implementation(libs.sqldelight.android)
        }

        iosMain.dependencies {
            implementation(libs.sqldelight.native)
        }
    }
}

android {
    namespace = "com.biomech.core.database"
}
