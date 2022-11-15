package ru.avem.stand.modules.r.communication.model.devices.instek.gpt

import ru.avem.kserialpooler.communication.utils.TransportException
import ru.avem.stand.modules.r.communication.adapters.stringascii.StringASCIIAdapter
import ru.avem.stand.modules.r.communication.model.DeviceController
import ru.avem.stand.modules.r.communication.model.DeviceRegister
import java.util.*

class GPT(
    override val name: String,
    override val protocolAdapter: StringASCIIAdapter,
    override val id: Byte
) : DeviceController() {
    val model = GPTModel()
    override var requestTotalCount = 0
    override var requestSuccessCount = 0
    override val pollingRegisters = mutableListOf<DeviceRegister>()

    override val writingRegisters = mutableListOf<Pair<DeviceRegister, Number>>()

    private lateinit var mode: Mode

    enum class Mode {
        ACW,
        DCW,
        IR
    }

    override fun readRequest(request: String): String {
        return protocolAdapter.read(request)
    }

    override fun readAllRegisters() {
        model.registers.values.forEach {
            readRegister(it)
        }
    }

    override fun writeRequest(request: String) {
        protocolAdapter.write(request)
    }

    override fun checkResponsibility() {
        try {
            model.registers.values.firstOrNull()?.let {
                readRegister(it)
            }
        } catch (ignored: TransportException) {
        }
    }

    override fun getRegisterById(idRegister: String) = model.getRegisterById(idRegister)

    fun remoteControl() {
        writeRequest("*idn?")
    }

    fun setMode(mode: Mode) {
        this.mode = mode
        writeRequest("MANU:EDIT:MODE $mode")
    }

    fun setVoltage(voltage: Double) {
        writeRequest("MANU:$mode:VOLTage ${"%.3f".format(Locale.US, voltage)}")
    }

    fun setMaxAmperage(amperage: Double) {
        writeRequest("MANU:$mode:CHIS ${"%.3f".format(Locale.US, amperage)}")
    }

    fun setRiseTime(timeRise: Double) {
        writeRequest("MANU:RTIM $timeRise")
    }

    fun setFreq(frequency: Double) {
        writeRequest("MANU:$mode:FREQ $frequency")
    }

    fun setTestTime(time: Double) {
        writeRequest("MANU:$mode:TTIMe $time")
    }

    fun onTest() {
        writeRequest("FUNC:TEST ON")
    }

    fun offTest() {
        writeRequest("FUNC:TEST OFF")
    }

    fun setNameTest(nameTest: String) {
        writeRequest("MANU:NAME $nameTest")
    }

    fun getMeas(): Array<String> {
        val readResponse = readRequest("MEAS?")
        try {
            val values = readResponse.split(',')
            val voltage = values[2].replace("kV", "").replace(",", ".")
            voltage.toDouble()
            val value = values[3].replace(" mA", "").replace(" ohm", "").replace("M", "000000").replace(",", ".")
            value.toDouble()
            val result = with(values[1]) {
                when {
                    contains("PASS") -> "0"
                    contains("FAIL") -> "1"
                    contains("VIEW") -> "2"
                    contains("TEST") -> "3"
                    contains("ERROR") -> "4"
                    else -> throw Exception()
                }
            }

            println("READ_RESPONSE(Valid) = $readResponse")
            return arrayOf(voltage, value, result)
        } catch (e: Exception) {
            println("READ_RESPONSE(Invalid) = $readResponse")
            throw TransportException("Невалидная схема ответа")
        }
    }

    override fun readRegister(register: DeviceRegister) {
        isResponding = try {
            transactionWithAttempts {
                register.value = getMeas()[register.address.toInt()].toDouble()
            }
            true
        } catch (e: TransportException) {
            false
        }
    }
}
