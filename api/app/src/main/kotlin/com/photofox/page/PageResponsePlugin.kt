package com.photofox.page

import io.ktor.http.CacheControl
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.BaseApplicationPlugin
import io.ktor.server.mustache.MustacheContent
import io.ktor.server.response.ApplicationSendPipeline
import io.ktor.server.response.cacheControl
import io.ktor.util.AttributeKey

/**
 * Provides a way to set cache control hints on template responses (which shouldn't be cached)
 */
class PageResponsePlugin {
    companion object Plugin : BaseApplicationPlugin<ApplicationCallPipeline, Any, Plugin> {
        override val key = AttributeKey<Plugin>("PageResponse")

        override fun install(pipeline: ApplicationCallPipeline, configure: Any.() -> Unit): Plugin {
            pipeline.sendPipeline.intercept(ApplicationSendPipeline.Before) { value ->
                if (value is PageResponse<*>) {
                    context.response.cacheControl(
                        CacheControl.MaxAge(
                            maxAgeSeconds = 0,
                            visibility = CacheControl.Visibility.Private,
                            mustRevalidate = true,
                        ),
                    )
                    val response = MustacheContent(value.templatePath, value.viewModel)
                    proceedWith(response)
                }
            }
            return this
        }
    }
}
