package ru.avem.stand.modules.r.common.editor

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.VBox
import ru.avem.stand.modules.i.views.Styles
import ru.avem.stand.modules.r.common.prefill.PreFillTab
import ru.avem.stand.modules.r.storage.Properties
import ru.avem.stand.modules.r.storage.database.entities.TestItem
import ru.avem.stand.modules.r.storage.testitemfields.TypeEnterField
import ru.avem.stand.modules.r.storage.testitemfields.TypeFormatTestItemField
import ru.avem.stand.utils.isBoolean
import tornadofx.*

class TestItemEditorTab : View("Редактор ОИ") {
    private val controller: TestItemEditorController by inject()
    private val prefillTab: PreFillTab by inject()

    private var testTypesEditor: ComboBox<TestItem> by singleAssign()

    private var fieldsScrollPaneEditor: ScrollPane by singleAssign()
    private var fieldsRootEditor: VBox by singleAssign()

    private val validator = ValidationContext()

    override val root = anchorpane {
        hbox(spacing = 16.0) {
            anchorpaneConstraints {
                leftAnchor = 16.0
                rightAnchor = 16.0
                bottomAnchor = 16.0
                topAnchor = 16.0
            }
            alignment = Pos.CENTER

            vbox(spacing = 16.0) {
                alignment = Pos.CENTER

                label("Выберите тип ОИ для редактирования") {
                    paddingTop = 8
                }.addClass(Styles.headerLabels)

                hbox(spacing = 50.0) {
                    alignmentProperty().set(Pos.CENTER_RIGHT)
                    label("Тип ОИ:") {
                        minWidth = 52.0
                    }

                    testTypesEditor =
                        combobox(property = TestItemEditorModel.selectedTestTypeEditor, values = Properties.testItems) {
                            prefWidth = 400.0

                            onAction = EventHandler {
                                if (selectionModel.selectedItem != null) {
                                    fieldsRootEditor.clear()

                                    val editorFields =
                                        TestItemEditorModel.selectedTestTypeEditor.value.fields.values.toList()

                                    TestItemEditorModel.editorFields = editorFields

                                    if (!editorFields.isNullOrEmpty()) {
                                        validator.validators.clear()
                                        editorFields.groupBy { it.blockName }.keys.sortedBy { it }
                                            .forEach { blockName ->
                                                if (blockName.isNotEmpty()) {
                                                    fieldsRootEditor.add(
                                                        label(blockName) {
                                                            paddingTop = 8
                                                        }.addClass(Styles.headerLabels)
                                                    )
                                                }

                                                editorFields.filter { it.blockName == blockName }
                                                    .forEach { field ->
                                                        fieldsRootEditor.add(
                                                            hbox(spacing = 8.0) {
                                                                alignment = Pos.CENTER_LEFT

                                                                label(field.caption) {
                                                                    paddingLeft = 16
                                                                    prefWidth = 400.0
                                                                    isWrapText = true
                                                                    tooltip = Tooltip(field.caption)
                                                                }

                                                                when (field.typeEnter) {
                                                                    TypeEnterField.TEXT -> {
                                                                        textfield {
                                                                            prefWidth = 200.0
                                                                            Pos.CENTER_LEFT
                                                                            text = field.value

                                                                            filterInput {
                                                                                when (field.typeFormat) {
                                                                                    TypeFormatTestItemField.BOOLEAN -> it.controlNewText.isBoolean()
                                                                                    TypeFormatTestItemField.INT -> it.controlNewText.isInt()
                                                                                    TypeFormatTestItemField.FLOAT -> it.controlNewText.isFloat()
                                                                                    TypeFormatTestItemField.LONG -> it.controlNewText.isLong()
                                                                                    TypeFormatTestItemField.DOUBLE -> it.controlNewText.isDouble()
                                                                                    TypeFormatTestItemField.STRING -> true
                                                                                }
                                                                            }

                                                                            val min =
                                                                                field.minValue.toDoubleOrNull()
                                                                                    ?: Double.MIN_VALUE
                                                                            val max =
                                                                                field.maxValue.toDoubleOrNull()
                                                                                    ?: Double.MAX_VALUE

                                                                            validator.addValidator(this) {
                                                                                when {
                                                                                    (field.typeFormat == TypeFormatTestItemField.STRING) -> null
                                                                                    (it?.toDoubleOrNull() == null) -> {
                                                                                        if (field.isNotVoid) {
                                                                                            error("Обязательное поле")
                                                                                        } else {
                                                                                            null
                                                                                        }
                                                                                    }
                                                                                    ((it.toDouble()) < min || (it.toDouble()) > max) -> {
                                                                                        error("Значение не в диапазоне $min — $max")
                                                                                    }
                                                                                    else -> null
                                                                                }
                                                                            }
                                                                        }.bind(field.valueProperty)
                                                                    }
                                                                    TypeEnterField.COMBO -> {
                                                                        if (!field.permittedTitles.isNullOrEmpty()) {
                                                                            combobox<String> {
                                                                                prefWidth = 200.0
                                                                                Pos.CENTER_LEFT
                                                                                items =
                                                                                    field.permittedTitles.observable()

                                                                                onAction = EventHandler {
                                                                                    if (selectionModel.selectedItem != null) {
                                                                                        field.valueProperty.value =
                                                                                            field.permittedValues[selectionModel.selectedIndex]
                                                                                    } else {
                                                                                        field.valueProperty.value =
                                                                                            field.value
                                                                                    }
                                                                                }

                                                                                if (field.permittedValues.contains(field.value)) {
                                                                                    selectionModel.select(
                                                                                        field.permittedValues.indexOf(
                                                                                            field.value
                                                                                        )
                                                                                    )
                                                                                }

                                                                                if (field.isNotVoid) {
                                                                                    validator.addValidator(
                                                                                        node = this,
                                                                                        property = selectionModel.selectedItemProperty()
                                                                                    ) {
                                                                                        if (it.isNullOrEmpty()) error("Обязательное значение") else null
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                    TypeEnterField.RADIO -> {
                                                                        if (!field.permittedTitles.isNullOrEmpty()) {
                                                                            vbox {
                                                                                alignment = Pos.CENTER_LEFT

                                                                                val group = ToggleGroup()

                                                                                field.permittedTitles.forEach { title ->
                                                                                    radiobutton(title) {
                                                                                        prefWidth = 200.0
                                                                                        paddingBottom = 8

                                                                                        isSelected =
                                                                                            field.permittedValues[field.permittedTitles.indexOf(
                                                                                                title
                                                                                            )] == field.value
                                                                                        toggleGroup = group
                                                                                        action {
                                                                                            field.valueProperty.value =
                                                                                                field.permittedValues[field.permittedTitles.indexOf(
                                                                                                    title
                                                                                                )]
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                    TypeEnterField.CHECK -> {
                                                                        checkbox("") {
                                                                            text = ""
                                                                            prefWidth = 200.0
                                                                            Pos.CENTER_LEFT
                                                                            isSelected = field.valueBitProperty.value
                                                                            action {
                                                                                field.valueBitProperty.value =
                                                                                    isSelected

                                                                                editorFields
                                                                                    .forEach {
                                                                                        it.valueBitProperty.value =
                                                                                            field.valueBitProperty.value
                                                                                    }
                                                                            }
                                                                        }.bind(field.valueBitProperty)
                                                                    }
                                                                }
                                                            }
                                                        )
                                                    }
                                            }
                                        validator.validate(focusFirstError = false)
                                    }
                                } else {
                                    fieldsRootEditor.clear()
                                }
                            }
                        }
                }

                hbox(spacing = 16.0) {
                    alignment = Pos.CENTER_RIGHT

                    button {
                        alignment = Pos.CENTER_RIGHT
                        graphic = MaterialDesignIconView(MaterialDesignIcon.PLUS).apply {
                            glyphSize = config.getProperty("text_size", "14").toDoubleOrNull() ?: 14.0
                        }
                        action {
                            controller.initNewTestItem()
                        }
                    }

                    button {
                        alignment = Pos.CENTER_RIGHT
                        graphic = MaterialDesignIconView(MaterialDesignIcon.MINUS).apply {
                            glyphSize = config.getProperty("text_size", "14").toDoubleOrNull() ?: 14.0
                        }
                        action {
                            controller.deleteSelectedTestItem()
                        }
                    }
                }
            }
            fieldsScrollPaneEditor = scrollpane {
                minWidth = 625.0
                isFitToWidth = true
                anchorpane {
                    fieldsRootEditor = vbox(spacing = 8.0) {
                        anchorpaneConstraints {
                            leftAnchor = 8.0
                            rightAnchor = 8.0
                            topAnchor = 8.0
                            bottomAnchor = 8.0
                        }
                        alignment = Pos.CENTER_LEFT
                    }
                }
            }

            hbox(spacing = 16.0) {
                alignment = Pos.BOTTOM_RIGHT

                anchorpaneConstraints {
                    bottomAnchor = 16.0
                    rightAnchor = 16.0
                    topAnchor = 16.0
                    leftAnchor = 16.0
                }
                button("Сохранить тип ОИ") {
                    padding = insets(2)
                    isDefaultButton = true
                    minWidth = 150.0
                    graphic = MaterialDesignIconView(MaterialDesignIcon.CONTENT_SAVE_ALL).apply {
                        glyphSize = 65
                        fill = c("black")
                    }
                    contentDisplay = ContentDisplay.TOP

                    action {
                        if (TestItemEditorModel.selectedTestTypeEditor.value != null) { // TODO
                            if (!validator.isValid) {
                                validator.validate()
                            } else {
                                controller.saveTestItem()
                            }
                        }
                    }
                }
            }
        }
    }

    fun selectTestItem(newTestItem: TestItem) {
        testTypesEditor.selectionModel.select(newTestItem)
    }

    fun resetComboBox() {
        testTypesEditor.selectionModel.selectFirst()
        testTypesEditor.selectionModel.select(-1)
        prefillTab.resetComboBox()
    }

    fun refreshComboBox() {
        testTypesEditor.items = Properties.testItems
        prefillTab.refreshComboBox()
    }
}
