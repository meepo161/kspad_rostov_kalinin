package ru.avem.stand.modules.r.storage.users

data class User(
    var login: String,
    var fio: String,
    var department: String,
    var position: String,
    var level: Int,
    var password: String
) {
    override fun toString() = login
}
