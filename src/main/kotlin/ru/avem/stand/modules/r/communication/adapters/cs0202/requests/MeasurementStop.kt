package ru.avem.stand.modules.r.communication.adapters.cs0202.requests

import ru.avem.kserialpooler.utils.LogicException
import ru.avem.stand.modules.r.communication.adapters.cs0202.requests.Cs0202Request.Companion.BYTE_SIZE_OF_CRC
import ru.avem.stand.modules.r.communication.adapters.cs0202.requests.Cs0202Request.Companion.BYTE_SIZE_OF_DATA_RESPONSE
import ru.avem.stand.modules.r.communication.adapters.cs0202.requests.Cs0202Request.Companion.BYTE_SIZE_OF_DEVICE_ID
import ru.avem.stand.modules.r.communication.adapters.cs0202.requests.Cs0202Request.Companion.BYTE_SIZE_OF_FUNCTION
import ru.avem.stand.modules.r.communication.adapters.cs0202.requests.Cs0202Request.Companion.REQUEST_SIZE
import ru.avem.stand.modules.r.communication.adapters.cs0202.utils.CRC
import java.nio.ByteBuffer
import kotlin.experimental.or

class MeasurementStop(override val deviceId: Byte, dataRequestRequire: Byte) : Cs0202Request {
    companion object {
        const val FUNCTION_CODE: Byte = 0x03

        const val STOP_MEASUREMENT_RESISTANCE_DATA_REQUEST: Byte = 0x31
        const val STOP_MEASUREMENT_VOLTAGE_DATA_REQUEST: Byte = 0x32
    }

    override val function: Byte = FUNCTION_CODE
    override val dataRequest: Byte = checkDataRequest(dataRequestRequire)

    private fun checkDataRequest(dataRequestRequire: Byte) =
        when (dataRequestRequire) {
            STOP_MEASUREMENT_RESISTANCE_DATA_REQUEST -> STOP_MEASUREMENT_RESISTANCE_DATA_REQUEST
            STOP_MEASUREMENT_VOLTAGE_DATA_REQUEST -> STOP_MEASUREMENT_VOLTAGE_DATA_REQUEST
            else -> throw LogicException("Ошибка прибора: Неизвестный dataRequestRequire [${dataRequestRequire}]")
        }

    override fun getResponseSize() =
        BYTE_SIZE_OF_DEVICE_ID + BYTE_SIZE_OF_FUNCTION + BYTE_SIZE_OF_DATA_RESPONSE + BYTE_SIZE_OF_CRC

    override fun getRequestBytes(): ByteArray = ByteBuffer.allocate(REQUEST_SIZE).apply {
        put(deviceId)
        put(function)
        put(dataRequest)

    }.also {
        CRC.sign(it)
    }.array()

    fun parseResponse(response: ByteArray) {
        checkResponse(response)
    }

    override fun checkFunctionIsError(response: ByteArray) {
        if (function == (response[Cs0202Request.FUNCTION_POSITION] or 0x80.toByte())) {
            throw LogicException("Ошибка прибора: Неизвестная ошибка [${response[Cs0202Request.ERROR_POSITION]}]")
        }
    }
}
