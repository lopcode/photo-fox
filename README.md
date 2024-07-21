# Photo Fox

A work-in-progress, self-hosted, photo management app 🦊📸.

I love taking and sharing photos, and wanted a tool that can:
* Store original photos, in a way that makes taking [3-2-1 backups](https://www.backblaze.com/blog/the-3-2-1-backup-strategy/) easy
  * And do basic transformations to generate device-appropriate variants, thumbnails, and previews
* Let me share my photos online
  * Via a simple, visually appealing, accessible website
  * In a quality of my choosing and not crunchified (especially in link previews)
  * Organised in to albums / sub-albums, with searchable tags
  * Annotated, automatically or manually, with photo metadata like license, location, camera type, and other interesting EXIF data

And from a technical perspective:
* Be built and deployable using current "best practices"
  * All user media stored in S3-compatible cloud storage ([S3](https://aws.amazon.com/s3/), [R2](https://www.cloudflare.com/en-gb/developer-platform/r2/), [B2](https://www.backblaze.com/cloud-storage) etc) by default
  * A stateless Kotlin/Ktor API, with all application state stored in Postgres
  * Documentation kept in-sync with the main repo branch
  * Containers, observability, canary deployments, zero-downtime upgrades, etc
* Be technically flexible enough to do things like host artwork, or house multiple tenants, in the future

Many great options exist, but none quite scratched my itch, and making something bespoke sounded like a really fun side
project.

If you like the idea, star the repo, or let me know and share your thoughts on [Bluesky](https://bsky.app/profile/lopcode.com),
or [GitHub Discussions](https://github.com/lopcode/photo-fox/discussions).