package ru.avem.stand.modules.i

abstract class Module {
    open val id: String = this::class.simpleName!!

    abstract val testModel: TestModel
}
