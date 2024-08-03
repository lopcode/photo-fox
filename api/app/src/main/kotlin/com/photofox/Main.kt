package com.photofox

import com.github.mustachejava.DefaultMustacheFactory
import com.photofox.page.PageResponse
import com.photofox.page.PageResponsePlugin
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.staticFiles
import io.ktor.server.mustache.Mustache
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.callid.CallId
import io.ktor.server.plugins.callid.callIdMdc
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.calllogging.CallLoggingConfig
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.origin
import io.ktor.server.request.host
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import io.ktor.server.request.port
import io.ktor.server.request.uri
import io.ktor.server.request.userAgent
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import java.io.File
import java.nio.file.Paths
import java.util.UUID

val logger = logger<Application>()

fun main() {
    logger.info("starting...")

    val port = System.getenv()["APP_PORT"]?.toInt() ?: 8080
    val host = System.getenv()["APP_HOST"] ?: "127.0.0.1"
    embeddedServer(
        Netty,
        port = port,
        host = host,
        module = Application::module,
    ).start(wait = true)

    logger.info("stopped")
}

fun Application.module() {
    configurePlugins()
    configureRouting()
}

private fun Application.configurePlugins() {
    install(CallId) {
        header(HttpHeaders.XRequestId)
        generate {
            UUID.randomUUID().toString() // todo: uuidv7
        }
    }
    install(CallLogging) {
        disableDefaultColors()
        setUpCallLoggingMDC()
    }
    install(ContentNegotiation) {
        json()
    }
    install(Mustache) {
        val templatesRoot = Paths.get("templates").toFile()
        mustacheFactory = DefaultMustacheFactory(templatesRoot)
    }
    install(PageResponsePlugin)
}

private fun CallLoggingConfig.setUpCallLoggingMDC() {
    mdc("http.method") {
        it.request.httpMethod.value
    }
    mdc("http.url") {
        it.request.uri
    }
    mdc("http.useragent") {
        it.request.userAgent()
    }
    mdc("http.url_details.host") {
        it.request.host()
    }
    mdc("http.url_details.port") {
        it.request.port().toString()
    }
    mdc("http.url_details.path") {
        it.request.path()
    }
    mdc("http.status_code") {
        it.response.status()?.value?.toString()
    }
    mdc("network.client.network_ip") {
        it.request.origin.remoteHost
    }
    mdc("network.client.ip") {
        it.request.origin.remoteHost
    }
    callIdMdc("http.request_id")
}

private fun Application.configureRouting() {
    val webPathPrefix = System.getenv()["WEB_PATH_PREFIX"] ?: "web"
    routing {
        route("/api") {
            get("/health") {
                call.respondText("🦊📸")
            }
        }
        route("/$webPathPrefix") {
            get("/") {
                call.respondPage("index")
            }
            staticFiles("/static", File("static"))
        }
    }
}

suspend fun <ViewModel : Any> RoutingCall.respondPage(pageTemplateName: String, viewModel: ViewModel) {
    return this.respond(
        PageResponse(
            makePageTemplatePath(pageTemplateName),
            viewModel,
        ),
    )
}

suspend fun RoutingCall.respondPage(pageTemplateName: String) {
    return this.respond(
        PageResponse(
            makePageTemplatePath(pageTemplateName),
            Unit,
        ),
    )
}

private fun makePageTemplatePath(pageTemplateName: String): String {
    return "page/$pageTemplateName.mustache"
}
