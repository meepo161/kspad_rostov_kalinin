package ru.avem.stand.modules.r.communication.model.devices.appa.model70x

import ru.avem.stand.modules.r.communication.model.DeviceRegister
import ru.avem.stand.modules.r.communication.model.IDeviceModel


class APPA70xModel : IDeviceModel {
    companion object {
        const val RESPONDING_PARAM = "RESPONDING_PARAM"
        const val MODE_PARAM = "MODE_PARAM"
        const val RESISTANCE_PARAM = "RESISTANCE_PARAM"
    }

    override val registers: Map<String, DeviceRegister> = mapOf(
        RESPONDING_PARAM to DeviceRegister(0, DeviceRegister.RegisterValueType.SHORT),
        MODE_PARAM to DeviceRegister(1, DeviceRegister.RegisterValueType.SHORT),
        RESISTANCE_PARAM to DeviceRegister(2, DeviceRegister.RegisterValueType.SHORT)
    )

    override fun getRegisterById(idRegister: String) =
        registers[idRegister] ?: error("Такого регистра нет в описанной карте $idRegister")
}
