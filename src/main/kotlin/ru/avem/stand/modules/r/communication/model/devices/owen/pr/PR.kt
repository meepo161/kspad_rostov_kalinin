package ru.avem.stand.modules.r.communication.model.devices.owen.pr

import ru.avem.kserialpooler.communication.adapters.modbusrtu.ModbusRTUAdapter
import ru.avem.kserialpooler.communication.adapters.utils.ModbusRegister
import ru.avem.kserialpooler.communication.utils.TransportException
import ru.avem.stand.modules.r.communication.model.DeviceController
import ru.avem.stand.modules.r.communication.model.DeviceRegister
import java.lang.Thread.sleep
import java.nio.ByteBuffer
import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.math.pow

class PR(
    override val name: String,
    override val protocolAdapter: ModbusRTUAdapter,
    override val id: Byte
) : DeviceController() {
    val model = PRModel()
    override var requestTotalCount = 0
    override var requestSuccessCount = 0
    override val pollingRegisters = mutableListOf<DeviceRegister>()

    override val writingRegisters = mutableListOf<Pair<DeviceRegister, Number>>()

    private var outMask01To16: Short = 0
    private var outMask17To32: Short = 0

    fun init() {
        writeRegister(getRegisterById(PRModel.WD_TIMEOUT), 8000.toShort())

        resetTriggers()

        writeRegister(getRegisterById(PRModel.DO_01_16_ERROR_S1_MASK_0), 0xFFFF.toShort())
        writeRegister(getRegisterById(PRModel.DO_01_16_ERROR_S1_MASK_1), 0x0000.toShort())
        writeRegister(getRegisterById(PRModel.DO_17_32_ERROR_S1_MASK_0), 0xFFFF.toShort())
        writeRegister(getRegisterById(PRModel.DO_17_32_ERROR_S1_MASK_1), 0x0000.toShort())

        writeRegister(getRegisterById(PRModel.DI_01_16_ERROR_MASK_1), 0x0C01.toShort())
        writeRegister(getRegisterById(PRModel.DI_01_16_ERROR_MASK_0), 0x0098.toShort())
        writeRegister(getRegisterById(PRModel.DI_17_32_ERROR_MASK_1), 0x0000.toShort())
        writeRegister(getRegisterById(PRModel.DI_17_32_ERROR_MASK_0), 0x0000.toShort())
        writeRegister(getRegisterById(PRModel.DI_33_48_ERROR_MASK_1), 0x0000.toShort())
        writeRegister(getRegisterById(PRModel.DI_33_48_ERROR_MASK_0), 0x0000.toShort())

        writeRegister(getRegisterById(PRModel.CMD), 3.toShort()) // RESET ERROR + WD_CYCLE
    }

    fun resetTriggers() {
        writeRegister(getRegisterById(PRModel.DI_01_16_RST), 0xFFFF.toShort())
        writeRegister(getRegisterById(PRModel.DI_01_16_RST), 0x0000.toShort())
        writeRegister(getRegisterById(PRModel.DI_17_32_RST), 0xFFFF.toShort())
        writeRegister(getRegisterById(PRModel.DI_17_32_RST), 0x0000.toShort())
        writeRegister(getRegisterById(PRModel.DI_33_48_RST), 0xFFFF.toShort())
        writeRegister(getRegisterById(PRModel.DI_33_48_RST), 0x0000.toShort())
    }

    fun initWithoutProtections() {
        writeRegister(getRegisterById(PRModel.WD_TIMEOUT), 8000.toShort())

        resetTriggers()

        writeRegister(getRegisterById(PRModel.DO_01_16_ERROR_S1_MASK_0), 0xFFFF.toShort())
        writeRegister(getRegisterById(PRModel.DO_01_16_ERROR_S1_MASK_1), 0x0000.toShort())
        writeRegister(getRegisterById(PRModel.DO_17_32_ERROR_S1_MASK_0), 0xFFFF.toShort())
        writeRegister(getRegisterById(PRModel.DO_17_32_ERROR_S1_MASK_1), 0x0000.toShort())

        writeRegister(getRegisterById(PRModel.DI_01_16_ERROR_MASK_1), 0x0000.toShort())
        writeRegister(getRegisterById(PRModel.DI_01_16_ERROR_MASK_0), 0x0000.toShort())
        writeRegister(getRegisterById(PRModel.DI_17_32_ERROR_MASK_1), 0x0000.toShort())
        writeRegister(getRegisterById(PRModel.DI_17_32_ERROR_MASK_0), 0x0000.toShort())
        writeRegister(getRegisterById(PRModel.DI_33_48_ERROR_MASK_1), 0x0000.toShort())
        writeRegister(getRegisterById(PRModel.DI_33_48_ERROR_MASK_0), 0x0000.toShort())

        writeRegister(getRegisterById(PRModel.CMD), 3.toShort()) // RESET ERROR + WD_CYCLE
    }

    override fun readRegister(register: DeviceRegister) {
        isResponding = try {
            transactionWithAttempts {
                val modbusRegister =
                    protocolAdapter.readHoldingRegisters(id, register.address, 1).map(ModbusRegister::toShort)
                register.value = modbusRegister.first()
            }
            true
        } catch (e: TransportException) {
            false
        }
    }

    override fun readAllRegisters() {
        model.registers.values.forEach {
            readRegister(it)
        }
    }

    @Synchronized
    override fun <T : Number> writeRegister(register: DeviceRegister, value: T) {
        isResponding = try {
            when (value) {
                is Float -> {
                    val bb = ByteBuffer.allocate(4).putFloat(value)
                    val registers = listOf(ModbusRegister(bb.getShort(2)), ModbusRegister(bb.getShort(0)))
                    transactionWithAttempts {
                        protocolAdapter.presetMultipleRegisters(id, register.address, registers)
                    }
                }
                is Short -> {
                    transactionWithAttempts {
                        protocolAdapter.presetMultipleRegisters(id, register.address, listOf(ModbusRegister(value)))
                    }
                }
                else -> {
                    throw UnsupportedOperationException("Method can handle only with Float and Short")
                }
            }
            true
        } catch (e: TransportException) {
            false
        }
    }

    override fun writeRegisters(register: DeviceRegister, values: List<Short>) {
        val registers = values.map { ModbusRegister(it) }
        isResponding = try {
            transactionWithAttempts {
                protocolAdapter.presetMultipleRegisters(id, register.address, registers)
            }
            true
        } catch (e: TransportException) {
            false
        }
    }

    override fun checkResponsibility() {
        model.registers.values.firstOrNull()?.let {
            readRegister(it)
        }
    }

    override fun getRegisterById(idRegister: String) = model.getRegisterById(idRegister)

    fun onStart() {
        onOutput01To16(1)
    }

    fun fromFI() {
        onOutput01To16(2)
    }

    fun onMaxAmperageStage() {
        onOutput01To16(3)
    }

    fun offMaxAmperageStage() {
        offOutput01To16(3)
    }

    fun onMinAmperageStage() {
        onOutput01To16(4)
    }

    fun offMinAmperageStage() {
        offOutput01To16(4)
    }

    fun onIKASP1() {
        onOutput01To16(5)
    }

    fun onTestItemP1() {
        onOutput01To16(6)
    }

    fun offTestItemP1() {
        offOutput01To16(6)
    }

    fun connectTI1() {
        onOutput01To16(7)
    }

    fun signalize() {
        onOutput01To16(8)
        sleep(3000)
        offOutput01To16(8)
        onOutput01To16(7)
    }

    fun onTVHV() {
        onOutput01To16(9)
    }

    fun offTVHV() {
        offOutput01To16(9)
    }

    fun on30To5AmperageStage() {
        onOutput01To16(10)
    }

    fun off30To5AmperageStage() {
        offOutput01To16(10)
    }

    fun onLoadMachineP1() {
        onOutput01To16(11)
    }

    fun offLoadMachineP1() {
        offOutput01To16(11)
    }

    fun onVoltageBoost() {
        onOutput01To16(12)
    }

    fun onAVR() {
        onOutput01To16(13)
    }

    fun onTestItemP2() {
        onOutput01To16(14)
    }

    fun offTestItemP2() {
        offOutput01To16(14)
    }

    fun onMVZ() {
        onOutput01To16(15)
    }

    fun onLoadMachineP2() {
        onOutput01To16(16)
    }

    fun offLoadMachineP2() {
        offOutput01To16(16)
    }

    fun onIKASP2() {
        onOutput17To32(1)
    }

    fun onMPT() {
        onOutput17To32(2)
    }

    fun onMeasuringAVEM() {
        onOutput17To32(4)
    }

    fun onShunting() {
        onOutput17To32(5)
    }

    fun offShunting() {
        offOutput17To32(5)
    }

    fun onHV() {
        onOutput17To32(6)
    }

    fun offHV() {
        offOutput17To32(6)
    }

    fun onMGR() {
        onOutput17To32(7)
    }

    fun offMGR() {
        offOutput17To32(7)
    }

    fun onPE() {
        onOutput17To32(8)
    }

    fun offPE() {
        offOutput17To32(8)
    }

    fun offOtherAmperageStages() {
        off30To5AmperageStage()
        offMinAmperageStage()
    }

    fun offAllKMs() {
        outMask01To16 = 0
        outMask17To32 = 0
        writeRegister(getRegisterById(PRModel.DO_01_16), outMask01To16)
        writeRegister(getRegisterById(PRModel.DO_17_32), outMask17To32)
    }

    private fun onOutput01To16(position: Short) {
        val bitPosition = position - 1
        outMask01To16 = outMask01To16 or 2.0.pow(bitPosition).toInt().toShort()
        writeRegister(getRegisterById(PRModel.DO_01_16), outMask01To16)
    }

    private fun offOutput01To16(position: Short) {
        val bitPosition = position - 1
        outMask01To16 = outMask01To16 and 2.0.pow(bitPosition).toInt().inv().toShort()
        writeRegister(getRegisterById(PRModel.DO_01_16), outMask01To16)
    }

    private fun onOutput17To32(position: Short) {
        val bitPosition = position - 1
        outMask17To32 = outMask17To32 or 2.0.pow(bitPosition).toInt().toShort()
        writeRegister(getRegisterById(PRModel.DO_17_32), outMask17To32)
    }

    private fun offOutput17To32(position: Short) {
        val bitPosition = position - 1
        outMask17To32 = outMask17To32 and 2.0.pow(bitPosition).toInt().inv().toShort()
        writeRegister(getRegisterById(PRModel.DO_17_32), outMask17To32)
    }
}
