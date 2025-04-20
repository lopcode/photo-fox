package app.photofox

import app.photofox.api.UploadPhotoRoute
import app.photofox.vipsffm.Vips
import gg.jte.ContentType
import gg.jte.TemplateEngine
import gg.jte.output.StringOutput
import gg.jte.resolve.DirectoryCodeResolver
import io.helidon.common.media.type.MediaTypes
import io.helidon.http.HeaderNames
import io.helidon.http.Method
import io.helidon.http.Status
import io.helidon.logging.common.LogConfig
import io.helidon.webserver.WebServer
import io.helidon.webserver.accesslog.AccessLogFeature
import io.helidon.webserver.http.HttpRouting
import io.helidon.webserver.staticcontent.FileSystemHandlerConfig
import io.helidon.webserver.staticcontent.StaticContentFeature
import org.jetbrains.kotlin.util.prefixIfNot
import org.slf4j.MDC
import java.nio.file.Path
import java.nio.file.Paths

val logger = logger<Main>()

object Main

fun main() {
    LogConfig.configureRuntime()
    Vips.init()

    logger.info("starting...")

    val port = System.getenv()["APP_PORT"]?.toInt() ?: 8080
    val host = System.getenv()["APP_HOST"] ?: "127.0.0.1"

    val server = WebServer.builder()
        .addFeature(
            AccessLogFeature.builder()
                .commonLogFormat()
                .build(),
        )
        .host(host)
        .port(port)
        .routing(::routing)
        .build()
    server.start()

    logger.info("server started...")
}

private fun routing(routing: HttpRouting.Builder) {
    val webPathPrefix = System.getenv()["WEB_PATH_PREFIX"] ?: "web"
        .prefixIfNot("/")
    val isDevMode = System.getenv()["DEV_MODE"]?.toBooleanStrictOrNull() ?: false
    val uploadPhotoRoute = UploadPhotoRoute()
    val resolver = DirectoryCodeResolver(Path.of("templates"))
    val templateEngine = TemplateEngine.create(resolver, ContentType.Html)
    logger.info("precompiling templates...")
    println(templateEngine.precompileAll())
    routing.route(Method.GET, "/api/health") { _, response ->
        MDC.put("test", "test-value")
        response.send("🦊📸")
    }
    routing.route(Method.POST, "/api/v1/upload_photo", uploadPhotoRoute)
    routing.route(Method.GET, webPathPrefix) { request, response ->
        val output = StringOutput()
        templateEngine.render("page/index.jte", Unit, output)
        response.headers().contentType(MediaTypes.TEXT_HTML)
        response.headers().set(HeaderNames.CACHE_CONTROL, "no-store")
        response.status(Status.OK_200)
        response.send(output.toString())
    }
    routing.route(Method.GET, "$webPathPrefix/upload") { request, response ->
        val output = StringOutput()
        templateEngine.render("page/upload.jte", Unit, output)
        response.headers().contentType(MediaTypes.TEXT_HTML)
        response.headers().set(HeaderNames.CACHE_CONTROL, "no-store")
        response.status(Status.OK_200)
        response.send(output.toString())
    }
    routing.register(
        "/static",
        StaticContentFeature.createService(
            FileSystemHandlerConfig.builder()
                .location(Paths.get("static"))
                .build(),
        ),
    )
}

// mustache templates

private fun makePageTemplatePath(pageTemplateName: String): String {
    return "page/$pageTemplateName.mustache"
}
