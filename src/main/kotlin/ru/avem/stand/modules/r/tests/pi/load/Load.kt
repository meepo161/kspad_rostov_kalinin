package ru.avem.stand.modules.r.tests.pi.load

import ru.avem.stand.modules.i.tests.LogTag
import ru.avem.stand.modules.i.views.showTwoWayDialog
import ru.avem.stand.modules.r.common.prefill.PreFillModel
import ru.avem.stand.modules.r.communication.model.CM
import ru.avem.stand.modules.r.communication.model.devices.hpmont.HPMont
import ru.avem.stand.modules.r.communication.model.devices.hpmont.HPMontModel
import ru.avem.stand.modules.r.communication.model.devices.optimus.Optimus
import ru.avem.stand.modules.r.communication.model.devices.owen.pr.PR
import ru.avem.stand.modules.r.communication.model.devices.owen.pr.PRModel
import ru.avem.stand.modules.r.communication.model.devices.owen.th01.TH01Model
import ru.avem.stand.modules.r.communication.model.devices.owen.trm202.TRM202Model
import ru.avem.stand.modules.r.communication.model.devices.satec.pm130.PM130Model
import ru.avem.stand.modules.r.tests.AmperageStage
import ru.avem.stand.modules.r.tests.KSPADTest
import ru.avem.stand.modules.r.tests.calcSyncRPM
import ru.avem.stand.modules.r.tests.calcZs
import ru.avem.stand.utils.autoformat
import ru.avem.stand.utils.toDoubleOrDefault
import tornadofx.find
import tornadofx.runLater
import java.lang.Thread.sleep
import kotlin.collections.set
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.pow

class Load : KSPADTest(view = LoadView::class, reportTemplate = "load.xlsx") {
    override val name = "Испытание под нагрузкой"

    override val testModel = LoadModel

    var frequency = 0.0

