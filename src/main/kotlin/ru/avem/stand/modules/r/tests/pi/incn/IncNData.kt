package ru.avem.stand.modules.r.tests.pi.incn

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty

data class IncNData(
    val descriptor: StringProperty,

    val U: StringProperty = SimpleStringProperty(""),
    val UAB: StringProperty = SimpleStringProperty(""),
    val UBC: StringProperty = SimpleStringProperty(""),
    val UCA: StringProperty = SimpleStringProperty(""),
    val I: StringProperty = SimpleStringProperty(""),
    val IA: StringProperty = SimpleStringProperty(""),
    val IB: StringProperty = SimpleStringProperty(""),
    val IC: StringProperty = SimpleStringProperty(""),

    val F: StringProperty = SimpleStringProperty(""),
    val RPM: StringProperty = SimpleStringProperty(""),

    val time: StringProperty = SimpleStringProperty(""),
    val result: StringProperty = SimpleStringProperty("")
)
