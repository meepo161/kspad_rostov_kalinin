package ru.avem.stand.modules.r.communication.model.devices.hpmont

import ru.avem.kserialpooler.communication.adapters.modbusrtu.ModbusRTUAdapter
import ru.avem.kserialpooler.communication.adapters.utils.ModbusRegister
import ru.avem.kserialpooler.communication.utils.TransportException
import ru.avem.kserialpooler.communication.utils.TypeByteOrder
import ru.avem.kserialpooler.communication.utils.allocateOrderedByteBuffer
import ru.avem.stand.modules.r.communication.model.DeviceRegister
import ru.avem.stand.modules.r.communication.model.devices.hpmont.HPMontModel.Companion.ASYNC_FREQ
import ru.avem.stand.modules.r.communication.model.devices.hpmont.HPMontModel.Companion.ASYNC_VOLTAGE
import ru.avem.stand.modules.r.communication.model.devices.hpmont.HPMontModel.Companion.CONTROL_REGISTER
import ru.avem.stand.modules.r.communication.model.devices.hpmont.HPMontModel.Companion.INITIAL_DIRECTION
import ru.avem.stand.modules.r.communication.model.devices.hpmont.HPMontModel.Companion.RUNNING_FREQUENCY
import ru.avem.stand.modules.r.communication.model.IDeviceController
import ru.avem.stand.modules.r.communication.model.devices.hpmont.HPMontModel.Companion.MAX_OPERATIONG_FREQ
import ru.avem.stand.modules.r.communication.model.devices.hpmont.HPMontModel.Companion.MAX_OUT_FREQ
import ru.avem.stand.modules.r.communication.model.devices.hpmont.HPMontModel.Companion.POINT_1_FREQUENCY_REGISTER
import ru.avem.stand.modules.r.communication.model.devices.hpmont.HPMontModel.Companion.POINT_1_VOLTAGE_REGISTER
import ru.avem.stand.modules.r.communication.model.devices.hpmont.HPMontModel.Companion.POINT_2_FREQUENCY_REGISTER
import ru.avem.stand.modules.r.communication.model.devices.hpmont.HPMontModel.Companion.POINT_2_VOLTAGE_REGISTER
import ru.avem.stand.modules.r.communication.model.devices.hpmont.HPMontModel.Companion.POINT_3_FREQUENCY_REGISTER
import ru.avem.stand.modules.r.communication.model.devices.hpmont.HPMontModel.Companion.POINT_3_VOLTAGE_REGISTER
import ru.avem.stand.modules.r.communication.model.devices.hpmont.HPMontModel.Companion.TEMP
import java.nio.ByteBuffer
import java.nio.ByteOrder

