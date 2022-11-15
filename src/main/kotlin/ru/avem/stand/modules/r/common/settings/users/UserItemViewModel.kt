package ru.avem.stand.modules.r.common.settings.users

import javafx.beans.binding.BooleanBinding
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import ru.avem.stand.modules.r.storage.Properties
import ru.avem.stand.modules.r.storage.users.User
import tornadofx.*

class UserItemViewModel : ItemViewModel<User>() {
    val users = Properties.users.filter { it.level <= 7 }.toMutableList().observable()
    private val userUpdated = SimpleBooleanProperty()

    override fun onCommit() {
        val user = User(
            login.value,
            fullName.value,
            department.value,
            position.value,
            level.value,
            password.value
        )

        if (users.firstOrNull { it.login == login.value } != null) {
            users.remove(users.first { it.login == login.value })
        }

        users.add(user)

        super.onCommit()
    }

    override val dirty: BooleanBinding
        get() = super.dirty.or(userUpdated)

    val login = bind(User::login)
    val fullName = bind(User::fio)
    val department = bind(User::department)
    val position = bind(User::position)
    val level = bind(User::level)
    val password = bind(User::password)
    val repeatedPassword = SimpleStringProperty()

    fun createUser() {
        val newUser = User(
            login = "Pending...",
            fio = "",
            department = "",
            position = "",
            level = 0,
            password = ""
        )

        rebind {
            item = newUser
        }
        userUpdated.value = true
    }

    fun cloneUser(parentUser: User) {
        val newUser = parentUser.copy(login = parentUser.login + " копия")

        users.add(newUser)
        rebind {
            item = newUser
        }
        userUpdated.value = true
    }

    fun deleteUser(user: User) {
        users.remove(user)
        userUpdated.value = true
    }
}
