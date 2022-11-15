package ru.avem.stand.modules.r.communication.model.devices.mitsubishi.fra8xx

import ru.avem.kserialpooler.communication.adapters.modbusrtu.ModbusRTUAdapter
import ru.avem.kserialpooler.communication.adapters.utils.ModbusRegister
import ru.avem.kserialpooler.communication.utils.TransportException
import ru.avem.kserialpooler.communication.utils.TypeByteOrder
import ru.avem.kserialpooler.communication.utils.allocateOrderedByteBuffer
import ru.avem.stand.modules.r.communication.model.DeviceController
import ru.avem.stand.modules.r.communication.model.DeviceRegister
import ru.avem.stand.modules.r.communication.model.devices.mitsubishi.fra8xx.FRA8XXModel.Companion.CONTROL_REGISTER
import ru.avem.stand.modules.r.communication.model.devices.mitsubishi.fra8xx.FRA8XXModel.Companion.CURRENT_FREQUENCY_REGISTER
import ru.avem.stand.modules.r.communication.model.devices.mitsubishi.fra8xx.FRA8XXModel.Companion.MAX_FREQUENCY_REGISTER
import ru.avem.stand.modules.r.communication.model.devices.mitsubishi.fra8xx.FRA8XXModel.Companion.MAX_VOLTAGE_REGISTER
import java.nio.ByteBuffer
import java.nio.ByteOrder

class FRA8XX(
    override val name: String,
    override val protocolAdapter: ModbusRTUAdapter,
    override val id: Byte
) : DeviceController() {
    val model = FRA8XXModel()
    override var requestTotalCount = 0
    override var requestSuccessCount = 0
    override val pollingRegisters = mutableListOf<DeviceRegister>()

    override val writingRegisters = mutableListOf<Pair<DeviceRegister, Number>>()

    override fun readRegister(register: DeviceRegister) {
        isResponding = try {
            transactionWithAttempts {
                val modbusRegister =
                    protocolAdapter.readHoldingRegisters(id, register.address, 2).map(ModbusRegister::toShort)
                register.value = allocateOrderedByteBuffer(modbusRegister, TypeByteOrder.BIG_ENDIAN, 4).float.toDouble()
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
                    val bb = ByteBuffer.allocate(4).putFloat(value).order(ByteOrder.BIG_ENDIAN)
                    val registers = listOf(ModbusRegister(bb.getShort(2)), ModbusRegister(bb.getShort(0)))
                    transactionWithAttempts {
                        protocolAdapter.presetMultipleRegisters(id, register.address, registers)
                    }
                }
                is Int -> {
                    val bb = ByteBuffer.allocate(4).putInt(value).order(ByteOrder.BIG_ENDIAN)
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
                    throw UnsupportedOperationException("Method can handle only with Float, Int and Short")
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
        try {
            model.registers.values.firstOrNull()?.let {
                readRegister(it)
            }
        } catch (ignored: TransportException) {
        }
    }

    override fun getRegisterById(idRegister: String) = model.getRegisterById(idRegister)


    fun startObject() {
        writeRegister(getRegisterById(CONTROL_REGISTER), (0b10).toShort())
    }

    fun stopObject() {
        writeRegister(getRegisterById(CONTROL_REGISTER), (0).toShort())
    }

    fun startReverseTestItem() {
        writeRegister(getRegisterById(CONTROL_REGISTER), (0b100).toShort())
    }

    fun setObjectParams(fCur: Int, voltageMax: Int, fMax: Int) {
        writeRegister(getRegisterById(MAX_VOLTAGE_REGISTER), voltageMax.v())
        writeRegister(getRegisterById(MAX_FREQUENCY_REGISTER), fMax.hz())
        writeRegister(getRegisterById(CURRENT_FREQUENCY_REGISTER), fCur.hz())
    }

    fun setTestItemUMax(voltageMax: Int) {
        writeRegister(getRegisterById(MAX_VOLTAGE_REGISTER), voltageMax.v())
    }

    private fun Number.hz(): Short = (this.toDouble() * 100).toInt().toShort()
    private fun Number.v(): Short = (this.toDouble() * 10).toInt().toShort()
}
