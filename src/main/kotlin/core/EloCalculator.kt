package core

// https://worldoftanks.ru/ru/content/strongholds_guide/elo_rating/

object EloCalculator {
    const val INITIAL_RATING = 1000

    fun ratingDiff(
        rating: Pair<Int, Int>,
        score: Pair<Int, Int>
    ): Pair<Int, Int> {
        val pa = individualDiff(rating.first, rating.second, score.first - score.second)
        val pb = individualDiff(rating.second, rating.first, score.second - score.first)
        return pa to pb
    }

    private fun individualDiff(ratingA: Int, ratingB: Int, scoreDiff: Int): Int {
        val e = 1.0 / (1 + Math.pow(10.0, (ratingB - ratingA) / 400.0))
        val s = when {
            scoreDiff > 0 -> 1.0
            scoreDiff == 0 -> 0.5
            scoreDiff < 0 -> 0.0
            else -> throw IllegalStateException()
        }
        val k = when {
            ratingA > 3000 -> 5
            ratingA >= 2401 -> 10
            ratingA >= 601 -> 15
            ratingA >= 0 -> 25
            else -> throw IllegalStateException()
        }.toDouble()
        val p = Math.round(k * (s - e)).toInt()
        // so rating will become 0 but not less than that
        if (ratingA + p < 0) {
            return -ratingA
        }
        return p
    }
}
