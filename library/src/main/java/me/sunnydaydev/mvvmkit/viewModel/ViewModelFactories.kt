package me.sunnydaydev.mvvmkit.viewModel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider

/**
 * Created by sunny on 31.05.2018.
 * mail: mail@sunnydaydev.me
 */

class InjectableViewModelsMapFactory @Inject constructor(
        private val creators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(klass: Class<T>): T {

        val creator = creators[klass] ?: creators.entries
                .firstOrNull { klass.isAssignableFrom(it.key) } ?.value
        ?: throw IllegalArgumentException("Unknown view model class: $klass")

        @Suppress("UNCHECKED_CAST")
        return creator.get() as T

    }

}