package ru.avem.stand.modules.i.tests

import javafx.scene.paint.Color
import tornadofx.*

enum class LogTag(val c: Color) {
    INFO(c("#5dbb25")),
    WARN(c("#e38819")),
    ERROR(c("#ff3935")),
    DEBUG(c("#359eee"))
}
