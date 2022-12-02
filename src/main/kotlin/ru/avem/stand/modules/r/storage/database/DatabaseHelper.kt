package ru.avem.stand.modules.r.storage.database

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import ru.avem.stand.modules.i.tests.Test
import ru.avem.stand.modules.r.common.authorization.AuthorizationModel
import ru.avem.stand.modules.r.common.prefill.PreFillModel.serialNumberProp
import ru.avem.stand.modules.r.common.prefill.PreFillModel.testTypeProp
import ru.avem.stand.modules.r.storage.database.entities.*
import ru.avem.stand.modules.r.storage.testitemfields.TestItemFieldScheme
import ru.avem.stand.modules.r.storage.testitemfields.TypeEnterField
import ru.avem.stand.modules.r.storage.testitemfields.TypeFormatTestItemField
import java.sql.Connection
import java.text.SimpleDateFormat

fun validateDB() {
    Database.connect("jdbc:sqlite:data.db", "org.sqlite.JDBC")
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

    transaction {
        SchemaUtils.create(TestItems, TestItemFields, Reports, ReportFields)

        if (TestItem.all().count() == 0) {
            TestItem.new {
                name = "АД 5АИ (30 кВт)"
                level = 1
            }.also { ti ->
                createAsyncEngineTemplateBig().forEach {
                    TestItemField.new {
                        testItem = ti

                        key = it.key
                        title = it.title

                        typeEnterRaw = it.typeEnterRaw
                        typeFormatRaw = it.typeFormatRaw

                        minValue = it.minValue
                        value = it.value
                        maxValue = it.maxValue
                        unit = it.unit

                        permittedValuesString = it.permittedValuesString
                        permittedTitlesString = it.permittedTitlesString

                        blockName = it.blockName

                        isNotVoid = it.isNotVoid
                    }
                }
            }
            TestItem.new {
                name = "АД 5АИ 132S4 У2 (7.6 кВт)"
                level = 1
            }.also { ti ->
                createAsyncEngineTemplateSmall().forEach {
                    TestItemField.new {
                        testItem = ti

                        key = it.key
                        title = it.title

                        typeEnterRaw = it.typeEnterRaw
                        typeFormatRaw = it.typeFormatRaw

                        minValue = it.minValue
                        value = it.value
                        maxValue = it.maxValue
                        unit = it.unit

                        permittedValuesString = it.permittedValuesString
                        permittedTitlesString = it.permittedTitlesString

                        blockName = it.blockName

                        isNotVoid = it.isNotVoid
                    }
                }
            }
        }
    }
}

