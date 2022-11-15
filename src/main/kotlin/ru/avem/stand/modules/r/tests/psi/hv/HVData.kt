package ru.avem.stand.modules.r.tests.psi.hv

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty

data class HVData(
    val U: StringProperty = SimpleStringProperty(""),
    val I: StringProperty = SimpleStringProperty(""),
    val F: StringProperty = SimpleStringProperty(""),

    val time: StringProperty = SimpleStringProperty(""),
    val result: StringProperty = SimpleStringProperty("")
)
