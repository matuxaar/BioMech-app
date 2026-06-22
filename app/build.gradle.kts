plugins {
    id("biomech.android.app")
}

android {
    namespace = "com.biomech.app"

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
}

dependencies {
    implementation(project(":composeApp"))
}
