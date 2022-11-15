package ru.avem.stand.modules.r.tests.pi.minm

import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.text.TextAlignment
import ru.avem.stand.modules.i.views.Styles
import ru.avem.stand.modules.i.views.TestViewModule
import tornadofx.*
import java.nio.file.Path
import java.nio.file.Paths

class MinMView(title: String = "МИНИМУМ (МОМЕНТ)", showOnStart: Boolean = true) : TestViewModule(title, showOnStart) {
    override val configPath: Path = Paths.get("cfg/app.properties")
    override lateinit var test: MinM

    override fun injectTest() {
        if (!this::test.isInitialized) {
            test = MinM()
        }
    }

    override fun EventTarget.testData() = vbox(spacing = 16) {
        padding = insets(8)

        hboxConstraints {
            hGrow = Priority.ALWAYS
        }

        label("Измеренные значения") {
            alignment = Pos.CENTER
            textAlignment = TextAlignment.CENTER
            useMaxWidth = true
            isWrapText = true
        }
        hbox {
            minWidth = 820.0
            prefWidth = 820.0
            hboxConstraints {
                hGrow = Priority.ALWAYS
            }
            tableview(observableList(test.testModel.measuredData)) {
                minHeight = 64.0
                maxHeight = 64.0
                minWidth = 400.0
                prefWidth = 400.0
                columnResizePolicy = SmartResize.POLICY
                mouseTransparentProperty().set(true)

                alignment = Pos.CENTER_LEFT

                column("U AB, В", MinMData::UAB.getter)
                column("U BC, В", MinMData::UBC.getter)
                column("U CA, В", MinMData::UCA.getter)
                column("I A, А", MinMData::IA.getter)
                column("I B, А", MinMData::IB.getter)
                column("I C, А", MinMData::IC.getter)
            }
        }
        hbox(spacing = 16) {
            tableview(observableList(test.testModel.measuredData)) {
                minHeight = 64.0
                maxHeight = 64.0
                minWidth = 220.0
                prefWidth = 220.0
                columnResizePolicy = SmartResize.POLICY
                mouseTransparentProperty().set(true)
                column("n, об/мин", MinMData::RPM.getter)
                column("M, Н⋅м", MinMData::torque.getter)
            }
        }
        hbox {
            vboxConstraints {
                vGrow = Priority.ALWAYS
            }
            tableview(observableList(test.testModel.measuredData)) {
                minHeight = 64.0
                maxHeight = 64.0
                minWidth = 220.0
                prefWidth = 220.0
                columnResizePolicy = SmartResize.POLICY
                mouseTransparentProperty().set(true)

                alignment = Pos.BOTTOM_RIGHT

                column("Результат", MinMData::result.getter)
            }
        }
    }.addClass(Styles.paneBorders)
}
