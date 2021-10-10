package data

import kotlinx.serialization.Serializable

@Serializable
class Game private constructor(
    val players: Pair<Long, Long>,
    val score: Pair<Int, Int>,
    val datetime: Long,
) {
    companion object {
        fun fromRequest(request: GameRequest): Game {
            return Game(
                request.players,
                request.score,
                request.timestamp
            )
        }
    }
}
