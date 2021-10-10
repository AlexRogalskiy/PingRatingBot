package flow

import data.User
import org.koin.core.component.inject
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.bots.AbsSender
import storage.State
import storage.Storage

class StartFlow(state: State): Flow(state) {
    private val storage by inject<Storage>()

    override fun next(update: Update, sender: AbsSender) {
        sender.execute(SendMessage(update.message.chatId.toString(), i18n.START_MESSAGE(0)))
        flowFinish()
        val from = update.message.from
        val name = from.firstName + from.lastName?.let { " ${from.lastName}" }
        val alias = from.userName?.let { "@${from.userName}" }
        storage.addUser(User(from.id, update.message.chatId, name, alias, 0))
    }
}
