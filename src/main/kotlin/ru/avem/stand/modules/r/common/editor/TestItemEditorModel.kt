package ru.avem.stand.modules.r.common.editor

import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty
import ru.avem.stand.modules.r.storage.database.entities.TestItem
import ru.avem.stand.modules.r.storage.database.entities.TestItemField

object TestItemEditorModel {
    val selectedTestTypeEditor: Property<TestItem> = SimpleObjectProperty()

    lateinit var editorFields: List<TestItemField>
}
