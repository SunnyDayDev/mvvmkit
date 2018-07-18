package me.sunnydaydev.mvvmkit.observable

import androidx.databinding.ListChangeRegistry
import androidx.databinding.ObservableList

/**
 * Created by sunny on 07.06.2018.
 * mail: mail@sunnydaydev.me
 */

class MergedMVVMList<T>(vararg lists: MVVMList<out T>): ImmutableMVVMList<T> {

    @Suppress("UNCHECKED_CAST")
    private val lists: List<MVVMList<T>> = lists.map { it as MVVMList<T> }

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

        private fun getFixedIndex(list: MVVMList<T>, index: Int): Int = getIndexOffset(list) + index

    }

    init {
        this.lists.forEach { it.addOnListChangedCallback(childOnListChangedCallback)}
    }

    override val size: Int get() = lists.sumBy { it.size }

    override fun contains(element: T): Boolean = lists.any { it.contains(element) }

    override fun containsAll(elements: Collection<T>): Boolean = elements.all { contains(it) }

    override fun get(index: Int): T {

        return lists.find {

            val offset = getIndexOffset(it)
            index >= offset && index < offset + it.size

        } ?.let {

            val checkedIndex = index - getIndexOffset(it)
            it[checkedIndex]

        } ?: throw IndexOutOfBoundsException("Index: $index, size: $size")

    }

    override fun indexOf(element: T): Int = lists.find {  it.contains(element) }
            ?.let { it.indexOf(element) + getIndexOffset(it) } ?: -1

    override fun lastIndexOf(element: T): Int = lists.findLast {  it.contains(element) }
            ?.let { it.indexOf(element) + getIndexOffset(it) } ?: -1

    override fun isEmpty(): Boolean = lists.all { it.isEmpty() }

    override fun addOnListChangedCallback(listener: ObservableList.OnListChangedCallback<out ObservableList<T>>) {
        listeners.add(listener)
    }

    override fun removeOnListChangedCallback(listener: ObservableList.OnListChangedCallback<out ObservableList<T>>) {
        listeners.remove(listener)
    }

    override fun iterator() = ImmutableMVVMList.ImmutableIterator(this)

    override fun listIterator() = ImmutableMVVMList.ImmutableListIterator(this, -1)

    override fun listIterator(index: Int) = ImmutableMVVMList.ImmutableListIterator(this, index)

    private fun getIndexOffset(list: MVVMList<T>): Int {
        val listIndex = lists.indexOf(list)
        return (0 until listIndex).sumBy { lists[it].size }
    }

    class Builder<T> {

        private val lists = mutableListOf<MVVMList<T>>()

        fun add(list: MVVMList<T>): Builder<T> = this.also { lists.add(list) }

        fun add(item: T): Builder<T> = this.also { lists.add(MVVMArrayList(item)) }

        fun build(): MergedMVVMList<T> = MergedMVVMList(*lists.toTypedArray())

    }

}