package ru.avem.stand.modules.r.common.settings.main

import ru.avem.stand.modules.r.storage.Properties
import ru.avem.stand.modules.r.storage.StandData
import tornadofx.*

class MainSettingsViewModel : ItemViewModel<StandData>(Properties.standData) {
    val fullscreen = bind(StandData::isMaximized)
    val width = bind(StandData::width)
    val height = bind(StandData::height)
    val sizeLocked = bind(StandData::isResizable)
    val kioskMode = bind(StandData::isFullScreen)
    val splashPic = bind(StandData::splashPicture)
    val splashTime = bind(StandData::splashTime)
    val icon = bind(StandData::icon)
    val textSize = bind(StandData::textSize)
    val exitConfirmation = bind(StandData::exitConfirmation)
    val project = bind(StandData::aiel)
    val customer = bind(StandData::customer)
    val customerPlace = bind(StandData::customerPlace)
    val manufacture = bind(StandData::manufacture)
    val manufacturePlace = bind(StandData::manufacturePlace)
    val manufactureDate = bind(StandData::manufactureDate)
    val titleFull = bind(StandData::titleFull)
    val titleShort = bind(StandData::titleShort)
    val serialNumber = bind(StandData::serialNumber)
    val hardwareID = bind(StandData::hardwareID)
    val softwareID = bind(StandData::softwareID)
    val isLogin0Enabled = bind(StandData::isLogin0Enabled)
    val login1Title = bind(StandData::login1Title)
    val login1Level = bind(StandData::login1Level)
    val isLogin1Enabled = bind(StandData::isLogin1Enabled)
    val login2Title = bind(StandData::login2Title)
    val login2Level = bind(StandData::login2Level)
}
