package ru.avem.stand.modules.r.common.authorization

import javafx.beans.property.SimpleStringProperty
import ru.avem.stand.modules.r.storage.Properties
import ru.avem.stand.modules.r.storage.users.User
import ru.avem.stand.modules.r.storage.users.UsersRepository.getUserByLogin
import tornadofx.*
import java.nio.file.Path
import java.nio.file.Paths

object AuthorizationModel : ViewModel() {
    override val configPath: Path = Paths.get("cfg/app.properties")

    var user0i = initUser0()
    private fun initUser0(): User {
        var user = User("default", "default", "default", "default", 8, "888")
        if (Properties.standData.isLogin0Enabled) {
            val login0 = Properties.users.find { it.login == config[LOGIN_0_KEY] }
            if (login0 != null) {
                user = login0
            }
        }
        return user
    }

    val user0Prop = SimpleStringProperty(user0i.login)
    val user0: User
        get() = getUserByLogin(user0Prop.value)

    val user0PswdProp = SimpleStringProperty(user0i.password)

    var user1i = initUser1()
    private fun initUser1(): User {
        var user = User("admin", "admin", "admin", "admin", 10, "101010")
        if (Properties.standData.isLogin1Enabled) {
            val login1 = Properties.users.find { it.login == config[LOGIN_1_KEY] }
            if (login1 != null) {
                user = login1
            }
        }
        return user
    }

    val user1Prop = SimpleStringProperty(user1i.login)
    val user1: User
        get() = getUserByLogin(user1Prop.value)

    val user1PswdProp = SimpleStringProperty(user1i.password)
}
