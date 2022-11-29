package ru.avem.stand.modules.r.tests

enum class AmperageStage(val ratio: Double) {
    FROM_500_TO_5(500.0 / 5.0),
    FROM_100_TO_5(100.0 / 5.0),
    FROM_30_TO_5(30.0 / 5.0),
    FROM_5_TO_5(5.0 / 5.0),
}

fun calcSyncRPM(F: Int, N: Int): Double {
    for (p in 2..7) {
        val sync = F * 60 / p
        if (N > sync) {
            return (F * 60 / (p - 1)).toDouble()
        }
    }
    return 0.0
}

fun calcZs(isFirstPlatform: Boolean, syncN: Int): Pair<Double, Double> {
    return when (syncN) {
        750 ->  500.0 to 250.0
        1000 -> 500.0 to 355.0
        1500 -> 355.0 to 355.0
        3000 -> 250.0 to 500.0
        else -> throw Exception("Не удалось расчитать параметры шиквов для ${if (isFirstPlatform) "первой" else "второй"} платформы с синхронными оборотами $syncN об/мин")
    }
}
