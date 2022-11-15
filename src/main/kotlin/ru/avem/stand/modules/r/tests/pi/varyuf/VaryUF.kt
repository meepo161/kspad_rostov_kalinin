package ru.avem.stand.modules.r.tests.pi.varyuf

import ru.avem.stand.modules.i.tests.LogTag
import ru.avem.stand.modules.r.common.prefill.PreFillModel
import ru.avem.stand.modules.r.communication.model.CM
import ru.avem.stand.modules.r.communication.model.CM.DeviceID.*
import ru.avem.stand.modules.r.communication.model.devices.delta.c2000.C2000
import ru.avem.stand.modules.r.communication.model.devices.owen.pr.PR
import ru.avem.stand.modules.r.communication.model.devices.owen.th01.TH01Model
import ru.avem.stand.modules.r.communication.model.devices.satec.pm130.PM130Model
import ru.avem.stand.modules.r.tests.AmperageStage
import ru.avem.stand.modules.r.tests.KSPADTest
import ru.avem.stand.utils.autoformat
import ru.avem.stand.utils.toDoubleOrDefault
import tornadofx.*
import java.lang.Thread.sleep
import kotlin.math.abs
import kotlin.math.sqrt

class VaryUF : KSPADTest(view = VaryUFView::class, reportTemplate = "varyuf.xlsx") {
    override val name = "Проверка работоспособности при изменении напряжения и частоты питающей сети"

    override val testModel = VaryUFModel

    override fun initVars() {
        super.initVars()

        testModel.specifiedU =
            testModel.testItemData.U.value.toDoubleOrDefault(0.0) // TODO так же везде? вынести в общий?
        testModel.specifiedI = testModel.testItemData.I.value.toDoubleOrDefault(0.0)

        testModel.specifiedCos = testModel.testItemData.cos.value.toDoubleOrDefault(0.0)
        testModel.specifiedEfficiency = testModel.testItemData.efficiency.value.toDoubleOrDefault(0.0)
        testModel.specifiedP = testModel.testItemData.P.value.toDoubleOrDefault(0.0)
        isFirstPlatform = testModel.specifiedP >= 8

        testModel.specifiedRPM = testModel.testItemData.RPM.value.toDoubleOrDefault(0.0)
        testModel.specifiedF = testModel.testItemData.F.value.toDoubleOrDefault(0.0)
        testModel.specifiedScheme = PreFillModel.testTypeProp.value.fields["SCHEME"]?.value ?: "λ"

        testModel.isSecondMode = false
    }

    override fun initView() {
        super.initView()

        runLater {
            testModel.progressProperty.value = -1.0

            testModel.measuredData1.U.value = ""
            testModel.measuredData1.UAB.value = ""
            testModel.measuredData1.UBC.value = ""
            testModel.measuredData1.UCA.value = ""

            testModel.measuredData1.I.value = ""
            testModel.measuredData1.IA.value = ""
            testModel.measuredData1.IB.value = ""
            testModel.measuredData1.IC.value = ""

            testModel.measuredData1.F.value = ""
            testModel.measuredData1.RPM.value = ""

            testModel.measuredData2.U.value = ""
            testModel.measuredData2.UAB.value = ""
            testModel.measuredData2.UBC.value = ""
            testModel.measuredData2.UCA.value = ""

            testModel.measuredData2.I.value = ""
            testModel.measuredData2.IA.value = ""
            testModel.measuredData2.IB.value = ""
            testModel.measuredData2.IC.value = ""

            testModel.measuredData2.F.value = ""
            testModel.measuredData2.RPM.value = ""

            testModel.measuredData1.time.value = ""
            testModel.measuredData1.result.value = ""
        }
    }

