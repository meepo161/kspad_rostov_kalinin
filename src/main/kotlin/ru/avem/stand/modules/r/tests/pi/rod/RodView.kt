package ru.avem.stand.modules.r.tests.pi.rod

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

class RodView(title: String = "ХХ", showOnStart: Boolean = true) : TestViewModule(title, showOnStart) {
    override val configPath: Path = Paths.get("cfg/app.properties")
    override lateinit var test: Rod

    override fun injectTest() {
        if (!this::test.isInitialized) {
            test = Rod()
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
                    minWidth = 400.0
                    prefWidth = 400.0
                    columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
                    mouseTransparentProperty().set(true)

                    alignment = Pos.CENTER_LEFT

                    column("U AB, В", RodData::UAB.getter)
//                    column("U BC, В", RodData::UBC.getter)
//                    column("U CA, В", RodData::UCA.getter)
                    column("I A, А", RodData::IA.getter)
//                    column("I B, А", RodData::IB.getter)
//                    column("I C, А", RodData::IC.getter)
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

//                    column("P1, кВт", RodData::P1.getter)
                    column("f, Гц", RodData::F.getter)
//                    column("cos φ", RodData::cos.getter)
                }
            }
        }
        hbox {
//            hbox {
//                minWidth = 820.0
//                prefWidth = 820.0
//                hboxConstraints {
//                    hGrow = Priority.ALWAYS
//                }
//                tableview(observableList(test.testModel.measuredData)) {
//                    minHeight = 91.0
//                    maxHeight = 91.0
//                    minWidth = 590.0
//                    prefWidth = 590.0
//                    columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
//                    mouseTransparentProperty().set(true)
//
//                    alignment = Pos.CENTER_LEFT

//                    nestedColumn("Вибро (полевая сторона) PG31") {
//                        column("Ось Y, мм/с", RodData::v1y.getter)
//                        column("Вибро (полевая сторона), мм/с", RodData::v1x.getter)
//                        column("Ось Z, мм/с", RodData::v1z.getter)
//                    }
//                    nestedColumn("Вибро (рабочая сторона) PG32") {
//                        column("Ось Y, мм/с", RodData::v2y.getter)
//                        column("Вибро (рабочая сторона), мм/с", RodData::v2x.getter)
//                        column("Ось Z, мм/с", RodData::v2z.getter)
//                    }
//                }
//            }
            hbox {
                hboxConstraints {
                    hGrow = Priority.ALWAYS
                }
                tableview(observableList(test.testModel.measuredData)) {
                    minHeight = 64.0
                    maxHeight = 64.0
                    minWidth = 220.0
                    prefWidth = 220.0
                    columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
                    mouseTransparentProperty().set(true)

                    alignment = Pos.CENTER_LEFT

//                    column("n об/мин", RodData::RPM.getter)
                    column("t BK1, °C", RodData::tempAmb.getter)
                    column("t BK2, °C", RodData::tempTI.getter)
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

//                column("Время, с", RodData::time.getter)
                column("Результат", RodData::result.getter)
            }
        }
    }.addClass(Styles.paneBorders)
}
