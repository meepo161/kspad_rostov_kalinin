package ru.avem.stand.modules.r.common.prefill

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.input.MouseEvent.MOUSE_PRESSED
import javafx.scene.layout.*
import ru.avem.stand.modules.i.views.Styles
import ru.avem.stand.modules.r.storage.Properties
import ru.avem.stand.modules.r.storage.database.entities.TestItem
import ru.avem.stand.utils.minusPercent
import tornadofx.*

class PreFillTab : View("Испытания") {
    private val controller: PreFillController by inject()

    private var testTypes: ComboBox<TestItem> by singleAssign()

    var selectUnselectButton: Button by singleAssign()

    var testsList: ListView<CheckBox> by singleAssign()

    override val root = anchorpane {
        vbox(spacing = 16.0) {
            anchorpaneConstraints {
                leftAnchor = 16.0
                rightAnchor = 16.0
                bottomAnchor = 120.0
                topAnchor = 16.0
            }
            alignment = Pos.CENTER

            label("Заполните все поля и нажмите \"Начать испытания\"").addClass(Styles.headerLabels)

            hbox(spacing = 16.0) {
                label("Заводской номер:") {
                    minWidth = 117.0
                }

                textfield(property = PreFillModel.serialNumberProp) {
                    hboxConstraints {
                        hGrow = Priority.ALWAYS
                    }
                    minWidth = Properties.standData.width minusPercent 50.0

                    text = ""
                }
            }

            hbox(spacing = 80.0) {
                label("Тип ОИ:") {
                    minWidth = 52.0
                }

                testTypes = combobox(PreFillModel.testTypeProp, Properties.testItems) {
                    hboxConstraints {
                        hGrow = Priority.ALWAYS
                    }
                    minWidth = Properties.standData.width minusPercent 50.0
                    useMaxWidth = true

                    selectionModel.selectFirst()
                }
            }

//            tabpane {
//                tabClosingPolicyProperty().set(TabPane.TabClosingPolicy.UNAVAILABLE)
//                tab("ПСИ") {
            hbox(spacing = 58.0) {
                paddingTop = 8
                vbox {
                    label("Испытание:") {
                        minWidth = 75.0
                    }
                    selectUnselectButton = button("Выбрать все") {
                        minWidth = 150.0
                        prefWidth = 150.0
                        maxWidth = 150.0
                        minHeight = 150.0
                        prefHeight = 150.0
                        maxHeight = 150.0
                        graphic = MaterialDesignIconView(MaterialDesignIcon.CHECK).apply {
                            glyphSize = 45
                            fill = c("black")
                        }
                        contentDisplay = ContentDisplay.TOP

                        action {
                            controller.toggleAllTests(selectUnselectButton.text)
                        }
                    }.addClass(Styles.toggleButtons)
                }

                testsList = listview {
                    hboxConstraints {
                        hGrow = Priority.ALWAYS
                    }
                    minHeight = Properties.standData.height minusPercent 55.0
                    maxHeight = Properties.standData.height minusPercent 55.0
                    minWidth = Properties.standData.width minusPercent 50.0

                    items = Properties.tests.map {
                        CheckBox(it.toString()).apply {
                            this.selectedProperty().onChange { isSelected ->
                                if (isSelected) {
                                    PreFillModel.selectedTests.add(it)
                                } else {
                                    PreFillModel.selectedTests.remove(it)
                                }
                            }
                            isWrapText = true
                            this@listview.widthProperty().onChange {
                                maxWidth = it - 20
                            }
                        }
                    }.observable()

                    addEventFilter(MOUSE_PRESSED) { event ->
                        event.target.getChildList()?.getOrNull(0)?.let {
                            if (it is CheckBox) it.isSelected = !it.isSelected
                            event.consume()
                        }
                    }
                }
            }
//                }
//                tab("ПИ") {
//
//                }
//            }
            button("Начать испытания") {
                minWidth = 150.0
                prefWidth = 150.0
                maxWidth = 150.0
                minHeight = 150.0
                prefHeight = 150.0
                maxHeight = 150.0
                isDefaultButton = true
                graphic = MaterialDesignIconView(MaterialDesignIcon.PLAY).apply {
                    glyphSize = 65
                    fill = c("green")
                }
                contentDisplay = ContentDisplay.TOP

                action {
                    controller.toTests()
                }
            }
        }
    }

    fun resetComboBox() {
        testTypes.selectionModel.selectFirst()
        testTypes.selectionModel.select(-1)
    }

    fun refreshComboBox() {
        testTypes.items = Properties.testItems
    }

    fun select() {
        selectUnselectButton.text = "Снять все"
    }

    fun unselect() {
        selectUnselectButton.text = "Выбрать все"
    }
}
