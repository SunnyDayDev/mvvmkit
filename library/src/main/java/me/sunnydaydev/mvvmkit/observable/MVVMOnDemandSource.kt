package me.sunnydaydev.mvvmkit.observable

import androidx.databinding.ListChangeRegistry
import androidx.databinding.ObservableList
import androidx.lifecycle.ViewModel
import me.sunnydaydev.mvvmkit.util.OnDemandSource
import me.sunnydaydev.mvvmkit.util.notSupportedOperation

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 17.08.2018.
 * mail: mail@sunnydaydev.me
 */

interface MVVMOnDemandSource<VM>: ImmutableMVVMList<VM> {

    companion object {
        private fun notSupportedOperation(): Nothing = error("Not supported")
    }

    @Deprecated(level = DeprecationLevel.HIDDEN, message = "Not supported")
    override fun contains(element: VM): Boolean = notSupportedOperation()

    @Deprecated(level = DeprecationLevel.HIDDEN, message = "Not supported")
    override fun containsAll(elements: Collection<VM>): Boolean = notSupportedOperation()

    @Deprecated(level = DeprecationLevel.HIDDEN, message = "Not supported")
    override fun indexOf(element: VM): Int = notSupportedOperation()

    @Deprecated(level = DeprecationLevel.HIDDEN, message = "Not supported")
    override fun lastIndexOf(element: VM): Int = notSupportedOperation()

    override fun isEmpty(): Boolean = size == 0

}

class MVVMMappableOnDemandSource<T, VM: ViewModel>(
        private val mapper: (T) -> VM
): MVVMOnDemandSource<VM> {

    private var source: OnDemandSource<T>? = null

    private val callbacks = ListChangeRegistry()

    constructor(source: OnDemandSource<T>, mapper: (T) -> VM): this(mapper) {
        setSource(source)
    }

    private val iteratorDelegate = object: MVVMListIterator.Delegate<VM> {

        override val size: Int get() = this@MVVMMappableOnDemandSource.size

        override fun get(index: Int): VM = this@MVVMMappableOnDemandSource[index]

        override fun remove(index: Int) = notSupportedOperation()

        override fun set(index: Int, value: VM) = notSupportedOperation()

        override fun add(index: Int, element: VM) = notSupportedOperation()

    }

    override val size get() = source?.size ?: 0

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

    override fun listIterator() = MVVMListIterator(iteratorDelegate)

    override fun listIterator(index: Int) = MVVMListIterator(iteratorDelegate, index)

    override fun iterator() = MVVMIterator(iteratorDelegate)

    fun setSource(source: OnDemandSource<T>?): OnDemandSource<T>? {
        if (source === this.source) return null

        val current = this.source
        this.source = source

        callbacks.notifyChanged(this)

        return current
    }

}