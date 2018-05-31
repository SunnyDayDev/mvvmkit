package me.sunnydaydev.mvvmkit.observable

import android.databinding.ListChangeRegistry
import android.databinding.ObservableList

/**
 * Created by sunny on 31.05.2018.
 * mail: mail@sunnydaydev.me
 */

open class ExtendedObservableArrayList<T>: ArrayList<T>(), ObservableList<T> {

    @Transient
    private var listeners: ListChangeRegistry = ListChangeRegistry()

    override fun addOnListChangedCallback(listener: ObservableList.OnListChangedCallback<out ObservableList<T>>) {
        listeners.add(listener)
    }

    override fun removeOnListChangedCallback(listener: ObservableList.OnListChangedCallback<out ObservableList<T>>) {
        listeners.remove(listener)
    }

    override fun add(element: T): Boolean {
        add(size -1, element, true)
        return true
    }

    override fun add(index: Int, element: T) = add(index, element, true)

    override fun addAll(elements: Collection<T>): Boolean = addAll(elements, true)

    override fun addAll(index: Int, elements: Collection<T>): Boolean = addAll(index, elements, true)

    override fun clear() = clear(true)

    override fun remove(element: T): Boolean {
        val index = indexOf(element)
        return if (index >= 0) {
            removeAt(index)
            true
        } else {
            false
        }
    }

    override fun removeAt(index: Int): T = removeAt(index, true)

    override fun set(index: Int, element: T): T = set(index, element, true)

    override fun removeRange(fromIndex: Int, toIndex: Int) = removeRange(fromIndex, toIndex, true)

    protected fun clear(notify: Boolean) {
        val oldSize = size
        super.clear()
        if (oldSize != 0 && notify) {
            notifyRemoved(0, oldSize)
        }
    }

    protected fun removeRange(fromIndex: Int, toIndex: Int, notify: Boolean) {
        super.removeRange(fromIndex, toIndex)
        if(notify) notifyRemoved(fromIndex, toIndex - fromIndex)
    }

    protected fun addAll(elements: Collection<T>, notify: Boolean): Boolean {
        val oldSize = size
        val added = super.addAll(elements)
        if (added && notify) {
            notifyInserted(oldSize, size - oldSize)
        }
        return added
    }

    protected fun addAll(index: Int, elements: Collection<T>, notify: Boolean): Boolean {
        val added = super.addAll(index, elements)
        if (added && notify) {
            notifyInserted(index, elements.size)
        }
        return added
    }

    protected fun add(index: Int, element: T, notify: Boolean) {

        super.add(index, element)
        if (notify) {
            notifyInserted(index, 1)
        }

    }

    protected fun set(index: Int, element: T, notify: Boolean): T {

        val prev = super.set(index, element)
        if (notify) {
            listeners.notifyChanged(this, index, 1)
        }
        return prev

    }

    protected fun removeAt(index: Int, notify: Boolean): T {

        val element = super.removeAt(index)
        if (notify) {
            notifyRemoved(index, 1)
        }
        return element

    }

    protected fun notifyInserted(start: Int, count: Int) {
        listeners.notifyInserted(this, start, count)
    }

    protected fun notifyMoved(from: Int, to: Int, count: Int) {
        listeners.notifyMoved(this, from, to, count)
    }

    protected fun notifyRemoved(start: Int, count: Int) {
        listeners.notifyRemoved(this, start, count)
    }

    protected fun notifyChanged() {
        listeners.notifyChanged(this)
    }

    protected fun notifyChanged(start: Int, count: Int) {
        listeners.notifyChanged(this, start, count)
    }

}