private fun createAsyncEngineTemplateSmall() = listOf(
    TestItemFieldScheme(
        key = "SCHEME",
        title = "Схема соединения обмоток",
        typeEnterRaw = TypeEnterField.RADIO.toString(),
        permittedValuesString = "△;λ;",
        permittedTitlesString = "Треугольник;Звезда;",
        value = "λ",
        blockName = "Номинальные параметры"
    ),

    TestItemFieldScheme(
        key = "U",
        title = "Напряжение",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "0",
        value = "380",
        maxValue = "400",
        unit = "В",
        blockName = "Номинальные параметры"
    ),
    TestItemFieldScheme(
        key = "I",
        title = "Ток",
        typeFormatRaw = TypeFormatTestItemField.FLOAT.toString(),
        minValue = "0",
        value = "15.6",
        maxValue = "130",
        unit = "А",
        blockName = "Номинальные параметры"
    ),
    TestItemFieldScheme(
        key = "RPM",
        title = "Частота вращения",
        typeFormatRaw = TypeFormatTestItemField.FLOAT.toString(),
        minValue = "0",
        value = "1455",
        maxValue = "3000",
        unit = "об/мин",
        blockName = "Номинальные параметры"
    ),
    TestItemFieldScheme(
        key = "F",
        title = "Частота питающей сети",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "50",
        value = "50",
        maxValue = "50",
        unit = "Гц",
        blockName = "Номинальные параметры"
    ),
    TestItemFieldScheme(
        key = "COS",
        title = "cos φ",
        typeFormatRaw = TypeFormatTestItemField.FLOAT.toString(),
        minValue = "0.1",
        value = "0.83",
        maxValue = "1",
        unit = "о.е.",
        blockName = "Номинальные параметры"
    ),
    TestItemFieldScheme(
        key = "EFFICIENCY",
        title = "КПД",
        typeFormatRaw = TypeFormatTestItemField.FLOAT.toString(),
        minValue = "0",
        value = "88",
        maxValue = "99",
        unit = "%",
        blockName = "Номинальные параметры"
    ),
    TestItemFieldScheme(
        key = "P",
        title = "Мощность",
        typeFormatRaw = TypeFormatTestItemField.FLOAT.toString(),
        minValue = "0.1",
        value = "7.6",
        maxValue = "55",
        unit = "кВт",
        blockName = "Номинальные параметры"
    ),

    TestItemFieldScheme(
        key = "IDLE_TIME",
        title = "Время испытания ХХ",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "0",
        value = "60",
        unit = "с",
        blockName = "Параметры испытания"
    ),

    TestItemFieldScheme(
        key = "RUNNING_TIME",
        title = "Время обкатки",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "0",
        value = "600",
        unit = "мин",
        blockName = "Параметры испытания"
    ),

    TestItemFieldScheme(
        key = "U_MGR",
        title = "Напряжение испытания мегаомметром",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "100",
        value = "1000",
        maxValue = "2500",
        unit = "В",
        blockName = "Параметры испытания"
    ),

    TestItemFieldScheme(
        key = "U_HV",
        title = "Напряжение ВИУ",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "0",
        value = "1000",
        maxValue = "3000",
        unit = "В",
        blockName = "Параметры испытания"
    ),
    TestItemFieldScheme(
        key = "I_HV",
        title = "Допустимый ток утечки ВИУ",
        typeFormatRaw = TypeFormatTestItemField.FLOAT.toString(),
        minValue = "0",
        value = "0.5",
        maxValue = "1",
        unit = "А",
        blockName = "Параметры испытания"
    ),
    TestItemFieldScheme(
        key = "T_HV",
        title = "Время испытания ВИУ",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "0",
        value = "60",
        maxValue = "3600",
        unit = "с",
        blockName = "Параметры испытания"
    ),
    TestItemFieldScheme(
        key = "RT_K_IKAS",
        title = "Температурный коэффициент сопротивления для расчета R приведённого",
        typeFormatRaw = TypeFormatTestItemField.DOUBLE.toString(),
        value = "0.00393",
        unit = "1/°C",
        blockName = "Ожидаемые параметры"
    ),

    TestItemFieldScheme(
        key = "R_MGR",
        title = "Сопротивление изоляции",
        typeFormatRaw = TypeFormatTestItemField.DOUBLE.toString(),
        minValue = "0",
        value = "50",
        unit = "МОм",
        blockName = "Ожидаемые параметры"
    ),

    TestItemFieldScheme(
        key = "U_KZ",
        title = "Напряжение КЗ",
        typeFormatRaw = TypeFormatTestItemField.FLOAT.toString(),
        minValue = "0",
        value = "100",
        maxValue = "200",
        unit = "В",
        blockName = "Параметры испытания"
    ),

    TestItemFieldScheme(
        key = "MVZ_TIME",
        title = "Время испытания МВЗ",
        typeFormatRaw = TypeFormatTestItemField.FLOAT.toString(),
        minValue = "0",
        value = "15",
        maxValue = "60",
        unit = "с",
        blockName = "Параметры испытания"
    ),
    TestItemFieldScheme(
        key = "MVZ_TOLERANCE",
        title = "Допустимое повышение тока при МВЗ",
        typeFormatRaw = TypeFormatTestItemField.FLOAT.toString(),
        minValue = "0",
        value = "2",
        maxValue = "5",
        unit = "%",
        blockName = "Параметры испытания"
    ),

    TestItemFieldScheme(
        key = "LOAD_TIME",
        title = "Время испытания НАГР",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "0",
        value = "60",
        unit = "с",
        blockName = "Параметры испытания"
    ),

    TestItemFieldScheme(
        key = "PERCENT_ROD",
        title = "Процент отличия тока стержней",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "0",
        value = "10",
        unit = "%",
        blockName = "Параметры испытания"
    ),
)

