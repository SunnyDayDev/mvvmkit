package me.sunnydaydev.mvvmkit.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Created by sunny on 31.05.2018.
 * mail: mail@sunnydaydev.me
 */

abstract class MVVMViewModelFactory: ViewModelProvider.Factory {

    protected abstract val creators: Map<Class<out ViewModel>, () -> ViewModel>

    override fun <T : ViewModel> create(klass: Class<T>): T {

        val creator = creators[klass] ?: creators.entries
                .firstOrNull { klass.isAssignableFrom(it.key) }
                ?.value
        ?: throw IllegalArgumentException("Unknown view model class: $klass")

        @Suppress("UNCHECKED_CAST")
        return creator() as T

    }

}