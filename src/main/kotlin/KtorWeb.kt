import io.ktor.application.Application

interface KtorWeb {
    fun getModule(): Application.() -> Unit
}
