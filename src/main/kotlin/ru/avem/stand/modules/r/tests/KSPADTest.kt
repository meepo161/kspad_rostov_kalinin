package ru.avem.stand.modules.r.tests

import ru.avem.stand.modules.i.tests.LogTag
import ru.avem.stand.modules.i.tests.Test
import ru.avem.stand.modules.i.views.TestViewModule
import ru.avem.stand.modules.r.common.prefill.PreFillModel
import ru.avem.stand.modules.r.communication.model.CM
import ru.avem.stand.modules.r.communication.model.CM.DeviceID.DD2
import ru.avem.stand.modules.r.communication.model.devices.delta.c2000.C2000
import ru.avem.stand.modules.r.communication.model.devices.optimus.Optimus
import ru.avem.stand.modules.r.communication.model.devices.owen.pr.PR
import ru.avem.stand.modules.r.communication.model.devices.owen.pr.PRModel
import tornadofx.*
import tornadofx.controlsfx.warningNotification
import java.lang.Thread.sleep
import kotlin.experimental.and
import kotlin.reflect.KClass

abstract class KSPADTest(
    view: KClass<out TestViewModule>,
    reportTemplate: String,
    isNeedToSaveProtocol: Boolean = true
) : Test(view, reportTemplate, isNeedToSaveProtocol) {
    @Volatile
    protected var isStartPressed: Boolean = false

    protected var isFirstPlatform: Boolean = true

    override fun initVars() {
        super.initVars()

        testModel.testItemData.P.value = PreFillModel.testTypeProp.value.fields["P"]?.value
        testModel.testItemData.U.value = PreFillModel.testTypeProp.value.fields["U"]?.value
        testModel.testItemData.I.value = PreFillModel.testTypeProp.value.fields["I"]?.value
        testModel.testItemData.cos.value = PreFillModel.testTypeProp.value.fields["COS"]?.value
        testModel.testItemData.RPM.value = PreFillModel.testTypeProp.value.fields["RPM"]?.value
        testModel.testItemData.F.value = PreFillModel.testTypeProp.value.fields["F"]?.value
        testModel.testItemData.efficiency.value = PreFillModel.testTypeProp.value.fields["EFFICIENCY"]?.value

        testModel.protections.resetAll()
    }

    override fun initDevices() {
        super.initDevices()

        addCheckableDevice(DD2)
        CM.addWritingRegister(
            DD2,
            PRModel.CMD,
            1.toShort()
        )
        CM.device<PR>(DD2).initWithoutProtections()
    }

    fun startPollControlUnit() {
        if (isRunning) {
            CM.startPoll(DD2, PRModel.STATE) {}
            CM.startPoll(DD2, PRModel.DI_01_16_TRIG) { value ->
                val isStopPressed = value.toShort() and 0b1 != 0.toShort() // 1
                if (isStopPressed) stop()

                isStartPressed = value.toShort() and 0b10 != 0.toShort() // 2
            }
            CM.startPoll(DD2, PRModel.DI_01_16_TRIG_INV) { value ->
                if (value.toShort() and 0b100 != 0.toShort()) { // 3
                    cause = "сработала токовая защита ВИУ"
                    testModel.protections.overCurrentVIU.set()
                }
                if (value.toShort() and 0b10000 != 0.toShort()) { // 5
                    cause = "сработала токовая защита ОИ"
                    testModel.protections.overCurrentOI.set()
                }
                if (value.toShort() and 0b1000000 != 0.toShort()) { // 7
                    cause = "открыты двери ШСО"
                    testModel.protections.doorsSHSO.set()
                }
                if (value.toShort() and 0b10000000 != 0.toShort()) { // 8
//                    cause = "открыты двери зоны"
                    testModel.protections.doorsZone.unknown()
                }
            }
        }
    }

    override fun prepare() {
        if (isRunning) {
           initPushButtonPost()
        }
        if (isRunning) {
            appendMessageToLog(LogTag.INFO, "Сигнализация")
            CM.device<PR>(DD2).signalize()
        }
    }

    private fun initPushButtonPost() {
        isStartPressed = false
        sleep(2000)
        if (isRunning && !isStartPressed) {
            appendMessageToLog(LogTag.WARN, "Нажмите кнопку ПУСК")
            runLater {
                warningNotification("Внимание", "Нажмите кнопку ПУСК")
            }
        }

        var timeout = 300 // 30c
        while (isRunning && !isStartPressed && timeout-- > 0) {
            sleep(100)
        }

        if (isRunning && !isStartPressed) {
            cause = "не была нажата кнопка ПУСК в течение 30 секунд"
        } else {
            CM.device<PR>(DD2).resetTriggers()
            sleep(1000)
        }
    }

    protected fun waitUntilFIToLoad() {
        appendMessageToLog(LogTag.INFO, "Загрузка ЧП...")
        sleepWhileRun(12)
    }

    protected fun startFI(fi: C2000, accTime: Int = 10) {
        appendMessageToLog(LogTag.INFO, "Запуск ЧП...")
        fi.startObject()
        waitUntilFIToRun(accTime)
    }

    protected fun startOptimus(fi: Optimus, accTime: Int = 10) {
        appendMessageToLog(LogTag.INFO, "Запуск ЧП...")
        fi.startObject()
        waitUntilFIToRun(accTime)
    }

    protected fun waitUntilFIToRun(accTime: Int = 15) {
        appendMessageToLog(LogTag.INFO, "Ожидание разгона...")
        sleepWhileRun(accTime)
    }

    protected fun stopFI(fi: C2000, stopTime: Int = 7) {
        appendMessageToLog(LogTag.INFO, "Останов...")
        fi.stopObject()
        waitUntilFIToStop(stopTime)
    }

    protected fun stopOptimus(fi: Optimus, stopTime: Int = 7) {
        appendMessageToLog(LogTag.INFO, "Останов...")
        fi.stopObject()
    }

    private fun waitUntilFIToStop(stopTime: Int = 7) {
        appendMessageToLog(LogTag.INFO, "Ожидание останова...")
        sleepWhileRun(stopTime)
    }

    override fun finalizeDevices() {
        CM.device<PR>(DD2).offAllKMs()
        super.finalizeDevices()
    }
}
