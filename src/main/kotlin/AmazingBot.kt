import flow.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.bots.AbsSender
import storage.StateHolder
import storage.Storage

class AmazingBot : UpdatesHandler, KoinComponent {

    private val storage by inject<Storage>()
    private val stateHolder by inject<StateHolder>()
    private val sender by inject<AbsSender>()

    override fun handle(update: Update) {
        if (update.hasMessage()) {
            val state = stateHolder.forChat(update.message.chatId)
            var flow: Flow? = Flow.currentFlow(state)
            if (update.message.hasText()) {
                when (update.message.text) {
                    "/start" -> {
                        flow = StartFlow(state)
                    }
                    "/new_game" -> {
                        flow = NewGameFlow(state)
                    }
                    "/info" -> {
                        flow = InfoFlow(state)
                    }
                    "/cancel" -> state.clear()
                }
            }
            if (flow == null) {
                unknownCommand(update)
            }
            state[Flow.CURRENT_FLOW] = flow
            flow?.next(update, sender)
        }
        if (update.hasCallbackQuery()) {
            val data = update.callbackQuery.data
            when {
                data.startsWith(NewGameConfirmation.DATA_PREFIX) -> {
                    NewGameConfirmation()
                        .next(update, sender)
                }
            }
        }
        updateUserInfo(update)
    }

    private fun unknownCommand(update: Update) {
        sender.executeAsync(SendMessage(update.message.chatId.toString(), i18n.UNKNOWN_COMMAND))
    }

    private fun updateUserInfo(update: Update) {
        if (!update.hasMessage() || !update.message.isUserMessage) return
        val from = update.message.from
        val name = from.firstName + from.lastName?.let { " ${from.lastName}" }
        val alias = from.userName?.let { "@${from.userName}" }
        storage.updateUser(from.id, update.message.chatId, name, alias)
    }
}
