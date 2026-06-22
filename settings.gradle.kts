rootProject.name = "BioMechApp"

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolution {
    repositories {
        google()
        mavenCentral()
    }
}

include(":composeApp")

include(":core:common")
include(":core:ui")
include(":core:network")
include(":core:database")
include(":core:ble")

include(":domain")

include(":feature:auth")
include(":feature:devices")
include(":feature:dashboard")
include(":feature:training")
include(":feature:settings")
