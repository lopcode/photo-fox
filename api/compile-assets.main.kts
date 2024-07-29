#!/usr/bin/env kotlin

@file:CompilerOptions("-jvm-target", "1.8")

import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.StandardCopyOption
import java.nio.file.Path
import java.nio.file.Paths
import java.security.MessageDigest
import java.util.stream.Collectors
import kotlin.system.exitProcess

fun ByteArray.toHexRepresentation(): String {
    return joinToString("") { "%02x".format(it) }
}

fun ByteArray.sha256(): String {
    val messageDigest = MessageDigest.getInstance("SHA-256")
    messageDigest.update(this)
    val digest = messageDigest.digest()
    return digest.toHexRepresentation()
}

fun walkFilesInPath(path: String): List<Path> {
    if (!Files.exists(Paths.get(path))) {
        return listOf()
    }
    return try {
        Files.walk(Paths.get(path))
            .filter { !Files.isHidden(it) }
            .filter { Files.isRegularFile(it) }
            .collect(Collectors.toList())
    } catch (exception: IOException) {
        println("$path folder not accessible (does it exist?)")
        exitProcess(1)
    }
}

fun deleteFilesInPath(path: Path) {
    try {
        Files.walk(path)
            .map { it.toFile() }
            .sorted(Comparator.reverseOrder())
            .forEach(File::delete)
    } catch (exception: NoSuchFileException) {
        return
    } catch (exception: IOException) {
        println("failed to delete files in $path $exception")
        exitProcess(1)
    }
}

val files = walkFilesInPath("static")
val buildPath = Paths.get("build")
val staticBuildPath = buildPath.resolve("static")
val templateBuildPath = buildPath.resolve("templates")
deleteFilesInPath(staticBuildPath)
deleteFilesInPath(templateBuildPath)
Files.createDirectories(staticBuildPath)
Files.createDirectories(templateBuildPath)

data class Candidate(
    val original: Path,
    val checksummed: Path
)

val staticCandidates = files.mapNotNull {
    val checksum = it.toFile().readBytes().sha256().take(6)
    if (checksum.length < 6) {
        println("bad checksum: $checksum $it")
        return@mapNotNull null
    }
    val path = it.toString()
    val lastPeriodIndex = path.lastIndexOf(".")
    if (lastPeriodIndex <= 0 || lastPeriodIndex > path.length - 1) {
        println("unexpected file extension: $it")
        return@mapNotNull null
    }

    val builder = StringBuilder(path)
    builder.insert(lastPeriodIndex + 1, "${checksum}.")
    val checksummedFilename = builder.toString()
    val candidate = Paths.get(checksummedFilename)

    return@mapNotNull Candidate(
        it,
        candidate
    )
}

if (staticCandidates.isEmpty()) {
    println("nothing to do")
    exitProcess(0)
}

staticCandidates.forEach {
    val replacementChecksumFile = buildPath.resolve(it.checksummed)
    val backupFile = buildPath.resolve(it.original)
    println("copying ${it.original}")
    Files.createDirectories(backupFile.parent)
    Files.copy(it.original, backupFile, StandardCopyOption.REPLACE_EXISTING)
    Files.copy(it.original, replacementChecksumFile, StandardCopyOption.REPLACE_EXISTING)
}

val originalTemplatePaths = walkFilesInPath("templates")
originalTemplatePaths.forEach {
    println("copying template: $it")
    val replacementTemplateFile = buildPath.resolve(it)
    Files.createDirectories(replacementTemplateFile.parent)
    Files.copy(it, replacementTemplateFile, StandardCopyOption.REPLACE_EXISTING)
}
val templatePaths = walkFilesInPath("build/templates")
val cssPaths = walkFilesInPath("build/static/css")
val jsPaths = walkFilesInPath("build/static/js")
val codePaths = walkFilesInPath("app/src/main/kotlin/com/photofox/processed")

val pathsToProcess = templatePaths + cssPaths + jsPaths + codePaths
pathsToProcess.forEach {
    println("found file to process: $it")
}

pathsToProcess.forEach { path ->
    val file = path.toFile()
    var fileText = file.readText(Charsets.UTF_8)

    staticCandidates.forEach { candidate ->
        val originalPath = "/${candidate.original.toString().removePrefix("/")}"
        val checksummedPath = "/${candidate.checksummed.toString().removePrefix("/")}"

        fileText = fileText.replace(originalPath, checksummedPath)
    }

    file.writeText(fileText)
    println("processed $path")
}