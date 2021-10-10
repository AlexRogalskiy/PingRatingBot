package flow

import org.koin.core.component.KoinComponent
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.bots.AbsSender
import storage.State

abstract class Flow(private val state: State): KoinComponent {
    abstract fun next(update: Update, sender: AbsSender)
    fun flowFinish() {
        state[CURRENT_FLOW] = null
    }

    companion object {
        const val CURRENT_FLOW = "current_flow"
        fun currentFlow(state: State): Flow? {
            return state[CURRENT_FLOW] as? Flow
        }
    }
}
