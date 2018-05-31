package me.sunnydaydev.mvvmkit.observable

import android.databinding.BaseObservable

/**
 * Created by sunny on 31.05.2018.
 * mail: mail@sunnydaydev.me
 */

class Command<T>: BaseObservable() {

    // Simple event
    object Fire

    private sealed class Event<T>{
        class Empty<T>: Event<T>()
        data class Value<T>(val value: T): Event<T>()
    }

    private var event: Event<T> = Event.Empty()

    fun fire(event: T) {
        this.event = Event.Value(event)
        notifyChange()
    }

    fun handle(action: (T) -> Unit) {
        val event = this.event
        if (event is Event.Value) {
            action(event.value)
        }
    }

}