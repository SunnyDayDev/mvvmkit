package me.sunnydaydev.mvvmkit.observable

import androidx.databinding.ListChangeRegistry
import androidx.databinding.ObservableList

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 17.08.2018.
 * mail: mail@sunnydaydev.me
 */

class MVVMMListWrapper<T>(
        private var source: List<T>
): MVVMList<T> {

    private val listeners = ListChangeRegistry()

    private val childOnListChangedCallback = object: ObservableList.OnListChangedCallback<MVVMList<T>>() {

        override fun onChanged(sender: MVVMList<T>) {
            if (sender === this@MVVMMListWrapper) return
            listeners.notifyChanged(this@MVVMMListWrapper)
        }

        override fun onItemRangeRemoved(sender: MVVMList<T>, positionStart: Int, itemCount: Int) {
            if (sender === this@MVVMMListWrapper) return
            listeners.notifyRemoved(this@MVVMMListWrapper, positionStart, itemCount)
        }

        override fun onItemRangeMoved(sender: MVVMList<T>, fromPosition: Int, toPosition: Int, itemCount: Int) {
            if (sender === this@MVVMMListWrapper) return
            listeners.notifyMoved(this@MVVMMListWrapper, fromPosition, toPosition, itemCount)
        }

        override fun onItemRangeInserted(sender: MVVMList<T>, positionStart: Int, itemCount: Int) {
            if (sender === this@MVVMMListWrapper) return
            listeners.notifyInserted(this@MVVMMListWrapper, positionStart, itemCount)
        }

        override fun onItemRangeChanged(sender: MVVMList<T>, positionStart: Int, itemCount: Int) {
            if (sender === this@MVVMMListWrapper) return
            listeners.notifyChanged(this@MVVMMListWrapper, positionStart, itemCount)
        }

    }

    private val iteratorDelegate = object: MVVMListIterator.Delegate<T> {

        override val size: Int get() = this@MVVMMListWrapper.size

        override fun get(index: Int): T = this@MVVMMListWrapper[index]

        override fun remove(index: Int) {
            this@MVVMMListWrapper.removeAt(index)
        }

        override fun set(index: Int, value: T) {
            this@MVVMMListWrapper[index] = value
        }

        override fun add(index: Int, element: T) {
            this@MVVMMListWrapper.add(index, element)
        }

    }

    init {
        source.addOnListChangedCallback()
    }

    override fun move(fromIndex: Int, toIndex: Int) {
        val source = this.source
        if (source is MVVMList) source.move(fromIndex, toIndex)
        else {
            source.mutable()[toIndex] = source.mutable().removeAt(fromIndex)
        }
    }

    override fun swap(fromIndex: Int, toIndex: Int) {
        val source = this.source
        if (source is MVVMList) source.swap(fromIndex, toIndex)
        else {
            val first = source[fromIndex]
            source.mutable()[fromIndex] = source[toIndex]
            source.mutable()[toIndex] = first
        }
    }

    override fun setAll(items: Collection<T>) {
        val source = this.source
        if (source is MVVMList) source.setAll(items)
        else {
            source.mutable().clear()
            source.mutable().addAll(items)
        }
    }

    override fun setAll(items: Collection<T>, startIndex: Int, count: Int) {
        val source = this.source
        if (source is MVVMList) source.setAll(items, startIndex, count)
        else {
            source.mutable().removeAll(source.subList(startIndex, startIndex + count))
        }
    }

    override val size get() = source.size

    override fun contains(element: T): Boolean = source.contains(element)

    override fun containsAll(elements: Collection<T>): Boolean = source.containsAll(elements)

    override fun get(index: Int): T = source[index]

    override fun indexOf(element: T): Int = source.indexOf(element)

    override fun isEmpty(): Boolean = source.isEmpty()

    override fun iterator(): MutableIterator<T> = MVVMIterator(iteratorDelegate)

    override fun lastIndexOf(element: T): Int = source.mutable().lastIndexOf(element)

    override fun add(element: T): Boolean = source.mutable().add(element)

    override fun add(index: Int, element: T) = source.mutable().add(index, element)

    override fun addAll(index: Int, elements: Collection<T>): Boolean = source.mutable().addAll(index, elements)

    override fun addAll(elements: Collection<T>): Boolean = source.mutable().addAll(elements)

    override fun clear() = source.mutable().clear()

    override fun listIterator(): MutableListIterator<T> = MVVMListIterator(iteratorDelegate)

    override fun listIterator(index: Int): MutableListIterator<T> = MVVMListIterator(iteratorDelegate, index)

    override fun remove(element: T): Boolean = source.mutable().remove(element)

    override fun removeAll(elements: Collection<T>): Boolean = source.mutable().removeAll(elements)

    override fun removeAt(index: Int): T = source.mutable().removeAt(index)

    override fun set(index: Int, element: T): T = set(index, element)

    override fun retainAll(elements: Collection<T>): Boolean = source.mutable().retainAll(elements)

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> = subList(fromIndex, toIndex)

    override fun addOnListChangedCallback(callback: ObservableList.OnListChangedCallback<out ObservableList<T>>?) {
        listeners.add(callback)
    }

    override fun removeOnListChangedCallback(callback: ObservableList.OnListChangedCallback<out ObservableList<T>>?) {
        listeners.remove(callback)
    }

    fun setSource(list: List<T>): List<T>? {
        if (list === source) return null

        val current = source
        source.removeOnListChangedCallback()
        source = list
        list.addOnListChangedCallback()
        notifyChanged()

        return current
    }

    fun notifyChanged() {
        listeners.notifyChanged(this)
    }

    private fun List<T>.addOnListChangedCallback() =
            (this as? ObservableList<T>)?.addOnListChangedCallback(childOnListChangedCallback)

    private fun List<T>.removeOnListChangedCallback() =
            (this as? ObservableList<T>)?.removeOnListChangedCallback(childOnListChangedCallback)

    private fun List<T>.mutable(): MutableList<T> = (this as? MutableList<T>)
            ?: error("Operation not supported. Source list is immutable")

}