    override fun initVars() {
        super.initVars()

        testModel.specifiedU = PreFillModel.testTypeProp.value.fields["U"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedI = PreFillModel.testTypeProp.value.fields["I"]?.value.toDoubleOrDefault(0.0)

        testModel.specifiedCos = PreFillModel.testTypeProp.value.fields["COS"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedEfficiency =
            PreFillModel.testTypeProp.value.fields["EFFICIENCY"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedP = PreFillModel.testTypeProp.value.fields["P"]?.value.toDoubleOrDefault(0.0)
        isFirstPlatform = testModel.specifiedP >= 8

        testModel.specifiedRPM = PreFillModel.testTypeProp.value.fields["RPM"]?.value.toDoubleOrDefault(0.0)

        testModel.specifiedF = PreFillModel.testTypeProp.value.fields["F"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedScheme = PreFillModel.testTypeProp.value.fields["SCHEME"]?.value ?: "λ"

        testModel.syncRPM = calcSyncRPM(testModel.specifiedF.toInt(), testModel.specifiedRPM.toInt())

        testModel.specifiedTestTime =
            PreFillModel.testTypeProp.value.fields["LOAD_TIME"]?.value.toDoubleOrDefault(0.0)

        testModel.isContinueAgree = false

        testModel.maxTemp = -999.0

        testModel.isNeedAbsTorque = false
        testModel.isStaticErrorTorqueSet = false
        testModel.staticErrorTorque = 0.0
    }

    override fun initView() {
        super.initView()

        runLater {
            testModel.progressProperty.value = -1.0

            testModel.measuredData.U.value = ""
            testModel.measuredData.UAB.value = ""
            testModel.measuredData.UBC.value = ""
            testModel.measuredData.UCA.value = ""

            testModel.measuredData.I.value = ""
            testModel.measuredData.IA.value = ""
            testModel.measuredData.IB.value = ""
            testModel.measuredData.IC.value = ""

            testModel.measuredData.cos.value = ""
            testModel.measuredData.P1.value = ""
            testModel.measuredData.F.value = ""

            testModel.measuredData.v1x.value = ""
//            testModel.measuredData.v1y.value = ""
//            testModel.measuredData.v1z.value = ""

            testModel.measuredData.v2x.value = ""
//            testModel.measuredData.v2y.value = ""
//            testModel.measuredData.v2z.value = ""

            testModel.measuredData.RPM.value = ""
            testModel.measuredData.tempAmb.value = ""
            testModel.measuredData.tempTI.value = ""
            testModel.measuredData.T0After.value = ""

            testModel.measuredData.efficiency.value = ""
            testModel.measuredData.sk.value = ""

            testModel.measuredData.time.value = ""
            testModel.measuredData.result.value = ""
        }
    }

    override fun startPollDevices() {
        super.startPollDevices()

        startPollControlUnit()

        if (isRunning) {
            with(CM.DeviceID.PAV41) {
                addCheckableDevice(this)
                CM.startPoll(this, PM130Model.U_AB_REGISTER) { value ->
                    testModel.measuredData.UAB.value = value.toDouble().autoformat()
                    testModel.measuredU =
                        (testModel.measuredData.UAB.value.toDoubleOrDefault(0.0) + testModel.measuredData.UBC.value.toDoubleOrDefault(
                            0.0
                        ) + testModel.measuredData.UCA.value.toDoubleOrDefault(0.0)) / 3.0
                    testModel.measuredData.U.value = testModel.measuredU.autoformat()
                }
                CM.startPoll(this, PM130Model.U_BC_REGISTER) { value ->
                    testModel.measuredData.UBC.value = value.toDouble().autoformat()
                    testModel.measuredU =
                        (testModel.measuredData.UAB.value.toDoubleOrDefault(0.0) + testModel.measuredData.UBC.value.toDoubleOrDefault(
                            0.0
                        ) + testModel.measuredData.UCA.value.toDoubleOrDefault(0.0)) / 3.0
                    testModel.measuredData.U.value = testModel.measuredU.autoformat()
                }
                CM.startPoll(this, PM130Model.U_CA_REGISTER) { value ->
                    testModel.measuredData.UCA.value = value.toDouble().autoformat()
                    testModel.measuredU =
                        (testModel.measuredData.UAB.value.toDoubleOrDefault(0.0) + testModel.measuredData.UBC.value.toDoubleOrDefault(
                            0.0
                        ) + testModel.measuredData.UCA.value.toDoubleOrDefault(0.0)) / 3.0
                    testModel.measuredData.U.value = testModel.measuredU.autoformat()
                }

                CM.startPoll(this, PM130Model.I_A_REGISTER) { value ->
                    testModel.measuredIA = abs(value.toDouble() * testModel.amperageStage.ratio)
                    testModel.measuredData.IA.value = testModel.measuredIA.autoformat()
                    testModel.measuredI = (testModel.measuredIA + testModel.measuredIB + testModel.measuredIC) / 3
                    testModel.measuredData.I.value = testModel.measuredI.autoformat()
                }
                CM.startPoll(this, PM130Model.I_B_REGISTER) { value ->
                    testModel.measuredIB = abs(value.toDouble() * testModel.amperageStage.ratio)
                    testModel.measuredData.IB.value = testModel.measuredIB.autoformat()
                    testModel.measuredI = (testModel.measuredIA + testModel.measuredIB + testModel.measuredIC) / 3
                    testModel.measuredData.I.value = testModel.measuredI.autoformat()
                }
                CM.startPoll(this, PM130Model.I_C_REGISTER) { value ->
                    testModel.measuredIC = abs(value.toDouble() * testModel.amperageStage.ratio)
                    testModel.measuredData.IC.value = testModel.measuredIC.autoformat()
                    testModel.measuredI = (testModel.measuredIA + testModel.measuredIB + testModel.measuredIC) / 3
                    testModel.measuredData.I.value = testModel.measuredI.autoformat()
                }

                CM.startPoll(this, PM130Model.COS_REGISTER) { value ->
                    testModel.measuredData.cos.value = value.toDouble().autoformat()
                }
                CM.startPoll(this, PM130Model.P_REGISTER) { value ->
                    testModel.measuredP1 = abs(value.toDouble() * testModel.amperageStage.ratio)
                    testModel.measuredData.P1.value = testModel.measuredP1.autoformat()
                }
                CM.startPoll(this, PM130Model.F_REGISTER) { value ->
                    testModel.measuredData.F.value = value.toDouble().autoformat()
                }
            }
        }

        if (isRunning) {
            with(CM.DeviceID.PC71) {
                addCheckableDevice(this)
                CM.startPoll(this, TH01Model.RPM) { value ->
                    testModel.measuredData.RPM.value = value.toDouble().autoformat()
                    testModel.measuredData.efficiency.value = "нет"
                    testModel.measuredData.sk.value = "нет"
                }
            }
        }

        if (isRunning) {
            with(CM.DeviceID.PS81) {
                addCheckableDevice(this)
                CM.startPoll(this, TRM202Model.T_1) { value ->
                    testModel.measuredData.tempAmb.value = value.toDouble().autoformat()
                }
                CM.startPoll(this, TRM202Model.T_2) { value ->
                    testModel.tempTI = value.toDouble()
                    testModel.measuredData.tempTI.value = testModel.tempTI.autoformat()
                }
            }
        }

        if (isRunning) {
            with(CM.DeviceID.DD2) {
                addCheckableDevice(this)

                CM.startPoll(this, PRModel.AI_01_F) { value ->
                    testModel.measuredData.v1x.value = value.toDouble().autoformat()
                }
                CM.startPoll(this, PRModel.AI_02_F) { value ->
                    testModel.measuredData.v2x.value = value.toDouble().autoformat()
                }
            }
        }
    }

    private fun startPollLM() {
        with(CM.DeviceID.UZ92) {
            addCheckableDevice(this)
            CM.startPoll(this, HPMontModel.OUTPUT_FREQUENCY_REGISTER) { value ->
                value.toDouble() / 100
            }
            CM.startPoll(this, HPMontModel.OUTPUT_VOLTAGE_REGISTER) { value ->
                value.toDouble()
            }
            CM.startPoll(this, HPMontModel.OUTPUT_CURRENT_REGISTER) { value ->
                value.toDouble() / 100
            }
        }
    }

    override fun logic() {
        if (isRunning) {
            calcF()
        }
        if (isRunning) {
            turnOnCircuit()
        }
        if (isRunning) {
            waitUntilFIToLoad()
        }
        if (isRunning) {
            startPollLM()
        }
        if (isRunning) {
            testModel.isNeedAbsTorque = false
            checkLMDirection()
        }
        if (isRunning) {
            startTIFI()
        }
        if (isRunning) {
            startLMFI()
        }
        if (isRunning) {
            load()
        }
        if (isRunning) {
            selectAmperageStage()
        }
        if (isRunning) {
            waiting()
        }
        storeTestValues()
        if (isRunning) {
            returnAmperageStage()
        }
        stopOptimus(CM.device(CM.DeviceID.UZ91))
        CM.device<HPMont>(CM.DeviceID.UZ92).stopObject()
        sleepWhileRun(3)
    }

    private fun calcF() {
        val (zTI, zLM) = calcZs(isFirstPlatform, testModel.syncRPM.toInt())
        showZDialog(zTI.toInt(), zLM.toInt())
        val nTI = testModel.specifiedRPM //TODO реальное значение
        val fNom = testModel.specifiedF
        val nLM = 1490.0

        testModel.fLM = (nTI * zTI * fNom) / (nLM * zLM)
        appendMessageToLog(LogTag.INFO, "частота НМ ${testModel.fLM} Гц")
    }

    private fun showZDialog(zTI: Int, zLM: Int) {
        try {
            showTwoWayDialog(
                "Внимание",
                "На ОИ был установлен шкив $zTI и на НМ был установлен шкив $zLM?",
                "ДА",
                "НЕТ",
                { },
                { cause = "Ошибка при установке шкивов" },
                200_000,
                { false },
                find(view).currentWindow!!
            )
        } catch (e: Exception) {
            cause = "Диалог не был обработан в течение 200 с"
        }
    }

    private fun turnOnCircuit() {
        appendMessageToLog(LogTag.INFO, "Сбор схемы")
        CM.device<PR>(CM.DeviceID.DD2).onStart()
        sleepWhileRun(1)
        CM.device<PR>(CM.DeviceID.DD2).onU()
        sleepWhileRun(1)
        CM.device<PR>(CM.DeviceID.DD2).onMaxAmperageStage()
        sleepWhileRun(1)
        CM.device<PR>(CM.DeviceID.DD2).onFromFI()
        testModel.amperageStage = AmperageStage.FROM_500_TO_5
        sleepWhileRun(1)
    }

    private fun checkLMDirection() {
        appendMessageToLog(LogTag.INFO, "Проверка направления вращения ОИ и НМ...")
        try {
            showOKCancelDialog("Посмотрите куда вращаются машины\n Нажмите ОК для продолжения")
        } catch (e: Exception) {
            cause = "Диалог не был обработан в течение 200 с"
        }
        CM.device<Optimus>(CM.DeviceID.UZ91).setObjectParamsRun(380.0)
        if (isRunning) {
            frequency = 0.0
            sleepWhileRun(3)
            CM.device<Optimus>(CM.DeviceID.UZ91).setObjectFCur(frequency)
            sleepWhileRun(3)
            CM.device<Optimus>(CM.DeviceID.UZ91).startObject()
            sleepWhileRun(3)
        }
        while (frequency < 5.0 && isRunning) {
            frequency += 0.1
            sleep(100)
            CM.device<Optimus>(CM.DeviceID.UZ91).setObjectFCur(frequency)
        }
        frequency = 0.0
        CM.device<Optimus>(CM.DeviceID.UZ91).stopObjectNaVibege()
        sleep(3000)

        if (isRunning) {
            CM.device<HPMont>(CM.DeviceID.UZ92).setObjectParams(100.0)
            sleepWhileRun(3)
            CM.device<HPMont>(CM.DeviceID.UZ92).setRunningFrequency(0.0)
            sleepWhileRun(3)
            CM.device<HPMont>(CM.DeviceID.UZ92).startObject()
            var frequencyLM = 0.0
            while (frequencyLM < 5.0 && isRunning) {
                frequencyLM += 0.1
                sleep(100)
                CM.device<HPMont>(CM.DeviceID.UZ92).setRunningFrequency(frequencyLM)
            }
        }
        CM.device<HPMont>(CM.DeviceID.UZ92).stopObject()
        sleepWhileRun(3)
        CM.device<HPMont>(CM.DeviceID.UZ92).setRunningFrequency(0.0)

        try {
            showTwoWayDialog(
                "Внимание",
                "ОИ и НМ вращаются в одну сторону?",
                "ДА",
                "НЕТ",
                { testModel.isTIDirectionRight = true },
                { testModel.isTIDirectionRight = false },
                200_000,
                { false },
                find(view).currentWindow!!
            )
        } catch (e: Exception) {
            cause = "Диалог не был обработан в течение 200 с"
        }

    }

    private fun startTIFI() {
        appendMessageToLog(LogTag.INFO, "Разгон ОИ...")
        CM.device<Optimus>(CM.DeviceID.UZ91).setObjectParamsRun(380.0)
        if (isRunning) {
            frequency = 0.0
            sleepWhileRun(3)
            CM.device<Optimus>(CM.DeviceID.UZ91).setObjectFCur(frequency)
            sleepWhileRun(3)
            if (testModel.isTIDirectionRight) {
                CM.device<Optimus>(CM.DeviceID.UZ91).startObject()
            } else {
                CM.device<Optimus>(CM.DeviceID.UZ91).startObjectReverse()
            }
            sleepWhileRun(3)
        }
        while (frequency < 50.0 && isRunning) {
            frequency += 1.0
            sleep(500)
            CM.device<Optimus>(CM.DeviceID.UZ91).setObjectFCur(frequency)
        }
        if (isRunning) {
            frequency = 50.0
            CM.device<Optimus>(CM.DeviceID.UZ91).setObjectFCur(frequency)
        }
        sleepWhileRun(3)
    }

    private fun startLMFI() {
        appendMessageToLog(LogTag.INFO, "Разгон НМ...")

        var u = 1.0
        val maxU = 100.0

        CM.device<HPMont>(CM.DeviceID.UZ92).setObjectParams(u)
        sleepWhileRun(3)
        CM.device<HPMont>(CM.DeviceID.UZ92).setRunningFrequency(testModel.fLM)
        sleepWhileRun(3)
        CM.device<HPMont>(CM.DeviceID.UZ92).startObject()
        sleepWhileRun(3)

        while (isRunning && u < maxU) {
            u++
            CM.device<HPMont>(CM.DeviceID.UZ92).setObjectU(u)
            if (u % 10 == 0.0) {
                appendMessageToLog(LogTag.INFO, "НМ U = ${u * 3.8} В")
            }
            sleep(100)
        }
        if (isRunning) {
            CM.device<HPMont>(CM.DeviceID.UZ92).setObjectU(100.0)
        }
    }

    private fun load() {
        appendMessageToLog(LogTag.INFO, "Нагрузка")

        if (isRunning) {
            appendMessageToLog(LogTag.INFO, "Грубая регулировка нагрузки")
            regulationTo(minPercent = 20.0, step = 0.05, wait = 750L)
        }

        if (isRunning) {
            selectAmperageStage()
        }

        if (isRunning && !testModel.isContinueAgree) {
            appendMessageToLog(LogTag.INFO, "Точная регулировка нагрузки")
            regulationTo(minPercent = 2.0, maxPercent = 2.0, step = 0.01, wait = 1250L)
        }
    }

    private fun regulationTo(minPercent: Double, maxPercent: Double = minPercent, step: Double, wait: Long) {
        val min = testModel.specifiedI * (100.0 - minPercent) / 100.0
        val max = testModel.specifiedI * (100.0 + maxPercent) / 100.0

        val initTime = System.currentTimeMillis()
        val initValue = testModel.measuredI
        val initPercent = 7
        val timeout = 20000

        while (isRunning && (testModel.measuredI < min || testModel.measuredI > max)) {
            if ((abs(testModel.measuredI - initValue) / initValue) < initPercent / 100.0) {
                val elapsedTime = System.currentTimeMillis() - initTime
                if (elapsedTime > timeout) {
//                    cause = "в течение ${timeout / 1000} секунд значение изменилось меньше, чем на $initPercent%"
                }
            }
            if (testModel.measuredI < min) {
                testModel.fLM -= step
            }
            if (testModel.fLM <= 0) {
                testModel.fLM = 0.0
                CM.device<HPMont>(CM.DeviceID.UZ92).setRunningFrequency(testModel.fLM)
                try {
                    showTwoWayDialog(
                        "Внимание",
                        "Достигнут предел регулирования(f min = 0 Гц). Продолжить опыт?",
                        "ДА",
                        "НЕТ",
                        { testModel.isContinueAgree = true },
                        { stop() },
                        200_000,
                        { false },
                        find(view).currentWindow!!
                    )
                } catch (e: Exception) {
                    cause = "Диалог не был обработан в течение 200 с"
                }
                return
            }
            if (testModel.measuredI > max) {
                testModel.fLM += step
            }
            if (testModel.fLM >= 55.0) {
                testModel.fLM = 55.0
                CM.device<HPMont>(CM.DeviceID.UZ92).setRunningFrequency(testModel.fLM)
                try {
                    showTwoWayDialog(
                        "Внимание",
                        "Достигнут предел регулирования(f max = 55 Гц). Продолжить опыт?",
                        "ДА",
                        "НЕТ",
                        { },
                        { stop() },
                        200_000,
                        { false },
                        find(view).currentWindow!!
                    )
                } catch (e: Exception) {
                    cause = "Диалог не был обработан в течение 200 с"
                }
                return
            }
            CM.device<HPMont>(CM.DeviceID.UZ92).setRunningFrequency(testModel.fLM)
            sleep(wait)
        }
    }


    fun selectAmperageStage() {
        appendMessageToLog(LogTag.INFO, "Подбор токовой ступени...")
        if (isRunning && testModel.measuredI < 100) {
            appendMessageToLog(LogTag.INFO, "Переключение на 100/5")
            CM.device<PR>(CM.DeviceID.DD2).on100To5AmperageStage()
            CM.device<PR>(CM.DeviceID.DD2).offMaxAmperageStage()
            testModel.amperageStage = AmperageStage.FROM_100_TO_5
            sleepWhileRun(5)
            if (isRunning && testModel.measuredI < 30) {
                appendMessageToLog(LogTag.INFO, "Переключение на 30/5")
                CM.device<PR>(CM.DeviceID.DD2).on30to5Amperage()
                CM.device<PR>(CM.DeviceID.DD2).off100To5AmperageStage()
                testModel.amperageStage = AmperageStage.FROM_30_TO_5
                sleepWhileRun(5)
                if (isRunning && testModel.measuredI < 5) {
                    appendMessageToLog(LogTag.INFO, "Переключение на 5/5")
                    CM.device<PR>(CM.DeviceID.DD2).onMinAmperageStage()
                    CM.device<PR>(CM.DeviceID.DD2).off30to5Amperage()
                    testModel.amperageStage = AmperageStage.FROM_5_TO_5
                    sleepWhileRun(5)
                }
            }
        }
    }

    private fun waiting() {
        appendMessageToLog(LogTag.INFO, "Ожидание ${testModel.specifiedTestTime.toInt()} с...")
        sleepWhileRun(testModel.specifiedTestTime.toInt(), progressProperty = testModel.progressProperty)
    }

    private fun storeTestValues() {
        testModel.storedData.U.value = testModel.measuredData.U.value
        testModel.storedData.UAB.value = testModel.measuredData.UAB.value
        testModel.storedData.UBC.value = testModel.measuredData.UBC.value
        testModel.storedData.UCA.value = testModel.measuredData.UCA.value

        testModel.storedData.I.value = testModel.measuredData.I.value
        testModel.storedData.IA.value = testModel.measuredData.IA.value
        testModel.storedData.IB.value = testModel.measuredData.IB.value
        testModel.storedData.IC.value = testModel.measuredData.IC.value

        testModel.storedData.P1.value = testModel.measuredData.P1.value
        testModel.storedData.F.value = testModel.measuredData.F.value
        testModel.storedData.cos.value = testModel.measuredData.cos.value

        testModel.storedData.v1x.value = testModel.measuredData.v1x.value
//        testModel.storedData.v1y.value = testModel.measuredData.v1y.value
//        testModel.storedData.v1z.value = testModel.measuredData.v1z.value
        testModel.storedData.v2x.value = testModel.measuredData.v2x.value
//        testModel.storedData.v2y.value = testModel.measuredData.v2y.value
//        testModel.storedData.v2z.value = testModel.measuredData.v2z.value

        testModel.storedData.RPM.value = testModel.measuredData.RPM.value
        testModel.storedData.tempAmb.value = testModel.measuredData.tempAmb.value
        testModel.storedData.tempTI.value = testModel.measuredData.tempTI.value

        testModel.storedData.efficiency.value = testModel.measuredData.efficiency.value
        testModel.storedData.sk.value = testModel.measuredData.sk.value
    }

    private fun returnAmperageStage() {
        appendMessageToLog(LogTag.INFO, "Возврат токовой ступени...")
        CM.device<PR>(CM.DeviceID.DD2).onMaxAmperageStage()
        testModel.amperageStage = AmperageStage.FROM_500_TO_5
        CM.device<PR>(CM.DeviceID.DD2).offOtherAmperageStages()
    }

    override fun result() {
        super.result()

        if (!isSuccess) {
            testModel.measuredData.result.value = "Прервано"
            appendMessageToLog(LogTag.ERROR, "Испытание прервано по причине: $cause")
        } else {
            testModel.measuredData.result.value = "Соответствует"
            appendMessageToLog(LogTag.INFO, "Испытание завершено успешно")
        }
    }

    override fun finalizeView() {
        super.finalizeView()
        restoreTestValues()
        runLater {
            testModel.progressProperty.value = 0.0
        }
    }

    private fun restoreTestValues() {
        testModel.measuredData.U.value = testModel.storedData.U.value
        testModel.measuredData.UAB.value = testModel.storedData.UAB.value
        testModel.measuredData.UBC.value = testModel.storedData.UBC.value
        testModel.measuredData.UCA.value = testModel.storedData.UCA.value

        testModel.measuredData.I.value = testModel.storedData.I.value
        testModel.measuredData.IA.value = testModel.storedData.IA.value
        testModel.measuredData.IB.value = testModel.storedData.IB.value
        testModel.measuredData.IC.value = testModel.storedData.IC.value

        testModel.measuredData.P1.value = testModel.storedData.P1.value
        testModel.measuredData.F.value = testModel.storedData.F.value
        testModel.measuredData.cos.value = testModel.storedData.cos.value

        testModel.measuredData.v1x.value = testModel.storedData.v1x.value
//        testModel.measuredData.v1y.value = testModel.storedData.v1y.value
//        testModel.measuredData.v1z.value = testModel.storedData.v1z.value
        testModel.measuredData.v2x.value = testModel.storedData.v2x.value
//        testModel.measuredData.v2y.value = testModel.storedData.v2y.value
//        testModel.measuredData.v2z.value = testModel.storedData.v2z.value

        testModel.measuredData.RPM.value = testModel.storedData.RPM.value
        testModel.measuredData.tempAmb.value = testModel.storedData.tempAmb.value
        testModel.measuredData.tempTI.value = testModel.storedData.tempTI.value

        testModel.measuredData.efficiency.value = testModel.storedData.efficiency.value
        testModel.measuredData.sk.value = testModel.storedData.sk.value
    }

    override fun saveProtocol() {
        reportFields["TEST_NAME_LOAD"] = name

        reportFields["POWER"] = testModel.specifiedP.toString()
        reportFields["VOLTAGE_LIN"] = testModel.specifiedU.toString()
        reportFields["COS"] = testModel.specifiedCos.toString()
        reportFields["EFFICIENCY"] = testModel.specifiedEfficiency.toString()
        reportFields["AMPERAGE_PHASE"] = testModel.specifiedI.toString()
        reportFields["RPM"] = testModel.specifiedRPM.toString()
        reportFields["FREQ"] = testModel.specifiedF.toString()
        reportFields["SCHEME"] = testModel.specifiedScheme

        reportFields["U_LOAD"] = testModel.measuredData.U.value
        reportFields["L1_U_LOAD"] = testModel.measuredData.UAB.value
        reportFields["L2_U_LOAD"] = testModel.measuredData.UBC.value
        reportFields["L3_U_LOAD"] = testModel.measuredData.UCA.value
        reportFields["I_LOAD"] = testModel.measuredData.I.value
        reportFields["L1_I_LOAD"] = testModel.measuredData.IA.value
        reportFields["L2_I_LOAD"] = testModel.measuredData.IB.value
        reportFields["L3_I_LOAD"] = testModel.measuredData.IC.value
        reportFields["FREQ_LOAD"] = testModel.measuredData.F.value

        reportFields["TEMP_AMB_LOAD"] = testModel.measuredData.tempAmb.value
        reportFields["TEMP_TI_LOAD"] = testModel.measuredData.tempTI.value

        reportFields["X_31_TEMP_TI_LOAD"] = testModel.measuredData.v1x.value
//        reportFields["Y_31_TEMP_TI_LOAD"] = testModel.measuredData.v1y.value
//        reportFields["Z_31_TEMP_TI_LOAD"] = testModel.measuredData.v1z.value
        reportFields["X_32_TEMP_TI_LOAD"] = testModel.measuredData.v2x.value
//        reportFields["Y_32_TEMP_TI_LOAD"] = testModel.measuredData.v2y.value
//        reportFields["Z_32_TEMP_TI_LOAD"] = testModel.measuredData.v2z.value

        reportFields["TOTAL_P1_LOAD"] = testModel.measuredData.P1.value
        reportFields["TOTAL_PF_LOAD"] = testModel.measuredData.cos.value

        reportFields["RPM_LOAD"] = testModel.measuredData.RPM.value
        reportFields["SK_LOAD"] = testModel.measuredData.sk.value
        reportFields["EFFICIENCY_LOAD"] = testModel.measuredData.efficiency.value
        reportFields["TORQUE_LOAD"] = testModel.measuredData.tempTI.value

        reportFields["RESULT_LOAD"] = testModel.measuredData.result.value

        super.saveProtocol()
    }
}
