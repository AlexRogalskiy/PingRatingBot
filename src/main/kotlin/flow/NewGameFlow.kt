package flow

import data.GameRequest
import org.koin.core.component.inject
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.bots.AbsSender
import storage.State
import storage.Storage

class NewGameFlow(state: State) : Flow(state) {
    private val storage by inject<Storage>()

    private var score: Pair<Int, Int>? = null
    private var opponent: Long? = null
    private var step = Steps.INIT
    override fun next(update: Update, sender: AbsSender) {
        val message = when (step) {
            Steps.INIT -> {
                step = Steps.WAIT_FOR_SCORE
                i18n.NEW_GAME_SCORE_REQUEST
            }
            Steps.WAIT_FOR_SCORE -> {
                score = tryParseScore(update)
                if (score == null) {
                    i18n.NEW_GAME_SCORE_REQUEST
                } else {
                    step = Steps.WAIT_FOR_OPPONENT
                    i18n.NEW_GAME_OPPONENT_REQUEST
                }
            }
            Steps.WAIT_FOR_OPPONENT -> {
                opponent = tryParseContact(update)
                if (opponent == null) {
                    i18n.SECOND_PLAYER_NOT_REGISTERED
                } else {
                    registerRequest(update.message.from.id to opponent!!, score!!, sender)
                    flowFinish()
                    i18n.CONFIRM_MESSAGE_SENT
                }
            }
        }
        sender.execute(SendMessage(update.message.chatId.toString(), message))
    }

    private fun tryParseScore(update: Update): Pair<Int, Int>? {
        if (!update.message.hasText()) return null
        val parts = update.message.text.split(" ")
        if (parts.size != 2) return null
        return try {
            Integer.parseInt(parts[0]) to Integer.parseInt(parts[1])
        } catch (e: NumberFormatException) {
            null
        }
    }

    private fun tryParseContact(update: Update): Long? {
        return if (update.message.hasContact()) {
            val user = storage.getUserById(update.message.contact.userId)
            user?.id
        } else if (update.message.hasText() && update.message.text.startsWith("@")) {
            val user = storage.getUserByAlias(update.message.text)
            user?.id
        } else {
            null
        }
    }

    private fun registerRequest(players: Pair<Long, Long>, score: Pair<Int, Int>, sender: AbsSender) {
        val request = GameRequest(players, score)
        storage.addGameRequest(request)
        sendConfirmation(request, sender)
    }

    private fun sendConfirmation(request: GameRequest, sender: AbsSender) {
        val firstUser = storage.getUserById(request.players.first)
            ?: throw IllegalStateException("User who sent game request is not registered!")
        val secondUser = storage.getUserById(request.players.second)
        if (secondUser == null) {
            sender.execute(SendMessage(firstUser.chatId.toString(), i18n.SECOND_PLAYER_NOT_REGISTERED))
        } else {
            val message = SendMessage(secondUser.chatId.toString(), i18n.CONFIRM_GAME_REQUEST(firstUser.aliasOrName(), request.score))
            message.replyMarkup = NewGameConfirmation.makeReplyMarkup(request)
            sender.execute(message)
        }
    }
}

private enum class Steps {
    INIT,
    WAIT_FOR_SCORE,
    WAIT_FOR_OPPONENT,
}
