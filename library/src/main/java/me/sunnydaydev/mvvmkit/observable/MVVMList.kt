package me.sunnydaydev.mvvmkit.observable

import androidx.databinding.ListChangeRegistry
import androidx.databinding.ObservableList

/**
 * Created by sunny on 31.05.2018.
 * mail: mail@sunnydaydev.me
 */

interface MVVMList<T>: ObservableList<T> {

    fun move(fromIndex: Int, toIndex: Int)

    fun swap(fromIndex: Int, toIndex: Int)

    fun setAll(items: Collection<T>)

    fun setAll(items: Collection<T>, startIndex: Int, count: Int)

}

open class MVVMArrayList<T>(): ArrayList<T>(), MVVMList<T> {

    @Transient
    private var listeners: ListChangeRegistry = ListChangeRegistry()

    private var notificationsEnabled = true

    constructor(vararg items: T): this() {
        notifiableAddAll(items.toList(), false)
    }

    override fun addOnListChangedCallback(listener: ObservableList.OnListChangedCallback<out ObservableList<T>>) {
        listeners.add(listener)
    }

    override fun removeOnListChangedCallback(listener: ObservableList.OnListChangedCallback<out ObservableList<T>>) {
        listeners.remove(listener)
    }

    override fun add(element: T): Boolean {
        notifiableAdd(size, element)
        return true
    }

    override fun add(index: Int, element: T) = notifiableAdd(index, element)

    override fun addAll(elements: Collection<T>): Boolean = notifiableAddAll(elements)

    override fun addAll(index: Int, elements: Collection<T>): Boolean = notifiableAddAll(index, elements)

    override fun clear() = notifiableClear()

    override fun remove(element: T): Boolean {
        val index = indexOf(element)
        return if (index >= 0) {
            removeAt(index)
            true
        } else {
            false
        }
    }

    override fun removeAt(index: Int): T = notifiableRemoveAt(index)

    override fun set(index: Int, element: T): T = notifiableSet(index, element)

    override fun removeRange(fromIndex: Int, toIndex: Int) = notifiableRemoveRange(fromIndex, toIndex)

    override fun move(fromIndex: Int, toIndex: Int) = notifiableMove(fromIndex, toIndex)

    override fun swap(fromIndex: Int, toIndex: Int) = notifiableSwap(fromIndex, toIndex)

    override fun setAll(items: Collection<T>) = setAll(items, 0, size)
    
    override fun setAll(items: Collection<T>, startIndex: Int, count: Int) =
            notifiableSetAll(items, startIndex, count)

    protected fun notifiableClear(notify: Boolean = true) {
        val oldSize = size
        super.clear()
        if (oldSize != 0 && notify) {
            notifyRemoved(0, oldSize)
        }
    }

    protected fun notifiableRemoveRange(fromIndex: Int, toIndex: Int, notify: Boolean = true) {
        super.removeRange(fromIndex, toIndex)
        if(notify) notifyRemoved(fromIndex, toIndex - fromIndex)
    }

    protected fun notifiableAddAll(elements: Collection<T>, notify: Boolean = true): Boolean {
        val oldSize = size
        val added = super.addAll(elements)
        if (added && notify) {
            notifyInserted(oldSize, size - oldSize)
        }
        return added
    }

    protected fun notifiableAddAll(
            index: Int, elements: Collection<T>,
            notify: Boolean = true
    ): Boolean {

        val added = super.addAll(index, elements)
        if (added && notify) {
            notifyInserted(index, elements.size)
        }
        return added

    }

    protected fun notifiableAdd(index: Int, element: T, notify: Boolean = true) {

        super.add(index, element)
        if (notify) {
            notifyInserted(index, 1)
        }

    }

    protected fun notifiableSet(index: Int, element: T, notify: Boolean = true): T {

        val prev = super.set(index, element)
        if (notify) {
            listeners.notifyChanged(this, index, 1)
        }
        return prev

    }

    protected fun notifiableRemoveAt(index: Int, notify: Boolean = true): T {

        val element = super.removeAt(index)
        if (notify) {
            notifyRemoved(index, 1)
        }
        return element

    }

    protected fun notifiableMove(fromIndex: Int, toIndex: Int, notify: Boolean = true) {

        if (fromIndex == toIndex) return

        synchronized(this) {
            notifiableAdd(toIndex, notifiableRemoveAt(fromIndex, false), false)
        }

        if (notify) {
            notifyMoved(fromIndex, toIndex, 1)
        }

    }

    protected fun notifiableSwap(firstIndex: Int, secondIndex: Int, notify: Boolean = true) {

        synchronized(this) {
            val first = this[firstIndex]
            notifiableSet(firstIndex, this[secondIndex], false)
            notifiableSet(secondIndex, first, false)
        }

        if (notify) {
            notifyMoved(firstIndex, secondIndex, 1)
            notifyMoved(secondIndex + 1, firstIndex, 1)
        }

    }
    
    protected fun notifiableSetAll(
            items: Collection<T>, startIndex: Int, count: Int, notify: Boolean = true) {
        
        synchronized(this) {
            notifiableRemoveRange(startIndex, startIndex + count, false)
            notifiableAddAll(startIndex, items, false)
        }
        
        if (notify) {
            
            when {
                count > items.size -> {
                    notifyChanged(startIndex, items.size)
                    notifyRemoved(startIndex + items.size, count - items.size)
                }
                count < items.size -> {
                    notifyChanged(startIndex, count)
                    notifyInserted(startIndex + count, items.size - count)
                }
                else -> notifyChanged(startIndex, count)
            }
            
        }
        
    }

    protected fun notifyInserted(start: Int, count: Int) {
        if (notificationsEnabled) listeners.notifyInserted(this, start, count)
    }

    protected fun notifyMoved(from: Int, to: Int, count: Int) {
        if (notificationsEnabled) listeners.notifyMoved(this, from, to, count)
    }

    protected fun notifyRemoved(start: Int, count: Int) {
        if (notificationsEnabled) listeners.notifyRemoved(this, start, count)
    }

    protected fun notifyChanged() {
        if (notificationsEnabled) listeners.notifyChanged(this)
    }

    protected fun notifyChanged(start: Int, count: Int) {
        if (notificationsEnabled) listeners.notifyChanged(this, start, count)
    }

    protected fun silent(action: () -> Unit) {
        synchronized(this) {
            notificationsEnabled = false
            action()
            notificationsEnabled = true
        }
    }

}