package me.sunnydaydev.mvvmkit.observable

import androidx.databinding.ListChangeRegistry
import androidx.databinding.ObservableList
import androidx.lifecycle.ViewModel
import me.sunnydaydev.mvvmkit.util.OnDemandSource

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 17.08.2018.
 * mail: mail@sunnydaydev.me
 */

interface MVVMOnDemandSource<T>: ImmutableMVVMList<T> {

    companion object {
        private fun notSupported(): Nothing = error("Not supported")
    }

    @Deprecated(level = DeprecationLevel.HIDDEN, message = "Not supported")
    override fun contains(element: T): Boolean = notSupported()

    @Deprecated(level = DeprecationLevel.HIDDEN, message = "Not supported")
    override fun containsAll(elements: Collection<T>): Boolean = notSupported()

    @Deprecated(level = DeprecationLevel.HIDDEN, message = "Not supported")
    override fun indexOf(element: T): Int = notSupported()

    @Deprecated(level = DeprecationLevel.HIDDEN, message = "Not supported")
    override fun lastIndexOf(element: T): Int = notSupported()

    override fun isEmpty(): Boolean = size == 0

}

class MVVMMappableOnDemandSource<T, VM: ViewModel>(
        private val mapper: (T) -> VM
): MVVMOnDemandSource<VM> {

    private var source: OnDemandSource<T>? = null

    private val callbacks = ListChangeRegistry()

    constructor(source: OnDemandSource<T>, mapper: (T) -> VM): this(mapper) {
        this.source = source
    }

    private val iteratorSource get() = object: ImmutableMVVMList.IteratorSource<VM> {

        override val size = this@MVVMMappableOnDemandSource.size

        override fun get(index: Int) = this@MVVMMappableOnDemandSource[index]

    }

    override val size = source?.size ?: 0

    override fun get(index: Int): VM {
        val source = source ?: error(IndexOutOfBoundsException("Index: $index, size: 0"))
        return mapper(source[index])
    }

    override fun addOnListChangedCallback(callback: ObservableList.OnListChangedCallback<out ObservableList<VM>>?) {
        callbacks.add(callback)
    }

    override fun removeOnListChangedCallback(callback: ObservableList.OnListChangedCallback<out ObservableList<VM>>?) {
        callbacks.remove(callback)
    }

    override fun listIterator(): ImmutableMVVMList.ImmutableListIterator<VM> =
            ImmutableMVVMList.ImmutableListIterator(iteratorSource, -1)

    override fun listIterator(index: Int): ImmutableMVVMList.ImmutableListIterator<VM> =
            ImmutableMVVMList.ImmutableListIterator(iteratorSource, index)

    override fun iterator(): ImmutableMVVMList.ImmutableIterator<VM> =
            ImmutableMVVMList.ImmutableIterator(iteratorSource)

    fun setSource(source: OnDemandSource<T>?): OnDemandSource<T>? {
        if (source == this.source) return null
        val current = this.source
        this.source = source

        callbacks.notifyChanged(this)

        return current
    }

}