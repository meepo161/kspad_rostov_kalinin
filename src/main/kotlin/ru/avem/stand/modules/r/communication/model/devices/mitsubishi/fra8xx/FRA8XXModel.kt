package ru.avem.stand.modules.r.communication.model.devices.mitsubishi.fra8xx

import ru.avem.stand.modules.r.communication.model.DeviceRegister
import ru.avem.stand.modules.r.communication.model.IDeviceModel

class FRA8XXModel : IDeviceModel {
    companion object {
        const val CONTROL_REGISTER = "CONTROL_REGISTER"
        const val CURRENT_FREQUENCY_REGISTER = "CURRENT_FREQUENCY_REGISTER"
        const val MAX_FREQUENCY_REGISTER = "MAX_FREQUENCY_REGISTER"
        const val MAX_VOLTAGE_REGISTER = "MAX_VOLTAGE_REGISTER"
    }

    override val registers: Map<String, DeviceRegister> = mapOf(
        CONTROL_REGISTER to DeviceRegister(8, DeviceRegister.RegisterValueType.SHORT),
        CURRENT_FREQUENCY_REGISTER to DeviceRegister(14, DeviceRegister.RegisterValueType.SHORT),
        MAX_FREQUENCY_REGISTER to DeviceRegister(1002, DeviceRegister.RegisterValueType.SHORT),
        MAX_VOLTAGE_REGISTER to DeviceRegister(1018, DeviceRegister.RegisterValueType.SHORT)
    )

    override fun getRegisterById(idRegister: String) =
        registers[idRegister] ?: error("Такого регистра нет в описанной карте $idRegister")
}
