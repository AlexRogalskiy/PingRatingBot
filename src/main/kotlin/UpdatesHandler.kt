import org.telegram.telegrambots.meta.api.objects.Update

interface UpdatesHandler {
    fun handle(update: Update)
}
