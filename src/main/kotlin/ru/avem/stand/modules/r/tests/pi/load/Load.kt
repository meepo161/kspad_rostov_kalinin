package ru.avem.stand.modules.r.tests.pi.load

import ru.avem.stand.modules.i.tests.LogTag
import ru.avem.stand.modules.i.views.showTwoWayDialog
import ru.avem.stand.modules.r.common.prefill.PreFillModel
import ru.avem.stand.modules.r.communication.model.CM
import ru.avem.stand.modules.r.communication.model.devices.avem.ikas.IKAS8
import ru.avem.stand.modules.r.communication.model.devices.delta.c2000.C2000
import ru.avem.stand.modules.r.communication.model.devices.owen.pr.PR
import ru.avem.stand.modules.r.communication.model.devices.owen.th01.TH01Model
import ru.avem.stand.modules.r.communication.model.devices.owen.trm202.TRM202Model
import ru.avem.stand.modules.r.communication.model.devices.prosoftsystems.ivd3c.IVD3CModel
import ru.avem.stand.modules.r.communication.model.devices.satec.pm130.PM130Model
import ru.avem.stand.modules.r.communication.model.devices.tilkom.T42Model
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
    override val name = "Испытание на нагрев"

    override val testModel = LoadModel

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
            testModel.measuredData.v1y.value = ""
            testModel.measuredData.v1z.value = ""

            testModel.measuredData.v2x.value = ""
            testModel.measuredData.v2y.value = ""
            testModel.measuredData.v2z.value = ""

            testModel.measuredData.RPM.value = ""
            testModel.measuredData.tempAmb.value = ""
            testModel.measuredData.tempTI.value = ""
            testModel.measuredData.T0After.value = ""

            testModel.measuredData.torque.value = ""
            testModel.measuredData.P2.value = ""
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
            with(CM.DeviceID.BT100) {
                addCheckableDevice(this)
                CM.startPoll(this, T42Model.TORQUE) { value ->
                    if (!testModel.isStaticErrorTorqueSet) {
                        testModel.isStaticErrorTorqueSet = true
                        testModel.staticErrorTorque = value.toDouble()
                    }
                    testModel.measuredTorque = if (testModel.isNeedAbsTorque) {
                        abs(value.toDouble() - testModel.staticErrorTorque)
                    } else {
                        value.toDouble() - testModel.staticErrorTorque
                    }

                    testModel.measuredData.torque.value = testModel.measuredTorque.autoformat()
                }
            }
        }

        if (isRunning) {
            with(CM.DeviceID.PC71) {
                addCheckableDevice(this)
                CM.startPoll(this, TH01Model.RPM) { value ->
                    testModel.measuredData.RPM.value = value.toDouble().autoformat()
                    testModel.measuredP2 =
                        abs(testModel.measuredData.torque.value.toDouble() * testModel.measuredData.RPM.value.toDouble() * 2 * Math.PI / 60.0 / 1000.0)
                    testModel.measuredData.P2.value = testModel.measuredP2.autoformat()
                    testModel.measuredData.efficiency.value = (testModel.measuredP2 / testModel.measuredP1).autoformat()

                    testModel.measuredData.sk.value =
                        (100 * (testModel.syncRPM - testModel.measuredData.RPM.value.toDouble()) / testModel.syncRPM).autoformat()
                }
            }
        }

        if (isRunning) { // TODO если на поверке
            with(CM.DeviceID.PG31) {
                addCheckableDevice(this)
                CM.startPoll(this, IVD3CModel.MEAS_X) { value ->
                    testModel.measuredData.v1x.value = value.toDouble().autoformat()
                }
                CM.startPoll(this, IVD3CModel.MEAS_Y) { value ->
                    testModel.measuredData.v1y.value = value.toDouble().autoformat()
                }
                CM.startPoll(this, IVD3CModel.MEAS_Z) { value ->
                    testModel.measuredData.v1z.value = value.toDouble().autoformat()
                }
            }
        }

        if (isRunning) {
            with(CM.DeviceID.PG32) {
                addCheckableDevice(this)
                CM.startPoll(this, IVD3CModel.MEAS_X) { value ->
                    testModel.measuredData.v2x.value = value.toDouble().autoformat()
                }
                CM.startPoll(this, IVD3CModel.MEAS_Y) { value ->
                    testModel.measuredData.v2y.value = value.toDouble().autoformat()
                }
                CM.startPoll(this, IVD3CModel.MEAS_Z) { value ->
                    testModel.measuredData.v2z.value = value.toDouble().autoformat()
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
    }

    override fun logic() {
        if (isRunning) {
            calcF()
        }
        if (isRunning) {
            proceedIKASBefore()
        }
        if (isRunning) {
            turnOnCircuit()
        }
        if (isRunning) {
            waitUntilFIToLoad()
        }
        if (isRunning) {
            testModel.isNeedAbsTorque = false
            checkLMDirection()
        }
        if (isRunning) {
            startTIFI()
            waitUntilFIToRun()
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
            waitingSteadyTemperature()
        }
        if (isRunning) {
            waiting()
        }
        storeTestValues()
        if (isRunning) {
            if (isFirstPlatform) {
                CM.device<PR>(CM.DeviceID.DD2).offLoadMachineP1()
            } else {
                CM.device<PR>(CM.DeviceID.DD2).offLoadMachineP2()
            }
            returnAmperageStage()
            stopFI(CM.device(CM.DeviceID.UZ91))
        }
        if (isRunning) {
            proceedIKASAfter()
        }
    }

    private fun proceedIKASBefore() {
        appendMessageToLog(LogTag.INFO, "Измерение активного сопротивления...")

        if (isRunning) {
            CM.device<IKAS8>(CM.DeviceID.PR61).startMeasuringAB()
            while (isRunning && testModel.status != 0 && testModel.status != 101) {
                sleep(100)
            }
            while (isRunning && testModel.measuredR == -1.0) {
                sleep(100)
            }
            testModel.measuredData.R1.value =
                if (testModel.measuredR != 1E9) testModel.measuredR.autoformat() else "Обрыв"
        }

        if (isRunning) {
            CM.device<IKAS8>(CM.DeviceID.PR61).startMeasuringBC()
            while (isRunning && testModel.status != 0 && testModel.status != 101) {
                sleep(100)
            }
            while (isRunning && testModel.measuredR == -1.0) {
                sleep(100)
            }
            testModel.measuredData.R2.value =
                if (testModel.measuredR != 1E9) testModel.measuredR.autoformat() else "Обрыв"
        }

        if (isRunning) {
            CM.device<IKAS8>(CM.DeviceID.PR61).startMeasuringCA()
            while (isRunning && testModel.status != 0 && testModel.status != 101) {
                sleep(100)
            }
            while (isRunning && testModel.measuredR == -1.0) {
                sleep(100)
            }
            testModel.measuredData.R3.value =
                if (testModel.measuredR != 1E9) testModel.measuredR.autoformat() else "Обрыв"
        }

        if (testModel.measuredData.R1.value == "Обрыв" || testModel.measuredData.R2.value == "Обрыв" || testModel.measuredData.R3.value == "Обрыв") {
            cause = "Не удалось определить R из-за \"обрыва\" на первом этапе измерения активного сопротивления"
        } else {
            val R1 = testModel.measuredData.R1.value.toDouble()
            val R2 = testModel.measuredData.R2.value.toDouble()
            val R3 = testModel.measuredData.R3.value.toDouble()

            val averageRCold = (R1 + R2 + R3) / 3.0
            val averageRHalf = averageRCold / 2.0
            testModel.RCold = averageRHalf / (1 + 0.00393f * (testModel.measuredData.tempAmb.value.toDouble() - 20))
        }
    }

    private fun calcF() {
        val (zTI, zLM) = calcZs(isFirstPlatform, testModel.syncRPM.toInt())
        showZDialog(zTI.toInt(), zLM.toInt())
        val nTI = testModel.specifiedRPM // TODO реальное значение
        val fNom = testModel.specifiedF
        val nLM = 1480.0

        testModel.fLM = (nTI * zTI * fNom) / (nLM * zLM)
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
        sleep(200)
        CM.device<PR>(CM.DeviceID.DD2).onMaxAmperageStage()
        testModel.amperageStage = AmperageStage.FROM_150_TO_5
        sleep(200)
        CM.device<PR>(CM.DeviceID.DD2).fromFI()
        sleep(200)
        if (isFirstPlatform) {
            CM.device<PR>(CM.DeviceID.DD2).onLoadMachineP1()
            CM.device<PR>(CM.DeviceID.DD2).onTestItemP1()
        } else {
            CM.device<PR>(CM.DeviceID.DD2).onLoadMachineP2()
            CM.device<PR>(CM.DeviceID.DD2).onTestItemP2()
        }
        sleep(200)
    }

    private fun checkLMDirection() {
        appendMessageToLog(LogTag.INFO, "Проверка направления вращения НМ...")
        CM.device<C2000>(CM.DeviceID.UZ92).setObjectParams(
            fOut = 50,

            voltageP1 = 380,
            fP1 = 50,

            voltageP2 = 1,
            fP2 = 1
        )
        startFI(CM.device(CM.DeviceID.UZ92), 5)
        testModel.isLMDirectionRight = testModel.measuredData.torque.value.toDouble() > 0.0
        stopFI(CM.device(CM.DeviceID.UZ92))
    }

    private fun startTIFI() {
        appendMessageToLog(LogTag.INFO, "Разгон ОИ...")
        CM.device<C2000>(CM.DeviceID.UZ91).setObjectParams(
            fOut = testModel.specifiedF,

            voltageP1 = testModel.specifiedU,
            fP1 = testModel.specifiedF,

            voltageP2 = 1,
            fP2 = 1
        )
        CM.device<C2000>(CM.DeviceID.UZ91).startObject()
        sleepWhileRun(5)
        testModel.isTIDirectionRight = testModel.measuredTorque < 0.0
        testModel.isNeedAbsTorque = true
    }

    private fun startLMFI() {
        appendMessageToLog(LogTag.INFO, "Разгон НМ...")

        var u = 5
        val maxU = 380

        CM.device<C2000>(CM.DeviceID.UZ92).setObjectParams(
            fOut = testModel.fLM,

            voltageP1 = u,
            fP1 = testModel.fLM,

            voltageP2 = 1,
            fP2 = 1
        )
        val lmDirection = if (!testModel.isLMDirectionRight xor !testModel.isTIDirectionRight) {
            appendMessageToLog(LogTag.INFO, "Реверс НМ")
            C2000.Direction.REVERSE
        } else {
            C2000.Direction.FORWARD
        }
        CM.device<C2000>(CM.DeviceID.UZ92).startObject(lmDirection)

        while (isRunning && u < maxU) {
            u++
            CM.device<C2000>(CM.DeviceID.UZ92).setObjectUMax(u)
            if (u % 10 == 0) {
                appendMessageToLog(LogTag.INFO, "НМ U = $u В")
            }
            sleep(100)
        }

        CM.device<C2000>(CM.DeviceID.UZ92).setObjectUMax(maxU)
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
        val min = testModel.specifiedP * (100.0 - minPercent) / 100.0
        val max = testModel.specifiedP * (100.0 + maxPercent) / 100.0

        val initTime = System.currentTimeMillis()
        val initValue = testModel.measuredP2
        val initPercent = 7
        val timeout = 20000

        while (isRunning && (testModel.measuredP2 < min || testModel.measuredP2 > max)) {
            if ((abs(testModel.measuredP2 - initValue) / initValue) < initPercent / 100.0) {
                val elapsedTime = System.currentTimeMillis() - initTime
                if (elapsedTime > timeout) {
//                    cause = "в течение ${timeout / 1000} секунд значение изменилось меньше, чем на $initPercent%"
                }
            }
            if (testModel.measuredP2 < min) {
                testModel.fLM -= step
            }
            if (testModel.fLM <= 0) {
                testModel.fLM = 0.0
                CM.device<C2000>(CM.DeviceID.UZ92).setObjectFOut(testModel.fLM)
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
            if (testModel.measuredP2 > max) {
                testModel.fLM += step
            }
            if (testModel.fLM >= 55.0) {
                testModel.fLM = 55.0
                CM.device<C2000>(CM.DeviceID.UZ92).setObjectFOut(testModel.fLM)
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
            CM.device<C2000>(CM.DeviceID.UZ92).setObjectFOut(testModel.fLM)
            sleep(wait)
        }
    }

    private fun selectAmperageStage() {
        appendMessageToLog(LogTag.INFO, "Подбор токовой ступени...")
        if (isRunning && testModel.measuredI < 30) {
            appendMessageToLog(LogTag.INFO, "Переключение на 30/5")
            CM.device<PR>(CM.DeviceID.DD2).on30To5AmperageStage()
            CM.device<PR>(CM.DeviceID.DD2).offMaxAmperageStage()
            testModel.amperageStage = AmperageStage.FROM_30_TO_5
            sleepWhileRun(3)
            if (isRunning && testModel.measuredI < 4) {
                appendMessageToLog(LogTag.INFO, "Переключение на 5/5")
                CM.device<PR>(CM.DeviceID.DD2).onMinAmperageStage()
                CM.device<PR>(CM.DeviceID.DD2).off30To5AmperageStage()
                testModel.amperageStage = AmperageStage.FROM_5_TO_5
            }
        }
    }

    private fun waitingSteadyTemperature() {
        appendMessageToLog(LogTag.INFO, "Ожидание нагрева...")
        val maxTime = 120 * 10
        var timer = maxTime
        runLater {
            testModel.progressProperty.value = -1.0
        }
        while (isRunning && timer-- > 0) {
            if (testModel.tempTI > testModel.maxTemp) {
                testModel.maxTemp = testModel.tempTI
                appendMessageToLog(LogTag.INFO, "Максимальная температура ОИ = ${testModel.maxTemp} °C")
                timer = maxTime
            }
            sleep(1000 / 10)
        }
        runLater {
            testModel.progressProperty.value = 0.0
        }
        appendMessageToLog(LogTag.INFO, "Нагрев завершён...")
    }

    private fun waiting() {
        appendMessageToLog(LogTag.INFO, "Ожидание ${testModel.specifiedTestTime.toInt()} с...")
        sleepWhileRun(testModel.specifiedTestTime.toInt(), progressProperty = testModel.progressProperty)
    }

    private fun proceedIKASAfter() {
        appendMessageToLog(LogTag.INFO, "Измерение активного сопротивления...")

        if (isRunning) {
            CM.device<IKAS8>(CM.DeviceID.PR61).startMeasuringAB()
            while (isRunning && testModel.status != 0 && testModel.status != 101) {
                sleep(100)
            }
            while (isRunning && testModel.measuredR == -1.0) {
                sleep(100)
            }
            testModel.measuredData.R1After.value =
                if (testModel.measuredR != 1E9) testModel.measuredR.autoformat() else "Обрыв"
        }

        if (isRunning) {
            CM.device<IKAS8>(CM.DeviceID.PR61).startMeasuringAB()
            while (isRunning && testModel.status != 0 && testModel.status != 101) {
                sleep(100)
            }
            while (isRunning && testModel.measuredR == -1.0) {
                sleep(100)
            }
            testModel.measuredData.R2After.value =
                if (testModel.measuredR != 1E9) testModel.measuredR.autoformat() else "Обрыв"
        }

        sleepWhileRun(10)

        if (isRunning) {
            CM.device<IKAS8>(CM.DeviceID.PR61).startMeasuringAB()
            while (isRunning && testModel.status != 0 && testModel.status != 101) {
                sleep(100)
            }
            while (isRunning && testModel.measuredR == -1.0) {
                sleep(100)
            }
            testModel.measuredData.R3After.value =
                if (testModel.measuredR != 1E9) testModel.measuredR.autoformat() else "Обрыв"
        }

        testModel.measuredData.R0After.value = calcR0().autoformat()

        if (isRunning) {
            val RHot = testModel.measuredData.R0After.value.toDouble() / 2.0

            testModel.measuredData.T0After.value = ((RHot / testModel.RCold - 1) / 0.00393 + 20).autoformat() // TODO 0.00393 is too hard
        }
    }

    private fun calcR0(): Double {
        return if (testModel.measuredData.R1After.value == "Обрыв" || testModel.measuredData.R2After.value == "Обрыв" || testModel.measuredData.R3After.value == "Обрыв") {
            cause = "Не удалось посчитать температуру перегрева из-за \"обрыва\" на втором этапе измерения активного сопротивления"
            Double.NaN
        } else {
            val r0Time = 0.0
            val r1 = testModel.measuredData.R1After.value.toDouble()
            val r1Time = testModel.measuredData.R1TimeAfter.value.toDouble()
            val r2 = testModel.measuredData.R2After.value.toDouble()
            val r2Time = testModel.measuredData.R2TimeAfter.value.toDouble()
            val r3 = testModel.measuredData.R3After.value.toDouble()
            val r3Time = testModel.measuredData.R3TimeAfter.value.toDouble()

            10.0.pow((r0Time - r2Time) * (r0Time - r3Time) / ((r1Time - r2Time) * (r1Time - r3Time)) * log10(r1) +
                        (r0Time - r1Time) * (r0Time - r3Time) / ((r2Time - r1Time) * (r2Time - r3Time)) * log10(r2) +
                        (r0Time - r1Time) * (r0Time - r2Time) / ((r3Time - r1Time) * (r3Time - r2Time)) * log10(r3)
            )
        }
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
        testModel.storedData.v1y.value = testModel.measuredData.v1y.value
        testModel.storedData.v1z.value = testModel.measuredData.v1z.value
        testModel.storedData.v2x.value = testModel.measuredData.v2x.value
        testModel.storedData.v2y.value = testModel.measuredData.v2y.value
        testModel.storedData.v2z.value = testModel.measuredData.v2z.value

        testModel.storedData.RPM.value = testModel.measuredData.RPM.value
        testModel.storedData.tempAmb.value = testModel.measuredData.tempAmb.value
        testModel.storedData.tempTI.value = testModel.measuredData.tempTI.value

        testModel.storedData.torque.value = testModel.measuredData.torque.value
        testModel.storedData.P2.value = testModel.measuredData.P2.value
        testModel.storedData.efficiency.value = testModel.measuredData.efficiency.value
        testModel.storedData.sk.value = testModel.measuredData.sk.value
    }

    private fun returnAmperageStage() {
        appendMessageToLog(LogTag.INFO, "Возврат токовой ступени...")
        CM.device<PR>(CM.DeviceID.DD2).onMaxAmperageStage()
        testModel.amperageStage = AmperageStage.FROM_150_TO_5
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
        testModel.measuredData.v1y.value = testModel.storedData.v1y.value
        testModel.measuredData.v1z.value = testModel.storedData.v1z.value
        testModel.measuredData.v2x.value = testModel.storedData.v2x.value
        testModel.measuredData.v2y.value = testModel.storedData.v2y.value
        testModel.measuredData.v2z.value = testModel.storedData.v2z.value

        testModel.measuredData.RPM.value = testModel.storedData.RPM.value
        testModel.measuredData.tempAmb.value = testModel.storedData.tempAmb.value
        testModel.measuredData.tempTI.value = testModel.storedData.tempTI.value

        testModel.measuredData.torque.value = testModel.storedData.torque.value
        testModel.measuredData.P2.value = testModel.storedData.P2.value
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
        reportFields["Y_31_TEMP_TI_LOAD"] = testModel.measuredData.v1y.value
        reportFields["Z_31_TEMP_TI_LOAD"] = testModel.measuredData.v1z.value
        reportFields["X_32_TEMP_TI_LOAD"] = testModel.measuredData.v2x.value
        reportFields["Y_32_TEMP_TI_LOAD"] = testModel.measuredData.v2y.value
        reportFields["Z_32_TEMP_TI_LOAD"] = testModel.measuredData.v2z.value

        reportFields["TOTAL_P1_LOAD"] = testModel.measuredData.P1.value
        reportFields["TOTAL_PF_LOAD"] = testModel.measuredData.cos.value

        reportFields["RPM_LOAD"] = testModel.measuredData.RPM.value
        reportFields["SK_LOAD"] = testModel.measuredData.sk.value
        reportFields["TOTAL_P2_LOAD"] = testModel.measuredData.P2.value
        reportFields["EFFICIENCY_LOAD"] = testModel.measuredData.efficiency.value
        reportFields["TORQUE_LOAD"] = testModel.measuredData.tempTI.value

        reportFields["RESULT_LOAD"] = testModel.measuredData.result.value

        super.saveProtocol()
    }
}
