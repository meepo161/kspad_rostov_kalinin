package ru.avem.stand.modules.r.tests.pi.ktr

import ru.avem.stand.modules.i.tests.LogTag
import ru.avem.stand.modules.r.common.prefill.PreFillModel
import ru.avem.stand.modules.r.communication.model.CM
import ru.avem.stand.modules.r.communication.model.CM.DeviceID.*
import ru.avem.stand.modules.r.communication.model.devices.hpmont.HPMont
import ru.avem.stand.modules.r.communication.model.devices.hpmont.HPMontModel
import ru.avem.stand.modules.r.communication.model.devices.optimus.Optimus
import ru.avem.stand.modules.r.communication.model.devices.optimus.OptimusModel
import ru.avem.stand.modules.r.communication.model.devices.owen.pr.PR
import ru.avem.stand.modules.r.communication.model.devices.owen.pr.PRModel
import ru.avem.stand.modules.r.communication.model.devices.owen.th01.TH01Model
import ru.avem.stand.modules.r.communication.model.devices.owen.trm202.TRM202Model
import ru.avem.stand.modules.r.communication.model.devices.satec.pm130.PM130Model
import ru.avem.stand.modules.r.tests.AmperageStage
import ru.avem.stand.modules.r.tests.KSPADTest
import ru.avem.stand.utils.autoformat
import ru.avem.stand.utils.toDoubleOrDefault
import tornadofx.runLater
import java.lang.Thread.sleep
import kotlin.collections.set
import kotlin.math.abs

class Ktr : KSPADTest(view = KtrView::class, reportTemplate = "ktr.xlsx") {
    override val name =
        "Определение коэфицента трансформации"

    override val testModel = KtrModel

    var frequency = 0.0
    var lastFIP1U = 0.0

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

