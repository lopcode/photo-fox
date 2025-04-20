package app.photofox.api

import app.photofox.logger
import app.photofox.vipsffm.VImage
import app.photofox.vipsffm.Vips
import io.helidon.http.HeaderNames
import io.helidon.http.Status
import io.helidon.http.media.multipart.MultiPart
import io.helidon.webserver.http.Handler
import io.helidon.webserver.http.ServerRequest
import io.helidon.webserver.http.ServerResponse

class UploadPhotoRoute : Handler {
    private val logger = logger<UploadPhotoRoute>()

    override fun handle(
        request: ServerRequest,
        response: ServerResponse,
    ) {
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

        // todo: validate that there's one multipart request item sent, with name photo?
        val multipart = request.content().`as`(MultiPart::class.java)
        multipart.forEachRemaining {
            logger.info("data: ${it.name()} ${it.contentType()} ${it.fileName()}")

            Vips.run { arena ->
                // todo: error handling for loading image
                val image = VImage.newFromStream(arena, it.inputStream())
                logger.info("image metadata: ${image.width} ${image.height}")
            }
        }

        // todo: where to forward to on success?
        // should it event redirect? this is just a "JSON upload" thing?
        response
            .status(Status.ACCEPTED_202)
            .header(HeaderNames.LOCATION, "/")
            .send()
    }
}
