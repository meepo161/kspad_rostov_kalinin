package ru.avem.stand.modules.r.common.settings

import ru.avem.stand.modules.r.common.settings.main.MainSettingsViewModel
import ru.avem.stand.modules.r.common.settings.users.UserItemViewModel
import tornadofx.*

class StandSettingsTabController : Controller() {
    fun <T> serializeModel(itemViewModel: ItemViewModel<T>) {
        if (itemViewModel.isNotDirty) {
            return
        } else {
            itemViewModel.commit()
        }

        when (itemViewModel) {
            is MainSettingsViewModel -> {
            }
            is UserItemViewModel -> {
            }
        }
    }
}
