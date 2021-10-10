package storage

import data.GameRequest
import data.RatingUpdate
import data.User

interface Storage {
    fun addGameRequest(request: GameRequest)
    fun getGameRequest(code: String): GameRequest?
    fun confirmGameRequest(
        code: String,
        ratingUpdates: Pair<RatingUpdate, RatingUpdate>
    )
    fun rejectGameRequest(code: String)

    fun addUser(user: User)
    fun updateUser(id: Long, chatId: Long, name: String, alias: String?)
    fun getUserById(id: Long): User?
    fun getUserByAlias(alias: String): User?
//    fun getUserInfo(q: String): UserInfo?
//    fun getGroupInfo(q: String): GroupInfo?
}