    override fun startPollDevices() {
        super.startPollDevices()

        startPollControlUnit()

        if (isRunning) {
            with(PAV41) {
                addCheckableDevice(this)
                CM.startPoll(this, PM130Model.U_AB_REGISTER) { value ->
                    if (!testModel.isSecondMode) {
                        testModel.measuredData1.UAB.value = value.toDouble().autoformat()
                        testModel.measuredU =
                            (testModel.measuredData1.UAB.value.toDoubleOrDefault(0.0) + testModel.measuredData1.UBC.value.toDoubleOrDefault(
                                0.0
                            ) + testModel.measuredData1.UCA.value.toDoubleOrDefault(0.0)) / 3.0
                        testModel.measuredData1.U.value = testModel.measuredU.autoformat()
                    } else {
                        testModel.measuredData2.UAB.value = value.toDouble().autoformat()
                        testModel.measuredU =
                            (testModel.measuredData2.UAB.value.toDoubleOrDefault(0.0) + testModel.measuredData2.UBC.value.toDoubleOrDefault(
                                0.0
                            ) + testModel.measuredData2.UCA.value.toDoubleOrDefault(0.0)) / 3.0
                        testModel.measuredData2.U.value = testModel.measuredU.autoformat()
                    }
                }
                CM.startPoll(this, PM130Model.U_BC_REGISTER) { value ->
                    if (!testModel.isSecondMode) {
                        testModel.measuredData1.UBC.value = value.toDouble().autoformat()
                        testModel.measuredU =
                            (testModel.measuredData1.UAB.value.toDoubleOrDefault(0.0) + testModel.measuredData1.UBC.value.toDoubleOrDefault(
                                0.0
                            ) + testModel.measuredData1.UCA.value.toDoubleOrDefault(0.0)) / 3.0
                        testModel.measuredData1.U.value = testModel.measuredU.autoformat()
                    } else {
                        testModel.measuredData2.UBC.value = value.toDouble().autoformat()
                        testModel.measuredU =
                            (testModel.measuredData2.UAB.value.toDoubleOrDefault(0.0) + testModel.measuredData2.UBC.value.toDoubleOrDefault(
                                0.0
                            ) + testModel.measuredData2.UCA.value.toDoubleOrDefault(0.0)) / 3.0
                        testModel.measuredData2.U.value = testModel.measuredU.autoformat()
                    }
                }
                CM.startPoll(this, PM130Model.U_CA_REGISTER) { value ->
                    if (!testModel.isSecondMode) {
                        testModel.measuredData1.UCA.value = value.toDouble().autoformat()
                        testModel.measuredU =
                            (testModel.measuredData1.UAB.value.toDoubleOrDefault(0.0) + testModel.measuredData1.UBC.value.toDoubleOrDefault(
                                0.0
                            ) + testModel.measuredData1.UCA.value.toDoubleOrDefault(0.0)) / 3.0
                        testModel.measuredData1.U.value = testModel.measuredU.autoformat()
                    } else {
                        testModel.measuredData2.UCA.value = value.toDouble().autoformat()
                        testModel.measuredU =
                            (testModel.measuredData2.UAB.value.toDoubleOrDefault(0.0) + testModel.measuredData2.UBC.value.toDoubleOrDefault(
                                0.0
                            ) + testModel.measuredData2.UCA.value.toDoubleOrDefault(0.0)) / 3.0
                        testModel.measuredData2.U.value = testModel.measuredU.autoformat()
                    }
                }

                CM.startPoll(this, PM130Model.I_A_REGISTER) { value ->
                    if (!testModel.isSecondMode) {
                        testModel.measuredIA = abs(value.toDouble() * testModel.amperageStage.ratio)
                        testModel.measuredData1.IA.value = testModel.measuredIA.autoformat()
                        testModel.measuredI = (testModel.measuredIA + testModel.measuredIB + testModel.measuredIC) / 3
                        testModel.measuredData1.I.value = testModel.measuredI.autoformat()
                    } else {
                        testModel.measuredIA = abs(value.toDouble() * testModel.amperageStage.ratio)
                        testModel.measuredData2.IA.value = testModel.measuredIA.autoformat()
                        testModel.measuredI = (testModel.measuredIA + testModel.measuredIB + testModel.measuredIC) / 3
                        testModel.measuredData2.I.value = testModel.measuredI.autoformat()
                    }
                }
                CM.startPoll(this, PM130Model.I_B_REGISTER) { value ->
                    if (!testModel.isSecondMode) {
                        testModel.measuredIB = abs(value.toDouble() * testModel.amperageStage.ratio)
                        testModel.measuredData1.IB.value = testModel.measuredIB.autoformat()
                        testModel.measuredI = (testModel.measuredIA + testModel.measuredIB + testModel.measuredIC) / 3
                        testModel.measuredData1.I.value = testModel.measuredI.autoformat()
                    } else {
                        testModel.measuredIB = abs(value.toDouble() * testModel.amperageStage.ratio)
                        testModel.measuredData2.IB.value = testModel.measuredIB.autoformat()
                        testModel.measuredI = (testModel.measuredIA + testModel.measuredIB + testModel.measuredIC) / 3
                        testModel.measuredData2.I.value = testModel.measuredI.autoformat()
                    }
                }
                CM.startPoll(this, PM130Model.I_C_REGISTER) { value ->
                    if (!testModel.isSecondMode) {
                        testModel.measuredIC = abs(value.toDouble() * testModel.amperageStage.ratio)
                        testModel.measuredData1.IC.value = testModel.measuredIC.autoformat()
                        testModel.measuredI = (testModel.measuredIA + testModel.measuredIB + testModel.measuredIC) / 3
                        testModel.measuredData1.I.value = testModel.measuredI.autoformat()
                    } else {
                        testModel.measuredIC = abs(value.toDouble() * testModel.amperageStage.ratio)
                        testModel.measuredData2.IC.value = testModel.measuredIC.autoformat()
                        testModel.measuredI = (testModel.measuredIA + testModel.measuredIB + testModel.measuredIC) / 3
                        testModel.measuredData2.I.value = testModel.measuredI.autoformat()
                    }
                }
                CM.startPoll(this, PM130Model.F_REGISTER) { value ->
                    if (!testModel.isSecondMode) {
                        testModel.measuredData1.F.value = value.toDouble().autoformat()
                    } else {
                        testModel.measuredData2.F.value = value.toDouble().autoformat()
                    }
                }
            }
        }

        if (isRunning) {
            with(PC71) {
                addCheckableDevice(this)
                CM.startPoll(this, TH01Model.RPM) { value ->
                    if (!testModel.isSecondMode) {
                        testModel.measuredData1.RPM.value = value.toDouble().autoformat()
                    } else {
                        testModel.measuredData2.RPM.value = value.toDouble().autoformat()
                    }
                }
            }
        }
    }

