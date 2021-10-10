package storage

interface State {
    operator fun get(key: String): Any?
    operator fun set(key: String, value: Any?)
    fun withPrefix(prefix: String): State
    fun clear()
}
