package ru.avem.stand.modules.r.tests.pi.ktr

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

class KtrView(title: String = "КТР", showOnStart: Boolean = true) : TestViewModule(title, showOnStart) {
    override val configPath: Path = Paths.get("cfg/app.properties")
    override lateinit var test: Ktr

    override fun injectTest() {
        if (!this::test.isInitialized) {
            test = Ktr()
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
                vbox {
                    tableview(observableList(test.testModel.measuredData)) {
                        minHeight = 64.0
                        maxHeight = 64.0
                        minWidth = 800.0
                        prefWidth = 800.0
                        columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
                        mouseTransparentProperty().set(true)

                        alignment = Pos.CENTER_LEFT

                        column("U AB, В", KtrData::UAB.getter)
                        column("U BC, В", KtrData::UBC.getter)
                        column("U CA, В", KtrData::UCA.getter)
                        column("I A, А", KtrData::IA.getter)
                        column("I B, А", KtrData::IB.getter)
                        column("I C, А", KtrData::IC.getter)
                    }
                    tableview(observableList(test.testModel.measuredData)) {
                        minHeight = 64.0
                        maxHeight = 64.0
                        minWidth = 400.0
                        prefWidth = 400.0
                        columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
                        mouseTransparentProperty().set(true)

                        alignment = Pos.CENTER_LEFT

                        column("U2 AB, В", KtrData::UAB2.getter)
                        column("U2 BC, В", KtrData::UBC2.getter)
                        column("U2 CA, В", KtrData::UCA2.getter)
                    }
                    tableview(observableList(test.testModel.measuredData)) {
                        minHeight = 64.0
                        maxHeight = 64.0
                        minWidth = 120.0
                        prefWidth = 120.0
                        columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
                        mouseTransparentProperty().set(true)

                        alignment = Pos.CENTER_LEFT

                        column("КТР", KtrData::KTR.getter)
                    }

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

                    column("P, кВт", KtrData::P1.getter)
                    column("f, Гц", KtrData::F.getter)
                    column("cos φ", KtrData::cos.getter)
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
//                        column("Ось Y, мм/с", KtrData::v1y.getter)
                    column("Вибро (полевая сторона), мм/с", KtrData::v1x.getter)
//                        column("Ось Z, мм/с", KtrData::v1z.getter)
//                    }
//                    nestedColumn("Вибро (рабочая сторона) PG32") {
//                        column("Ось Y, мм/с", KtrData::v2y.getter)
                    column("Вибро (рабочая сторона), мм/с", KtrData::v2x.getter)
//                        column("Ось Z, мм/с", KtrData::v2z.getter)
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

                    column("n об/мин", KtrData::RPM.getter)
                    column("t BK1, °C", KtrData::tempAmb.getter)
                    column("t BK2, °C", KtrData::tempTI.getter)
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

                column("Время, с", KtrData::time.getter)
                column("Результат", KtrData::result.getter)
            }
        }
    }.addClass(Styles.paneBorders)
}
