plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinter)
    alias(libs.plugins.detekt)
    jacoco
}

group = "no.java.partner"
version = "0.0.1"

application {
    mainClass.set("no.java.partner.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

java {
    sourceCompatibility = JavaVersion.VERSION_20
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ktor.server.call.id)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.host)
    implementation(libs.ktor.server.metrics.micrometer)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.status.pages)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.content.negotiation)

    implementation(libs.ktor.jackson)

    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.arrow.core)

    implementation(libs.kotliquery)
    implementation(libs.flyway)
    implementation(libs.postgres)
    implementation(libs.hikaricp)
    implementation(libs.jackson.datatype.jsr310)

    implementation(libs.logback.classic)
    implementation(libs.kotlin.logging)
    implementation(libs.micrometer.registry.prometheus)

    testImplementation(libs.ktor.server.tests)
    testImplementation(libs.ktor.client.mock)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.framework.datatest)
    testImplementation(libs.kotest.extensions.testcontainers)
    testImplementation(libs.testcontainers.core)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.mockk.jvm)
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xcontext-receivers")
            jvmTarget = "20"
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
    }
    dependsOn(tasks.test)
}

