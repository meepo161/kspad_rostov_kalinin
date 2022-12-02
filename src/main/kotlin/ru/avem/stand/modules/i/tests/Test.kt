package ru.avem.stand.modules.i.tests

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.beans.property.DoubleProperty
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.WritableImage
import javafx.scene.text.Text
import ru.avem.stand.modules.i.Module
import ru.avem.stand.modules.i.views.TestViewModule
import ru.avem.stand.modules.i.views.showTwoWayDialog
import ru.avem.stand.modules.r.communication.model.CM
import ru.avem.stand.modules.r.communication.model.CM.device
import ru.avem.stand.modules.r.communication.model.IDeviceController
import ru.avem.stand.modules.r.storage.Properties
import ru.avem.stand.modules.r.storage.database.saveProtocol
import tornadofx.*
import java.io.File
import java.lang.Thread.sleep
import java.text.SimpleDateFormat
import javax.imageio.ImageIO
import kotlin.concurrent.thread
import kotlin.reflect.KClass

abstract class Test(
    val view: KClass<out TestViewModule>,
    val reportTemplate: String,
    private val isNeedToSaveProtocol: Boolean = true
) : Module() {
    companion object {
        var count = 0
    }

    val order = ++count

    abstract val name: String

    @Volatile
    protected var isRunning = false

    @Volatile
    var isFinished: Boolean = true

    @Volatile
    protected var cause: String = ""
        set(value) {
            if (value.isNotEmpty()) {
                isRunning = false
                if (!field.contains(value)) field += "${if (field != "") "/" else ""}$value"
            } else {
                field = value
            }
        }

    val isSuccess: Boolean
        get() = cause.isEmpty()

    private val checkableDevices: MutableList<IDeviceController> = mutableListOf()

    val reportFields: MutableMap<String, String> = mutableMapOf()

    fun appendOneMessageToLog(tag: LogTag, msg: String) {
        val lastText = find(view).vBoxLog.children.lastOrNull()
        if (lastText != null) {
            if (!((lastText as Text).text).contains(msg)) {
                appendMessageToLog(tag, msg)
            }
        } else {
            appendMessageToLog(tag, msg)
        }
    }

    protected open fun appendMessageToLog(tag: LogTag, msg: String) {
        runLater {
            find(view).vBoxLog.add(
                Text("${SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis())} | $msg").apply {
                    style {
                        fill = tag.c
                    }
                }
            )
        }
    }

    protected fun showOKCancelDialog(text: String) {
        try {
            showTwoWayDialog(
                "Внимание",
                text,
                "ОК",
                "Отмена",
                { },
                { stop() },
                600_000,
                { false },
                find(view).currentWindow!!
            )
        } catch (e: Exception) {
            cause = "Диалог не был обработан в течение 600 с"
        }
    }

    open fun start() {
        thread(isDaemon = true) {
//            snapshot()
            init()
            if (isRunning) {
                startPollDevices()
            }

            if (isRunning) {
                prepare()
            }
            logic()
            result()

            finalize()

            if (isNeedToSaveProtocol) {
                saveProtocol()
            }
        }
    }

    private fun snapshot() {
        runLater {
            ImageIO.write(
                SwingFXUtils.fromFXImage(
                    find(view).modalStage?.scene?.snapshot(
                        WritableImage(
                            find(view).modalStage?.scene?.width?.toInt() ?: 0,
                            find(view).modalStage?.scene?.height?.toInt() ?: 0
                        )
                    ),
                    null
                ),
                "png",
                File("${name.replace("/", "")}.png")
            )
        }
    }

    protected open fun init() {
        initVars()
        if (isRunning) {
            initView()
            initDevices()
        }
    }

    protected open fun initVars() {
        cause = ""
        isRunning = true
        isFinished = false
    }

    protected open fun initView() {
        runLater {
            clearLog()
            find(view).cancelAllTestsButton.isVisible = false

            find(view).stopReloadButton.text = "Стоп"
            find(view).stopReloadButton.graphic = MaterialDesignIconView(MaterialDesignIcon.STOP).apply {
                glyphSize = 65
                fill = c("red")
            }

            find(view).nextTestButton.isVisible = false
        }
    }

    private fun clearLog() {
        find(view).vBoxLog.clear()
    }

    protected open fun initDevices() {
        appendMessageToLog(LogTag.INFO, "Инициализация приборов")

        thread(isDaemon = true) {
            while (isRunning) {
                val list = CM.listOfUnresponsiveDevices(checkableDevices)
                if (list.isNotEmpty()) {
                    val listNameDevices = mutableListOf<String>()
                    list.forEach {
                        when (it.name) {
                            "DD2" -> {
                                listNameDevices.add("DD2 ОВЕН ПР102 - Программируемое реле ")
                            }
                            "PAV41" -> {
                                listNameDevices.add("PAV41 Satec PM130-PLUS-P - Универсальный прибор ")
                            }
                            "UZ91" -> {
                                listNameDevices.add("UZ91 Optimus - Преобразователь частоты ОИ ")
                            }
                            "UZ92" -> {
                                listNameDevices.add("UZ92 HPMont - Преобразователь частоты НМ ")
                            }
                            "PS81" -> {
                                listNameDevices.add("PS81 ТРМ202 - Термометр - (Вход 1 - окр. воздух | Вход 2 - ОИ вал) ")
                            }
                            "PV24" -> {
                                listNameDevices.add("PV24 АВЭМ-3-04 - Прибор ВВ ")
                            }
                            "PR61" -> {
                                listNameDevices.add("PR61 ЦС0202-1 - Меггер ")
                            }
                            "PC71" -> {
                                listNameDevices.add("PC71 ОВЕН ТХ01-224.Щ2.Р.RS - Тахометр ")
                            }
                        }
                    }
                    cause =
                        "следующие приборы не отвечают на запросы: $listNameDevices"
                            .replace("[", "")
                            .replace("]", "")
                }
                sleep(100)
            }
        }
    }

    protected fun addCheckableDevice(id: CM.DeviceID) {
        with(device<IDeviceController>(id)) {
            checkResponsibility()
            checkableDevices.add(this)
        }
    }

    protected fun removeCheckableDevice(id: CM.DeviceID) {
        checkableDevices.remove(device(id))
    }

    open fun startPollDevices() {

    }

    protected open fun prepare() {

    }

    abstract fun logic()

    protected fun sleepWhileRunOld(
        timeSecond: Int,
        progressProperty: DoubleProperty? = null,
        isNeedContinue: () -> Boolean = { true }
    ) {
        var timer = timeSecond * 10
        while (isRunning && timer-- > 0 && isNeedContinue()) {
            if (timer % 10 == 0) {
                runLater {
                    progressProperty?.value = 1.0 - (timer / 10.0) / timeSecond
                }
            }
            sleep(100)
        }
        if (isNeedContinue()) {
            runLater {
                progressProperty?.value = -1.0
            }
        }
    }

    protected fun sleepWhileRun(
        timeSecond: Number,
        progressProperty: DoubleProperty? = null,
        isNeedContinue: () -> Boolean = { true }
    ) {
        val startStamp = System.currentTimeMillis()
        while (isRunning && isNeedContinue()) {
            val progress = (System.currentTimeMillis() - startStamp) / (timeSecond.toDouble() * 1000)
            if (progress < 1.0) {
                runLater {
                    progressProperty?.value = 1.0 - progress
                }
                sleep(100)
            } else {
                if (isNeedContinue()) {
                    runLater {
                        progressProperty?.value = -1.0
                    }
                }
                break
            }
        }
    }

    protected open fun result() {

    }

    protected open fun finalize() {
        finalizeDevices()
        finalizeView()
        finalizeVars()
    }

    open fun finalizeDevices() {
        checkableDevices.clear()
        CM.clearPollingRegisters()
    }

    open fun finalizeView() {
        runLater {
            find(view).cancelAllTestsButton.isVisible = true

            find(view).stopReloadButton.text = "Повторить"
            find(view).stopReloadButton.graphic = MaterialDesignIconView(MaterialDesignIcon.RELOAD).apply {
                glyphSize = 65
                fill = c("black")
            }

            find(view).nextTestButton.isVisible = true
        }
    }

    private fun finalizeVars() {
        isFinished = true
        isRunning = false
    }

    open fun saveProtocol() {
        reportFields["CUSTOMER"] = Properties.standData.customer
        reportFields["CUSTOMER_PLACE"] = Properties.standData.customerPlace
        reportFields["STAND_AIEL"] = Properties.standData.aiel
        reportFields["STAND_SERIAL_NUMBER"] = Properties.standData.serialNumber
        reportFields["MANUFACTURE"] = Properties.standData.manufacture
        reportFields["OPERATOR_POS_1"] = Properties.standData.login1Title
        reportFields["OPERATOR_POS_2"] = Properties.standData.login2Title

        saveProtocol(this)
    }

    fun stopReload() {
        if (isRunning) {
            stop()
        } else if (isFinished) {
            start()
        }
    }

    fun stop() {
        cause = "отменено оператором"
    }

    fun showOKCancelDialog(
        title: String = "Внимание",
        text: String,
        way1Text: String = "ОК",
        way2Text: String = "Отмена"
    ) {
        try {
            showTwoWayDialog(
                title,
                text,
                way1Text,
                way2Text,
                { },
                { },
                1_200_000,
                { false },
                find(view).currentWindow!!
            )
        } catch (e: Exception) {
            cause = "диалог не был обработан в течение 20 минут"
        }
    }

    override fun toString() = "$order. $name"
}
