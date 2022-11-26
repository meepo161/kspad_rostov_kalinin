package ru.avem.stand.modules.r.tests.psi.mgr

import ru.avem.stand.modules.i.tests.LogTag
import ru.avem.stand.modules.r.common.prefill.PreFillModel
import ru.avem.stand.modules.r.communication.model.CM
import ru.avem.stand.modules.r.communication.model.CM.DeviceID.*
import ru.avem.stand.modules.r.communication.model.devices.megaohmmeter.cs02021.CS02021
import ru.avem.stand.modules.r.communication.model.devices.owen.pr.PR
import ru.avem.stand.modules.r.communication.model.devices.owen.trm202.TRM202
import ru.avem.stand.modules.r.communication.model.devices.owen.trm202.TRM202Model
import ru.avem.stand.modules.r.tests.KSPADTest
import ru.avem.stand.utils.autoformat
import ru.avem.stand.utils.toDoubleOrDefault
import tornadofx.runLater
import java.lang.Thread.sleep
import kotlin.collections.set

class MGR : KSPADTest(view = MGRView::class, reportTemplate = "mgr.xlsx") {
    override val name = "Измерение сопротивления изоляции обмоток и встроенных термодатчиков относительно корпуса и " +
            "между обмотками в практически холодном состоянии"

    override val testModel = MGRModel

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

        testModel.specifiedUMGR = PreFillModel.testTypeProp.value.fields["U_MGR"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedRMGR = PreFillModel.testTypeProp.value.fields["R_MGR_HV"]?.value.toDoubleOrDefault(0.0)
    }

    override fun initView() {
        super.initView()

        runLater {
            testModel.progressProperty.value = -1.0

            testModel.measuredData.U.value = ""
            testModel.measuredData.R15.value = ""
            testModel.measuredData.R60.value = ""
            testModel.measuredData.kABS.value = ""
            testModel.measuredData.tempAmb.value = ""
            testModel.measuredData.tempTI.value = ""
            testModel.measuredData.time.value = ""
            testModel.measuredData.result.value = ""
        }
    }

    override fun startPollDevices() {
        super.startPollDevices()
        startPollControlUnit()

        if (isRunning) {
            with(CM.device<TRM202>(PS81)) {
                with(getRegisterById(TRM202Model.T_1)) {
                    readRegister(this)
                    testModel.measuredData.tempAmb.value = value.toDouble().autoformat()
                }
                with(getRegisterById(TRM202Model.T_2)) {
                    readRegister(this)
                    testModel.measuredData.tempTI.value = value.toDouble().autoformat()
                }
            }
        }
    }

    override fun logic() {
        if (isRunning) {
            turnOnCircuit()
        }
        if (isRunning) {
            startMeasuring()
        }
    }

    private fun turnOnCircuit() {
        appendMessageToLog(LogTag.INFO, "Сбор схемы")
        CM.device<PR>(DD2).onShuntViu()
        sleep(200)
        CM.device<PR>(DD2).onGround()
        sleep(200)

        sleepWhileRun(1)

        CM.device<PR>(DD2).onMGR()
        sleep(200)
        CM.device<PR>(DD2).offShuntViu()
    }