    override fun logic() {
        if (isRunning) {
            turnOnCircuit()
        }
        if (isRunning) {
            waitUntilFIToLoad()
            startFI()
            waitUntilFIToRun()
        }
        if (isRunning) {
            selectAmperageStage()
        }
        if (isRunning) {
            waiting("0.8*Uн | 0.94*fН")
            storeTestValues1()
            testModel.isSecondMode = true
            sleepWhileRun(2)
            restoreTestValues1()
            returnAmperageStage()
        }
        if (isRunning) {
            CM.device<C2000>(UZ91).setObjectFOut(testModel.specifiedF * 1.03)
            sleepWhileRun(7)
        }
        if (isRunning) {
            selectAmperageStage()
        }
        if (isRunning) {
            waiting("1.1*Uн | 1.03*fН")
        }
        storeTestValues2()
        if (isRunning) {
            returnAmperageStage()
            stopFI(CM.device(UZ91))
        }
    }

    private fun turnOnCircuit() {
        appendMessageToLog(LogTag.INFO, "Сбор схемы")
        CM.device<PR>(DD2).onStart()
        sleep(200)
        CM.device<PR>(DD2).onMaxAmperageStage()
        testModel.amperageStage = AmperageStage.FROM_150_TO_5
        sleep(200)
        CM.device<PR>(DD2).onVoltageBoost()
        sleep(200)
        CM.device<PR>(DD2).onMVZ()
        sleep(200)
        if (isFirstPlatform) {
            CM.device<PR>(DD2).onTestItemP1()
        } else {
            CM.device<PR>(DD2).onTestItemP2()
        }
        sleep(200)
    }

