package info.git.versionHelper

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

@JvmOverloads
fun String.runCommand(workingDir: File = File("./")): String {
    val parts = this.split("\\s".toRegex())
    val proc = ProcessBuilder(*parts.toTypedArray())
        .directory(workingDir)
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
        .start()

    proc.waitFor(1, TimeUnit.MINUTES)
    return proc.inputStream.bufferedReader().readText().trim()
}

@Suppress("SimpleDateFormat")
fun getDate(): String {
    val timestamp = SimpleDateFormat("yyyy.MM.dd-HH:mm").format(Date())
    return timestamp
}

fun getGitOriginRemote(): String {
    val process = "git remote -v".runCommand()
    val values = process.trim().split("\n")
    val foundLine = values.find {
        it.startsWith("origin") && it.endsWith("(push)")
    }
    return foundLine
        ?.replace("origin", "")
        ?.replace("(push)", "")
        ?.replace(".git", "")
        ?.trim()!!
}

fun getGitCommitCount(): Int {
    val process = "git rev-list HEAD --count".runCommand()
    return process.toInt() + 580
}

fun getVersionText(): String {
    // val processChanges = "git diff-index --name-only HEAD --".runCommand()
    val processChanges = "git status --porcelain".runCommand()
    var dirty = ""
    if (processChanges.trim().isNotEmpty())
        dirty = "-DIRTY"

    val processDescribe = "git describe".runCommand()
    val processDate = "date +%Y-%m-%d".runCommand()
    return processDescribe.trim() + dirty + "-" + processDate.trim()
}

fun getLatestGitHash(): String {
    val process = "git rev-parse --short HEAD".runCommand()
    return process.trim()
}

fun getSHA1(): String {
    val process = "git rev-parse HEAD".runCommand()
    return process.trim()
}