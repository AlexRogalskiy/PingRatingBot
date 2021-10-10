package flow

import data.GameRequest
import data.RatingUpdate
import data.User
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.bots.AbsSender
import storage.Storage

class NewGameConfirmation : KoinComponent {
    private val storage by inject<Storage>()
    private val sender by inject<AbsSender>()

    fun next(update: Update, sender: AbsSender) {
        val data = update.callbackQuery.data
        val split = data.split("_")
        require(data.startsWith(DATA_PREFIX) && split.size == 3)
        val requestCode = split[1]
        val answer = split[2] == YES

        val gameRequest = storage.getGameRequest(requestCode) ?: throw IllegalStateException("No such game request code=$requestCode")
        handleAnswer(gameRequest, answer)

        val delete = DeleteMessage.builder()
            .chatId(update.callbackQuery.message.chatId.toString())
            .messageId(update.callbackQuery.message.messageId)
            .build()
        sender.executeAsync(delete)
        val toastText = if (answer) i18n.OK else i18n.FALSE_REQUEST
        val toast = AnswerCallbackQuery.builder()
            .callbackQueryId(update.callbackQuery.id)
            .text(toastText)
            .build()
        sender.executeAsync(toast)
    }

    private fun handleAnswer(request: GameRequest, answer: Boolean) {
        val author = storage.getUserById(request.players.first)
            ?: throw IllegalStateException("user must be already registered")
        val responder = storage.getUserById(request.players.second)
            ?: throw IllegalStateException("user must be already registered")

        if (answer) {
            val firstRating = RatingUpdate(request.players.first, 0)
            val secondRating = RatingUpdate(request.players.second, 0)
            storage.confirmGameRequest(request.requestCode, firstRating to secondRating)
            notifyGameConfirmed(request, firstRating to secondRating, author, responder)
        } else {
            storage.rejectGameRequest(request.requestCode)
            notifyGameRejected(request, author, responder)
        }
    }

    private fun notifyGameConfirmed(
        request: GameRequest,
        ratingUpdates: Pair<RatingUpdate, RatingUpdate>,
        author: User,
        responder: User
    ) {
        val authorText = i18n.GAME_CONFIRM_FOR_AUTHOR(responder.aliasOrName(), ratingUpdates.first.diff, 0)
        val responderText = i18n.GAME_CONFIRM_FOR_RESPONDER(author.aliasOrName(), ratingUpdates.second.diff, 0)

        sender.executeAsync(SendMessage(author.chatId.toString(), authorText))
        sender.executeAsync(SendMessage(responder.chatId.toString(), responderText))
    }

    private fun notifyGameRejected(request: GameRequest, author: User, responder: User) {
        val messageText = i18n.GAME_REJECTED(responder.aliasOrName(), request.score)
        sender.executeAsync(SendMessage(author.chatId.toString(), messageText))
        sender.executeAsync(SendMessage(responder.chatId.toString(), messageText))
    }

    companion object {
        const val DATA_PREFIX = "NewGameConfirmation"
        private const val YES = "yes"
        private const val NO = "no"

        fun makeReplyMarkup(request: GameRequest): InlineKeyboardMarkup {
            val data = "${DATA_PREFIX}_${request.requestCode}_"
            return InlineKeyboardMarkup().also {
                it.keyboard = listOf(
                    listOf(
                        InlineKeyboardButton.builder()
                            .text(i18n.YES)
                            .callbackData(data + YES)
                            .build(),
                        InlineKeyboardButton.builder()
                            .text(i18n.NO)
                            .callbackData(data + NO)
                            .build()
                    )
                )
            }
        }
    }
}
