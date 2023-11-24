import java.util.Properties

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

kotlin {
    jvmToolchain(17)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.call.id)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.host)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.status.pages)

    implementation(libs.ktor.jackson)

    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.arrow.core)

    implementation(libs.kompendium.core)
    implementation(libs.kotliquery)
    implementation(libs.flyway)
    implementation(libs.flyway.postgres)
    implementation(libs.postgres)
    implementation(libs.hikaricp)
    implementation(libs.jackson.datatype.jsr310)

    implementation(libs.logback.classic)
    implementation(libs.kotlin.logging)

    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.okhttp)

    testImplementation(libs.ktor.server.tests)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.framework.datatest)
    testImplementation(libs.kotest.extensions.testcontainers)
    testImplementation(libs.testcontainers.core)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.mockk.jvm)
    testImplementation(libs.ktor.client.mock)
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xcontext-receivers")
        }
    }
}

tasks.shadowJar {
    archiveFileName.set("orbit.jar")
}

tasks.jar {
    enabled = false
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

tasks.withType<ProcessResources> {
    doLast {
        val propertiesFile = rootProject.layout.buildDirectory.dir("resources/main/version.properties").get().asFile
        propertiesFile.parentFile.mkdirs()
        val properties = Properties()
        properties.setProperty("version", rootProject.version.toString())
        propertiesFile.writer().use { properties.store(it, null) }
    }
}