private fun createAsyncEngineTemplateBig() = listOf(
    TestItemFieldScheme(
        key = "SCHEME",
        title = "Схема соединения обмоток",
        typeEnterRaw = TypeEnterField.RADIO.toString(),
        permittedValuesString = "△;λ;",
        permittedTitlesString = "Треугольник;Звезда;",
        value = "λ",
        blockName = "Номинальные параметры"
    ),

    TestItemFieldScheme(
        key = "U",
        title = "Напряжение",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "0",
        value = "380",
        maxValue = "400",
        unit = "В",
        blockName = "Номинальные параметры"
    ),
    TestItemFieldScheme(
        key = "I",
        title = "Ток",
        typeFormatRaw = TypeFormatTestItemField.FLOAT.toString(),
        minValue = "0",
        value = "63",
        maxValue = "130",
        unit = "А",
        blockName = "Номинальные параметры"
    ),
    TestItemFieldScheme(
        key = "RPM",
        title = "Частота вращения",
        typeFormatRaw = TypeFormatTestItemField.FLOAT.toString(),
        minValue = "0",
        value = "735",
        maxValue = "3000",
        unit = "об/мин",
        blockName = "Номинальные параметры"
    ),
    TestItemFieldScheme(
        key = "F",
        title = "Частота питающей сети",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "50",
        value = "50",
        maxValue = "50",
        unit = "Гц",
        blockName = "Номинальные параметры"
    ),
    TestItemFieldScheme(
        key = "COS",
        title = "cos φ",
        typeFormatRaw = TypeFormatTestItemField.FLOAT.toString(),
        minValue = "0.1",
        value = "0.8",
        maxValue = "1",
        unit = "о.е.",
        blockName = "Номинальные параметры"
    ),
    TestItemFieldScheme(
        key = "EFFICIENCY",
        title = "КПД",
        typeFormatRaw = TypeFormatTestItemField.FLOAT.toString(),
        minValue = "0",
        value = "90",
        maxValue = "99",
        unit = "%",
        blockName = "Номинальные параметры"
    ),
    TestItemFieldScheme(
        key = "P",
        title = "Мощность",
        typeFormatRaw = TypeFormatTestItemField.FLOAT.toString(),
        minValue = "0.1",
        value = "30",
        maxValue = "55",
        unit = "кВт",
        blockName = "Номинальные параметры"
    ),

    TestItemFieldScheme(
        key = "IDLE_TIME",
        title = "Время испытания ХХ",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "0",
        value = "60",
        unit = "с",
        blockName = "Параметры испытания"
    ),
    TestItemFieldScheme(
        key = "RUNNING_TIME",
        title = "Время обкатки",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "0",
        value = "600",
        unit = "мин",
        blockName = "Параметры испытания"
    ),

    TestItemFieldScheme(
        key = "U_MGR",
        title = "Напряжение испытания мегаомметром",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "100",
        value = "1000",
        maxValue = "2500",
        unit = "В",
        blockName = "Параметры испытания"
    ),

    TestItemFieldScheme(
        key = "U_HV",
        title = "Напряжение ВИУ",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "0",
        value = "1000",
        maxValue = "3000",
        unit = "В",
        blockName = "Параметры испытания"
    ),
    TestItemFieldScheme(
        key = "I_HV",
        title = "Допустимый ток утечки ВИУ",
        typeFormatRaw = TypeFormatTestItemField.FLOAT.toString(),
        minValue = "0",
        value = "0.5",
        maxValue = "1",
        unit = "А",
        blockName = "Параметры испытания"
    ),
    TestItemFieldScheme(
        key = "T_HV",
        title = "Время испытания ВИУ",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "0",
        value = "60",
        maxValue = "3600",
        unit = "с",
        blockName = "Параметры испытания"
    ),

    TestItemFieldScheme(
        key = "RT_K_IKAS",
        title = "Температурный коэффициент сопротивления для расчета R приведённого",
        typeFormatRaw = TypeFormatTestItemField.DOUBLE.toString(),
        value = "0.00393",
        unit = "1/°C",
        blockName = "Ожидаемые параметры"
    ),

    TestItemFieldScheme(
        key = "R_MGR",
        title = "Сопротивление изоляции",
        typeFormatRaw = TypeFormatTestItemField.DOUBLE.toString(),
        minValue = "0",
        value = "50",
        unit = "МОм",
        blockName = "Ожидаемые параметры"
    ),

    TestItemFieldScheme(
        key = "U_KZ",
        title = "Напряжение КЗ",
        typeFormatRaw = TypeFormatTestItemField.FLOAT.toString(),
        minValue = "0",
        value = "100",
        maxValue = "200",
        unit = "В",
        blockName = "Параметры испытания"
    ),

    TestItemFieldScheme(
        key = "MVZ_TIME",
        title = "Время испытания МВЗ",
        typeFormatRaw = TypeFormatTestItemField.FLOAT.toString(),
        minValue = "0",
        value = "15",
        maxValue = "60",
        unit = "с",
        blockName = "Параметры испытания"
    ),
    TestItemFieldScheme(
        key = "MVZ_TOLERANCE",
        title = "Допустимое повышение тока при МВЗ",
        typeFormatRaw = TypeFormatTestItemField.FLOAT.toString(),
        minValue = "0",
        value = "2",
        maxValue = "5",
        unit = "%",
        blockName = "Параметры испытания"
    ),

    TestItemFieldScheme(
        key = "LOAD_TIME",
        title = "Время испытания НАГР",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "0",
        value = "60",
        unit = "с",
        blockName = "Параметры испытания"
    ),
    TestItemFieldScheme(
        key = "PERCENT_ROD",
        title = "Процент отличия тока стержней",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "0",
        value = "10",
        unit = "%",
        blockName = "Параметры испытания"
    ),
)

