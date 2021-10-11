package utils

import java.io.File

class DotEnv(file: File = File(".env")) {
    val canRead: Boolean = file.canRead()
    private val map = mutableMapOf<String, String>()

    init {
        file.readLines()
            .filter { it.isNotBlank() && !it.startsWith("#") }
            .map { it.trim().split("=", limit = 2) }
            .forEach {
                require(it.size == 2) { "Bad string in .env file ${it.joinToString("=")}" }
                map[it.first()] = it.last()
            }
    }

    operator fun get(key: String): String? = map[key]
}
