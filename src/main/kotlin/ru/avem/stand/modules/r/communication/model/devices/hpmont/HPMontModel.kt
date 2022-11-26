package ru.avem.stand.modules.r.communication.model.devices.hpmont

import ru.avem.stand.modules.r.communication.model.DeviceRegister
import ru.avem.stand.modules.r.communication.model.IDeviceModel

class HPMontModel : IDeviceModel {
    companion object {
        const val CONTROL_REGISTER = "CONTROL_REGISTER"
        const val POINT_1_FREQUENCY_REGISTER = "POINT_1_FREQUENCY_REGISTER"
        const val POINT_1_VOLTAGE_REGISTER = "POINT_1_VOLTAGE_REGISTER"
        const val POINT_2_FREQUENCY_REGISTER = "POINT_2_FREQUENCY_REGISTER"
        const val POINT_2_VOLTAGE_REGISTER = "POINT_2_VOLTAGE_REGISTER"
        const val POINT_3_FREQUENCY_REGISTER = "POINT_3_FREQUENCY_REGISTER"
        const val POINT_3_VOLTAGE_REGISTER = "POINT_3_VOLTAGE_REGISTER"

        const val RUNNING_FREQUENCY = "RUNNING_FREQUENCY"
        const val CONTROL_MOD = "CONTROL_MOD"
        const val MAX_OUT_FREQ = "MAX_OUT_FREQ"
        const val MAX_OPERATIONG_FREQ = "MAX_OPERATIONG_FREQ"
        const val INITIAL_DIRECTION = "INITIAL_DIRECTION"
        const val POINTS_COUNT = "POINTS_COUNT"

        const val SYN_POWER = "SYN_POWER"
        const val SYN_VOLTAGE = "SYN_VOLTAGE"
        const val SYN_CURRENT = "SYN_CURRENT"
        const val SYN_FREQ = "SYN_FREQ"
        const val SYN_RPM = "SYN_RPM"

        const val ASYNC_POWER = "ASYNC_POWER"
        const val ASYNC_CURRENT = "ASYNC_CURRENT"
        const val ASYNC_VOLTAGE = "ASYNC_VOLTAGE"
        const val ASYNC_FREQ = "ASYNC_FREQ"
        const val ASYNC_RPM = "ASYNC_RPM"

        const val ENCODER_TYPE = "ENCODER_TYPE" //1 - ABZ 2 - UVW 3 - SINCOS 4 - 1313 serial 5 - Resolver
        const val ENCODER_PULSE_COUNT = "ENCODER_PULSE_COUNT" // 1 - 9999
        const val ENCODER_DISCONNECTION = "ENCODER_DISCONNECTION" // 0 - freewheel 1 - Emergency stop

        const val OUTPUT_VOLTAGE_REGISTER = "OUTPUT_VOLTAGE_REGISTER"
        const val OUTPUT_CURRENT_REGISTER = "OUTPUT_CURRENT_REGISTER"
        const val OUTPUT_FREQUENCY_REGISTER = "OUTPUT_FREQUENCY_REGISTER"
        const val TEMP = "TEMP"
    }

    override val registers: Map<String, DeviceRegister> = mapOf(
        CONTROL_MOD to DeviceRegister(0x0001, DeviceRegister.RegisterValueType.SHORT),
        MAX_OUT_FREQ to DeviceRegister(0x0006, DeviceRegister.RegisterValueType.SHORT),
        MAX_OPERATIONG_FREQ to DeviceRegister(0x0008, DeviceRegister.RegisterValueType.SHORT),
        INITIAL_DIRECTION to DeviceRegister(0x0017, DeviceRegister.RegisterValueType.SHORT),

        SYN_POWER to DeviceRegister(0x0C01, DeviceRegister.RegisterValueType.SHORT),
        SYN_VOLTAGE to DeviceRegister(0x0C02, DeviceRegister.RegisterValueType.SHORT),
        SYN_CURRENT to DeviceRegister(0x0C03, DeviceRegister.RegisterValueType.SHORT),
        SYN_FREQ to DeviceRegister(0x0C04, DeviceRegister.RegisterValueType.SHORT),
        SYN_RPM to DeviceRegister(0x0C05, DeviceRegister.RegisterValueType.SHORT),

        ENCODER_TYPE to DeviceRegister(0x0E00, DeviceRegister.RegisterValueType.SHORT),
        ENCODER_PULSE_COUNT to DeviceRegister(0x0E01, DeviceRegister.RegisterValueType.SHORT),
        ENCODER_DISCONNECTION to DeviceRegister(0x0E04, DeviceRegister.RegisterValueType.SHORT),

        CONTROL_REGISTER to DeviceRegister(0x3200, DeviceRegister.RegisterValueType.SHORT),
        RUNNING_FREQUENCY to DeviceRegister(0x3201, DeviceRegister.RegisterValueType.SHORT),
        POINTS_COUNT to DeviceRegister(0x0900, DeviceRegister.RegisterValueType.SHORT),
        POINT_1_VOLTAGE_REGISTER to DeviceRegister(0x0906, DeviceRegister.RegisterValueType.SHORT),
        POINT_1_FREQUENCY_REGISTER to DeviceRegister(0x0905, DeviceRegister.RegisterValueType.SHORT),
        POINT_2_VOLTAGE_REGISTER to DeviceRegister(0x0904, DeviceRegister.RegisterValueType.SHORT),
        POINT_2_FREQUENCY_REGISTER to DeviceRegister(0x0903, DeviceRegister.RegisterValueType.SHORT),
        POINT_3_VOLTAGE_REGISTER to DeviceRegister(0x0902, DeviceRegister.RegisterValueType.SHORT),
        POINT_3_FREQUENCY_REGISTER to DeviceRegister(0x0901, DeviceRegister.RegisterValueType.SHORT),

        ASYNC_POWER to DeviceRegister(0x0800, DeviceRegister.RegisterValueType.SHORT),
        ASYNC_VOLTAGE to DeviceRegister(0x0801, DeviceRegister.RegisterValueType.SHORT),
        ASYNC_CURRENT to DeviceRegister(0x0802, DeviceRegister.RegisterValueType.SHORT),
        ASYNC_FREQ to DeviceRegister(0x0803, DeviceRegister.RegisterValueType.SHORT),
        ASYNC_RPM to DeviceRegister(0x0804, DeviceRegister.RegisterValueType.SHORT),
        TEMP to DeviceRegister(0x0900, DeviceRegister.RegisterValueType.SHORT),

        OUTPUT_VOLTAGE_REGISTER to DeviceRegister(0x3314, DeviceRegister.RegisterValueType.SHORT),
        OUTPUT_CURRENT_REGISTER to DeviceRegister(0x3315, DeviceRegister.RegisterValueType.SHORT),
        OUTPUT_FREQUENCY_REGISTER to DeviceRegister(0x3310, DeviceRegister.RegisterValueType.SHORT),
    )

    override fun getRegisterById(idRegister: String) =
        registers[idRegister] ?: error("Такого регистра нет в описанной карте $idRegister")
}
