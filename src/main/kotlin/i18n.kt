import data.RatingDataType

@Suppress("FunctionName")
object i18n {
    fun START_MESSAGE(currentRating: RatingDataType) =
        "Привет, я бот подсчета рейтинга для пинг-понга.\n" +
                "твой текущий рейтинг = $currentRating, узнать его и место в топе можно отправив /info.\n" +
                "После игры один из игроков отправляет мне комманду /new_game\n" +
                "Второму игроку придет сообщение с необходимостью подтвердить результат."

    const val UNKNOWN_COMMAND = "Неизвестная комманда, попробуй другую.\nИли отправь /start чтобы узнать как пользоваться."

    // New game flow
    const val NEW_GAME_SCORE_REQUEST =
        "Пришли мне результат, сколько партий выйграл(а) ты и сколько оппонент. Два числа через пробел.\n Например: 2 3"
    const val NEW_GAME_OPPONENT_REQUEST =
        "Пришли мне алиас второго игрока, например @gordinmitya. Или поделись контактом."
    const val SECOND_PLAYER_NOT_REGISTERED =
        "Второй игрок не найден.\nЕсли он(а) еще не пользовался этим ботом - пусть напишет мне любое сообщение. А потом пришли мне его контакт ещё раз."
    const val CONFIRM_MESSAGE_SENT =
        "Второму игроку отправлено сообщение для подтверждения игры. После подтверждения я пришлю новые рейтинги."

    // New game conformation
    fun CONFIRM_GAME_REQUEST(firstPlayer: String, score: Pair<Int, Int>): String {
        return "Игрок $firstPlayer утвержает что вы сыграли со счетом (он/она) ${score.first} ${score.second} (ты).\nВсё верно?"
    }
    const val YES = "Да"
    const val NO = "Нет"
    const val OK = "OK"
    const val FALSE_REQUEST = "Забудем про эту игру."

    private fun RATING_CHANGED(newRating: RatingDataType, diff: RatingDataType): String {
        val plusSign = if (diff >= 0) "+" else ""
        return "Теперь твой рейтинг $newRating ($plusSign$diff)."
    }

    fun GAME_CONFIRM_FOR_AUTHOR(responder: String, newRating: RatingDataType, diff: RatingDataType) =
        "$responder подтвердил результаты игры.\n${i18n.RATING_CHANGED(newRating, diff)}"

    fun GAME_CONFIRM_FOR_RESPONDER(author: String, newRating: RatingDataType, diff: RatingDataType) =
        "Игра с $author подтверждена.\n${i18n.RATING_CHANGED(newRating, diff)}"

    fun GAME_REJECTED(responder: String, score: Pair<Int, Int>) =
        "$responder отклонил запись об игре со счетом ${score.first} ${score.second}."
}
