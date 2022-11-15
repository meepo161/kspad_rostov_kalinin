package ru.avem.stand.modules.r.communication.adapters.cs0202.utils

import ru.avem.stand.modules.r.communication.adapters.CRC16
import ru.avem.stand.utils.toShort
import java.nio.ByteBuffer

object CRC {
    fun sign(b: ByteBuffer) {
        b.putShort(calc(b))
    }

    private fun calc(b: ByteBuffer) = CRC16().apply { update(b.array(), 2, b.position() - 2) }.value.toShort()

    fun isValid(dst: ByteArray) = calc(dst) == (dst[dst.size - 2] to dst[dst.size - 1]).toShort()
    private fun calc(b: ByteArray) = CRC16().apply { update(b.copyOfRange(2, b.size - 2)) }.value.toShort()
}
