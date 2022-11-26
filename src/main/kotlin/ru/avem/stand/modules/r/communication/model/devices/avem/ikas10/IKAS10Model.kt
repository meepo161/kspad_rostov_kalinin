package ru.avem.stand.modules.r.communication.model.devices.avem.ikas10

import ru.avem.stand.modules.r.communication.model.DeviceRegister
import ru.avem.stand.modules.r.communication.model.IDeviceModel

class IKAS10Model : IDeviceModel {
    companion object {
        const val STATUS = "STATUS"
        const val CFG_SCHEME = "CFG_SCHEME"
        const val START_STOP = "START_STOP"
        const val RESIST_MEAS = "RESIST_MEAS"
    }

    enum class Scheme(val value: Short) {
        AA(0x0047),
        BB(0x0042),
        CC(0x0043),
        AB(0x0046),
        BC(0x0044),
        CA(0x0045)
    }

    override val registers: Map<String, DeviceRegister> = mapOf(
        STATUS to DeviceRegister(
            0x0005,
            DeviceRegister.RegisterValueType.SHORT
        ), // 0x00h=Завершено,0x65=Ожидание,0x80=Ошибка,0x81=Ошибка (АЦП),0x82=Ошибка (шунт),0x83=Оошибка (ток),0x84=Ошибка (напряжение),404=Измерение
        CFG_SCHEME to DeviceRegister(
            0x0001,
            DeviceRegister.RegisterValueType.SHORT
        ),
        START_STOP to DeviceRegister(0x0006, DeviceRegister.RegisterValueType.SHORT),
        RESIST_MEAS to DeviceRegister(0x0009, DeviceRegister.RegisterValueType.FLOAT),
    )

    override fun getRegisterById(idRegister: String) =
        registers[idRegister] ?: error("Такого регистра нет в описанной карте $idRegister")
}
