package me.sunnydaydev.mvvmkit.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 10.08.2018.
 * mail: mail@sunnydaydev.me
 */

enum class LifecycleScope { CREATED, STARTED, RESUMED }

internal class ScopeLifecycleObserver<T: Any>(
        private val scope: LifecycleScope,
        private val onEnter: () -> T,
        private val onExit: (T) -> Unit
): LifecycleObserver {

    private lateinit var value: T

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    internal fun onCreated() {
        if (scope == LifecycleScope.CREATED) value = onEnter()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    internal fun onStarted() {
        if (scope == LifecycleScope.STARTED) value = onEnter()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    internal fun onResumed() {
        if (scope == LifecycleScope.RESUMED) value = onEnter()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    internal fun onPaused() {
        if (scope == LifecycleScope.RESUMED) onExit(value)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    internal fun onStopped() {
        if (scope == LifecycleScope.STARTED) onExit(value)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    internal fun onDestroyed() {
        if (scope == LifecycleScope.CREATED) onExit(value)
    }

}

fun <T: Any> Lifecycle.addScopedObserver(
        scope: LifecycleScope,
        onEnter: () -> T,
        onExit: (T) -> Unit
) {
    val observer = ScopeLifecycleObserver(scope, onEnter, onExit)
    addObserver(observer)
}
