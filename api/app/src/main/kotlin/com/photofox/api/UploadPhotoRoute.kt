package com.photofox.api

import io.ktor.server.application.ApplicationCall

class UploadPhotoRoute : CallHandling {
    override fun handle(call: ApplicationCall) {
        // stream post call?
        // limit to jpeg/heif to begin with?
        //  at least need file metadata
        // how does validation work for images?
        //  limits on height/width, size, file formats, ability to interpret?
        // store as "original"
        //  file path in storage - content addressable? ab/ab1234.jpeg?
        // store metadata in postgres first
        //  some sort of "stage" - upload could take time?
        //  able to mark as failed or succeeded?
        // queueing other transformations
        //  sensible defaults?
        // how much to bake in vips-ffm?
    }
}
