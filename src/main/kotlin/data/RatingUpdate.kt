package data

class RatingUpdate(
    val userId: Long,
    val diff: Int,
    val newRating: Int,
)
