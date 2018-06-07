package me.sunnydaydev.mvvmkit.observable

import androidx.databinding.ListChangeRegistry
import androidx.databinding.ObservableList

/**
 * Created by sunny on 07.06.2018.
 * mail: mail@sunnydaydev.me
 */

class MergedMVVMList<T>(private vararg val lists: MVVMList<T>): MVVMList<T> {

    @Transient
    private var listeners: ListChangeRegistry = ListChangeRegistry()

    private val childOnListChangedCallback = object: ObservableList.OnListChangedCallback<MVVMList<T>>() {

        override fun onChanged(sender: MVVMList<T>) {
            listeners.notifyChanged(this@MergedMVVMList, getFixedIndex(sender, 0), sender.size)
        }

        override fun onItemRangeRemoved(sender: MVVMList<T>, positionStart: Int, itemCount: Int) {
            listeners.notifyRemoved(this@MergedMVVMList, getFixedIndex(sender, positionStart), itemCount)
        }

        override fun onItemRangeMoved(sender: MVVMList<T>, fromPosition: Int, toPosition: Int, itemCount: Int) {
            listeners.notifyMoved(this@MergedMVVMList, getFixedIndex(sender, fromPosition), getFixedIndex(sender, toPosition), itemCount)
        }

        override fun onItemRangeInserted(sender: MVVMList<T>, positionStart: Int, itemCount: Int) {
            listeners.notifyInserted(this@MergedMVVMList, getFixedIndex(sender, positionStart), itemCount)
        }

        override fun onItemRangeChanged(sender: MVVMList<T>, positionStart: Int, itemCount: Int) {
            listeners.notifyChanged(this@MergedMVVMList, getFixedIndex(sender, positionStart), itemCount)
        }

        private fun getFixedIndex(list: MVVMList<T>, index: Int): Int {
            val listIndex = lists.indexOf(list)
            return (0 until listIndex).sumBy { lists[it].size } + index
        }

    }

    init {
        lists.forEach { it.addOnListChangedCallback(childOnListChangedCallback) }
    }

    override fun move(fromIndex: Int, toIndex: Int) = notSupported()

    override fun swap(fromIndex: Int, toIndex: Int) = notSupported()

    override val size: Int
        get() = lists.sumBy { it.size }

    override fun contains(element: T): Boolean = lists.any { it.contains(element) }

    override fun containsAll(elements: Collection<T>): Boolean = elements.all { contains(it) }

    override fun get(index: Int): T =  getTargetList(index).let {
        val checkedIndex = index - it.indexOffset
        it.list[checkedIndex]
    }

    override fun indexOf(element: T): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isEmpty(): Boolean = lists.all { it.isEmpty() }

    override fun addOnListChangedCallback(listener: ObservableList.OnListChangedCallback<out ObservableList<T>>) {
        listeners.add(listener)
    }

    override fun removeOnListChangedCallback(listener: ObservableList.OnListChangedCallback<out ObservableList<T>>) {
        listeners.remove(listener)
    }

    override fun iterator(): Iterator = Iterator()

    @Deprecated(message = "Merged list immutable",level = DeprecationLevel.HIDDEN)
    override fun lastIndexOf(element: T): Int = notSupported()

    @Deprecated(message = "Merged list immutable",level = DeprecationLevel.HIDDEN)
    override fun add(element: T): Boolean = notSupported()

    @Deprecated(message = "Merged list immutable",level = DeprecationLevel.HIDDEN)
    override fun add(index: Int, element: T) = notSupported()

    @Deprecated(message = "Merged list immutable",level = DeprecationLevel.HIDDEN)
    override fun addAll(index: Int, elements: Collection<T>): Boolean = notSupported()

    @Deprecated(message = "Merged list immutable",level = DeprecationLevel.HIDDEN)
    override fun addAll(elements: Collection<T>): Boolean = notSupported()

    @Deprecated(message = "Merged list immutable",level = DeprecationLevel.HIDDEN)
    override fun clear() = notSupported()

    @Deprecated(message = "Merged list immutable",level = DeprecationLevel.HIDDEN)
    override fun listIterator(): MutableListIterator<T> = notSupported()

    @Deprecated(message = "Merged list immutable",level = DeprecationLevel.HIDDEN)
    override fun listIterator(index: Int): MutableListIterator<T> = notSupported()

    @Deprecated(message = "Merged list immutable",level = DeprecationLevel.HIDDEN)
    override fun remove(element: T): Boolean = notSupported()

    @Deprecated(message = "Merged list immutable",level = DeprecationLevel.HIDDEN)
    override fun removeAll(elements: Collection<T>): Boolean = notSupported()

    @Deprecated(message = "Merged list immutable",level = DeprecationLevel.HIDDEN)
    override fun removeAt(index: Int): T = notSupported()

    @Deprecated(message = "Merged list immutable",level = DeprecationLevel.HIDDEN)
    override fun retainAll(elements: Collection<T>): Boolean = notSupported()

    @Deprecated(message = "Merged list immutable",level = DeprecationLevel.HIDDEN)
    override fun set(index: Int, element: T): T = notSupported()

    @Deprecated(message = "Merged list immutable",level = DeprecationLevel.HIDDEN)
    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> = notSupported()

    private fun notSupported(): Nothing = error("Not supported")

    private fun getTargetList(index: Int): TargetList<T> {

        var indexOffset = 0

        return lists.find {

            val containIndex = index >= indexOffset && index <= it.lastIndex + indexOffset
            indexOffset += it.size

            containIndex

        } ?.let {

            TargetList(indexOffset, it)

        } ?: throw IndexOutOfBoundsException("Index: $index, size: $size")

    }

    private data class TargetList<T>(val indexOffset: Int, val list: MVVMList<T>)

    inner class Iterator: MutableIterator<T> {

        private var index = -1

        override fun hasNext(): Boolean = size != 0 && index < size - 1

        override fun next(): T = this@MergedMVVMList[+index]

        @Deprecated(message = "Merged list immutable",level = DeprecationLevel.HIDDEN)
        override fun remove() = notSupported()

    }

}