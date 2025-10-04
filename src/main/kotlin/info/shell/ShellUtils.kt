package info.shell

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

@Suppress("NewApi")
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

fun String.runCommandApi23(workingDir: File = File("./")): String {
    val parts = this.split("\\s".toRegex())
    val proc = ProcessBuilder(*parts.toTypedArray())
        .directory(workingDir)
        .start()

    proc.waitFor()
    val output = proc.inputStream.bufferedReader().readText()
    val error = proc.errorStream.bufferedReader().readText()
    return (output + error).trim()
}

fun getUnixCreateTime() = System.currentTimeMillis() / 1000

@Suppress("SimpleDateFormat")
fun getDate(onlyMonth: Boolean = false): String {
    val timestamp = if (onlyMonth)
        SimpleDateFormat("yyyy.MM").format(Date())
    else
        SimpleDateFormat("yyyy.MM.dd-HH:mm").format(Date())
    return timestamp
}
