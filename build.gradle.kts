import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	alias(connectorLibs.plugins.kotlin.jvm)
	alias(connectorLibs.plugins.kotlin.serialization)
	alias(connectorLibs.plugins.kotest)
	id("jacoco")
}

group = "org.wagham"
version = "0.21.0"

repositories {
	mavenCentral()
}

dependencies {
	implementation(connectorLibs.kmongo)
	implementation(connectorLibs.kotlinx.serialization.json)
	implementation(connectorLibs.kotlinx.coroutines.core)
	runtimeOnly(connectorLibs.kotlinx.coroutines.reactor)
	implementation(connectorLibs.slf4j.api)
	implementation(connectorLibs.slf4j.simple)
	testImplementation(connectorLibs.bundles.kotest)
}

tasks.withType<Test> {
	useJUnitPlatform()
}

java {
	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<KotlinCompile> {
	kotlinOptions.jvmTarget = "21"
}
