import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.dsl.bind
import org.koin.dsl.module
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.generics.LongPollingBot
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

import org.koin.dsl.binds
import org.telegram.telegrambots.meta.bots.AbsSender
import storage.InMemoryStateHolder
import storage.JsonFileStorage
import storage.StateHolder
import storage.Storage

object Application : KoinComponent {

    @Suppress("USELESS_CAST")
    private val amazingModule = module {
        single { EnvConfigProvider() } binds arrayOf(
            BotConfigProvider::class,
            WebConfigProvider::class,
            DbConfigProvider::class
        )
        single { InMemoryStateHolder() as StateHolder }
        single { AmazingBot() as UpdatesHandler }
        single { BotWrapper(get(), get()) } binds arrayOf(LongPollingBot::class, AbsSender::class)
        single { JsonFileStorage.load(get()) as Storage }
        single { AmazingWeb(get()) as KtorWeb }
    }

    fun start() {
        val koin = startKoin {
            printLogger()
            modules(amazingModule)
        }.koin

        TelegramBotsApi(DefaultBotSession::class.java)
            .registerBot(koin.get())

        val webConfig = koin.get<WebConfigProvider>()
        val web = koin.get<KtorWeb>()
        embeddedServer(
            Netty,
            host = webConfig.host,
            port = webConfig.port,
            module = web.getModule()
        ).start()
    }
}
