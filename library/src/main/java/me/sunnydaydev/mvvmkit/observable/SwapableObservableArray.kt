package me.sunnydaydev.mvvmkit.observable


/**
 * Created by sunny on 31.05.2018.
 * mail: mail@sunnydaydev.me
 */

open class SwapableObservableList<T>: ExtendedObservableArrayList<T>() {

    fun move(fromIndex: Int, toIndex: Int, notify: Boolean = true) {

        if (fromIndex == toIndex) return

        synchronized(this) {
            add(toIndex, removeAt(fromIndex, false), false)
        }

        if (notify) {
            notifyMoved(fromIndex, toIndex, 1)
        }

    }

    fun swap(fromIndex: Int, toIndex: Int, notify: Boolean = true) {

        synchronized(this) {
            add(toIndex, removeAt(fromIndex, false), false)
            add(fromIndex, removeAt(toIndex + 1, false), false)
        }

        if (notify) {
            notifyMoved(fromIndex, toIndex, 1)
            notifyMoved(toIndex + 1, fromIndex, 1)
        }

    }

}