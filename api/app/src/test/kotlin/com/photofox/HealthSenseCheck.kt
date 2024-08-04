package com.photofox

import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class HealthSenseCheck {
    @Test
    fun `health check returns 2xx`() = testApplication {
        application {
            module()
        }

        val response = client.get("/api/health")
        assertEquals(HttpStatusCode.OK, response.status)
    }
}
