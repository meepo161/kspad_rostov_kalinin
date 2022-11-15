package ru.avem.stand.modules.r.common.editor

import javafx.geometry.Pos
import javafx.scene.control.TextInputDialog
import javafx.stage.Stage
import javafx.util.Duration
import ru.avem.stand.modules.i.views.Styles
import ru.avem.stand.modules.r.storage.database.createTestItem
import ru.avem.stand.modules.r.storage.database.deleteTestItemByEntity
import ru.avem.stand.modules.r.storage.database.entities.TestItemField
import tornadofx.*
import tornadofx.controlsfx.infoNotification
import tornadofx.controlsfx.warningNotification

class TestItemEditorController : Controller() {
    val view: TestItemEditorTab by inject()

    fun initNewTestItem() {
        textInput(
            "Создание нового типа ОИ",
            "Введите наименование нового типа ОИ:",
            "Наименование: ",
            "новый_тип_ои"
        ) { value ->
            infoNotification(
                "Внимание",
                "Для завершения создания нового типа ОИ \"$value\" заполните поля и нажмите кнопку \"Сохранить тип ОИ\"",
                Pos.BOTTOM_CENTER,
                hideAfter = Duration.millis(3000.0)
            )
            val emptyTestItem = createEmptyTestItem(value)
            view.refreshComboBox()
            view.selectTestItem(emptyTestItem)
        }
    }

    private fun textInput(
        _title: String,
        header: String,
        content: String = "",
        default: String = "",
        callback: (String) -> Unit
    ) = TextInputDialog(default).apply {
        title = _title
        headerText = header
        contentText = content
    }.also {
        (it.dialogPane.scene.window as Stage).icons.add(primaryStage.icons[0])
        it.dialogPane.style {
            fontSize = Styles.titleFontSize.px
        }
        it.showAndWait().ifPresent { value -> callback(value) }
    }

    private fun createEmptyTestItem(name: String) = createTestItem(name)

    fun saveTestItem() {
        TestItemEditorModel.editorFields.forEach(TestItemField::updateDefaultValue)

        view.resetComboBox()
        infoNotification(
            "Тип ОИ успешно сохранён",
            "",
            Pos.BOTTOM_CENTER,
            hideAfter = Duration.millis(1000.0)
        )
    }

    fun deleteSelectedTestItem() {
        if (TestItemEditorModel.selectedTestTypeEditor.value != null) {
            deleteTestItem()
            infoNotification(
                "Тип ОИ успешно удалён",
                "",
                Pos.BOTTOM_CENTER,
                hideAfter = Duration.millis(1000.0)
            )
        } else {
            warningNotification(
                "Не выбран тип ОИ",
                "Выберите тип и повторите",
                Pos.CENTER,
                owner = view.currentWindow
            )
            return
        }
    }

    private fun deleteTestItem() {
        deleteTestItemByEntity(TestItemEditorModel.selectedTestTypeEditor.value)
        view.refreshComboBox()
        view.resetComboBox()
    }
}
