import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.9.20"
	id("io.kotest") version "0.3.8"
	id("org.jetbrains.kotlin.plugin.serialization") version "1.9.20"
	id("jacoco")
}

buildscript {
	repositories {
		mavenCentral()
	}
}


group = "org.wagham"
version = "0.21.0"

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.litote.kmongo:kmongo-coroutine:4.11.0")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
	runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.7.1")
	implementation("io.kotest:kotest-runner-junit5-jvm:5.5.5")
	implementation("org.slf4j:slf4j-api:2.0.5")
	implementation("org.slf4j:slf4j-simple:2.0.5")
	testImplementation("io.kotest:kotest-assertions-core-jvm:5.5.5")
	testImplementation("io.kotest:kotest-framework-engine-jvm:5.5.5")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

java {
	sourceCompatibility = JavaVersion.VERSION_19
	targetCompatibility = JavaVersion.VERSION_19
}

tasks.withType<KotlinCompile> {
	kotlinOptions.jvmTarget = "19"
}
