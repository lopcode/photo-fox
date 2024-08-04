package com.photofox

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.images.builder.ImageFromDockerfile
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals

class IntegrationTest {
    companion object {
        private val logger = logger<IntegrationTest>()

        @JvmStatic
        val app = GenericContainer(
            ImageFromDockerfile()
                .withFileFromPath("Dockerfile", Path.of("../Dockerfile"))
                .withFileFromPath("app-all.jar", Path.of("build/libs/app-all.jar"))
                .withFileFromPath("static", Path.of("static"))
                .withFileFromPath("templates", Path.of("templates"))
                .withBuildArgs(
                    mapOf(
                        "jar" to "app-all.jar",
                        "static_assets" to "static",
                        "template_assets" to "templates",
                        "version" to "integration_test",
                    ),
                ),
        )
            .withExposedPorts(8080)
            .withEnv("APP_PORT", "8080")
            .withEnv("APP_HOST", "0.0.0.0")
            .withLogConsumer(Slf4jLogConsumer(logger))
            .waitingFor(Wait.forListeningPorts(8080))!!

        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            app.start()
        }

        @AfterAll
        @JvmStatic
        fun afterAll() {
            app.stop()
        }
    }

    @Test
    fun `docker container builds and launches`() = runBlocking {
        val client = HttpClient()

        val baseHost = "http://${app.host}:${app.getMappedPort(8080)}"
        val response = client.get("$baseHost/api/health")

        assertEquals(HttpStatusCode.OK, response.status)
    }
}
