package ru.avem.stand.modules.r.tests.psi.mgr

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty

data class MGRData(
    val U: StringProperty = SimpleStringProperty(""),
    val R15: StringProperty = SimpleStringProperty(""),
    val R60: StringProperty = SimpleStringProperty(""),
    val kABS: StringProperty = SimpleStringProperty(""),
    val tempAmb: StringProperty = SimpleStringProperty(""),
    val tempTI: StringProperty = SimpleStringProperty(""),

    val time: StringProperty = SimpleStringProperty(""),
    val result: StringProperty = SimpleStringProperty("")
)
