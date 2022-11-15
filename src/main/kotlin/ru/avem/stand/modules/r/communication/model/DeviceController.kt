package ru.avem.stand.modules.r.communication.model

abstract class DeviceController : IDeviceController {
    override var isResponding: Boolean = true
}
