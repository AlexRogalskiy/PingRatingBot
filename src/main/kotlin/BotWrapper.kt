import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update

class BotWrapper(
    private val config: BotConfigProvider,
    private val handler: UpdatesHandler
) : TelegramLongPollingBot() {
    override fun getBotToken(): String = config.token

    override fun getBotUsername(): String = config.username

    override fun onUpdateReceived(update: Update) {
        handler.handle(update)
    }
}
