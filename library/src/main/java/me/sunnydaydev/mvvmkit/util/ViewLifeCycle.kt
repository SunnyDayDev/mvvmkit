package me.sunnydaydev.mvvmkit.util

import androidx.lifecycle.*

/**
 * Created by sunny on 08.06.2018.
 * mail: mail@sunnydaydev.me
 */

class ViewLifeCycle: Lifecycle(), LifecycleObserver {

    private val lifecycle = LifecycleWrapper()

    private val registry get() = lifecycle.registry

    init {
        registry.markState(State.INITIALIZED)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    internal fun onCreate() {
        registry.markState(State.CREATED)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    internal fun onStart() {
        registry.markState(State.STARTED)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    internal fun onResume() {
        registry.markState(State.RESUMED)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    internal fun onPause() {
        registry.markState(State.STARTED)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    internal fun onStop() {
        registry.markState(State.CREATED)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    internal fun onDestroy() {
        registry.markState(State.DESTROYED)
    }

    override fun addObserver(observer: LifecycleObserver) = registry.addObserver(observer)

    override fun removeObserver(observer: LifecycleObserver) = registry.removeObserver(observer)

    override fun getCurrentState(): State = registry.currentState

    internal class LifecycleWrapper: LifecycleOwner {

        val registry = LifecycleRegistry(this)

        override fun getLifecycle(): Lifecycle = registry

    }

}