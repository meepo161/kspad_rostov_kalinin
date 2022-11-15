package ru.avem.stand.modules.r.communication.model.devices.prosoftsystems.ivd3c

import ru.avem.stand.modules.r.communication.model.DeviceRegister
import ru.avem.stand.modules.r.communication.model.IDeviceModel

class IVD3CModel : IDeviceModel {
    companion object {
        const val MEAS_X = "MEAS_X"
        const val MEAS_Y = "MEAS_Y"
        const val MEAS_Z = "MEAS_Z"
    }

    override val registers: Map<String, DeviceRegister> = mapOf(
        MEAS_X to DeviceRegister(0x0003, DeviceRegister.RegisterValueType.FLOAT),
        MEAS_Y to DeviceRegister(0x0005, DeviceRegister.RegisterValueType.FLOAT),
        MEAS_Z to DeviceRegister(0x0001, DeviceRegister.RegisterValueType.FLOAT),
    )

    override fun getRegisterById(idRegister: String) =
        registers[idRegister] ?: error("Такого регистра нет в описанной карте $idRegister")
}
