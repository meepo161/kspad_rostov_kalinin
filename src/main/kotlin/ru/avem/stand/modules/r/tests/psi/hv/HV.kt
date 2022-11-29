package ru.avem.stand.modules.r.tests.psi.hv

import ru.avem.stand.modules.i.tests.LogTag
import ru.avem.stand.modules.r.common.prefill.PreFillModel
import ru.avem.stand.modules.r.communication.model.CM
import ru.avem.stand.modules.r.communication.model.CM.DeviceID.*
import ru.avem.stand.modules.r.communication.model.devices.avem.avem3.AVEM3Model
import ru.avem.stand.modules.r.communication.model.devices.delta.c2000.C2000
import ru.avem.stand.modules.r.communication.model.devices.optimus.Optimus
import ru.avem.stand.modules.r.communication.model.devices.owen.pr.PR
import ru.avem.stand.modules.r.communication.model.devices.satec.pm130.PM130Model
import ru.avem.stand.modules.r.tests.KSPADTest
import ru.avem.stand.utils.autoformat
import ru.avem.stand.utils.toDoubleOrDefault
import tornadofx.*
import java.lang.Thread.sleep
import java.util.*

class HV : KSPADTest(view = HVView::class, reportTemplate = "hv.xlsx") {
    override val name = "Испытание электрической прочности изоляции обмотки и встроенных термодатчиков относительно " +
            "корпуса и между обмотками в практически холодном состоянии двигателя"

    override val testModel = HVModel
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

        testModel.specifiedUHV = PreFillModel.testTypeProp.value.fields["U_HV"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedIHV = PreFillModel.testTypeProp.value.fields["I_HV"]?.value.toDoubleOrDefault(0.0)
        testModel.specifiedTestTime = PreFillModel.testTypeProp.value.fields["T_HV"]?.value.toDoubleOrDefault(0.0)
    }

    override fun initView() {
        super.initView()

        runLater {
            testModel.progressProperty.value = -1.0

            testModel.measuredData.U.value = ""
            testModel.measuredData.I.value = ""
            testModel.measuredData.F.value = ""

            testModel.measuredData.time.value = ""
            testModel.measuredData.result.value = ""
        }
    }

    override fun startPollDevices() {
        super.startPollDevices()
        startPollControlUnit()

        if (isRunning) {
            with(PV24) {
                addCheckableDevice(this)
                CM.startPoll(this, AVEM3Model.U_TRMS) { value ->
                    testModel.measuredData.U.value = (value.toDouble()).autoformat()
                    testModel.measuredU = testModel.measuredData.U.value.toDoubleOrDefault(0.0)
                }
            }
        }

        if (isRunning) {
            with(PAV41) {
                addCheckableDevice(this)
                CM.startPoll(this, PM130Model.I_A_REGISTER) { value ->
                    testModel.measuredData.I.value = "%.3f".format(Locale.ENGLISH, value.toDouble() / 5.0)
                    testModel.measuredI = testModel.measuredData.I.value.toDoubleOrDefault(0.0)
                    if (testModel.measuredI > testModel.specifiedIHV) {
                        cause = "ток утечки превысил заданный"
                    }
                }
                CM.startPoll(this, PM130Model.F_REGISTER) { value ->
                    testModel.measuredData.F.value = value.autoformat()
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
            appendMessageToLog(LogTag.INFO, "Грубое регулирование напряжения...")
            regulateVoltage(specifiedU = testModel.specifiedUHV, minPercent = 3.0, step = 2.0, wait = 200L)
        }
        if (isRunning) {
            appendMessageToLog(LogTag.INFO, "Точное регулирование напряжения...")
            regulateVoltage(specifiedU = testModel.specifiedUHV, minPercent = 0.0, maxPercent = 3.0, step = 0.3)
        }
        if (isRunning) {
            waiting()
        }
        storeTestValues()

        CM.device<Optimus>(UZ91).stopObjectNaVibege()
        sleep(3000)
        CM.device<PR>(DD2).offVIU()
        sleep(200)
    }

    private fun turnOnCircuit() {
        appendMessageToLog(LogTag.INFO, "Сбор схемы")
        CM.device<PR>(DD2).onStart()
        sleepWhileRun(1)
        CM.device<PR>(DD2).onShuntViu()
        sleepWhileRun(1)
        CM.device<PR>(DD2).onPE()
        sleepWhileRun(1)
        CM.device<PR>(DD2).onVIU()
        sleepWhileRun(1)
        CM.device<PR>(DD2).offShuntViu()
        sleepWhileRun(1)
    }

    private fun startFI() {
        appendMessageToLog(LogTag.INFO, "Разгон ЧП...")
        lastFIP1U = 1.0
        CM.device<Optimus>(UZ91).setObjectParamsRun(lastFIP1U)
        if (isRunning) {
            frequency = 50.0
            sleepWhileRun(3)
            CM.device<Optimus>(UZ91).setObjectFCur(frequency)
            sleepWhileRun(3)
            CM.device<Optimus>(UZ91).startObject()
            sleepWhileRun(3)
        }
    }

    private fun waitUntilFIToRun() {
        appendMessageToLog(LogTag.INFO, "Ожидание разгона...")
        sleepWhileRun(3)
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

    private fun waiting() {
        appendMessageToLog(LogTag.INFO, "Ожидание ${testModel.specifiedTestTime.toInt()} с...")
        sleepWhileRun(testModel.specifiedTestTime.toInt(), progressProperty = testModel.progressProperty)
    }

    private fun storeTestValues() {
        testModel.storedData.U.value = testModel.measuredData.U.value
        testModel.storedData.I.value = testModel.measuredData.I.value
        testModel.storedData.F.value = testModel.measuredData.F.value
        testModel.storedData.time.value = testModel.measuredData.time.value
    }

    override fun result() {
        super.result()

        if (!isSuccess) {
            if (cause == "ток утечки превысил заданный") {
                testModel.measuredData.result.value = "Пробой"
                appendMessageToLog(LogTag.ERROR, "Испытание прервано по причине: $cause")
            } else {
                testModel.measuredData.result.value = "Прервано"
                appendMessageToLog(LogTag.ERROR, "Испытание прервано по причине: $cause")
            }
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
        testModel.measuredData.I.value = testModel.storedData.I.value
        testModel.measuredData.F.value = testModel.storedData.F.value
        testModel.measuredData.time.value = testModel.storedData.time.value
    }

    override fun saveProtocol() {
        reportFields["TEST_NAME_HV"] = name

        reportFields["POWER"] = testModel.specifiedP.toString()
        reportFields["VOLTAGE_LIN"] = testModel.specifiedU.toString()
        reportFields["COS"] = testModel.specifiedCos.toString()
        reportFields["EFFICIENCY"] = testModel.specifiedEfficiency.toString()
        reportFields["AMPERAGE_PHASE"] = testModel.specifiedI.toString()
        reportFields["RPM"] = testModel.specifiedRPM.toString()
        reportFields["FREQ"] = testModel.specifiedF.toString()
        reportFields["SCHEME"] = testModel.specifiedScheme

        reportFields["U_SPEC_HV"] = testModel.specifiedData.U.value
        reportFields["U_MEAS_HV"] = testModel.measuredData.U.value
        reportFields["I_SPEC_HV"] = testModel.specifiedData.I.value
        reportFields["I_MEAS_HV"] = testModel.measuredData.I.value
        reportFields["FREQ_HV"] = testModel.measuredData.F.value
        reportFields["RESULT_HV"] = testModel.measuredData.result.value

        super.saveProtocol()
    }
}
