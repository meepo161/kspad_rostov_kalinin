package ru.avem.stand.modules.r.storage.users

import tornadofx.*

object UsersRepository {
    val users = observableList(
        User("admin", "admin", "admin", "admin", 10, "101010"),
        User("vip", "vip", "vip", "vip", 9, "999"),
        User("default", "default", "default", "default", 8, "888")
    )

    fun getUserByLogin(login: String): User = users.first { it.login == login }
}
