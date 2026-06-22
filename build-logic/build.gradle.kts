plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("com.android.tools.build:gradle:8.2.2")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
    implementation("org.jetbrains.compose:compose-gradle-plugin:1.6.0")
}

gradlePlugin {
    plugins {
        register("biomechAndroidLibrary") {
            id = "biomech.android.library"
            implementationClass = "com.biomech.buildlogic.AndroidLibraryConventionPlugin"
        }
        register("biomechKmpLibrary") {
            id = "biomech.kmp.library"
            implementationClass = "com.biomech.buildlogic.KmpLibraryConventionPlugin"
        }
        register("biomechKmpComposeLibrary") {
            id = "biomech.kmp.compose"
            implementationClass = "com.biomech.buildlogic.KmpComposeConventionPlugin"
        }
        register("biomechAndroidApp") {
            id = "biomech.android.app"
            implementationClass = "com.biomech.buildlogic.AndroidAppConventionPlugin"
        }
    }
}
