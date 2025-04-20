package app.photofox

import io.helidon.common.media.type.MediaTypes
import io.helidon.webclient.api.WebClient
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
                .withFileFromPath("app.jar", Path.of("build/libs/app.jar"))
                .withFileFromPath("libs", Path.of("build/libs/libs"))
                .withFileFromPath("static", Path.of("static"))
                .withFileFromPath("templates", Path.of("templates"))
                .withBuildArgs(
                    mapOf(
                        "jar" to "app.jar",
                        "libs" to "libs",
                        "static_assets" to "static",
                        "template_assets" to "templates",
                        "version" to "integration_test",
                    ),
                ),
        )
            .withExposedPorts(8089)
            .withEnv("APP_PORT", "8089")
            .withEnv("APP_HOST", "0.0.0.0")
            .withLogConsumer(Slf4jLogConsumer(logger))
            .waitingFor(Wait.forListeningPorts(8089))!!

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
    fun `docker container builds and launches`() {
        val client = WebClient.builder()
            .baseUri("http://${app.host}:${app.getMappedPort(8089)}")
            .build()

        val result = client
            .get("/api/health")
            .contentType(MediaTypes.APPLICATION_JSON)
            .request()
        assertEquals(200, result.status().code())
    }
}
