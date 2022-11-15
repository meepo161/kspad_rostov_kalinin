package ru.avem.stand.modules.r.storage.testitemfields

data class TestItemFieldScheme(
    var key: String,
    var title: String = "",

    var typeEnterRaw: String = TypeEnterField.TEXT.toString(),
    var typeFormatRaw: String = TypeFormatTestItemField.STRING.toString(),

    var minValue: String = "",
    var value: String = "",
    var maxValue: String = "",
    var unit: String = "",

    var permittedValuesString: String = "",
    var permittedTitlesString: String = "",

    var blockName: String = "Дополнительные",

    var isNotVoid: Boolean = true
)
