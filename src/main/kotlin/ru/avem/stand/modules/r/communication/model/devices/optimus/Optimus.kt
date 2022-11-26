package ru.avem.stand.modules.r.communication.model.devices.optimus

import ru.avem.kserialpooler.communication.adapters.modbusrtu.ModbusRTUAdapter
import ru.avem.kserialpooler.communication.adapters.utils.ModbusRegister
import ru.avem.kserialpooler.communication.utils.TransportException
import ru.avem.kserialpooler.communication.utils.TypeByteOrder
import ru.avem.kserialpooler.communication.utils.allocateOrderedByteBuffer
import ru.avem.stand.modules.r.communication.model.DeviceController
import ru.avem.stand.modules.r.communication.model.DeviceRegister
import ru.avem.stand.modules.r.communication.model.devices.delta.c2000.C2000Model
import java.nio.ByteBuffer
import java.nio.ByteOrder

class Optimus(
    override val name: String,
    override val protocolAdapter: ModbusRTUAdapter,
    override val id: Byte
) : DeviceController() {
    private val model = OptimusModel()
    override var requestTotalCount = 0
    override var requestSuccessCount = 0
    override val pollingRegisters = mutableListOf<DeviceRegister>()

    override val writingRegisters = mutableListOf<Pair<DeviceRegister, Number>>()

    init {
        protocolAdapter.connection.connect()
    }

    override fun readRegister(register: DeviceRegister) {
        isResponding = try {
            transactionWithAttempts {
                when (register.valueType) {
                    DeviceRegister.RegisterValueType.SHORT -> {
                        val modbusRegister =
                            protocolAdapter.readHoldingRegisters(id, register.address, 1).map(ModbusRegister::toShort)
                        register.value = modbusRegister.first().toDouble()
                    }

                    DeviceRegister.RegisterValueType.FLOAT -> {
                        val modbusRegister =
                            protocolAdapter.readHoldingRegisters(id, register.address, 2).map(ModbusRegister::toShort)
                        register.value =
                            allocateOrderedByteBuffer(modbusRegister, TypeByteOrder.BIG_ENDIAN, 4).float.toDouble()
                    }

                    DeviceRegister.RegisterValueType.INT32 -> {
                        val modbusRegister =
                            protocolAdapter.readHoldingRegisters(id, register.address, 2).map(ModbusRegister::toShort)
                        register.value =
                            allocateOrderedByteBuffer(modbusRegister, TypeByteOrder.BIG_ENDIAN, 4).int.toDouble()
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
                    transactionWithAttempts {
                        protocolAdapter.presetSingleRegister(id, register.address, ModbusRegister(value.toShort()))
                    }
                }

                is Short -> {
                    transactionWithAttempts {
                        protocolAdapter.presetSingleRegister(id, register.address, ModbusRegister(value))
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
        transactionWithAttempts {
            protocolAdapter.presetMultipleRegisters(id, register.address, registers)
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
        writeRegister(getRegisterById(OptimusModel.CONTROL_REGISTER), 1.toShort())
    }

    fun startObjectReverse() {
        writeRegister(getRegisterById(OptimusModel.CONTROL_REGISTER), 2.toShort())
    }

    fun stopObject() {
        writeRegister(getRegisterById(OptimusModel.CONTROL_REGISTER), 5.toShort())
    }

    fun stopObjectNaVibege() {
        writeRegister(getRegisterById(OptimusModel.CONTROL_REGISTER), 6.toShort())
    }

    fun setObjectParamsRun(
        voltage: Double
    ) {
        writeRegister(getRegisterById(OptimusModel.VOLTAGE_1_REGISTER), 0.v())
        writeRegister(getRegisterById(OptimusModel.FREQUENCY_1_REGISTER), 0.hz())

        writeRegister(getRegisterById(OptimusModel.VOLTAGE_2_REGISTER), 0.v())
        writeRegister(getRegisterById(OptimusModel.FREQUENCY_2_REGISTER), 0.hz())

        writeRegister(getRegisterById(OptimusModel.VOLTAGE_3_REGISTER), 0.v())
        writeRegister(getRegisterById(OptimusModel.FREQUENCY_3_REGISTER), 0.hz())

        writeRegister(getRegisterById(OptimusModel.VOLTAGE_4_REGISTER), voltage.v())
        writeRegister(getRegisterById(OptimusModel.FREQUENCY_4_REGISTER), 50.hz())

        writeRegister(getRegisterById(OptimusModel.VOLTAGE_5_REGISTER), voltage.v())
        writeRegister(getRegisterById(OptimusModel.FREQUENCY_5_REGISTER), 60.hz())
    }

    fun setObjectParamsVIU(
    ) {
        writeRegister(getRegisterById(OptimusModel.VOLTAGE_1_REGISTER), 0.v())
        writeRegister(getRegisterById(OptimusModel.FREQUENCY_1_REGISTER), 0.hz())

        writeRegister(getRegisterById(OptimusModel.VOLTAGE_2_REGISTER), 0.v())
        writeRegister(getRegisterById(OptimusModel.FREQUENCY_2_REGISTER), 0.hz())

        writeRegister(getRegisterById(OptimusModel.VOLTAGE_3_REGISTER), 0.v())
        writeRegister(getRegisterById(OptimusModel.FREQUENCY_3_REGISTER), 0.hz())

        writeRegister(getRegisterById(OptimusModel.VOLTAGE_4_REGISTER), 0.v())
        writeRegister(getRegisterById(OptimusModel.FREQUENCY_4_REGISTER), 0.hz())

        writeRegister(getRegisterById(OptimusModel.VOLTAGE_5_REGISTER), 1.v())
        writeRegister(getRegisterById(OptimusModel.FREQUENCY_5_REGISTER), 50.hz())
    }

    fun setObjectUMax(voltageMax: Number) {
        writeRegister(getRegisterById(OptimusModel.VOLTAGE_4_REGISTER), voltageMax.v())
    }

    fun setObjectFCur(f: Double) {
        if (f > 0) {
            writeRegister(getRegisterById(OptimusModel.OUTPUT_FREQUENCY_REGISTER), f.hz() * 10)
        } else {
            writeRegister(getRegisterById(OptimusModel.OUTPUT_FREQUENCY_REGISTER), 0)
        }
    }

    private fun Number.hz(): Short = (this.toDouble() * 10).toInt().toShort()
    private fun Number.v(): Short = (this.toDouble() * 10).toInt().toShort()

}