@file:Suppress("UnstableApiUsage")

import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import java.nio.file.Path

plugins {
    kotlin("jvm") version "2.0.20"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
    id("gg.jte.gradle") version ("3.1.15")
    application
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(22))
    }
}

kotlin {
    jvmToolchain(22)
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useKotlinTest("2.0.0")
            testType = TestSuiteType.UNIT_TEST

            dependencies {
            }
        }

        register<JvmTestSuite>("integrationTest") {
            useKotlinTest("2.0.0")
            testType = TestSuiteType.INTEGRATION_TEST

            dependencies {
                implementation(project())
                implementation("org.testcontainers:testcontainers:1.20.6")
                implementation("io.helidon.webclient:helidon-webclient:4.2.0")
            }

            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(test)
                    }
                }
            }
        }
    }
}

tasks.withType<Test> {
    testLogging {
        events = TestLogEvent.values().toSet() - TestLogEvent.STARTED
        exceptionFormat = TestExceptionFormat.FULL
    }
    outputs.upToDateWhen { false }
}

tasks.named("check") {
    dependsOn(testing.suites.named("test"))
    dependsOn(testing.suites.named("integrationTest"))
}

tasks.named("integrationTest") {
    dependsOn(tasks.named("assemble"))
}

dependencies {
    implementation(platform("io.helidon:helidon-bom:4.2.0"))
    implementation("io.helidon.webserver:helidon-webserver")
    implementation("io.helidon.webclient:helidon-webclient")
    implementation("io.helidon.logging:helidon-logging-common")
    implementation("io.helidon.webserver:helidon-webserver-access-log")
    implementation("io.helidon.logging:helidon-logging-slf4j")
    implementation("ch.qos.logback:logback-classic:1.5.18")
    implementation("io.helidon.webserver:helidon-webserver-static-content")
    implementation("io.helidon.http.media:helidon-http-media-multipart")
    implementation("org.slf4j:slf4j-api:2.0.16")
    implementation("org.slf4j:jul-to-slf4j:2.0.16")
    implementation("org.flywaydb:flyway-core:11.7.2")
    implementation("org.flywaydb:flyway-database-postgresql:11.7.2")
    implementation(platform("org.jdbi:jdbi3-bom:3.49.0"))
    implementation("org.postgresql:postgresql:42.7.5")
    implementation("org.jdbi:jdbi3-core")
    implementation("org.jdbi:jdbi3-sqlobject")
    implementation("org.jdbi:jdbi3-kotlin")
    implementation("org.jdbi:jdbi3-kotlin-sqlobject")
    implementation("net.logstash.logback:logstash-logback-encoder:8.0")
    implementation("app.photofox.vips-ffm:vips-ffm-core:1.5.2")
    implementation("gg.jte:jte:3.2.0")
    implementation("gg.jte:jte-kotlin:3.1.15")
}

application {
    mainClass = "app.photofox.MainKt"
}

configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    version.set("1.3.1")
    verbose.set(true)
}

tasks.register<Copy>("copyLibs") {
    from(configurations.runtimeClasspath)
    into("build/libs/libs")
}

tasks.named("assemble") {
    dependsOn(tasks.named("copyLibs"))
}

tasks.jar {
    dependsOn(tasks.precompileJte)
    duplicatesStrategy = DuplicatesStrategy.WARN
    from(
        fileTree("jte-classes") {
            include("**/*.class")
            include("**/*.bin") // Only required if you use binary templates
        },
    )
    manifest {
        attributes(
            mapOf(
                "Main-Class" to "app.photofox.MainKt",
                "Class-Path" to configurations.runtimeClasspath.get().files.joinToString(" ") { "libs/${it.name}" },
            ),
        )
    }
}

jte {
    precompile()
    sourceDirectory.set(Path.of("templates"))
    packageName = "app.photofox.generated.template"
}
