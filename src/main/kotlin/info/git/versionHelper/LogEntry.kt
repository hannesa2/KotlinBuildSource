package info.git.versionHelper

import kotlinx.serialization.Serializable

@Serializable
data class LogEntry(val version: String, val code: Int, val date: String, val message: String)
