package storage

import data.Game
import data.GameRequest
import data.RatingUpdate
import data.User
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
class JsonFileStorage private constructor() : Storage {
    private val requests = mutableListOf<GameRequest>()
    private val games = mutableListOf<Game>()
    private val users = mutableListOf<User>()

    @Synchronized
    override fun addGameRequest(request: GameRequest) {
        requests.add(request)
        save()
    }

    @Synchronized
    override fun getGameRequest(code: String): GameRequest? {
        return requests.firstOrNull { it.requestCode == code }
    }

    @Synchronized
    override fun confirmGameRequest(code: String, ratingUpdates: Pair<RatingUpdate, RatingUpdate>) {
        val request = getGameRequest(code)
            ?: throw IllegalArgumentException("Game request with code=$code doesn't exist")
        requests.removeIf { it.requestCode == code }
        games.add(Game.fromRequest(request))
        ratingUpdates.toList().forEach { update ->
            val old = users.firstOrNull { it.id == update.userId }
                ?: throw IllegalArgumentException("Rating update for non existing user#${update.userId}")
            val updated = old.copy(rating = update.newRating)
            users.removeIf { it.id == old.id }
            users.add(updated)
        }
        save()
    }

    @Synchronized
    override fun rejectGameRequest(code: String) {
        requests.removeIf { it.requestCode == code }
        save()
    }

    @Synchronized
    override fun addNewUser(user: User) {
        if (users.firstOrNull { it.id == user.id } != null) {
            throw IllegalArgumentException("user ${user.name} with id=${user.id} already exist")
        }
        users.add(user)
        save()
    }

    @Synchronized
    override fun updateUser(id: Long, chatId: Long, name: String, alias: String?) {
        val user = users.firstOrNull { it.id == id }
            ?: throw IllegalArgumentException("No user registered with $id")
        if (
            user.chatId == chatId
            && user.name == name
            && user.alias == alias
        ) return
        val updated = user.copy(chatId = chatId, name = name, alias = alias)
        users.removeIf { it.id == id }
        users.add(updated)
        save()
    }

    @Synchronized
    override fun getUserById(id: Long): User? {
        return users.firstOrNull { it.id == id }
    }

    @Synchronized
    override fun getUserByAlias(alias: String): User? {
        return users.firstOrNull { it.alias == alias }
    }

    private fun save() {
        val json = Json.encodeToString(this)
        File(DB_PATH).outputStream().bufferedWriter().use {
            it.write(json)
        }
    }

    companion object {
        const val DB_PATH = "/home/mitya/Desktop/db.json"

        fun load(): JsonFileStorage {
            val file = File(DB_PATH)
            if (!file.exists()) {
                return JsonFileStorage()
            }
            if (!file.canWrite())
                throw IllegalStateException("DB file ${file.absoluteFile} is not writable!")
            return Json.decodeFromString(file.reader().readText())
        }
    }
}