        testModel.specifiedKtrI =
            PreFillModel.testTypeProp.value.fields["KTR_I"]?.value?.toDoubleOrDefault(0.0) ?: 0.0
        testModel.specifiedTestTime =
            PreFillModel.testTypeProp.value.fields["KTR_TIME"]?.value.toDoubleOrDefault(0.0)
    }

    override fun initView() {
        super.initView()

        runLater {
            testModel.progressProperty.value = -1.0

            testModel.measuredData.U.value = ""
            testModel.measuredData.UAB.value = ""
            testModel.measuredData.UBC.value = ""
            testModel.measuredData.UCA.value = ""
            testModel.measuredData.UAB2.value = ""
            testModel.measuredData.UBC2.value = ""
            testModel.measuredData.UCA2.value = ""

            testModel.measuredData.I.value = ""
            testModel.measuredData.IA.value = ""
            testModel.measuredData.IB.value = ""
            testModel.measuredData.IC.value = ""

            testModel.measuredData.P1.value = ""
            testModel.measuredData.F.value = ""
            testModel.measuredData.cos.value = ""

            testModel.measuredData.v1x.value = ""
//            testModel.measuredData.v1y.value = ""
//            testModel.measuredData.v1z.value = ""

            testModel.measuredData.v2x.value = ""
//            testModel.measuredData.v2y.value = ""
//            testModel.measuredData.v2z.value = ""

            testModel.measuredData.RPM.value = ""
            testModel.measuredData.tempAmb.value = ""
            testModel.measuredData.tempTI.value = ""

            testModel.measuredData.result.value = ""
        }
    }

    override fun startPollDevices() {
        super.startPollDevices()

        startPollControlUnit()

        if (isRunning) {
            with(PAV41) {
                addCheckableDevice(this)
                CM.startPoll(this, PM130Model.U_AB_REGISTER) { value ->
                    testModel.measuredData.UAB.value = value.toDouble().autoformat()
                    testModel.measuredU =
                        (testModel.measuredData.UAB.value.toDoubleOrDefault(0.0)
                                + testModel.measuredData.UBC.value.toDoubleOrDefault(0.0)
                                + testModel.measuredData.UCA.value.toDoubleOrDefault(0.0)) / 3.0
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
                    testModel.measuredData.P1.value =
                        abs(value.toDouble() * testModel.amperageStage.ratio).autoformat()
                }
                CM.startPoll(this, PM130Model.F_REGISTER) { value ->
                    testModel.measuredData.F.value = value.toDouble().autoformat()
                }
            }
        }

        if (isRunning) {
            with(PC71) {
                addCheckableDevice(this)
                CM.startPoll(this, TH01Model.RPM) { value ->
                    testModel.measuredData.RPM.value = value.toDouble().autoformat()
                }
            }
        }

        if (isRunning) {
            with(PS81) {
                addCheckableDevice(this)

                CM.startPoll(this, TRM202Model.T_1) { value ->
                    testModel.measuredData.tempAmb.value = value.toDouble().autoformat()
                }
                CM.startPoll(this, TRM202Model.T_2) { value ->
                    testModel.measuredData.tempTI.value = value.toDouble().autoformat()
                }
            }
        }

        if (isRunning) {
            with(DD2) {
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

    private fun startPollFi() {
        if (isRunning) {
            with(UZ91) {
                addCheckableDevice(this)
                CM.startPoll(this, OptimusModel.CURRENT_FREQUENCY_REGISTER) { value ->
                    testModel.measuredData.optimusF.value = (value.toDouble() / 10).autoformat()
                }
                CM.startPoll(this, OptimusModel.CURRENT_VOLTAGE_REGISTER) { value ->
                    testModel.measuredData.optimusU.value = (value.toDouble()).autoformat()
                }
                CM.startPoll(this, OptimusModel.CURRENT_CURRENT_REGISTER) { value ->
                    testModel.measuredData.optimusI.value = (value.toDouble() / 10).autoformat()
                }
                CM.startPoll(this, OptimusModel.VOLTAGE_4_REGISTER) { value ->
                    testModel.measuredData.optimusV4.value = (value.toDouble() / 10).autoformat()
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
            startPollFi()
            startFI()
            regulateVoltage(specifiedU = 380.0, minPercent = 0.5, step = 1.0)
            storeTestValues()
            checkKTR()
        }
        if (isRunning) {
            returnAmperageStage()
        }
        CM.device<Optimus>(UZ91).stopObjectNaVibege()
        sleep(3000)
        restoreTestValues()
    }

    private fun turnOnCircuit() {
        appendMessageToLog(LogTag.INFO, "Сбор схемы")
        CM.device<PR>(DD2).onStart()
        sleepWhileRun(1)
        CM.device<PR>(DD2).onU()
        sleepWhileRun(1)
        CM.device<PR>(DD2).onMaxAmperageStage()
        sleepWhileRun(1)
        CM.device<PR>(DD2).onFromFI()
        testModel.amperageStage = AmperageStage.FROM_500_TO_5
        sleepWhileRun(1)
    }

    private fun startFI() {
        appendMessageToLog(LogTag.INFO, "Разгон ЧП...")
        CM.device<Optimus>(UZ91).setObjectParamsRun(380.0)
        if (isRunning) {
            frequency = 0.0
            sleepWhileRun(3)
            CM.device<Optimus>(UZ91).setObjectFCur(frequency)
            sleepWhileRun(3)
            CM.device<Optimus>(UZ91).startObject()
            sleepWhileRun(3)
        }
        while (frequency < 50.0 && isRunning) {
            frequency += 1
            sleep(100)
            CM.device<Optimus>(UZ91).setObjectFCur(frequency)
        }
        if (isRunning) {
            frequency = 50.0
            CM.device<Optimus>(UZ91).setObjectFCur(frequency)
        }
    }

    private fun regulateVoltage(
        specifiedU: Double,
        minPercent: Double,
        maxPercent: Double = minPercent,
        step: Double,
        wait: Long = 400L
    ) {
        val min = specifiedU * (100.0 - minPercent) / 100.0
        val max = specifiedU * (100.0 + maxPercent) / 100.0

        lastFIP1U = testModel.measuredData.optimusV4.value.replace(",", ".").toDouble()
        while (isRunning && (testModel.measuredU < min || testModel.measuredU > max)) {
            if (testModel.measuredU < min) {
                lastFIP1U += step
            }
            if (testModel.measuredU > max) {
                lastFIP1U -= step
            }
            CM.device<Optimus>(UZ91).setObjectUMax(lastFIP1U)
            sleep(wait)
        }
    }

    private fun checkKTR() {
        sleepWhileRun(5)
        CM.device<PR>(DD2).offU()
        CM.device<PR>(DD2).onKTR()
        sleepWhileRun(5)
        testModel.measuredData.UAB2.value = testModel.measuredData.UAB.value
        testModel.measuredData.UBC2.value = testModel.measuredData.UBC.value
        testModel.measuredData.UCA2.value = testModel.measuredData.UCA.value
        testModel.storedData.UAB2.value = testModel.measuredData.UAB2.value
        testModel.storedData.UBC2.value = testModel.measuredData.UBC2.value
        testModel.storedData.UCA2.value = testModel.measuredData.UCA2.value
        testModel.measuredData.KTR.value =
            (((testModel.measuredData.UAB.value.toDouble() + testModel.measuredData.UBC.value.toDouble() + testModel.measuredData.UCA.value.toDouble()) / 3) /
                    ((testModel.measuredData.UAB2.value.toDouble() + testModel.measuredData.UBC2.value.toDouble() + testModel.measuredData.UCA2.value.toDouble()) / 3)).autoformat()
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

        testModel.storedData.v1.value = testModel.measuredData.v1.value
        testModel.storedData.v1x.value = testModel.measuredData.v1x.value
//        testModel.storedData.v1y.value = testModel.measuredData.v1y.value
//        testModel.storedData.v1z.value = testModel.measuredData.v1z.value

        testModel.storedData.v2.value = testModel.measuredData.v2.value
        testModel.storedData.v2x.value = testModel.measuredData.v2x.value
//        testModel.storedData.v2y.value = testModel.measuredData.v2y.value
//        testModel.storedData.v2z.value = testModel.measuredData.v2z.value

        testModel.storedData.RPM.value = testModel.measuredData.RPM.value
        testModel.storedData.tempAmb.value = testModel.measuredData.tempAmb.value
        testModel.storedData.tempTI.value = testModel.measuredData.tempTI.value
    }

    private fun returnAmperageStage() {
        appendMessageToLog(LogTag.INFO, "Возврат токовой ступени...")
        CM.device<PR>(DD2).onMaxAmperageStage()
        testModel.amperageStage = AmperageStage.FROM_500_TO_5
        CM.device<PR>(DD2).offOtherAmperageStages()
    }

    private fun stopFi() {
        appendMessageToLog(LogTag.INFO, "Остановка ЧП...")
        while (frequency >= 0.1) {
            frequency -= 0.5
            sleep(100)
            CM.device<Optimus>(UZ91).setObjectFCur(frequency)
        }
        CM.device<Optimus>(UZ91).stopObject()
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
        testModel.measuredData.UAB2.value = testModel.storedData.UAB2.value
        testModel.measuredData.UBC2.value = testModel.storedData.UBC2.value
        testModel.measuredData.UCA2.value = testModel.storedData.UCA2.value

        testModel.measuredData.I.value = testModel.storedData.I.value
        testModel.measuredData.IA.value = testModel.storedData.IA.value
        testModel.measuredData.IB.value = testModel.storedData.IB.value
        testModel.measuredData.IC.value = testModel.storedData.IC.value

        testModel.measuredData.P1.value = testModel.storedData.P1.value
        testModel.measuredData.F.value = testModel.storedData.F.value
        testModel.measuredData.cos.value = testModel.storedData.cos.value

        testModel.measuredData.v1.value = testModel.storedData.v1.value
        testModel.measuredData.v1x.value = testModel.storedData.v1x.value
//        testModel.measuredData.v1y.value = testModel.storedData.v1y.value
//        testModel.measuredData.v1z.value = testModel.storedData.v1z.value

        testModel.measuredData.v2.value = testModel.storedData.v2.value
        testModel.measuredData.v2x.value = testModel.storedData.v2x.value
//        testModel.measuredData.v2y.value = testModel.storedData.v2y.value
//        testModel.measuredData.v2z.value = testModel.storedData.v2z.value

        testModel.measuredData.RPM.value = testModel.storedData.RPM.value
        testModel.measuredData.tempAmb.value = testModel.storedData.tempAmb.value
        testModel.measuredData.tempTI.value = testModel.storedData.tempTI.value
    }

    override fun saveProtocol() {
        reportFields["TEST_NAME_KTR"] = name

        reportFields["POWER"] = testModel.specifiedP.toString()
        reportFields["VOLTAGE_LIN"] = testModel.specifiedU.toString()
        reportFields["COS"] = testModel.specifiedCos.toString()
        reportFields["EFFICIENCY"] = testModel.specifiedEfficiency.toString()
        reportFields["AMPERAGE_PHASE"] = testModel.specifiedI.toString()
        reportFields["RPM"] = testModel.specifiedRPM.toString()
        reportFields["FREQ"] = testModel.specifiedF.toString()
        reportFields["SCHEME"] = testModel.specifiedScheme

        reportFields["U_KTR"] = testModel.measuredData.U.value
        reportFields["L1_U_KTR"] = testModel.measuredData.UAB.value
        reportFields["L2_U_KTR"] = testModel.measuredData.UBC.value
        reportFields["L3_U_KTR"] = testModel.measuredData.UCA.value
        reportFields["L1_U_KTR_2"] = testModel.measuredData.UAB2.value
        reportFields["L2_U_KTR_2"] = testModel.measuredData.UBC2.value
        reportFields["L3_U_KTR_2"] = testModel.measuredData.UCA2.value
        reportFields["I_KTR"] = testModel.measuredData.I.value
        reportFields["KTR"] = testModel.measuredData.KTR.value
        reportFields["L1_I_KTR"] = testModel.measuredData.IA.value
        reportFields["L2_I_KTR"] = testModel.measuredData.IB.value
        reportFields["L3_I_KTR"] = testModel.measuredData.IC.value
        reportFields["TOTAL_P_KTR"] = testModel.measuredData.P1.value
        reportFields["TOTAL_PF_KTR"] = testModel.measuredData.cos.value
        reportFields["F_KTR"] = testModel.measuredData.F.value
        reportFields["X_31_KTR"] = testModel.measuredData.v1x.value
//        reportFields["Y_31_KTR"] = testModel.measuredData.v1y.value
//        reportFields["Z_31_KTR"] = testModel.measuredData.v1z.value
        reportFields["X_32_KTR"] = testModel.measuredData.v2x.value
//        reportFields["Y_32_KTR"] = testModel.measuredData.v2y.value
//        reportFields["Z_32_KTR"] = testModel.measuredData.v2z.value
        reportFields["RPM_KTR"] = testModel.measuredData.RPM.value
        reportFields["TEMP_AMB_KTR"] = testModel.measuredData.tempAmb.value
        reportFields["TEMP_OI_KTR"] = testModel.measuredData.tempTI.value
        reportFields["RESULT_KTR"] = testModel.measuredData.result.value

        super.saveProtocol()
    }
}
