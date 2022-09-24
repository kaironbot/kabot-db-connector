import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    id("io.kotest") version "0.3.8"
}

group = "org.wagham"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.litote.kmongo:kmongo-coroutine:4.7.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("io.kotest:kotest-runner-junit5-jvm:5.4.2")
    implementation("org.slf4j:slf4j-api:2.0.0")
    implementation("org.slf4j:slf4j-simple:2.0.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}