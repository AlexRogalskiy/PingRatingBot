package flow

import data.User
import org.koin.core.component.inject
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.bots.AbsSender
import storage.State
import storage.Storage

class StartFlow(state: State) : Flow(state) {
    private val storage by inject<Storage>()

    override fun next(update: Update, sender: AbsSender) {
        val from = update.message.from
        val exist = storage.getUserById(from.id)
        val user = if (exist == null) {
            val newUser = User.createWithDefaultEloRating(
                from.id,
                update.message.chatId,
                User.nameFrom(from.firstName, from.lastName),
                User.aliasFrom(from.userName)
            )
            storage.addNewUser(newUser)
            newUser
        } else {
            exist
        }

        sender.executeAsync(SendMessage(update.message.chatId.toString(), i18n.START_MESSAGE(user.rating)))
        flowFinish()
    }
}
