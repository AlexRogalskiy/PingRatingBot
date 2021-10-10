package data

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
class GameRequest(
    val players: Pair<Long, Long>,
    val score: Pair<Int, Int>,
    val requestCode: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
)
