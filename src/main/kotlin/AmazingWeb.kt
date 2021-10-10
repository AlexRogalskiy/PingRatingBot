import io.ktor.application.*
import io.ktor.application.Application
import io.ktor.response.*
import io.ktor.routing.*
import storage.Storage

class AmazingWeb(
    private val storage: Storage
) : KtorWeb {
    override fun getModule(): Application.() -> Unit = {
        routing {
            userRoute()
            groupRoute()
        }
    }

    private fun Route.home() {
        get("/") {
            call.respondText("About this bot")
        }
    }

    private fun Route.userRoute() {
        get("/u/{user}") {
            call.respondText("Hello, ${call.parameters["user"]}!")
        }
    }

    private fun Route.groupRoute() {
        get("/g/{group}") {
            call.respondText("Hello, world!")
        }
    }
}
