import utils.DotEnv

class EnvConfigProvider : BotConfigProvider, WebConfigProvider, DbConfigProvider {
    private val env = System.getenv()
    private val file = DotEnv()

    override val token: String = getVariable("BOT_TOKEN")
    override val username: String = getVariable("BOT_NAME", "PingRatingBot")
    override val host: String = getVariable("HOST", "localhost")
    override val port: Int = getVariable("PORT", "8080").toInt()
    override val dbFilePath: String = getVariable("JSON_DB_FILE", "/data/pingpong.db.json")

    private fun getVariable(key: String, default: String? = null): String {
        env[key]?.let { return it }
        file[key]?.let { return it }
        default?.let { return it }
        throw IllegalArgumentException("Key $key is not present in env variables nor .env file")
    }
}
