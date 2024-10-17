import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.svet"
version = "0.0.1"
description = "Application for screen capture"

plugins {
    java
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(project(":core"))

    implementation(libs.kotlin.logging.jvm)
    implementation(libs.logback.classic)
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.compilerOptions {
    freeCompilerArgs.add("-Xjsr305=strict")
    jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "com.svet.CaptureKt"
        )
    }

    /*duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })*/
}
