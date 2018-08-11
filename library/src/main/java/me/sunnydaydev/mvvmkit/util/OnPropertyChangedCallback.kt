package me.sunnydaydev.mvvmkit.util

import androidx.databinding.Observable
import androidx.lifecycle.Lifecycle

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 03.08.2018.
 * mail: mail@sunnydaydev.me
 */

private class OnPropertyChangedCallback<T: Observable>(
        private val action: (T, Int) -> Unit
): Observable.OnPropertyChangedCallback() {

    override fun onPropertyChanged(sender: Observable, propertyId: Int) {
        @Suppress("UNCHECKED_CAST")
        val target = sender as? T ?: return
        action(target, propertyId)
    }

}

fun <T: Observable> T.addOnPropertyChangedCallback(
        action: (T, Int) -> Unit
): Observable.OnPropertyChangedCallback {
    val callback = OnPropertyChangedCallback(action)
    addOnPropertyChangedCallback(callback)
    return callback
}

fun <T: Observable> T.addOnPropertyChangedCallback(
        lifeCycle: Lifecycle,
        scope: LifecycleScope = LifecycleScope.CREATED,
        listener: (T, Int) -> Unit
) = lifeCycle.addScopedObserver(
        scope,
        onEnter = { this.addOnPropertyChangedCallback(listener) },
        onExit = ::removeOnPropertyChangedCallback
)