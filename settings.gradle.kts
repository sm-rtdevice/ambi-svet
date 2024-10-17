rootProject.name = "ambient-svet"
include(":core")
include(":capture-screen")
include(":configurator")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}
