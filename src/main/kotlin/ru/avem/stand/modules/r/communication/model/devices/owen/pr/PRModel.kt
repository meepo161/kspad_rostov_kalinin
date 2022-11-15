package ru.avem.stand.modules.r.communication.model.devices.owen.pr

import ru.avem.stand.modules.r.communication.model.DeviceRegister
import ru.avem.stand.modules.r.communication.model.IDeviceModel

class PRModel : IDeviceModel {
    companion object {
        const val DO_01_16 = "DO_01_16"
        const val DO_17_32 = "DO_17_32"

        const val WD_TIMEOUT = "WD_TIMEOUT"
        const val CMD = "CMD"
        const val STATE = "STATE"

        const val DI_01_16_RST = "DI_01_16_RST"
        const val DI_17_32_RST = "DI_17_32_RST"
        const val DI_33_48_RST = "DI_33_48_RST"

        const val DI_01_16_TRIG = "DI_01_16_TRIG"
        const val DI_17_32_TRIG = "DI_17_32_TRIG"
        const val DI_33_48_TRIG = "DI_33_48_TRIG"

        const val DI_01_16_TRIG_INV = "DI_01_16_TRIG_INV"
        const val DI_17_32_TRIG_INV = "DI_17_32_TRIG_INV"
        const val DI_33_48_TRIG_INV = "DI_33_48_TRIG_INV"

        const val DI_01_16_ERROR_MASK_1 = "DI_01_16_ERROR_MASK_1"
        const val DI_01_16_ERROR_MASK_0 = "DI_01_16_ERROR_MASK_0"
        const val DI_17_32_ERROR_MASK_1 = "DI_17_32_ERROR_MASK_1"
        const val DI_17_32_ERROR_MASK_0 = "DI_17_32_ERROR_MASK_0"
        const val DI_33_48_ERROR_MASK_1 = "DI_33_48_ERROR_MASK_1"
        const val DI_33_48_ERROR_MASK_0 = "DI_33_48_ERROR_MASK_0"

        const val DO_01_16_ERROR_S1_MASK_0 = "DO_01_16_ERROR_S1_MASK_0"
        const val DO_01_16_ERROR_S1_MASK_1 = "DO_01_16_ERROR_S1_MASK_1"
        const val DO_17_32_ERROR_S1_MASK_0 = "DO_17_32_ERROR_S1_MASK_0"
        const val DO_17_32_ERROR_S1_MASK_1 = "DO_17_32_ERROR_S1_MASK_1"

        const val AI_01_F = "AI_01_F"
        const val AO_01_F = "AO_01_F"
    }

    override val registers: Map<String, DeviceRegister> = mapOf(
        DO_01_16 to DeviceRegister(512, DeviceRegister.RegisterValueType.SHORT),
        DO_17_32 to DeviceRegister(513, DeviceRegister.RegisterValueType.SHORT),

        WD_TIMEOUT to DeviceRegister(573, DeviceRegister.RegisterValueType.SHORT),
        CMD to DeviceRegister(574, DeviceRegister.RegisterValueType.SHORT),
        STATE to DeviceRegister(575, DeviceRegister.RegisterValueType.SHORT),

        DI_01_16_RST to DeviceRegister(517, DeviceRegister.RegisterValueType.SHORT),
        DI_17_32_RST to DeviceRegister(521, DeviceRegister.RegisterValueType.SHORT),
        DI_33_48_RST to DeviceRegister(525, DeviceRegister.RegisterValueType.SHORT),

        DI_01_16_TRIG to DeviceRegister(518, DeviceRegister.RegisterValueType.SHORT),
        DI_17_32_TRIG to DeviceRegister(522, DeviceRegister.RegisterValueType.SHORT),
        DI_33_48_TRIG to DeviceRegister(526, DeviceRegister.RegisterValueType.SHORT),

        DI_01_16_TRIG_INV to DeviceRegister(519, DeviceRegister.RegisterValueType.SHORT),
        DI_17_32_TRIG_INV to DeviceRegister(523, DeviceRegister.RegisterValueType.SHORT),
        DI_33_48_TRIG_INV to DeviceRegister(527, DeviceRegister.RegisterValueType.SHORT),

        DI_01_16_ERROR_MASK_1 to DeviceRegister(547, DeviceRegister.RegisterValueType.SHORT),
        DI_01_16_ERROR_MASK_0 to DeviceRegister(548, DeviceRegister.RegisterValueType.SHORT),
        DI_17_32_ERROR_MASK_1 to DeviceRegister(549, DeviceRegister.RegisterValueType.SHORT),
        DI_17_32_ERROR_MASK_0 to DeviceRegister(550, DeviceRegister.RegisterValueType.SHORT),
        DI_33_48_ERROR_MASK_1 to DeviceRegister(551, DeviceRegister.RegisterValueType.SHORT),
        DI_33_48_ERROR_MASK_0 to DeviceRegister(552, DeviceRegister.RegisterValueType.SHORT),

        DO_01_16_ERROR_S1_MASK_0 to DeviceRegister(554, DeviceRegister.RegisterValueType.SHORT),
        DO_01_16_ERROR_S1_MASK_1 to DeviceRegister(553, DeviceRegister.RegisterValueType.SHORT),
        DO_17_32_ERROR_S1_MASK_0 to DeviceRegister(556, DeviceRegister.RegisterValueType.SHORT),
        DO_17_32_ERROR_S1_MASK_1 to DeviceRegister(555, DeviceRegister.RegisterValueType.SHORT),

        AI_01_F to DeviceRegister(531, DeviceRegister.RegisterValueType.FLOAT), // CDAB
        AO_01_F to DeviceRegister(539, DeviceRegister.RegisterValueType.FLOAT), // CDAB
    )

    override fun getRegisterById(idRegister: String) =
        registers[idRegister] ?: error("Такого регистра нет в описанной карте $idRegister")
}