    private fun startFI() {
        appendMessageToLog(LogTag.INFO, "Разгон ЧП...")
        CM.device<C2000>(UZ91).setObjectParams(
            fOut = testModel.specifiedF * 0.94,

            voltageP1 = testModel.specifiedU * 1.1 / ((220.0 + 80.0) * sqrt(3.0) / 380.0),
            fP1 = testModel.specifiedF * 1.03,

            voltageP2 = testModel.specifiedU * 0.8 / ((220.0 + 80.0) * sqrt(3.0) / 380.0),
            fP2 = testModel.specifiedF * 0.94
        )
        CM.device<C2000>(UZ91).startObject()
    }

    private fun selectAmperageStage() {
        appendMessageToLog(LogTag.INFO, "Подбор токовой ступени...")
        if (isRunning && testModel.measuredI < 30) {
            appendMessageToLog(LogTag.INFO, "Переключение на 30/5")
            CM.device<PR>(DD2).on30To5AmperageStage()
            CM.device<PR>(DD2).offMaxAmperageStage()
            testModel.amperageStage = AmperageStage.FROM_30_TO_5
            sleepWhileRun(3)
            if (isRunning && testModel.measuredI < 4) {
                appendMessageToLog(LogTag.INFO, "Переключение на 5/5")
                CM.device<PR>(DD2).onMinAmperageStage()
                CM.device<PR>(DD2).off30To5AmperageStage()
                testModel.amperageStage = AmperageStage.FROM_5_TO_5
            }
        }
    }

    private fun waiting(title: String) {
        appendMessageToLog(LogTag.INFO, "Ожидание ($title)...")
        sleepWhileRun(testModel.specifiedTestTime.toInt(), progressProperty = testModel.progressProperty)
    }

    private fun storeTestValues1() {
        testModel.storedData1.U.value = testModel.measuredData1.U.value
        testModel.storedData1.UAB.value = testModel.measuredData1.UAB.value
        testModel.storedData1.UBC.value = testModel.measuredData1.UBC.value
        testModel.storedData1.UCA.value = testModel.measuredData1.UCA.value

        testModel.storedData1.I.value = testModel.measuredData1.I.value
        testModel.storedData1.IA.value = testModel.measuredData1.IA.value
        testModel.storedData1.IB.value = testModel.measuredData1.IB.value
        testModel.storedData1.IC.value = testModel.measuredData1.IC.value

        testModel.storedData1.F.value = testModel.measuredData1.F.value
        testModel.storedData1.RPM.value = testModel.measuredData1.RPM.value
    }

    private fun storeTestValues2() {
        testModel.storedData2.U.value = testModel.measuredData2.U.value
        testModel.storedData2.UAB.value = testModel.measuredData2.UAB.value
        testModel.storedData2.UBC.value = testModel.measuredData2.UBC.value
        testModel.storedData2.UCA.value = testModel.measuredData2.UCA.value

        testModel.storedData2.I.value = testModel.measuredData2.I.value
        testModel.storedData2.IA.value = testModel.measuredData2.IA.value
        testModel.storedData2.IB.value = testModel.measuredData2.IB.value
        testModel.storedData2.IC.value = testModel.measuredData2.IC.value

        testModel.storedData2.F.value = testModel.measuredData2.F.value
        testModel.storedData2.RPM.value = testModel.measuredData2.RPM.value
    }

    private fun returnAmperageStage() {
        appendMessageToLog(LogTag.INFO, "Возврат токовой ступени...")
        CM.device<PR>(DD2).onMaxAmperageStage()
        testModel.amperageStage = AmperageStage.FROM_150_TO_5
        CM.device<PR>(DD2).offOtherAmperageStages()
    }

    override fun result() {
        super.result()

        if (!isSuccess) {
            testModel.measuredData1.result.value = "Прервано"
            appendMessageToLog(LogTag.ERROR, "Испытание прервано по причине: $cause")
        } else {
            testModel.measuredData1.result.value = "Соответствует"
            appendMessageToLog(LogTag.INFO, "Испытание завершено успешно")
        }
    }

    override fun finalizeView() {
        super.finalizeView()
        restoreTestValues2()
        runLater {
            testModel.progressProperty.value = 0.0
        }
    }

