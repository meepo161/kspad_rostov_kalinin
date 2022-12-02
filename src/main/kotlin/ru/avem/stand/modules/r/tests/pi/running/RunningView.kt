package ru.avem.stand.modules.r.tests.pi.running

import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.control.TableView
import javafx.scene.layout.Priority
import javafx.scene.text.TextAlignment
import ru.avem.stand.modules.i.views.Styles
import ru.avem.stand.modules.i.views.TestViewModule
import tornadofx.*
import java.nio.file.Path
import java.nio.file.Paths

class RunningView(title: String = "Обкатка", showOnStart: Boolean = true) : TestViewModule(title, showOnStart) {
    override val configPath: Path = Paths.get("cfg/app.properties")
    override lateinit var test: Running

    override fun injectTest() {
        if (!this::test.isInitialized) {
            test = Running()
        }
    }

    override fun EventTarget.testData() = vbox(spacing = 16) {
        padding = insets(8)

        hboxConstraints {
            hGrow = Priority.ALWAYS
        }

        label("Измеренные значения") {
            alignment = Pos.TOP_CENTER
            textAlignment = TextAlignment.CENTER
            useMaxWidth = true
            isWrapText = true
        }

        hbox {
            hbox {
                minWidth = 820.0
                prefWidth = 820.0
                hboxConstraints {
                    hGrow = Priority.ALWAYS
                }
                tableview(observableList(test.testModel.measuredData)) {
                    minHeight = 64.0
                    maxHeight = 64.0
                    minWidth = 800.0
                    prefWidth = 800.0
                    columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
                    mouseTransparentProperty().set(true)

                    alignment = Pos.CENTER_LEFT

                    column("U AB, В", RunningData::UAB.getter)
                    column("U BC, В", RunningData::UBC.getter)
                    column("U CA, В", RunningData::UCA.getter)
                    column("I A, А", RunningData::IA.getter)
                    column("I B, А", RunningData::IB.getter)
                    column("I C, А", RunningData::IC.getter)
                }
            }
            hbox {
                hboxConstraints {
                    hGrow = Priority.ALWAYS
                }
                tableview(observableList(test.testModel.measuredData)) {
                    minHeight = 64.0
                    maxHeight = 64.0
                    minWidth = 200.0
                    prefWidth = 200.0
                    columnResizePolicy = SmartResize.POLICY
                    mouseTransparentProperty().set(true)

                    alignment = Pos.CENTER_LEFT

                    column("P, кВт", RunningData::P1.getter)
                    column("f, Гц", RunningData::F.getter)
                    column("cos φ", RunningData::cos.getter)
                }
            }
        }
        hbox {
            hbox {
                minWidth = 820.0
                prefWidth = 820.0
                hboxConstraints {
                    hGrow = Priority.ALWAYS
                }
                tableview(observableList(test.testModel.measuredData)) {
                    minHeight = 91.0
                    maxHeight = 91.0
                    minWidth = 590.0
                    prefWidth = 590.0
                    columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
                    mouseTransparentProperty().set(true)

                    alignment = Pos.CENTER_LEFT

//                    nestedColumn("Вибро (полевая сторона) PG31") {
//                        column("Ось Y, мм/с", RunningData::v1y.getter)
                        column("Вибро (полевая сторона), мм/с", RunningData::v1x.getter)
//                        column("Ось Z, мм/с", RunningData::v1z.getter)
//                    }
//                    nestedColumn("Вибро (рабочая сторона) PG32") {
//                        column("Ось Y, мм/с", RunningData::v2y.getter)
                        column("Вибро (рабочая сторона), мм/с", RunningData::v2x.getter)
//                        column("Ось Z, мм/с", RunningData::v2z.getter)
//                    }
                }
            }
            hbox {
                hboxConstraints {
                    hGrow = Priority.ALWAYS
                }
                tableview(observableList(test.testModel.measuredData)) {
                    minHeight = 64.0
                    maxHeight = 64.0
                    minWidth = 220.0
                    prefWidth = 220.0
                    columnResizePolicy = SmartResize.POLICY
                    mouseTransparentProperty().set(true)

                    alignment = Pos.CENTER_LEFT

                    column("n об/мин", RunningData::RPM.getter)
                    column("t BK1, °C", RunningData::tempAmb.getter)
                    column("t BK2, °C", RunningData::tempTI.getter)
                }
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

                column("Время, с", RunningData::time.getter)
                column("Результат", RunningData::result.getter)
            }
        }
    }.addClass(Styles.paneBorders)
}
