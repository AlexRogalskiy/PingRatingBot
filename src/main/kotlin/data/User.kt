package data

import kotlinx.serialization.Serializable

@Serializable
class User(
    val id: Long,
    val chatId: Long,
    val name: String,
    val alias: String?,
    val rating: RatingDataType
) {
    fun aliasOrName(): String {
        return alias ?: name
    }
}
