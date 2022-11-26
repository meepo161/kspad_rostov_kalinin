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
    val result: StringProperty = SimpleStringProperty(""),

    val optimusU: StringProperty = SimpleStringProperty(""),
    val optimusI: StringProperty = SimpleStringProperty(""),
    val optimusF: StringProperty = SimpleStringProperty(""),
    val optimusV1: StringProperty = SimpleStringProperty(""),
    val optimusV2: StringProperty = SimpleStringProperty(""),
    val optimusV3: StringProperty = SimpleStringProperty(""),
    val optimusV4: StringProperty = SimpleStringProperty(""),
    val optimusV5: StringProperty = SimpleStringProperty(""),
    val optimusF1: StringProperty = SimpleStringProperty(""),
    val optimusF2: StringProperty = SimpleStringProperty(""),
    val optimusF3: StringProperty = SimpleStringProperty(""),
    val optimusF4: StringProperty = SimpleStringProperty(""),
    val optimusF5: StringProperty = SimpleStringProperty("")
)
