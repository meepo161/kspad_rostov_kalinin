package ru.avem.stand.utils

import java.lang.Thread.sleep
import java.security.MessageDigest
import kotlin.experimental.and
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

fun hash(value: String, algorithm: String = "SHA-256"): String {
    val bytes = value.toByteArray()
    val md = MessageDigest.getInstance(algorithm)
    val digest = md.digest(bytes)
    return digest.fold("", { str, it -> str + "%02x".format(it) })
}

fun toHexStr(src: ByteArray): String {
    val builder = StringBuilder()
    for (element in src) {
        val s = Integer.toHexString((element and 0xFF.toByte()).toInt())
        if (s.length < 2) {
            builder.append(0)
        }
        builder.append(s).append(' ')
    }
    return builder.toString().toUpperCase().trim { it <= ' ' }
}

fun String.hexStrToAsciiStr(): String {
    val builder = StringBuilder()
    for (i in this.indices) {
        val s = this.substring(i, i + 1)

        if (s != " ")
            builder.append(s)
    }
    val output = StringBuilder("")
    if (builder.length > 1) {
        var i = 0
        while (i < builder.length) {
            val str = builder.substring(i, i + 2)
            output.append(Integer.parseInt(str.trim { it <= ' ' }, 16).toChar())
            i += 2
        }
    }
    return output.toString()
}

fun trimByteArray(bytes: ByteArray): ByteArray? {
    var i = bytes.size - 1
    while (i >= 0 && bytes[i].toInt() == 0) {
        --i
    }
    return bytes.copyOf(i + 1)
}

fun getAlignedString(input: String, columnWidth: Int, alignment: String = "LEFT"): String {
    var voidPlace = ""

    if (columnWidth > input.length) {
        for (i in 1..(columnWidth - input.length)) {
            voidPlace += " "
        }
    }

    return when (alignment) {
        "RIGHT" -> voidPlace + input
        else -> input + voidPlace
    }
}

@ExperimentalTime
fun toHHmmss(time: Long): String {
    return time.milliseconds.toComponents { hours, minutes, seconds, _ ->
        "${hours.padZero()}:${minutes.padZero()}:${seconds.padZero()}"
    }
}

private fun Int.padZero() = toString().padStart(2, '0')

fun smartSleep(
    mills: Long,
    breakCondition: () -> Boolean = { false },
    pauseCondition: () -> Boolean = { false }
) {
    if (mills > 0) {
        val initNanos = System.nanoTime()
        val stepMills = 10L
        val minusErrorNanos = 3_000_000L
        var iterations = mills / stepMills

        while (iterations >= 0L && !breakCondition()) {
            if (!pauseCondition()) {
                iterations--
            }

            sleep(stepMills - minusErrorNanos / 1_000_000)
        }

        val plusErrorNanos = (mills * 1_000_000 - (System.nanoTime() - initNanos)) - minusErrorNanos

        if (plusErrorNanos > 0L && !breakCondition()) {
            sleep(
                (plusErrorNanos / 1_000_000),
                (plusErrorNanos % 1_000_000).toInt()
            )
        }
    }
}