fun saveProtocol(test: Test) = transaction {
    Report.new {
        serialNumber = serialNumberProp.value
        testType = testTypeProp.value.toString()
        this.test = test.name

        user1Name = AuthorizationModel.user0.fio
        user2Name = AuthorizationModel.user1.fio

        val millis = System.currentTimeMillis()
        date = SimpleDateFormat("dd.MM.yyyy").format(millis)
        time = SimpleDateFormat("HH:mm").format(millis)

        isSuccess = test.isSuccess.toString()

        template = test.reportTemplate
    }.also {
        ReportField.new {
            protocol = it
            key = "\$PROTOCOL_NUMBER\$"
            value = it.id.toString()
        }
        ReportField.new {
            protocol = it
            key = "\$TEST_TYPE\$"
            value = it.testType
        }
        ReportField.new {
            protocol = it
            key = "\$SERIAL_NUMBER\$"
            value = it.serialNumber
        }
        ReportField.new {
            protocol = it
            key = "\$TEST_NAME\$"
            value = it.test
        }
        test.reportFields.forEach { register ->
            ReportField.new {
                protocol = it
                key = "\$${register.key}\$"
                value = register.value
            }
        }
        ReportField.new {
            protocol = it
            key = "\$OPERATOR_NAME_1\$"
            value = it.user1Name
        }
        ReportField.new {
            protocol = it
            key = "\$OPERATOR_NAME_2\$"
            value = if (it.user2Name != "admin") it.user2Name else ""
        }
        ReportField.new {
            protocol = it
            key = "\$DATE\$"
            value = it.date
        }
        ReportField.new {
            protocol = it
            key = "\$TIME\$"
            value = it.time
        }
    }
}

