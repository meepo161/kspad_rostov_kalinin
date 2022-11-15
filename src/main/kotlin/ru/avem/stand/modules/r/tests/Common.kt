package ru.avem.stand.modules.r.tests

enum class AmperageStage(val ratio: Double) {
    FROM_150_TO_5(150.0 / 5.0),
    FROM_30_TO_5(30.0 / 5.0),
    FROM_5_TO_5(5.0 / 5.0)
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
    return if (isFirstPlatform) {
        when (syncN) {
            500 -> 630.0 to 212.0
            600 -> 630.0 to 250.0
            750 -> 630.0 to 315.0
            1000 -> 630.0 to 425.0
            1500 -> 425.0 to 425.0
            3000 -> 315.0 to 630.0
            else -> throw Exception("Не удалось расчитать параметры шиквов для ${if (isFirstPlatform) "первой" else "второй"} платформы с синхронными оборотами $syncN об/мин")
        }
    } else {
        when (syncN) {
            500 -> 355.0 to 118.0
            600 -> 355.0 to 140.0
            750 -> 355.0 to 180.0
            1000 -> 355.0 to 236.0
            1500 -> 236.0 to 236.0
            3000 -> 180.0 to 355.0
            else -> throw Exception("Не удалось расчитать параметры шиквов для ${if (isFirstPlatform) "первой" else "второй"} платформы с синхронными оборотами $syncN об/мин")
        }
    }
}
