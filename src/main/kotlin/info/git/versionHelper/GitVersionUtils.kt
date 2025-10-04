package info.git.versionHelper

import info.shell.runCommand

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
    return processDescribe.trim() + dirty
}

fun getLatestGitHash(): String {
    val process = "git rev-parse --short HEAD".runCommand()
    return process.trim()
}

fun getLatestCommitText(): String {
    val process = "git log -1 --pretty=%B".runCommand()
    return process.trim()
}

fun getSHA1(): String {
    val process = "git rev-parse HEAD".runCommand()
    return process.trim()
}