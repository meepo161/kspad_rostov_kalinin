package ru.avem.stand.modules.r.communication.model.devices.termodat.r10i5

import ru.avem.stand.modules.r.communication.model.DeviceRegister
import ru.avem.stand.modules.r.communication.model.IDeviceModel

class R10I5Model : IDeviceModel {
    companion object {
        const val T_1 = "T_1"
    }

    override val registers: Map<String, DeviceRegister> = mapOf(
        T_1 to DeviceRegister(0x0000, DeviceRegister.RegisterValueType.SHORT, coefficient = 1.0 / 10.0)
    )

    override fun getRegisterById(idRegister: String) =
        registers[idRegister] ?: error("Такого регистра нет в описанной карте $idRegister")
}
