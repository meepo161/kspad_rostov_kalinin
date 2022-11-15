package ru.avem.stand.modules.i

import ru.avem.stand.modules.i.tests.Test
import ru.avem.stand.modules.i.views.ViewModule
import tornadofx.*
import kotlin.reflect.KClass

class Head {
    companion object {
        fun head(op: (head: Head) -> Unit) = Head().apply(op)

        fun tests(op: () -> Unit) {
            op()
        }

        fun views(op: () -> Unit) {
            op()
        }
    }

    private val views = mutableListOf<KClass<out ViewModule>>()
    private val modules = mutableMapOf<String, Module>()

    val tests
        get() = modules.values.filterIsInstance<Test>()

    fun addModules(vararg modules: Module) {
        modules.forEach(::addModule)
    }

    private fun addModule(module: Module) {
        modules[module.id] = module
    }

    fun addView(idToModule: KClass<out ViewModule>) {
        views.add(idToModule)
    }

    fun showRequiredViews() {
        views.map { find(it) }.filter(ViewModule::showOnStart).forEach(ViewModule::show)
    }

    fun showTestView(test: Test) {
        showView(test.view)
    }

    fun showView(view: KClass<out ViewModule>) {
        find(view).show()
    }
}
