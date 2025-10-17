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

fun getGitCommitCount(offset: Int = 0): Int {
    val process = "git rev-list HEAD --count".runCommand()
    return process.toInt() + offset
}

fun getVersionText(): String {
    val processChanges = "git diff-index --name-only HEAD --".runCommand()
//    val processChanges = "git status --porcelain".runCommand()
    val dirty = if (processChanges.trim().isNotEmpty()) {
        // split lines by TAB (10) and add a leading space for better visibility
        println { "git status".italic+" is not clean, changes are:" }
        processChanges.split(10.toChar()).forEach { println { " $it".red.bold } }
        "-DIRTY"
    } else ""

    val givenVersion = System.getenv("tag") ?: "git describe".runCommand().trim()
    return givenVersion + dirty
}

fun getLatestGitHash(): String {
    val process = "git rev-parse --short HEAD".runCommand()
    return process.trim()
}

fun getLatestCommitText(): String {
    val process = "git log -1 --pretty=%B".runCommand()
    return process.split("\n")[0].trim()
}

fun getSHA1(): String {
    val process = "git rev-parse HEAD".runCommand()
    return process.trim()
}