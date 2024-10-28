package com.photofox.api

import io.ktor.server.application.ApplicationCall

fun interface CallHandling {
    fun handle(call: ApplicationCall)
}
