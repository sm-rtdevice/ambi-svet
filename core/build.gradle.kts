import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.svet"
version = "0.0.1"
description = "Common library for project"

plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.kotlin.logging.jvm)
    implementation(libs.logback.classic)

    implementation(libs.jackson.databind)

    implementation(libs.jssc)

    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.compilerOptions {
    freeCompilerArgs.add("-Xjsr305=strict")
    jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
}

tasks.withType<Test> {
    useJUnitPlatform()
}
