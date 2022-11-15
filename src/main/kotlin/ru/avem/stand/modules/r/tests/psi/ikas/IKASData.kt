package ru.avem.stand.modules.r.tests.psi.ikas

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty

data class IKASData(
    val descriptor: StringProperty,

    val tempAmb: StringProperty = SimpleStringProperty(""),
    val tempTI: StringProperty = SimpleStringProperty(""),

    val R1: StringProperty = SimpleStringProperty(""),
    val R2: StringProperty = SimpleStringProperty(""),
    val R3: StringProperty = SimpleStringProperty(""),

    val averR: StringProperty = SimpleStringProperty(""),

    val result: StringProperty = SimpleStringProperty("")
)
