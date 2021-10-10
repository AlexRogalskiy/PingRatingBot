package storage

class InMemoryStateHolder: StateHolder {
    private val map = mutableMapOf<Long, MapState>()

    override fun forChat(id: Long): State {
        return map.getOrPut(id) { MapState() }
    }

    class MapState(
        private val prefix: String? = null,
        private val map: MutableMap<String, Any?> = hashMapOf()
    ): State {
        override fun get(key: String): Any? = map.getOrDefault(prefixed(key), null)

        override fun set(key: String, value: Any?) = map.set(prefixed(key), value)

        override fun withPrefix(prefix: String): State = MapState(prefixed(prefix), map)

        override fun clear() {
            map.keys
                .filter { it.startsWith(prefix ?: "") }
                .forEach { map.remove(it) }
        }

        private fun prefixed(key: String) = if (prefix == null) key else "${prefix}_$key"
    }
}