    private fun restoreTestValues1() {
        testModel.measuredData1.U.value = testModel.storedData1.U.value
        testModel.measuredData1.UAB.value = testModel.storedData1.UAB.value
        testModel.measuredData1.UBC.value = testModel.storedData1.UBC.value
        testModel.measuredData1.UCA.value = testModel.storedData1.UCA.value

        testModel.measuredData1.I.value = testModel.storedData1.I.value
        testModel.measuredData1.IA.value = testModel.storedData1.IA.value
        testModel.measuredData1.IB.value = testModel.storedData1.IB.value
        testModel.measuredData1.IC.value = testModel.storedData1.IC.value

        testModel.measuredData1.F.value = testModel.storedData1.F.value
        testModel.measuredData1.RPM.value = testModel.storedData1.RPM.value
    }

    private fun restoreTestValues2() {
        testModel.measuredData2.U.value = testModel.storedData2.U.value
        testModel.measuredData2.UAB.value = testModel.storedData2.UAB.value
        testModel.measuredData2.UBC.value = testModel.storedData2.UBC.value
        testModel.measuredData2.UCA.value = testModel.storedData2.UCA.value

        testModel.measuredData2.I.value = testModel.storedData2.I.value
        testModel.measuredData2.IA.value = testModel.storedData2.IA.value
        testModel.measuredData2.IB.value = testModel.storedData2.IB.value
        testModel.measuredData2.IC.value = testModel.storedData2.IC.value

        testModel.measuredData2.F.value = testModel.storedData2.F.value
        testModel.measuredData2.RPM.value = testModel.storedData2.RPM.value
    }

    override fun saveProtocol() {
        reportFields["TEST_NAME_VARY_U_F"] = name

        reportFields["POWER"] = testModel.specifiedP.toString()
        reportFields["VOLTAGE_LIN"] = testModel.specifiedU.toString()
        reportFields["COS"] = testModel.specifiedCos.toString()
        reportFields["EFFICIENCY"] = testModel.specifiedEfficiency.toString()
        reportFields["AMPERAGE_PHASE"] = testModel.specifiedI.toString()
        reportFields["RPM"] = testModel.specifiedRPM.toString()
        reportFields["FREQ"] = testModel.specifiedF.toString()
        reportFields["SCHEME"] = testModel.specifiedScheme

        reportFields["U_1_VARY_U_F"] = testModel.measuredData1.U.value
        reportFields["L1_U_1_VARY_U_F"] = testModel.measuredData1.UAB.value
        reportFields["L2_U_1_VARY_U_F"] = testModel.measuredData1.UBC.value
        reportFields["L3_U_1_VARY_U_F"] = testModel.measuredData1.UCA.value
        reportFields["I_1_VARY_U_F"] = testModel.measuredData1.I.value
        reportFields["L1_I_1_VARY_U_F"] = testModel.measuredData1.IA.value
        reportFields["L2_I_1_VARY_U_F"] = testModel.measuredData1.IB.value
        reportFields["L3_I_1_VARY_U_F"] = testModel.measuredData1.IC.value
        reportFields["F_1_VARY_U_F"] = testModel.measuredData1.F.value
        reportFields["RPM_1_VARY_U_F"] = testModel.measuredData1.RPM.value

        reportFields["U_2_VARY_U_F"] = testModel.measuredData2.U.value
        reportFields["L1_U_2_VARY_U_F"] = testModel.measuredData2.UAB.value
        reportFields["L2_U_2_VARY_U_F"] = testModel.measuredData2.UBC.value
        reportFields["L3_U_2_VARY_U_F"] = testModel.measuredData2.UCA.value
        reportFields["I_2_VARY_U_F"] = testModel.measuredData2.I.value
        reportFields["L1_I_2_VARY_U_F"] = testModel.measuredData2.IA.value
        reportFields["L2_I_2_VARY_U_F"] = testModel.measuredData2.IB.value
        reportFields["L3_I_2_VARY_U_F"] = testModel.measuredData2.IC.value
        reportFields["F_2_VARY_U_F"] = testModel.measuredData2.F.value
        reportFields["RPM_2_VARY_U_F"] = testModel.measuredData2.RPM.value

        reportFields["RESULT_VARY_U_F"] = testModel.measuredData1.result.value

        super.saveProtocol()
    }
}
