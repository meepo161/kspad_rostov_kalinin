package ru.avem.stand.modules.r.communication.model

interface IDeviceModel {
    val registers: Map<String, DeviceRegister>

    fun getRegisterById(idRegister: String): DeviceRegister
}
