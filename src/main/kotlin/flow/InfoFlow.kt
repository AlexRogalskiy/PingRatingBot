package flow

import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.bots.AbsSender
import storage.State

class InfoFlow(state: State): Flow(state) {
    override fun next(update: Update, sender: AbsSender) {
        TODO("Not yet implemented")
    }
}