    private fun startMeasuring() {
        appendMessageToLog(LogTag.INFO, "Начало измерения...")

        with(CM.device<CS02021>(PR65)) {
            if (isResponding) {
                appendMessageToLog(LogTag.INFO, "Формирование напряжения...")
                setVoltage(testModel.specifiedUMGR.toInt())
                sleepWhileRun(30, progressProperty = testModel.progressProperty)

                appendMessageToLog(LogTag.INFO, "Измерение сопротивления...")
                sleepWhileRun(testModel.specifiedTestTime, progressProperty = testModel.progressProperty)

                val measuredR60 = readData()[0].toDouble()
                val measuredUr = readData()[1].toDouble()
                val measuredAbs = readData()[2].toDouble()
                val measuredR15 = readData()[3].toDouble()

                val measuredR60Mohm = (measuredR60 / 1_000_000)
                val measuredR15Mohm = (measuredR15 / 1_000_000)
                if (measuredR60Mohm > 200_000) {
                    testModel.measuredData.U.value = measuredUr.autoformat()
                    testModel.measuredData.R15.value = "обрыв"
                    testModel.measuredData.R60.value = "обрыв"
                    testModel.measuredData.kABS.value = "обрыв"
                    cause = "обрыв"
                } else {
                    testModel.measuredData.U.value = measuredUr.autoformat()
                    testModel.measuredData.R15.value = measuredR15Mohm.autoformat()
                    testModel.measuredData.R60.value = measuredR60Mohm.autoformat()
                    testModel.measuredData.kABS.value = measuredAbs.autoformat()
                }
                CM.device<PR>(DD2).offMGR()
                CM.device<PR>(DD2).offGround()
                sleepWhileRun(3)
                CM.device<PR>(DD2).resetTriggers()
                appendMessageToLog(LogTag.DEBUG, "Заземление...")
                sleepWhileRun(30, progressProperty = testModel.progressProperty)
            } else {
                cause = "Меггер не отвечает"
            }
        }
    }

    override fun result() {
        super.result()

        when {
            !isSuccess -> {
                testModel.measuredData.result.value = "Прервано"
                appendMessageToLog(LogTag.ERROR, "Испытание прервано по причине: $cause")
            }
            (!testModel.specifiedData.R60.value.isNullOrBlank()) && (testModel.measuredData.R60.value.toDouble() < testModel.specifiedData.R60.value.toDouble()) -> {
                testModel.measuredData.result.value = "Не соответствует"
                appendMessageToLog(LogTag.ERROR, "Измеренное сопротивление < ${testModel.specifiedData.R60.value} МОм")
            }
            testModel.measuredData.kABS.value.toDouble() < 1.3 -> {
                testModel.measuredData.result.value = "Не соответствует"
                appendMessageToLog(LogTag.ERROR, "Измеренный kABS < 1.3")
            }
            else -> {
                testModel.measuredData.result.value = "Соответствует"
                appendMessageToLog(LogTag.INFO, "Испытание завершено успешно")
            }
        }
    }

    override fun finalizeView() {
        super.finalizeView()
        runLater {
            testModel.progressProperty.value = 0.0
        }
    }

    override fun saveProtocol() {
        reportFields["TEST_NAME_MGR"] = name

        reportFields["POWER"] = testModel.specifiedP.toString()
        reportFields["VOLTAGE_LIN"] = testModel.specifiedU.toString()
        reportFields["COS"] = testModel.specifiedCos.toString()
        reportFields["EFFICIENCY"] = testModel.specifiedEfficiency.toString()
        reportFields["AMPERAGE_PHASE"] = testModel.specifiedI.toString()
        reportFields["RPM"] = testModel.specifiedRPM.toString()
        reportFields["FREQ"] = testModel.specifiedF.toString()
        reportFields["SCHEME"] = testModel.specifiedScheme

        reportFields["U_SPEC_MGR"] = testModel.specifiedData.U.value
        reportFields["R_SPEC_MGR"] = testModel.specifiedData.R60.value
        reportFields["U_MEAS_MGR"] = testModel.measuredData.U.value
        reportFields["R15_MEAS_MGR"] = testModel.measuredData.R15.value
        reportFields["R60_MEAS_MGR"] = testModel.measuredData.R60.value
        reportFields["K_ABS_MEAS_MGR"] = testModel.measuredData.kABS.value
        reportFields["TEMP_AMB_MGR"] = testModel.measuredData.tempAmb.value
        reportFields["TEMP_TI_MGR"] = testModel.measuredData.tempTI.value
        reportFields["RESULT_MGR"] = testModel.measuredData.result.value

        super.saveProtocol()
    }
}
