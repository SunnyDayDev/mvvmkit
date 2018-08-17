package me.sunnydaydev.mvvmkit.observable

import androidx.databinding.ListChangeRegistry
import androidx.databinding.ObservableList
import androidx.lifecycle.ViewModel
import me.sunnydaydev.mvvmkit.util.OnDemandSource

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 17.08.2018.
 * mail: mail@sunnydaydev.me
 */

interface MVVMSourceList<T>: ImmutableMVVMList<T> {

    companion object {
        private fun notSupported(): Nothing = error("Not supported")
    }

    @Deprecated(level = DeprecationLevel.HIDDEN, message = "Not supported")
    override fun contains(element: T): Boolean = notSupported()

    override fun containsAll(elements: Collection<T>): Boolean = notSupported()

    override fun indexOf(element: T): Int = notSupported()

    override fun isEmpty(): Boolean = size == 0

    override fun lastIndexOf(element: T): Int = notSupported()

}

class MVVMMapOnDemandSourceList<T, VM: ViewModel>(
        private val source: OnDemandSource<T>,
        private val mapper: (T) -> VM
): MVVMSourceList<VM> {

    private val callbacks = ListChangeRegistry()

    private val iteratorSource get() = object: ImmutableMVVMList.IteratorSource<VM> {

        override val size = this@MVVMMapOnDemandSourceList.size

        override fun get(index: Int) = this@MVVMMapOnDemandSourceList[index]

    }

    override val size = source.size

    override fun get(index: Int): VM = mapper(source[index])

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

}