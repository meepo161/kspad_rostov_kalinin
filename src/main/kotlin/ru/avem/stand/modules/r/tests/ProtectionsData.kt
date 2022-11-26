package ru.avem.stand.modules.r.tests

import javafx.beans.property.SimpleStringProperty
import javafx.scene.paint.Color
import tornadofx.*

data class ProtectionsData(
    val overCurrentOI: Protection = Protection(),
    val overCurrentVIU: Protection = Protection(),
    val doorsZone: Protection = Protection(),
    val doorsSHSO: Protection = Protection()
) {
    fun resetAll() {
        overCurrentOI.reset()
        overCurrentVIU.reset()
        doorsZone.reset()
        doorsSHSO.reset()
    }
}

class Protection(
    private val notTriggeredValue: String = "В НОРМЕ",
    private val triggeredValue: String = "СРАБОТАЛА",
    private val unknownValue: String = "НЕИЗВЕСТНО",
) {
    val prop = SimpleStringProperty(notTriggeredValue)

    var isTriggered: Boolean = false

    fun unknown() {
        prop.value = unknownValue
    }

    fun set() {
        isTriggered = true
        prop.value = triggeredValue
    }

    fun reset() {
        isTriggered = false
        prop.value = notTriggeredValue
    }
}
