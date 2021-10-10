package storage

interface StateHolder {
    fun forChat(id: Long): State
}
