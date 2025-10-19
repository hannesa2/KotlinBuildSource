package info.git.versionHelper

import info.shell.runCommand
import kotlinx.serialization.json.Json
import org.gradle.api.logging.Logger
import java.io.File

fun getReleaseNotes(filter: String? = null): String {
    val contBase = filter?.let {
        filter
    } ?: run {
        "HEAD"
    }
    val code = "git rev-list $contBase --count".runCommand()
    val prev = "git describe --abbrev=0 --tags $filter 2>/dev/null".runCommand()
    println("code=$code")
    println("prev=$prev")
//    if [[ $? == 0 ]]; then
//    git log --no-merges --grep=$FILTER --pretty=format:'* %f' $version...$prev | sed 's/-/ /g' > /tmp/releaseNotes
//    head -c 497 /tmp/releaseNotes && echo ...
//    fi
//
    return prev
}

fun getTagGroupedGitlog(filter: String? = null, filename: String, logger: Logger? = null): String {
    val logEntries = mutableListOf<LogEntry>()
    logger?.info("plain=" + "git log --no-walk --tags --pretty=format:'%d' --abbrev-commit".runCommand())
    // split lines like '(tag: refs/tags/3.7.1)'
    val tags = "git log --no-walk --tags --pretty=format:'%d' --abbrev-commit".runCommand()
        .split("\n")
        .map { it.replace("HEAD, ", "") } // eg. (HEAD, tag: 3.8.2-beta14)
        .map { it.replace("refs/tags/", "") }
        .map { it.substringBefore(",") }
        .map { it.substringAfterLast(" ") }
        .map { it.substringBefore(")") }
        .toMutableList().also {
            it.add(0, "HEAD")
            it.add(
                it.count(),
                "git rev-list --max-parents=0 HEAD".runCommand() // add initial commit
            )
        }
    logger?.info(tags.toString())
    tags.forEachIndexed { i, element ->
        if (i == tags.count() - 1) return@forEachIndexed
        val code = "git rev-list $element --count".runCommand()
        logger?.info("git log --no-mergeqs --pretty=format:%s|%ad $element...${tags[i + 1]}")
        "git log --no-merges --pretty=format:%s|%ad $element...${tags[i + 1]}".runCommand()
            .split("\n")
            .filter { filter == null || it.contains(filter) }
            .forEach {
                val message = it.replace("$filter-", "").substringBefore("|")
                logger?.debug("commit=$message")
                val entry = LogEntry(
                    version = element,
                    code = code.toInt(),
                    message = message,
                    date = it.substringAfter("|")
                )
                logEntries.add(entry)
            }
    }
    val file = File(filename)
    if (!file.exists()) {
        val dir = File(filename.substringBeforeLast("/"))
        logger?.info("create path =${dir}")
        dir.mkdirs()
    }
    File(filename).writeText(Json.encodeToString(logEntries))
    return filename
}