class HPMont(
    override val name: String,
    override val protocolAdapter: ModbusRTUAdapter,
    override val id: Byte
) : IDeviceController {
    private val model = HPMontModel()
    override var isResponding = false
    override var requestTotalCount = 0
    override var requestSuccessCount = 0
    override val pollingRegisters = mutableListOf<DeviceRegister>()
    override val writingRegisters = mutableListOf<Pair<DeviceRegister, Number>>()

    override fun readRegister(register: DeviceRegister) {
        isResponding = try {
            transactionWithAttempts {
                when (register.valueType) {
                    DeviceRegister.RegisterValueType.SHORT -> {
                        val value =
                            protocolAdapter.readHoldingRegisters(id, register.address, 1).first().toShort()
                        register.value = value
                    }

                    DeviceRegister.RegisterValueType.FLOAT -> {
                        val modbusRegister =
                            protocolAdapter.readHoldingRegisters(id, register.address, 2).map(ModbusRegister::toShort)
                        register.value =
                            allocateOrderedByteBuffer(modbusRegister, TypeByteOrder.BIG_ENDIAN, 4).float
                    }

                    DeviceRegister.RegisterValueType.INT32 -> {
                        val modbusRegister =
                            protocolAdapter.readHoldingRegisters(id, register.address, 2).map(ModbusRegister::toShort)
                        register.value =
                            allocateOrderedByteBuffer(modbusRegister, TypeByteOrder.BIG_ENDIAN, 4).int
                    }
                }
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

    override fun <T : Number> writeRegister(register: DeviceRegister, value: T) {
        when (value) {
            is Float -> {
                val bb = ByteBuffer.allocate(4).putFloat(value).order(ByteOrder.BIG_ENDIAN)
                val registers = listOf(ModbusRegister(bb.getShort(2)), ModbusRegister(bb.getShort(0)))
                isResponding = try {
                    transactionWithAttempts {
                        protocolAdapter.presetMultipleRegisters(id, register.address, registers)
                    }
                    true
                } catch (e: TransportException) {
                    false
                }
            }

            is Int -> {
                isResponding = try {
                    transactionWithAttempts {
                        protocolAdapter.presetSingleRegister(id, register.address, ModbusRegister(value.toShort()))
                    }
                    true
                } catch (e: TransportException) {
                    false
                }
            }

            is Short -> {
                isResponding = try {
                    transactionWithAttempts {
                        protocolAdapter.presetSingleRegister(id, register.address, ModbusRegister(value))
                    }
                    true
                } catch (e: TransportException) {
                    false
                }
            }

            else -> {
                throw UnsupportedOperationException("Method can handle only with Float, Int and Short")
            }
        }
    }

    override fun writeRegisters(register: DeviceRegister, values: List<Short>) {
        val registers = values.map { ModbusRegister(it) }
        transactionWithAttempts {
            protocolAdapter.presetMultipleRegisters(id, register.address, registers)
        }
    }

    override fun writeRequest(request: String) {
        TODO("Not yet implemented")
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
        writeRegister(getRegisterById(CONTROL_REGISTER), 0x1001.toShort())
    }

    fun stopObject() {
        writeRegister(getRegisterById(CONTROL_REGISTER), 0x1010.toShort())
    }

    fun setRunningFrequency(frequency: Double) {
        writeRegister(getRegisterById(RUNNING_FREQUENCY), (frequency.hz()))
    }

    fun setObjectParams(
        vReg2: Double = 1.0,
        vReg3: Double = 1.0,
        direction: Int = 0
    ) {
//        writeRegister(getRegisterById(INITIAL_DIRECTION), direction.toShort())
        writeRegister(getRegisterById(TEMP), 4.toShort())
        writeRegister(getRegisterById(MAX_OUT_FREQ), 5555.toShort())
        writeRegister(getRegisterById(MAX_OPERATIONG_FREQ), 5555.toShort())
//        writeRegister(getRegisterById(ASYNC_VOLTAGE), 380.v())
//        writeRegister(getRegisterById(ASYNC_FREQ), 555.toShort())
        writeRegister(getRegisterById(POINT_3_FREQUENCY_REGISTER), 100.percent())
        writeRegister(getRegisterById(POINT_3_VOLTAGE_REGISTER), vReg3.v())
        writeRegister(getRegisterById(POINT_2_FREQUENCY_REGISTER), 50.percent())
        writeRegister(getRegisterById(POINT_2_VOLTAGE_REGISTER), vReg2.v())
        writeRegister(getRegisterById(POINT_1_FREQUENCY_REGISTER), 0.percent())
        writeRegister(getRegisterById(POINT_1_VOLTAGE_REGISTER), 0.v())
    }

    fun setObjectU(voltage: Double) {
        writeRegister(getRegisterById(POINT_3_VOLTAGE_REGISTER), voltage.v())
        writeRegister(getRegisterById(POINT_2_VOLTAGE_REGISTER), voltage.v())
    }

    private fun Number.hz(): Short = (this.toDouble() * 100).toInt().toShort()
    private fun Number.percent(): Short = (this.toDouble() * 10).toInt().toShort()
    private fun Number.v(): Short = (this.toDouble()).toInt().toShort()
}

