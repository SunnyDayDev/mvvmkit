package me.sunnydaydev.mvvmkit.observable

import androidx.databinding.BaseObservable

/**
 * Created by sunny on 31.05.2018.
 * mail: mail@sunnydaydev.me
 */

object Event

internal sealed class OptionalBox<T>{
    class Empty<T>: OptionalBox<T>()
    data class Value<T>(val value: T): OptionalBox<T>()
}

open class BaseCommand<T>: BaseObservable() {

    internal var event: OptionalBox<T> = OptionalBox.Empty()

    protected fun internalFire(event: T) {
        this.event = OptionalBox.Value(event)
        notifyChange()
    }

    protected fun internalHandle(handleAction: (T) -> Boolean) {
        val event = this.event
        if (event is OptionalBox.Value && handleAction(event.value)) {
            clear()
        }
    }

    fun clear() {
        event = OptionalBox.Empty()
    }

}

class PureCommand: BaseCommand<Event>() {

    fun fire() = internalFire(Event)

    fun handle(action: () -> Unit) = internalHandle {
        action()
        true
    }

}

class Command<T>: BaseCommand<T>() {

    fun fire(event: T) = internalFire(event)

    fun handle(action: (T) -> Unit) = internalHandle {
        action(it)
        true
    }

}

class TargetedCommand<E, T: Any>: BaseCommand<Pair<E, T>>() {

    fun fire(event: E, target: T) = internalFire(event to target)

    fun handle(target: T, action: (E) -> Unit) = internalHandle {
        if (target == it.second) {
            action(it.first)
            true
        } else {
            false
        }
    }

}

class TargetedPureCommand<T: Any>: BaseCommand<T>() {

    fun fire(target: T) = internalFire(target)

    fun handle(target: T, action: () -> Unit) = internalHandle {
        if (target == it) {
            action()
            true
        } else {
            false
        }
    }

}