fun getAllProtocols() = transaction { Report.all().toList() }

fun deleteProtocolByEntity(p: Report) {
    transaction {
        p.fields.forEach {
            it.delete()
        }
        p.delete()
    }
}

fun deleteProtocolById(id: EntityID<Int>) {
    transaction {
        Reports.deleteWhere {
            Reports.id eq id
        }
        ReportFields.deleteWhere {
            ReportFields.protocol eq id
        }
    }
}

fun deleteAllData() {
    transaction {
        Reports.deleteAll()
        ReportFields.deleteAll()
    }
}

fun createTestItem(name: String) = transaction {
    TestItem.new {
        this.name = name
        level = 1
    }.also { ti ->
        createAsyncEngineScheme().forEach {
            TestItemField.new {
                testItem = ti

                key = it.key
                title = it.title

                typeEnterRaw = it.typeEnterRaw
                typeFormatRaw = it.typeFormatRaw

                minValue = it.minValue
                value = it.value
                maxValue = it.maxValue
                unit = it.unit

                permittedValuesString = it.permittedValuesString
                permittedTitlesString = it.permittedTitlesString

                blockName = it.blockName

                isNotVoid = it.isNotVoid
            }
        }
    }
}

fun createAsyncEngineScheme() = listOf(
    TestItemFieldScheme(
        key = "SCHEME",
        title = "Схема соединения обмоток",
        typeEnterRaw = TypeEnterField.RADIO.toString(),
        permittedValuesString = "△;λ;",
        permittedTitlesString = "Треугольник;Звезда;",
        value = "λ",
        blockName = "Номинальные параметры"
    ),

    TestItemFieldScheme(
        key = "U",
        title = "Напряжение",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "0",
        value = "380",
        maxValue = "400",
        unit = "В",
        blockName = "Номинальные параметры"
    ),
    TestItemFieldScheme(
        key = "I",
        title = "Ток",
        typeFormatRaw = TypeFormatTestItemField.FLOAT.toString(),
        minValue = "0",
        value = "",
        maxValue = "130",
        unit = "А",
        blockName = "Номинальные параметры"
    ),
    TestItemFieldScheme(
        key = "RPM",
        title = "Частота вращения",
        typeFormatRaw = TypeFormatTestItemField.FLOAT.toString(),
        minValue = "0",
        value = "1499",
        maxValue = "3000",
        unit = "об/мин",
        blockName = "Номинальные параметры"
    ),
    TestItemFieldScheme(
        key = "F",
        title = "Частота питающей сети",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "50",
        value = "50",
        maxValue = "50",
        unit = "Гц",
        blockName = "Номинальные параметры"
    ),
    TestItemFieldScheme(
        key = "COS",
        title = "cos φ",
        typeFormatRaw = TypeFormatTestItemField.FLOAT.toString(),
        minValue = "0.1",
        value = "",
        maxValue = "1",
        unit = "о.е.",
        blockName = "Номинальные параметры"
    ),
    TestItemFieldScheme(
        key = "EFFICIENCY",
        title = "КПД",
        typeFormatRaw = TypeFormatTestItemField.FLOAT.toString(),
        minValue = "0",
        value = "",
        maxValue = "99",
        unit = "%",
        blockName = "Номинальные параметры"
    ),
    TestItemFieldScheme(
        key = "P",
        title = "Мощность",
        typeFormatRaw = TypeFormatTestItemField.FLOAT.toString(),
        minValue = "0.1",
        value = "",
        maxValue = "55",
        unit = "кВт",
        blockName = "Номинальные параметры"
    ),

    TestItemFieldScheme(
        key = "IDLE_TIME",
        title = "Время испытания ХХ",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "0",
        value = "",
        unit = "с",
        blockName = "Параметры испытания"
    ),
    TestItemFieldScheme(
        key = "RUNNING_TIME",
        title = "Время обкатки",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "0",
        value = "600",
        unit = "мин",
        blockName = "Параметры испытания"
    ),

    TestItemFieldScheme(
        key = "U_MGR",
        title = "Напряжение испытания мегаомметром",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "100",
        value = "1000",
        maxValue = "2500",
        unit = "В",
        blockName = "Параметры испытания"
    ),

    TestItemFieldScheme(
        key = "U_HV",
        title = "Напряжение ВИУ",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "0",
        value = "1000",
        maxValue = "3000",
        unit = "В",
        blockName = "Параметры испытания"
    ),
    TestItemFieldScheme(
        key = "I_HV",
        title = "Допустимый ток утечки ВИУ",
        typeFormatRaw = TypeFormatTestItemField.FLOAT.toString(),
        minValue = "0",
        value = "0.5",
        maxValue = "1",
        unit = "А",
        blockName = "Параметры испытания"
    ),
    TestItemFieldScheme(
        key = "T_HV",
        title = "Время испытания ВИУ",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "0",
        value = "60",
        maxValue = "3600",
        unit = "с",
        blockName = "Параметры испытания"
    ),
    TestItemFieldScheme(
        key = "RT_K_IKAS",
        title = "Температурный коэффициент сопротивления для расчета R приведённого",
        typeFormatRaw = TypeFormatTestItemField.DOUBLE.toString(),
        value = "0.00393",
        unit = "1/°C",
        blockName = "Ожидаемые параметры"
    ),

    TestItemFieldScheme(
        key = "R_MGR",
        title = "Сопротивление изоляции",
        typeFormatRaw = TypeFormatTestItemField.DOUBLE.toString(),
        minValue = "0",
        value = "50",
        unit = "МОм",
        blockName = "Ожидаемые параметры"
    ),

    TestItemFieldScheme(
        key = "U_KZ",
        title = "Напряжение КЗ",
        typeFormatRaw = TypeFormatTestItemField.FLOAT.toString(),
        minValue = "0",
        value = "100",
        maxValue = "200",
        unit = "В",
        blockName = "Параметры испытания"
    ),

    TestItemFieldScheme(
        key = "MVZ_TIME",
        title = "Время испытания МВЗ",
        typeFormatRaw = TypeFormatTestItemField.FLOAT.toString(),
        minValue = "0",
        value = "15",
        maxValue = "60",
        unit = "с",
        blockName = "Параметры испытания"
    ),

    TestItemFieldScheme(
        key = "MVZ_TOLERANCE",
        title = "Допустимое повышение тока при МВЗ",
        typeFormatRaw = TypeFormatTestItemField.FLOAT.toString(),
        minValue = "0",
        value = "2",
        maxValue = "5",
        unit = "%",
        blockName = "Параметры испытания"
    ),

    TestItemFieldScheme(
        key = "LOAD_TIME",
        title = "Время испытания НАГР",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "0",
        value = "60",
        unit = "с",
        blockName = "Параметры испытания"
    ),

    TestItemFieldScheme(
        key = "PERCENT_ROD",
        title = "Процент отличия тока стержней",
        typeFormatRaw = TypeFormatTestItemField.INT.toString(),
        minValue = "0",
        value = "10",
        unit = "%",
        blockName = "Параметры испытания"
    ),
)

fun deleteTestItemByEntity(ti: TestItem) {
    transaction {
        ti.fieldsIterable.forEach {
            it.delete()
        }
        ti.delete()
    }
}

fun getAllTestItems() = transaction { TestItem.all().toList() }
