package data

import core.EloCalculator
import kotlinx.serialization.Serializable

@Serializable
data class User private constructor(
    val id: Long,
    val chatId: Long,
    val name: String,
    val alias: String?,
    val rating: Int
) {
    fun aliasOrName(): String {
        return alias ?: name
    }

    companion object {
        fun createWithDefaultEloRating(
            id: Long,
            chatId: Long,
            name: String,
            alias: String?
        ): User {
            return User(id, chatId, name, alias, EloCalculator.INITIAL_RATING)
        }
        fun nameFrom(firstName: String, lastName: String?): String {
            return firstName + lastName?.let { " $it" }
        }
        fun aliasFrom(userName: String?): String? {
            return userName?.let { "@$it" }
        }
    }
}
