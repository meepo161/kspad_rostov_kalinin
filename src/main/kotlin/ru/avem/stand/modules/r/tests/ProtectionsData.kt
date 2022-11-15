package ru.avem.stand.modules.r.tests

import javafx.beans.property.SimpleStringProperty
import javafx.scene.paint.Color
import tornadofx.*

data class ProtectionsData(
    val overcurrentTI: Protection = Protection(),
    val overcurrentHV: Protection = Protection(),
    val doorsPEC: Protection = Protection(),
    val doorsZone: Protection = Protection(),
    val overheatingChokes: Protection = Protection(),
    val earthingSwitch: Protection = Protection(
        "НА ЗЕМЛЕ",
        c(168, 168, 168),
        "ВЫСОКОЕ НАПРЯЖЕНИЕ",
        c(118, 165, 175)
    ),
    val overheatingLM1: Protection = Protection(),
    val overheatingLM2: Protection = Protection(),
) {
    fun resetAll() {
        overcurrentTI.reset()
        overcurrentHV.reset()
        doorsPEC.reset()
        doorsZone.reset()
        overheatingChokes.reset()
        earthingSwitch.reset()
        overheatingLM1.reset()
        overheatingLM2.reset()
    }
}

class Protection(
    private val notTriggeredValue: String = "В НОРМЕ",
    private val notTriggeredColor: Color = c(0, 255, 0),
    private val triggeredValue: String = "СРАБОТАЛА",
    private val triggeredColor: Color = c(255, 0, 0)
) {
    val prop = SimpleStringProperty(notTriggeredValue)

    var isTriggered: Boolean = false

    fun set() {
        isTriggered = true
        prop.value = triggeredValue
    }

    fun reset() {
        isTriggered = false
        prop.value = notTriggeredValue
    }

    fun color() = if (isTriggered) {
        triggeredColor
    } else {
        notTriggeredColor
    }
}
